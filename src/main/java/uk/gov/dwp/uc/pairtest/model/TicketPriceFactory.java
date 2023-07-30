package uk.gov.dwp.uc.pairtest.model;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;

public interface TicketPriceFactory {
    TicketPrice createTicketPrice(final Type type);
}
