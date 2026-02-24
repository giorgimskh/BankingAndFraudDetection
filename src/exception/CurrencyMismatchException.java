package exception;


public class CurrencyMismatchException extends RuntimeException {
    public CurrencyMismatchException() {
        super();
    }

    public CurrencyMismatchException(String message) {
        super(message);
    }

    public CurrencyMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
