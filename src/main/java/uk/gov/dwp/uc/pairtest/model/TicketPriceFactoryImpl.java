package uk.gov.dwp.uc.pairtest.model;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidTicketTypeException;

public class TicketPriceFactoryImpl implements TicketPriceFactory {

    @Override
    public TicketPrice createTicketPrice(Type type) throws InvalidTicketTypeException {
        switch (type) {
            case ADULT:
                return new AdultTicket();

            case CHILD:
                return new ChildTicket();

            case INFANT:
                return new InfantTicket();

            default:
                throw new InvalidTicketTypeException("Ticket type not found");
        }
    }

}
