package uk.gov.dwp.uc.pairtest.service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

class TicketRequestValidatorTest {

    private final TicketRequestValidator validator = new TicketRequestValidator();

    //negative ways tests
    @Test
    void shouldThrowExceptionWhenNoAdultWithChildren() {
        TicketTypeRequest child = new TicketTypeRequest(CHILD, 2);
        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, new TicketTypeRequest[]{child}));
    }

    @Test
    void shouldThrowWhenNoAdultWithInfant() {
        TicketTypeRequest infant = new TicketTypeRequest(INFANT, 1);
        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, new TicketTypeRequest[]{infant}));
    }

    @Test
    void shouldThrowWhenMoreThan25Tickets() {
        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 26);
        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, new TicketTypeRequest[]{adult}));
    }

    @Test
    void shouldThrowWhenMoreThan25TicketsForTwoDifferentTicketTypes() {
        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 23);
        TicketTypeRequest child = new TicketTypeRequest(CHILD, 12);
        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, new TicketTypeRequest[]{adult, child}));
    }

    @Test
    void shouldThrowWhenAccountIdIsZero() {
        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 1);
        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(0L, new TicketTypeRequest[]{adult}));
    }

    @Test
    void shouldThrowWhenAccountIdIsNull() {
        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 1);
        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(null, new TicketTypeRequest[]{adult}));
    }

    static Stream<TicketTypeRequest> negativeRequests() {
        return Stream.of(
                new TicketTypeRequest(ADULT, -1),
                new TicketTypeRequest(CHILD, -2),
                new TicketTypeRequest(INFANT, -3)
        );
    }

    @ParameterizedTest
    @MethodSource("negativeRequests")
    void shouldThrowWhenNegativeTicketCount(TicketTypeRequest bad) {
        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, new TicketTypeRequest[]{bad}));
    }

    @Test
    void shouldThrowWhenAnyEntryNegativeEvenIfOthersValid() {
        TicketTypeRequest adultOk = new TicketTypeRequest(ADULT, 2);
        TicketTypeRequest childBad = new TicketTypeRequest(CHILD, -1);
        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, new TicketTypeRequest[]{adultOk, childBad}));
    }

    //positive ways tests
    //We don't need too many positive path tests
    //because the validator logic is driven by throwing exceptions.
    @Test
    void shouldPassForValidCombination() {
        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 1);
        TicketTypeRequest child = new TicketTypeRequest(CHILD, 2);
        assertDoesNotThrow(() -> validator.validate(1L, new TicketTypeRequest[]{adult, child}));
    }

    @Test
    void shouldPassForOnlyAdults() {
        TicketTypeRequest adults = new TicketTypeRequest(ADULT, 3);
        assertDoesNotThrow(() -> validator.validate(1L, new TicketTypeRequest[]{adults}));
    }

    @Test
    void shouldPassForAdultWithInfant() {
        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 1);
        TicketTypeRequest infant = new TicketTypeRequest(INFANT, 1);
        assertDoesNotThrow(() -> validator.validate(1L, new TicketTypeRequest[]{adult, infant}));
    }

    @Test
    void shouldPassForAdultWithChildrenAndInfant() {
        TicketTypeRequest adult = new TicketTypeRequest(ADULT, 2);
        TicketTypeRequest child = new TicketTypeRequest(CHILD, 1);
        TicketTypeRequest infant = new TicketTypeRequest(INFANT, 1);
        assertDoesNotThrow(() -> validator.validate(1L, new TicketTypeRequest[]{adult, child, infant}));
    }
}