package uk.gov.dwp.uc.pairtest.service.validator;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketRequestValidator {

    public void validate(Long accountId, TicketTypeRequest[] requests) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException();
        }
        if (requests == null || requests.length == 0) {
            throw new InvalidPurchaseException();
        }

        int totalTickets = 0;
        int adultTickets = 0;
        int childTickets = 0;
        int infantTickets = 0;

        for (TicketTypeRequest request : requests) {
            if (request.noOfTickets() < 0) {
                throw new InvalidPurchaseException();
            }
            totalTickets += request.noOfTickets();
            if (totalTickets > 25) {
                throw new InvalidPurchaseException();
            }
            switch (request.type()) {
                case ADULT -> adultTickets += request.noOfTickets();
                case CHILD -> childTickets += request.noOfTickets();
                case INFANT -> infantTickets += request.noOfTickets();
            }
        }

        if (totalTickets == 0) {
            throw new InvalidPurchaseException();
        }

        if ((childTickets > 0 || infantTickets > 0) && adultTickets==0) {
            throw new InvalidPurchaseException();
        }
    }
}
