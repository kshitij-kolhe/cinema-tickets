package uk.gov.dwp.uc.pairtest.model;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidTicketTypeException;

public class TicketPriceFactoryImplTest extends TestCase {

    @Test(expected = InvalidTicketTypeException.class)
    public void testCreateTicketPrice() {

        final TicketPriceFactoryImpl ticketPriceFactory = new TicketPriceFactoryImpl();

        Assert.assertEquals("should get adult ticket class", AdultTicket.class, ticketPriceFactory.createTicketPrice(Type.ADULT).getClass());

        Assert.assertEquals("should get child ticket class", ChildTicket.class, ticketPriceFactory.createTicketPrice(Type.CHILD).getClass());

        Assert.assertEquals("should get infant ticket class", InfantTicket.class, ticketPriceFactory.createTicketPrice(Type.INFANT).getClass());
    }
}