package by.gabriel.gerenciadorEstoque.Api.Headler.Os;

import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Exception.Os.OsNaoEncontradoException;
import by.gabriel.gerenciadorEstoque.Exception.Os.OsStatusInvalido;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Order(1)
public class OsException {

    @ExceptionHandler(OsNaoEncontradoException.class)
    public ResponseEntity<ResponseDTO> handlerOsNaoEncontradoException(OsNaoEncontradoException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false, "OS não encontrado","OS_NOT_FOUND", Instant.now().toString()));

    }

    @ExceptionHandler(OsStatusInvalido.class)
    public ResponseEntity<ResponseDTO> handlerOsStatusInvalido(OsStatusInvalido ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false, "Status da OS é invalido","OS_STATUS_INVALID", Instant.now().toString()));

    }


}
