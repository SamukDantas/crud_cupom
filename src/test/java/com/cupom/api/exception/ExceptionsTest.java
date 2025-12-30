package com.cupom.api.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes para as exceções customizadas e GlobalExceptionHandler.
 */
class ExceptionsTest {

    @Test
    @DisplayName("CupomNotFoundException - Deve criar exceção com mensagem")
    void testCupomNotFoundException() {
        String message = "Cupom não encontrado";
        CupomNotFoundException exception = new CupomNotFoundException(message);

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("CupomAlreadyDeletedException - Deve criar exceção com mensagem")
    void testCupomAlreadyDeletedException() {
        String message = "Cupom já foi deletado";
        CupomAlreadyDeletedException exception = new CupomAlreadyDeletedException(message);

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("InvalidCupomException - Deve criar exceção com mensagem")
    void testInvalidCupomException() {
        String message = "Cupom inválido";
        InvalidCupomException exception = new InvalidCupomException(message);

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("DuplicateCupomCodeException - Deve criar exceção com mensagem")
    void testDuplicateCupomCodeException() {
        String message = "Código duplicado";
        DuplicateCupomCodeException exception = new DuplicateCupomCodeException(message);

        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("GlobalExceptionHandler - Deve tratar CupomNotFoundException")
    void testGlobalExceptionHandlerCupomNotFound() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        CupomNotFoundException exception = new CupomNotFoundException("Cupom não encontrado");

        var response = handler.handleCupomNotFound(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("Cupom não encontrado");
    }

    @Test
    @DisplayName("GlobalExceptionHandler - Deve tratar CupomAlreadyDeletedException")
    void testGlobalExceptionHandlerCupomAlreadyDeleted() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        CupomAlreadyDeletedException exception = new CupomAlreadyDeletedException("Já deletado");

        var response = handler.handleCupomAlreadyDeleted(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).isEqualTo("Já deletado");
    }

    @Test
    @DisplayName("GlobalExceptionHandler - Deve tratar InvalidCupomException")
    void testGlobalExceptionHandlerInvalidCupom() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        InvalidCupomException exception = new InvalidCupomException("Cupom inválido");

        var response = handler.handleInvalidCupom(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Invalid Cupom");
        assertThat(response.getBody().getMessage()).isEqualTo("Cupom inválido");
    }

    @Test
    @DisplayName("GlobalExceptionHandler - Deve tratar DuplicateCupomCodeException")
    void testGlobalExceptionHandlerDuplicateCode() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        DuplicateCupomCodeException exception = new DuplicateCupomCodeException("Código duplicado");

        var response = handler.handleDuplicateCode(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getError()).isEqualTo("Duplicate Code");
        assertThat(response.getBody().getMessage()).isEqualTo("Código duplicado");
    }

    @Test
    @DisplayName("GlobalExceptionHandler - Deve tratar exceções genéricas")
    void testGlobalExceptionHandlerGenericException() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Exception exception = new Exception("Erro inesperado");

        var response = handler.handleGeneric(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).contains("Ocorreu um erro inesperado");
    }
}
