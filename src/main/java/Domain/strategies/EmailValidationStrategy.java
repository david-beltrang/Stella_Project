package Domain.strategies;

public class EmailValidationStrategy implements ValidationStrategy {
    @Override
    public void validate(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar un correo.");
        }
        // Basic regex for email format could be added here if needed,
        // but for now we stick to the existing logic's level of validation (not empty).
        // If strict regex is required by domain rules, it should be added here.
    }
}
