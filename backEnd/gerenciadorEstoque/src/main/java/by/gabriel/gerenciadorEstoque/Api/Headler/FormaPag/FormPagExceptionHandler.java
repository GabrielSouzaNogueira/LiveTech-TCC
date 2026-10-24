package by.gabriel.gerenciadorEstoque.Api.Headler.FormaPag;

import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Exception.FormaPag.FormPagAlreadyExistException;
import by.gabriel.gerenciadorEstoque.Exception.FormaPag.FormPagNotExistException;
import by.gabriel.gerenciadorEstoque.Exception.FormaPag.FormPagNotNullException;
import by.gabriel.gerenciadorEstoque.Exception.FormaPag.FormaPagStatusInvalid;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Order(1)
public class FormPagExceptionHandler {

    @ExceptionHandler(FormPagNotNullException.class)
    public ResponseEntity<ResponseDTO> handlerFormPagNotNull(FormPagNotNullException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false, "Forma de pagamento não pode ser null ou vazia","FORMPAG_NOT_NULL", Instant.now().toString()));

    }

    @ExceptionHandler(FormPagNotExistException.class)
    public ResponseEntity<ResponseDTO> handlerFormPagNotExist(FormPagNotExistException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false, "Forma de pagamento não foi encontrada","FORMPAG_NOT_EXIST", Instant.now().toString()));
    }

    @ExceptionHandler(FormPagAlreadyExistException.class)
    public ResponseEntity<ResponseDTO> handlerFormPagAlreadyExist(FormPagAlreadyExistException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseDTO(false,"Forma de pagamento já existente no sistema","FORMPAG_ALREADY_EXIST", Instant.now().toString()));
    }

    @ExceptionHandler(FormaPagStatusInvalid.class)
    public ResponseEntity<ResponseDTO> handlerFormaPagStatusInvalid(FormaPagStatusInvalid ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false,"Forma de pagamento com status Invalido","FORMPAG_STATUS_INVALID", Instant.now().toString()));
    }




}
