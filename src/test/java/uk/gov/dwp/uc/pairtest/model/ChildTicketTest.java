package uk.gov.dwp.uc.pairtest.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ChildTicketTest {

    private ChildTicket childTicket;

    @Before
    public void setup() {
        childTicket = new ChildTicket();
    }

    @Test
    public void testGetTicketPrice() {

        Assert.assertEquals("Child ticket price should be 10", 10, childTicket.getTicketPrice());
    }
}