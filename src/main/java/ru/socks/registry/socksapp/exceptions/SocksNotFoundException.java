package ru.socks.registry.socksapp.exceptions;

// Исключение при нехватке носков на складе
public class SocksNotFoundException extends RuntimeException {
    public SocksNotFoundException(String message) {
        super(message);
    }
}
