package fon.bank.accountservice.exception;

public class NotOwnerException extends RuntimeException {
    public NotOwnerException(String msg) { super(msg); }
}
