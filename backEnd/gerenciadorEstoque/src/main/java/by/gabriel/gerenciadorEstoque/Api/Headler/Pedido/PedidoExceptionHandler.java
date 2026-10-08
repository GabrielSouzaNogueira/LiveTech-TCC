package by.gabriel.gerenciadorEstoque.Api.Headler.Pedido;

import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Exception.Pedido.*;
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
                .body(new ResponseDTO(false, ex.getMessage(), "QTD_VENDA_NULL_ZERO", Instant.now().toString()));
    }

    @ExceptionHandler(QuantidadeMaiorEstoqueAtual.class)
    public ResponseEntity<ResponseDTO> handlerQuantidadeMaiorEstoqueAtual(QuantidadeMaiorEstoqueAtual ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false,ex.getMessage(), "QTD_VENDA_MAIOR_ESTOQUE", Instant.now().toString()));
    }

    @ExceptionHandler(PedidoNaoEncontrado.class)
    public ResponseEntity<ResponseDTO> handlerPedidoNaoEncontrado(PedidoNaoEncontrado ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false,ex.getMessage(), "PEDIDO_NAO_ENCONTRADO", Instant.now().toString()));
    }

    @ExceptionHandler(PedidoComStatusInvalido.class)
    public ResponseEntity<ResponseDTO> handlerPedidoComStatusInvalido(PedidoComStatusInvalido ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false,ex.getMessage(), "PEDIDO_STATUS_INVALIDO", Instant.now().toString()));
    }

    @ExceptionHandler(TotalPagoMenorQueValorDaVenda.class)
    public ResponseEntity<ResponseDTO> handlerTotalPagoMenorQueValorDaVenda(TotalPagoMenorQueValorDaVenda ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO(false,ex.getMessage(), "TOTAL_MENOR_QUE_VALORVENDA", Instant.now().toString()));
    }




}
