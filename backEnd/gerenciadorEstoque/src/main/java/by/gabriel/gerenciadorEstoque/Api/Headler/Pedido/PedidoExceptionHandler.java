package by.gabriel.gerenciadorEstoque.Api.Headler.Pedido;

import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Exception.Pedido.QuantidadeMaiorEstoqueAtual;
import by.gabriel.gerenciadorEstoque.Exception.Pedido.QuantidadeMenorIgualZero;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
@Order(1)
public class PedidoExceptionHandler {

    @ExceptionHandler(QuantidadeMenorIgualZero.class)
    public ResponseEntity<ResponseDTO> handlerQuantidadeMenorIgualZero(QuantidadeMenorIgualZero ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false,"Quantidade da Venda é menor ou Igual a Zero", "QTD_VENDA_NULL_ZERO", Instant.now().toString()));
    }

    @ExceptionHandler(QuantidadeMaiorEstoqueAtual.class)
    public ResponseEntity<ResponseDTO> handlerQuantidadeMaiorEstoqueAtual(QuantidadeMaiorEstoqueAtual ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false,"Quantidade da Venda é maior que o estoque atual", "QTD_VENDA_MAIOR_ESTOQUE", Instant.now().toString()));
    }
}
