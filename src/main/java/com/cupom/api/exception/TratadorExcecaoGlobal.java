package com.cupom.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tratador global de exceções
 */
@RestControllerAdvice
public class TratadorExcecaoGlobal {

    @ExceptionHandler(CupomNaoEncontradoException.class)
    public ResponseEntity<RespostaErro> tratarCupomNaoEncontrado(CupomNaoEncontradoException ex) {
        RespostaErro erro = RespostaErro.builder()
                .dataHora(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erro("Não Encontrado")
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(CupomJaExcluidoException.class)
    public ResponseEntity<RespostaErro> tratarCupomJaExcluido(CupomJaExcluidoException ex) {
        RespostaErro erro = RespostaErro.builder()
                .dataHora(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Requisição Inválida")
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(CupomInvalidoException.class)
    public ResponseEntity<RespostaErro> tratarCupomInvalido(CupomInvalidoException ex) {
        RespostaErro erro = RespostaErro.builder()
                .dataHora(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Cupom Inválido")
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(CodigoCupomDuplicadoException.class)
    public ResponseEntity<RespostaErro> tratarCodigoDuplicado(CodigoCupomDuplicadoException ex) {
        RespostaErro erro = RespostaErro.builder()
                .dataHora(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .erro("Código Duplicado")
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((erro) -> {
            String nomeCampo = ((FieldError) erro).getField();
            String mensagemErro = erro.getDefaultMessage();
            erros.put(nomeCampo, mensagemErro);
        });

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("dataHora", LocalDateTime.now());
        resposta.put("status", HttpStatus.BAD_REQUEST.value());
        resposta.put("erro", "Validação Falhou");
        resposta.put("erros", erros);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaErro> tratarExcecaoGenerica(Exception ex) {
        RespostaErro erro = RespostaErro.builder()
                .dataHora(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .erro("Erro Interno do Servidor")
                .mensagem("Ocorreu um erro inesperado: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    @lombok.Data
    @lombok.Builder
    public static class RespostaErro {
        private LocalDateTime dataHora;
        private int status;
        private String erro;
        private String mensagem;
    }
}
