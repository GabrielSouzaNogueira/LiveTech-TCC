package by.gabriel.gerenciadorEstoque.Api.Headler.Equipamento;

import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;
import by.gabriel.gerenciadorEstoque.Exception.Equipamento.EquipamentoNaoEncontrado;
import by.gabriel.gerenciadorEstoque.Exception.Equipamento.ModeloNotNull;
import by.gabriel.gerenciadorEstoque.Exception.Equipamento.NumeroSerieNotNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;

@RestControllerAdvice
@Order(1)
public class EquipamentoExceptionHeader {

    @ExceptionHandler(EquipamentoNaoEncontrado.class)
    public ResponseEntity<ResponseDTO>handlerEquipamentoNaoEncontrado(EquipamentoNaoEncontrado ex){

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(
                false,
                "Equipamento não encontrado",
                "NOT_FOUND",
                Instant.now().toString()
        ));
    }

    @ExceptionHandler(ModeloNotNull.class)
    public ResponseEntity<ResponseDTO>handlerModeloNotNull(ModeloNotNull ex){

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(
                false,
                "Equipamento não pode ser NULL ou Vazio",
                "NOT_NULL",
                Instant.now().toString()
        ));
    }

    @ExceptionHandler(NumeroSerieNotNull.class)
    public ResponseEntity<ResponseDTO>handlerNumeroSerieNotNull(ModeloNotNull ex){

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(
                false,
                "Numero de serie não pode ser NULL ou Vazio",
                "NOT_NULL",
                Instant.now().toString()
        ));
    }


}
