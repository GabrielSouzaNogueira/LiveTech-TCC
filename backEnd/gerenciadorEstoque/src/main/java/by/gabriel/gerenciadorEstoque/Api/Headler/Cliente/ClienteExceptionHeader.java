package by.gabriel.gerenciadorEstoque.Api.Headler.Cliente;

import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Exception.Cliente.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Order(1)
public class ClienteExceptionHeader {

    @ExceptionHandler(NomeClienteNotNull.class)
    public ResponseEntity<ResponseDTO> handlerNomeClienteNotNull(NomeClienteNotNull ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false, "Nome do cliente não pode ser null ou vazia","NOME_CLIENTE_NOT_NULL", Instant.now().toString()));

    }

    @ExceptionHandler(EmailClienteJaExiste.class)
    public ResponseEntity<ResponseDTO> headerClienteJaExiste(EmailClienteJaExiste ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false, "Email já existente em outro cadastro", "EMAIL_CLIENTE_JA_EXISTE", Instant.now().toString()));

    }

    @ExceptionHandler(ClienteNaoEncontrado.class)
    public ResponseEntity<ResponseDTO> headerClienteNaoEncontrado(ClienteNaoEncontrado ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false, "Cliente não foi encontrado", "CLIENTE_NAO_ENCONTRADO", Instant.now().toString()));

    }

    @ExceptionHandler(TelefoneNaoEncontrado.class)
    public ResponseEntity<ResponseDTO> headerTelefoneNãoEncontrado(TelefoneNaoEncontrado ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false, "Tefone não encontrado", "TELEFONE_NAO_ENCONTRADO", Instant.now().toString()));

    }

    @ExceptionHandler(TelefoneExistente.class)
    public ResponseEntity<ResponseDTO> headerTelefoneExistente(TelefoneExistente ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false, "Tefone já existente para este usuário", "TELEFONE_JA_EXISTENTE", Instant.now().toString()));

    }


}
