package by.gabriel.gerenciadorEstoque.Api.Headler.User;

import java.time.Instant;

import by.gabriel.gerenciadorEstoque.Exception.Usuario.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import by.gabriel.gerenciadorEstoque.Api.DTO.Response.ResponseDTO;

@RestControllerAdvice
@Order(1)
public class UserExceptionHandler {

    //EXCEÇÃO USUARIO JÁ EXISTE
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ResponseDTO> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ResponseDTO(false, ex.getMessage(), "USER_ALREADY_EXISTS", Instant.now().toString()));
    }

    //EXCEÇÃO SENHA INVALIDA
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ResponseDTO> handleInvalidPassword(InvalidPasswordException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401 é mais semântico
        .body(new ResponseDTO(false, ex.getMessage(), "INVALID_PASSWORD", Instant.now().toString()));
    }

    //EXCEÇÃO USUARIO INATIVO
    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<ResponseDTO> handleUserInactive(UserInactiveException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ResponseDTO(false, ex.getMessage(), "USER_INACTIVE", Instant.now().toString()));
    }

    //EXCEÇÃO USUARIO NÃO ENCONTRADO
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleUserNotFound(UserNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ResponseDTO(false, ex.getMessage(), "USER_NOT_FOUND", Instant.now().toString()));
    }

    //EXCEÇÃO NOME NULO
   @ExceptionHandler(UserNameNotNullException.class)
    public ResponseEntity<ResponseDTO> handleUserNameNotNull(UserNameNotNullException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ResponseDTO(false, ex.getMessage(), "NAME_REQUIRED", Instant.now().toString()));
    }

    //EXCEÇÃO SENHA NULA
    @ExceptionHandler(UserPasswordNotNullException.class)
    public ResponseEntity<ResponseDTO> handleUserPasswordNotNull(UserPasswordNotNullException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ResponseDTO(false, ex.getMessage(), "PASSWORD_REQUIRED", Instant.now().toString()));
    }

    //EXCEÇÃO EMAIL JÁ EXISTE
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ResponseDTO> handleEmailAlreadyExist(EmailAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseDTO(false, ex.getMessage(), "EMAIL_ALREADY_EXISTS", Instant.now().toString()));
    }

    @ExceptionHandler(UserNotPermission.class)
    public ResponseEntity<ResponseDTO> handleUserNotPermission(UserNotPermission ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(false, ex.getMessage(),"USER_NOT_PERMISSION",Instant.now().toString()));
    }

    @ExceptionHandler(NoUsersFoundInList.class)
    public ResponseEntity<ResponseDTO> handleUserNotPermission(NoUsersFoundInList ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(false, ex.getMessage(),"USERS_NOT_FOUND",Instant.now().toString()));
    }

    @ExceptionHandler(UserLogadoNotNull.class)
    public ResponseEntity<ResponseDTO> UserLogadoNotNull(UserLogadoNotNull ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(false, "Header do usuario logado não foi enviado","USERLOGADO_NOT_NULL",Instant.now().toString()));
    }

    @ExceptionHandler(TelefoneNaoEncontrado.class)
    public ResponseEntity<ResponseDTO> TelefoneNaoEncontrado(TelefoneNaoEncontrado ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(false, "Telefone não foi encontrado","TELEFONE_NAO_ENCONTRADO",Instant.now().toString()));
    }

    @ExceptionHandler(TelefoneExistente.class)
    public ResponseEntity<ResponseDTO> TelefoneExistente(TelefoneExistente ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(false, "Telefone ja existente","TELEFONE_JA_EXISTENTE",Instant.now().toString()));
    }


}

