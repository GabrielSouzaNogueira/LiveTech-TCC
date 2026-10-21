package by.gabriel.gerenciadorEstoque.Api.Headler.Servico;

import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Exception.FormaPag.FormPagNotNullException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Order(1)
public class ServiceException {

    @ExceptionHandler(FormPagNotNullException.class)
    public ResponseEntity<ResponseDTO> handlerServiceNotFound(FormPagNotNullException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false, "Forma de pagamento não pode ser null ou vazia","FORMPAG_NOT_NULL", Instant.now().toString()));

    }


}
