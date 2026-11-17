package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    private final TicketPaymentService paymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService seatReservationService) {
        this.paymentService = paymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        validatePurchaseInputs(accountId, ticketTypeRequests);

        PurchaseTotals totals = calculateTotals(ticketTypeRequests);

        paymentService.makePayment(accountId, totals.price());
        seatReservationService.reserveSeat(accountId, totals.seats());
    }

    private PurchaseTotals calculateTotals(TicketTypeRequest... requests) {
        TicketPurchase purchase = new TicketPurchase();

        for (TicketTypeRequest request : requests) {
            purchase.add(request);
        }

        purchase.requireAdultTicket();

        return new PurchaseTotals(purchase.totalPrice(), purchase.totalSeats());
    }

    private void validatePurchaseInputs(Long accountId, TicketTypeRequest[] requests) {
        if (accountId == null || accountId <= 0 || requests == null || requests.length == 0) {
            throw new InvalidPurchaseException();
        }
    }
}
