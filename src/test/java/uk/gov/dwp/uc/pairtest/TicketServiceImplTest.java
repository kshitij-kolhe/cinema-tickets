package uk.gov.dwp.uc.pairtest;


import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.model.AdultTicket;
import uk.gov.dwp.uc.pairtest.model.ChildTicket;
import uk.gov.dwp.uc.pairtest.model.InfantTicket;
import uk.gov.dwp.uc.pairtest.model.TicketPriceFactoryImpl;

public class TicketServiceImplTest {

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Mock
    private TicketPaymentServiceImpl ticketPaymentService;
    @Mock
    private SeatReservationServiceImpl seatReservationService;
    @Mock
    private TicketPriceFactoryImpl ticketPriceFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(ticketPriceFactory.createTicketPrice(Type.ADULT)).thenReturn(new AdultTicket());
        Mockito.when(ticketPriceFactory.createTicketPrice(Type.CHILD)).thenReturn(new ChildTicket());
        Mockito.when(ticketPriceFactory.createTicketPrice(Type.INFANT)).thenReturn(new InfantTicket());
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionWhenNoAdultIsPresentForInfants() {

        ticketService.purchaseTickets(12L, new TicketTypeRequest(Type.INFANT, 2));
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionWhenAccountIdIsInvalid() {

        ticketService.purchaseTickets(-10L, new TicketTypeRequest(Type.ADULT, 2));
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionWhenNoAdultIsPresentForChildren() {

        ticketService.purchaseTickets(12L, new TicketTypeRequest(Type.CHILD, 2));
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionWhenNumberOfInfantsIsMoreThanAdults() {

        ticketService.purchaseTickets(12L, new TicketTypeRequest(Type.INFANT, 13),
                new TicketTypeRequest(Type.INFANT, 5),
                new TicketTypeRequest(Type.ADULT, 5));
    }

    @Test(expected = InvalidPurchaseException.class)
    public void shouldThrowExceptionWhenTicketsGreaterThan20() {
        ticketService.purchaseTickets(12L, new TicketTypeRequest(Type.ADULT, 25));
    }

    @Test
    public void shouldCallPaymentService() {
        int expectedAmount = 200;
        ticketService.purchaseTickets(12L, new TicketTypeRequest(Type.ADULT, 8),
                new TicketTypeRequest(Type.CHILD, 4),
                new TicketTypeRequest(Type.INFANT, 5));

        Mockito.verify(ticketPaymentService).makePayment(12L, expectedAmount);
    }

    @Test
    public void shouldCallSeatReservationService() {
        int expectedSeats = 12;
        ticketService.purchaseTickets(12L, new TicketTypeRequest(Type.ADULT, 8),
                new TicketTypeRequest(Type.CHILD, 4),
                new TicketTypeRequest(Type.INFANT, 5));

        Mockito.verify(seatReservationService).reserveSeat(12L, expectedSeats);
    }
}