package com.cupom.api.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes para as exceções customizadas e TratadorExcecaoGlobal
 */
class ExcecoesTest {

    @Test
    @DisplayName("CupomNaoEncontradoException - Deve criar exceção com mensagem")
    void deveCriarCupomNaoEncontradoException() {
        String mensagem = "Cupom não encontrado";
        CupomNaoEncontradoException excecao = new CupomNaoEncontradoException(mensagem);

        assertThat(excecao).isInstanceOf(RuntimeException.class);
        assertThat(excecao.getMessage()).isEqualTo(mensagem);
    }

    @Test
    @DisplayName("CupomJaExcluidoException - Deve criar exceção com mensagem")
    void deveCriarCupomJaExcluidoException() {
        String mensagem = "Cupom já foi deletado";
        CupomJaExcluidoException excecao = new CupomJaExcluidoException(mensagem);

        assertThat(excecao).isInstanceOf(RuntimeException.class);
        assertThat(excecao.getMessage()).isEqualTo(mensagem);
    }

    @Test
    @DisplayName("CupomInvalidoException - Deve criar exceção com mensagem")
    void deveCriarCupomInvalidoException() {
        String mensagem = "Cupom inválido";
        CupomInvalidoException excecao = new CupomInvalidoException(mensagem);

        assertThat(excecao).isInstanceOf(RuntimeException.class);
        assertThat(excecao.getMessage()).isEqualTo(mensagem);
    }

    @Test
    @DisplayName("CodigoCupomDuplicadoException - Deve criar exceção com mensagem")
    void deveCriarCodigoCupomDuplicadoException() {
        String mensagem = "Código duplicado";
        CodigoCupomDuplicadoException excecao = new CodigoCupomDuplicadoException(mensagem);

        assertThat(excecao).isInstanceOf(RuntimeException.class);
        assertThat(excecao.getMessage()).isEqualTo(mensagem);
    }

    @Test
    @DisplayName("TratadorExcecaoGlobal - Deve tratar CupomNaoEncontradoException")
    void deveTratarCupomNaoEncontradoException() {
        TratadorExcecaoGlobal tratador = new TratadorExcecaoGlobal();
        CupomNaoEncontradoException excecao = new CupomNaoEncontradoException("Cupom não encontrado");

        var resposta = tratador.tratarCupomNaoEncontrado(excecao);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getStatus()).isEqualTo(404);
        assertThat(resposta.getBody().getErro()).isEqualTo("Não Encontrado");
        assertThat(resposta.getBody().getMensagem()).isEqualTo("Cupom não encontrado");
    }

    @Test
    @DisplayName("TratadorExcecaoGlobal - Deve tratar CupomJaExcluidoException")
    void deveTratarCupomJaExcluidoException() {
        TratadorExcecaoGlobal tratador = new TratadorExcecaoGlobal();
        CupomJaExcluidoException excecao = new CupomJaExcluidoException("Já deletado");

        var resposta = tratador.tratarCupomJaExcluido(excecao);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getStatus()).isEqualTo(400);
        assertThat(resposta.getBody().getErro()).isEqualTo("Requisição Inválida");
        assertThat(resposta.getBody().getMensagem()).isEqualTo("Já deletado");
    }

    @Test
    @DisplayName("TratadorExcecaoGlobal - Deve tratar CupomInvalidoException")
    void deveTratarCupomInvalidoException() {
        TratadorExcecaoGlobal tratador = new TratadorExcecaoGlobal();
        CupomInvalidoException excecao = new CupomInvalidoException("Cupom inválido");

        var resposta = tratador.tratarCupomInvalido(excecao);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getStatus()).isEqualTo(400);
        assertThat(resposta.getBody().getErro()).isEqualTo("Cupom Inválido");
        assertThat(resposta.getBody().getMensagem()).isEqualTo("Cupom inválido");
    }

    @Test
    @DisplayName("TratadorExcecaoGlobal - Deve tratar CodigoCupomDuplicadoException")
    void deveTratarCodigoCupomDuplicadoException() {
        TratadorExcecaoGlobal tratador = new TratadorExcecaoGlobal();
        CodigoCupomDuplicadoException excecao = new CodigoCupomDuplicadoException("Código duplicado");

        var resposta = tratador.tratarCodigoDuplicado(excecao);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getStatus()).isEqualTo(409);
        assertThat(resposta.getBody().getErro()).isEqualTo("Código Duplicado");
        assertThat(resposta.getBody().getMensagem()).isEqualTo("Código duplicado");
    }

    @Test
    @DisplayName("TratadorExcecaoGlobal - Deve tratar exceções genéricas")
    void deveTratarExcecaoGenerica() {
        TratadorExcecaoGlobal tratador = new TratadorExcecaoGlobal();
        Exception excecao = new Exception("Erro inesperado");

        var resposta = tratador.tratarExcecaoGenerica(excecao);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resposta.getBody()).isNotNull();
        assertThat(resposta.getBody().getStatus()).isEqualTo(500);
        assertThat(resposta.getBody().getErro()).isEqualTo("Erro Interno do Servidor");
        assertThat(resposta.getBody().getMensagem()).contains("Ocorreu um erro inesperado");
    }
}
