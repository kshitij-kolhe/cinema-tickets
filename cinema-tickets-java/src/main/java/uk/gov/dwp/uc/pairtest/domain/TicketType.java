package uk.gov.dwp.uc.pairtest.domain;

public enum TicketType {
    ADULT(25, 1),
    CHILD(15, 1),
    INFANT(0, 0);

    private final int price;
    private final int seats;

    TicketType(int price, int seats) {
        this.price = price;
        this.seats = seats;
    }

    public int getPrice() {
        return price;
    }

    public int getSeats() {
        return seats;
    }
}
