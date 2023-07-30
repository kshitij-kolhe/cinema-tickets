package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.exception.InvalidTicketTypeException;
import uk.gov.dwp.uc.pairtest.model.TicketPrice;
import uk.gov.dwp.uc.pairtest.model.TicketPriceFactoryImpl;


public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private final TicketPriceFactoryImpl ticketPriceFactory;
    private final TicketPaymentServiceImpl ticketPaymentService;
    private final SeatReservationServiceImpl seatReservationService;

    public TicketServiceImpl() {
        ticketPriceFactory = new TicketPriceFactoryImpl();
        ticketPaymentService = new TicketPaymentServiceImpl();
        seatReservationService = new SeatReservationServiceImpl();
    }


    public TicketServiceImpl(final TicketPriceFactoryImpl ticketPriceFactory, final TicketPaymentServiceImpl ticketPaymentService, final SeatReservationServiceImpl seatReservationService) {
        this.ticketPriceFactory = ticketPriceFactory;
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        int totalAmountToPay = 0;
        int totalNumberOfSeats = 0;

        if ( accountId <= 0 || !isPurchaseRequestValid(ticketTypeRequests)) {
            throw new InvalidPurchaseException("Invalid Purchase Request");
        }

        totalAmountToPay = calculateTotalTicketsPrice(ticketTypeRequests);
        totalNumberOfSeats = calculateNumberOfSeats(ticketTypeRequests);

        ticketPaymentService.makePayment(accountId, totalAmountToPay);
        seatReservationService.reserveSeat(accountId, totalNumberOfSeats);

    }


    private boolean isPurchaseRequestValid(final TicketTypeRequest[] ticketTypeRequests) {

        if (calculateNumberOfSeats(ticketTypeRequests) > 20) {
            return false;
        }

        if (!isAdultPresent(ticketTypeRequests)) {
            return false;
        }

        if (numberOfInfantsMoreThanAdults(ticketTypeRequests)) {
            return false;
        }

        return true;
    }

    private boolean isAdultPresent(final TicketTypeRequest[] ticketTypeRequests) {

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {

            if (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.ADULT && ticketTypeRequest.getNoOfTickets() > 0) {

                return true;
            }
        }

        return false;
    }

    private boolean numberOfInfantsMoreThanAdults(final TicketTypeRequest[] ticketTypeRequests) {
        int adults = 0;
        int infants = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {

            if (ticketTypeRequest.getNoOfTickets() > 0) {

                if (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.ADULT) {

                    adults += ticketTypeRequest.getNoOfTickets();
                }

                if (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.INFANT) {

                    infants += ticketTypeRequest.getNoOfTickets();
                }
            }
        }

        return infants > adults;
    }


    private int calculateTotalTicketsPrice(final TicketTypeRequest[] ticketTypeRequests) {

        int totalTicketsPrice = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {

            if (ticketTypeRequest.getNoOfTickets() > 0) {

                try {

                    TicketPrice ticketPrice = ticketPriceFactory.createTicketPrice(ticketTypeRequest.getTicketType());
                    totalTicketsPrice += ticketPrice.getTicketPrice() * ticketTypeRequest.getNoOfTickets();

                } catch (InvalidTicketTypeException e) {

                    throw new InvalidPurchaseException("Invalid ticket type");
                }
            }
        }

        return totalTicketsPrice;
    }

    private int calculateNumberOfSeats(final TicketTypeRequest[] ticketTypeRequests) {
        int totalNumberOfSeats = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {

            if (ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.ADULT || ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.CHILD) {

                totalNumberOfSeats += ticketTypeRequest.getNoOfTickets();
            }
        }

        return totalNumberOfSeats;
    }

}
