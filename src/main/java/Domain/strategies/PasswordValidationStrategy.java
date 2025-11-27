package Domain.strategies;

public class PasswordValidationStrategy implements ValidationStrategy {
    @Override
    public void validate(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una contraseña.");
        }
    }
}
