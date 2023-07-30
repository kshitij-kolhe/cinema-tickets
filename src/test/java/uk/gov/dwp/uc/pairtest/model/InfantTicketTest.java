package uk.gov.dwp.uc.pairtest.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InfantTicketTest {

    private InfantTicket infantTicket;

    @Before
    public void setup() {
        infantTicket = new InfantTicket();
    }

    @Test
    public void testGetTicketPrice() {

        Assert.assertEquals("Infant ticket price should be 0", 0, infantTicket.getTicketPrice());
    }
}