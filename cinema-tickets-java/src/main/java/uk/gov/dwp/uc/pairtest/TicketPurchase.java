package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketType;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

final class TicketPurchase {
    private static final int MAX_TICKETS = 25;

    private int ticketCount;
    private int totalPrice;
    private int totalSeats;
    private boolean hasAdult;

    void add(TicketTypeRequest request) {
        validateTicketRequest(request);

        int quantity = request.getNoOfTickets();

        validateTicketLimit(quantity);

        addTickets(request.getTicketType(), quantity);
    }

    void requireAdultTicket() {
        if (!hasAdult) {
            throw new InvalidPurchaseException();
        }
    }

    int totalPrice() {
        return totalPrice;
    }

    int totalSeats() {
        return totalSeats;
    }

    private void validateTicketLimit(int quantity) {
        if (quantity > MAX_TICKETS - ticketCount) {
            throw new InvalidPurchaseException();
        }
    }

    private void addTickets(TicketType type, int quantity) {
        ticketCount += quantity;
        totalPrice += quantity * type.getPrice();
        totalSeats += quantity * type.getSeats();

        if (type == TicketType.ADULT && quantity > 0) {
            hasAdult = true;
        }
    }

    private void validateTicketRequest(TicketTypeRequest request) {
        if (request == null || request.getTicketType() == null || request.getNoOfTickets() < 0) {
            throw new InvalidPurchaseException();
        }
    }
}
