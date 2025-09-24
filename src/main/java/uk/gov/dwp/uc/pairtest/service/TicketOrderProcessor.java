package uk.gov.dwp.uc.pairtest.service;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.service.validator.TicketRequestValidator;

public class TicketOrderProcessor {
    private final TicketRequestValidator validator;
    private final PriceCalculator paymentService;

    public TicketOrderProcessor(TicketRequestValidator validator, PriceCalculator paymentService) {
        this.validator = validator;
        this.paymentService = paymentService;
    }

    public TicketCalculationResult process(Long accountId, TicketTypeRequest... requests) {
        validator.validate(accountId, requests);

        int totalAmount = 0;
        int totalSeats = 0;

        for (TicketTypeRequest req : requests) {
            switch (req.type()) {
                case ADULT -> {
                    totalAmount += paymentService.calculate(req.noOfTickets(), TicketTypeRequest.Type.ADULT);
                    totalSeats += req.noOfTickets();
                }
                case CHILD -> {
                    totalAmount += paymentService.calculate(req.noOfTickets(), TicketTypeRequest.Type.CHILD);
                    totalSeats += req.noOfTickets();
                }
                // INFANT - intentionally omitted
            }
        }

        return new TicketCalculationResult(totalAmount, totalSeats);
    }
}
