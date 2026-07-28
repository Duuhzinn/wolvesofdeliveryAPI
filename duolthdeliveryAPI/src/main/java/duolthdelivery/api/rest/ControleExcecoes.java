package duolthdelivery.api.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControleExcecoes extends ResponseEntityExceptionHandler {
	

    // USUÁRIO NÃO ENCONTRADO
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ObjetoErro> handleRuntimeException(RuntimeException e) {
    	ObjetoErro erro = new ObjetoErro(
            HttpStatus.NOT_FOUND.value(),
            "Usuário nao encontrado",
            e.getMessage()
        );
        return new ResponseEntity<>(erro, HttpStatus.NOT_FOUND);
    }



    // LOGIN DUPLICADO (CONSTRAINT DO BANCO)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ObjetoErro> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    	ObjetoErro erro = new ObjetoErro(
            HttpStatus.CONFLICT.value(),
            "Dados duplicados",
            "Login ou email já cadastrado no sistema"
        );
        return new ResponseEntity<>(erro, HttpStatus.CONFLICT);
    }

    // ERRO GENÉRICO DO SERVIDOR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ObjetoErro> handleException(Exception e) {
    	ObjetoErro erro = new ObjetoErro(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno do servidor",
            "Ocorreu um erro inesperado, tente novamente mais tarde"
        );
        return new ResponseEntity<>(erro, HttpStatus.INTERNAL_SERVER_ERROR);
    }

	
}
