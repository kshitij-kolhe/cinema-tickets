package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static uk.gov.dwp.uc.pairtest.domain.TicketType.*;

class TicketServiceImplTest {
    private final TicketPaymentService payments = mock(TicketPaymentService.class);
    private final SeatReservationService seats = mock(SeatReservationService.class);
    private final TicketServiceImpl service = new TicketServiceImpl(payments, seats);

    @Test
    void shouldChargeAndReserveSeatsForAdultsAndChildrenWhenBookingIncludesInfants() {
        var random = ThreadLocalRandom.current();

        int adults = random.nextInt(1, 6);
        int children = random.nextInt(1, 6);
        int infants = random.nextInt(1, adults + 1);

        int expectedPayment = adults * 25 + children * 15;
        int expectedSeatCount = adults + children;

        service.purchaseTickets(42L,
                new TicketTypeRequest(ADULT, adults),
                new TicketTypeRequest(CHILD, children),
                new TicketTypeRequest(INFANT, infants));

        verify(payments).makePayment(42L, expectedPayment);
        verify(seats).reserveSeat(42L, expectedSeatCount);
        verifyNoMoreInteractions(payments, seats);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void shouldRejectPurchaseWhenAccountIdIsInvalid(Long accountId) {
        assertPurchaseRejected(accountId, new TicketTypeRequest(ADULT, 1));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldRejectPurchaseWhenNoTicketRequestsAreProvided(TicketTypeRequest[] requests) {
        assertPurchaseRejected(42L, requests);
    }

    @Test
    void shouldRejectEntirePurchaseWhenALaterRequestExceedsTheTicketLimit() {
        assertPurchaseRejected(42L, new TicketTypeRequest(ADULT, 25), new TicketTypeRequest(INFANT, 1));
    }

    @Test
    void shouldRejectPurchaseWhenThereIsNoAdultTicket() {
        assertPurchaseRejected(42L, new TicketTypeRequest(CHILD, 1));
    }

    private void assertPurchaseRejected(Long accountId, TicketTypeRequest... requests) {
        assertThrows(InvalidPurchaseException.class, () -> service.purchaseTickets(accountId, requests));
        verifyNoInteractions(payments, seats);
    }
}
