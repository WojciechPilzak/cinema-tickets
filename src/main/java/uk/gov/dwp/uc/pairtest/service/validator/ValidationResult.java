package uk.gov.dwp.uc.pairtest.service.validator;

public record ValidationResult(boolean isValid, String message) {

    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult failure(String message) {
        return new ValidationResult(false, message);
    }
}
