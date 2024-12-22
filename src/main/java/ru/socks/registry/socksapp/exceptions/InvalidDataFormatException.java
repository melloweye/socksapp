package ru.socks.registry.socksapp.exceptions;

// Исключения при некорректном формате данных
public class InvalidDataFormatException extends RuntimeException {
    public InvalidDataFormatException(String message) {
        super(message);
    }
}
