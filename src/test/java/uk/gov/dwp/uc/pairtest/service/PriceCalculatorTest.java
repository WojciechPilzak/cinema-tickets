package uk.gov.dwp.uc.pairtest.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;

class PriceCalculatorTest {


    private final PriceCalculator calculator = new PriceCalculator();

    @ParameterizedTest
    @CsvSource({
            "1, ADULT, 25",
            "2, ADULT, 50",
            "1, CHILD, 15",
            "3, CHILD, 45",
            "1, INFANT, 0",
            "10, INFANT, 0"
    })
    void shouldCalculateCorrectPriceForDifferentQuantitiesAndTypes(int quantity, TicketTypeRequest.Type type, int expectedPrice) {
        int result = calculator.calculate(quantity, type);
        assertEquals(expectedPrice, result);
    }

    @ParameterizedTest
    @CsvSource({
            "ADULT",
            "CHILD",
            "INFANT"
    })
    void shouldReturnZeroForZeroQuantity(TicketTypeRequest.Type type) {
        assertEquals(0, calculator.calculate(0, type));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -100})
    void shouldThrowExceptionForNegativeQuantities(int invalidQuantity) {
        InvalidPurchaseException exception = assertThrows(
                InvalidPurchaseException.class,
                () -> calculator.calculate(invalidQuantity, ADULT)
        );
        assertEquals("Number of tickets must not be negative", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 0, -1})
    void shouldThrowExceptionWhenTicketTypeIsNull(int quantity) {
        InvalidPurchaseException exception = assertThrows(
                InvalidPurchaseException.class,
                () -> calculator.calculate(quantity, null)
        );
        assertEquals("ticketType must not be null", exception.getMessage());
    }

    @Test
    void shouldCalculatePriceAsQuantityTimesUnitPrice() {
        int quantity = 7;
        int unitPriceAdult = ADULT.getPrice();
        int calculatedPrice = calculator.calculate(quantity, ADULT);
        assertEquals(quantity * unitPriceAdult, calculatedPrice);
    }
}