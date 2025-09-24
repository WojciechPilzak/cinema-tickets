package uk.gov.dwp.uc.pairtest.service;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.service.validator.TicketRequestValidator;

public class TicketOrderProcessor {
    private final TicketRequestValidator validator = new TicketRequestValidator();

    private static final int ADULT_PRICE = 25;
    private static final int CHILD_PRICE = 15;

    public TicketCalculationResult process(Long accountId, TicketTypeRequest... requests) {
        validator.validate(accountId, requests);

        int totalAmount = 0;
        int totalSeats = 0;

        for (TicketTypeRequest req : requests) {
            switch (req.type()) {
                case ADULT -> {
                    totalAmount += req.noOfTickets() * ADULT_PRICE;
                    totalSeats += req.noOfTickets();
                }
                case CHILD -> {
                    totalAmount += req.noOfTickets() * CHILD_PRICE;
                    totalSeats += req.noOfTickets();
                }
                case INFANT -> {
                    // free, no seat
                }
            }
        }

        return new TicketCalculationResult(totalAmount, totalSeats);
    }
}
