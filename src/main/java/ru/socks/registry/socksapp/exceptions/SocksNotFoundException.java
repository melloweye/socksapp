package ru.socks.registry.socksapp.exceptions;

public class SocksNotFoundException extends RuntimeException {
    public SocksNotFoundException(String message) {
        super(message);
    }
}
