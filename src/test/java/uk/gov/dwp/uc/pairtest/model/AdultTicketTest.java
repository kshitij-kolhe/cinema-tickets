package uk.gov.dwp.uc.pairtest.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AdultTicketTest {

    private AdultTicket adultTicket;

    @Before
    public void setup() {
        adultTicket = new AdultTicket();
    }

    @Test
    public void testGetTicketPrice() {

        Assert.assertEquals("Adult ticket price should be 20", 20, adultTicket.getTicketPrice());
    }
}