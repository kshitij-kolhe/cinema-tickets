package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.dwp.uc.pairtest.domain.TicketType.ADULT;
import static uk.gov.dwp.uc.pairtest.domain.TicketType.CHILD;
import static uk.gov.dwp.uc.pairtest.domain.TicketType.INFANT;

class TicketPurchaseTest {
    private final TicketPurchase purchase = new TicketPurchase();

    @Test
    void shouldAccumulatePriceAndSeatsWhenTicketRequestsAreMixedAndRepeated() {
        var random = ThreadLocalRandom.current();

        int adults = random.nextInt(1, 6);
        int infants = random.nextInt(1, adults + 1);
        int firstChildCount = random.nextInt(1, 6);
        int secondChildCount = random.nextInt(1, 6);
        int children = firstChildCount + secondChildCount;

        int expectedPrice = adults * 25 + children * 15;
        int expectedSeatCount = adults + children;

        purchase.add(new TicketTypeRequest(CHILD, firstChildCount));
        purchase.add(new TicketTypeRequest(INFANT, infants));
        purchase.add(new TicketTypeRequest(ADULT, adults));
        purchase.add(new TicketTypeRequest(CHILD, secondChildCount));

        assertEquals(expectedPrice, purchase.totalPrice());
        assertEquals(expectedSeatCount, purchase.totalSeats());
    }

    @Test
    void requiresAtLeastOneAdultTicket() {
        assertThrows(InvalidPurchaseException.class, purchase::requireAdultTicket);

        purchase.add(new TicketTypeRequest(ADULT, 0));
        purchase.add(new TicketTypeRequest(CHILD, 1));
        purchase.add(new TicketTypeRequest(INFANT, 1));

        assertThrows(InvalidPurchaseException.class, purchase::requireAdultTicket);
    }

    @Test
    void rejectsMalformedRequests() {
        assertThrows(InvalidPurchaseException.class, () -> purchase.add(null));
        assertThrows(InvalidPurchaseException.class, () -> purchase.add(new TicketTypeRequest(null, 1)));
        assertThrows(InvalidPurchaseException.class, () -> purchase.add(new TicketTypeRequest(ADULT, -1)));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, Integer.MAX_VALUE})
    void acceptsExactly25TicketsIncludingInfantsAndRejectsAnyMore(int extraTickets) {
        purchase.add(new TicketTypeRequest(ADULT, 24));
        purchase.add(new TicketTypeRequest(INFANT, 1));

        assertThrows(InvalidPurchaseException.class, () -> purchase.add(new TicketTypeRequest(CHILD, extraTickets)));
        assertEquals(600, purchase.totalPrice());
        assertEquals(24, purchase.totalSeats());
    }
}
