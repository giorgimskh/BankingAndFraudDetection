package exception;

public class AccountStatusMismatchException extends RuntimeException {
    public AccountStatusMismatchException(String message) {
        super(message);
    }
}
