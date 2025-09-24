package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.service.TicketCalculationResult;
import uk.gov.dwp.uc.pairtest.service.TicketOrderProcessor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {
    @Mock
    private TicketPaymentService paymentService;

    @Mock
    private SeatReservationService seatReservationService;

    @Mock
    private TicketOrderProcessor processor;

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketServiceImpl(paymentService, seatReservationService, processor);
    }

    @Test
    void shouldCallProcessorPaymentAndSeatServicesInCorrectOrder() {
        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 1);

        when(processor.process(1L, adult))
                .thenReturn(new TicketCalculationResult(25, 1));

        InOrder inOrder = inOrder(processor, paymentService, seatReservationService);

        ticketService.purchaseTickets(1L, adult);

        inOrder.verify(processor).process(1L, adult);
        inOrder.verify(paymentService).makePayment(1L, 25);
        inOrder.verify(seatReservationService).reserveSeat(1L, 1);
    }


    @Test
    void shouldNotCallExternalServicesWhenValidationFails() {

        TicketTypeRequest child = new TicketTypeRequest(CHILD, 1);

        when(processor.process(1L, child))
                .thenThrow(new InvalidPurchaseException());

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, child));

        verify(processor).process(1L, child);
        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    void shouldHandleFamilyPurchase() {

        TicketTypeRequest adults = new TicketTypeRequest(ADULT, 2);
        TicketTypeRequest children = new TicketTypeRequest(CHILD, 2);
        TicketTypeRequest infants = new TicketTypeRequest(INFANT, 1);

        when(processor.process(5L, adults, children, infants))
                .thenReturn(new TicketCalculationResult(80, 4));

        ticketService.purchaseTickets(5L, adults, children, infants);

        verify(paymentService).makePayment(5L, 80);
        verify(seatReservationService).reserveSeat(5L, 4);
    }

    @Test
    void shouldHandleInfantsWithAdults() {

        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 1);
        TicketTypeRequest infants = new TicketTypeRequest(INFANT, 3);

        when(processor.process(7L, adult, infants))
                .thenReturn(new TicketCalculationResult(25, 1));

        ticketService.purchaseTickets(7L, adult, infants);

        verify(paymentService).makePayment(7L, 25);
        verify(seatReservationService).reserveSeat(7L, 1);
    }

    @Test
    void shouldHandleMaximumTickets() {

        TicketTypeRequest adults = new TicketTypeRequest(ADULT, 15);
        TicketTypeRequest children = new TicketTypeRequest(CHILD, 10);

        when(processor.process(99L, adults, children))
                .thenReturn(new TicketCalculationResult(525, 25));

        ticketService.purchaseTickets(99L, adults, children);

        verify(paymentService).makePayment(99L, 525);
        verify(seatReservationService).reserveSeat(99L, 25);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 25, 1",
            "5, 125, 5"
    })
    void shouldHandleAdultOnlyPurchases(int quantity, int expectedAmount, int expectedSeats) {
        TicketTypeRequest adults = new TicketTypeRequest(ADULT, quantity);

        when(processor.process(1L, adults))
                .thenReturn(new TicketCalculationResult(expectedAmount, expectedSeats));

        ticketService.purchaseTickets(1L, adults);

        verify(processor).process(1L, adults);
        verify(paymentService).makePayment(1L, expectedAmount);
        verify(seatReservationService).reserveSeat(1L, expectedSeats);
    }
}