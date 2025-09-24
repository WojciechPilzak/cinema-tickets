package uk.gov.dwp.uc.pairtest.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.service.validator.TicketRequestValidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

class TicketOrderProcessorTest {

    private final TicketRequestValidator validator = new TicketRequestValidator(25);
    private final PriceCalculator paymentService = new PriceCalculator();
    private final TicketOrderProcessor processor = new TicketOrderProcessor(validator, paymentService);

    @Test
    void shouldCalculateOnlyAdult() {
        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 2);

        TicketCalculationResult result = processor.process(1L, adult);

        assertEquals(50, result.totalAmount());
        assertEquals(2, result.totalSeats());
    }

    @Test
    void shouldHandleMaximumTickets() {
        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 25);

        TicketCalculationResult result = processor.process(1L, adult);

        assertEquals(625, result.totalAmount());
        assertEquals(25, result.totalSeats());
    }

    @Test
    void shouldCalculateOnlyInfantsWithAdult() {
        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 1);
        TicketTypeRequest infants = new TicketTypeRequest(INFANT, 5);

        TicketCalculationResult result = processor.process(1L,
                adult, infants);

        assertEquals(25, result.totalAmount());
        assertEquals(1, result.totalSeats());
    }

    @ParameterizedTest
    @CsvSource({
            "1, 0, 0, 25, 1",
            "2, 1, 1, 65, 3",
            "1, 1, 2, 40, 2"
    })
    void shouldCalculateCorrectly(int adults, int children, int infants,
                                  int expectedAmount, int expectedSeats) {
        TicketTypeRequest[] requests = {
                new TicketTypeRequest(ADULT, adults),
                new TicketTypeRequest(CHILD, children),
                new TicketTypeRequest(INFANT, infants)
        };

        TicketCalculationResult result = processor.process(1L, requests);

        assertEquals(expectedAmount, result.totalAmount());
        assertEquals(expectedSeats, result.totalSeats());
    }

    @Test
    void shouldAllowExactly25Tickets() {
        TicketTypeRequest adults = new TicketTypeRequest(ADULT, 20);
        TicketTypeRequest children = new TicketTypeRequest(CHILD, 5);

        assertDoesNotThrow(() -> processor.process(1L, adults, children));
    }

    @Test
    void shouldAllowExactly25TicketsWithMixedTypes() {
        TicketTypeRequest adults = new TicketTypeRequest(ADULT, 10);
        TicketTypeRequest children = new TicketTypeRequest(CHILD, 10);
        TicketTypeRequest infants = new TicketTypeRequest(INFANT, 5);
        TicketCalculationResult result = processor.process(1L,
                adults, children, infants);

        assertEquals(400, result.totalAmount());
        assertEquals(20, result.totalSeats());
    }
}