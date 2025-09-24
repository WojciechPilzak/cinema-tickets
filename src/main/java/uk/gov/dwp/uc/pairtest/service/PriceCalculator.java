package uk.gov.dwp.uc.pairtest.service;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class PriceCalculator {

    public int calculate(int noOfTickets, TicketTypeRequest.Type ticketType) {
        if (ticketType == null) {
            throw new InvalidPurchaseException("ticketType must not be null");
        }
        if (noOfTickets < 0) {
            throw new InvalidPurchaseException("Number of tickets must not be negative");
        }
        if (noOfTickets == 0) {
            return 0;
        }
        switch (ticketType) {
            case ADULT -> {
                return noOfTickets* TicketTypeRequest.Type.ADULT.getPrice();
            }
            case CHILD -> {
                return noOfTickets* TicketTypeRequest.Type.CHILD.getPrice();
            }
            case INFANT ->  {
                return 0;
            }
            default -> throw new InvalidPurchaseException("Unsupported ticket type: " + ticketType);
        }
    }
}
