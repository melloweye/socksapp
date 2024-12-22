package ru.socks.registry.socksapp.exceptions;

// Исключение при ошибках обработки файлов
public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message) {
        super(message);
    }
}
