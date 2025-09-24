package uk.gov.dwp.uc.pairtest.service.validator;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketRequestValidator {

    public void validate(Long accountId, TicketTypeRequest[] requests) {
        ValidationResult result = isValid(accountId, requests);
        if (!result.isValid()) {
            throw new InvalidPurchaseException();
        }
    }

    public ValidationResult isValid(Long accountId, TicketTypeRequest[] requests) {
        if (accountId == null || accountId <= 0) {
            return new ValidationResult(false, "Invalid account id");
        }
        if (requests == null || requests.length == 0) {
            return new ValidationResult(false, "No ticket requests found");
        }

        int totalTickets = 0;
        int adultTickets = 0;
        int childTickets = 0;
        int infantTickets = 0;

        for (TicketTypeRequest request : requests) {
            if (request.noOfTickets() < 0) {
                return new ValidationResult(false, "Tickets no should be greater than zero");
            }
            totalTickets += request.noOfTickets();
            if (totalTickets > 25) {
                return new ValidationResult(false, "Too many tickets found");
            }
            switch (request.type()) {
                case ADULT -> adultTickets += request.noOfTickets();
                case CHILD -> childTickets += request.noOfTickets();
                case INFANT -> infantTickets += request.noOfTickets();
            }
        }

        if (totalTickets == 0) {
            return new ValidationResult(false, "No tickets found");
        }

        if ((childTickets > 0 || infantTickets > 0) && adultTickets == 0) {
            return new ValidationResult(false, "No adult tickets found");
        }

        return new ValidationResult(true, "");
    }
}
