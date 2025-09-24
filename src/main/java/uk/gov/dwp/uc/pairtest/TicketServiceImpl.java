package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.service.TicketCalculationResult;
import uk.gov.dwp.uc.pairtest.service.TicketOrderProcessor;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private final TicketPaymentService paymentService;
    private final SeatReservationService seatService;
    private final TicketOrderProcessor processor;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService seatService, TicketOrderProcessor processor) {
        this.paymentService = paymentService;
        this.seatService = seatService;
        this.processor = processor;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        TicketCalculationResult result = processor.process(accountId, ticketTypeRequests);
        paymentService.makePayment(accountId, result.totalAmount());
        seatService.reserveSeat(accountId, result.totalSeats());
    }
}
