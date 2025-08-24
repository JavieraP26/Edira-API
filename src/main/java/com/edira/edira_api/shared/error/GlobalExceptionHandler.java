package com.edira.edira_api.shared.error;

import com.edira.edira_api.shared.validation.ValidationErrorDetail;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/*Para leer el archivo y para mi yo del futuro:
        Linea 39: Error 400 body (MethodArgumentNotValidException)
        Linea 71: Error 400 params (ConstraintViolationException)
        Linea 101: Error 400 (IllegalArgumentException)
        Linea 130: Error 404 (NotFoundException)
        Linea 148: Error 409 (DataIntegrityViolation)
        Linea 162: Error 500 (fallback)
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<ValidationErrorDetail> details =
                ex.getBindingResult()
                        .getFieldErrors()                   // List<FieldError>
                        .stream()
                        .map(ValidationErrorDetail::from)
                        .toList();

        String path = request.getRequestURI();

        int status = HttpStatus.BAD_REQUEST.value();
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        String message = "La solicitud tiene datos inválidos.";

        ApiError body = ApiError.validation(status, code, message, path, details);

        log.warn("400 VALIDATION_ERROR path={} errorId={} invalidFields={}",
                path, body.errorId(), details.size());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    /*
    Metodo para @RequestParam, @PathVariale como anotaciones @Min o @NotBlank etc.
     */

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation (
            ConstraintViolationException ex,
            HttpServletRequest request){

        List<ValidationErrorDetail> details =
                ex.getConstraintViolations()
                        .stream()
                        .map(ValidationErrorDetail :: from)
                        .toList();


        String path = request.getRequestURI();

        int status = HttpStatus.BAD_REQUEST.value();
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        String message = "Parámetros inválidos.";

        ApiError body = ApiError.validation(status, code, message, path, details);

        log.warn("400 VALIDATION_ERROR path={} errorId={} invalidParams={}",
                path, body.errorId(), details.size());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    //Para recursos no encontrados

    @ExceptionHandler({
            NotFoundException.class,
            ChangeSetPersister.NotFoundException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFound(
            Exception ex,
            HttpServletRequest request) {

        String path = request.getRequestURI();

        int status = HttpStatus.NOT_FOUND.value();
        ErrorCode code = ErrorCode.NOT_FOUND;
        String message =  (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : "Recurso no encontrado.";

        ApiError body = ApiError.of(status, code, message, path);

        log.warn("404 NOT_FOUND path={} errorId={}", path, body.errorId());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);

    }

    //Datos que no deberian ir ahi.

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument (IllegalArgumentException ex,
    HttpServletRequest request){

        String path = request.getRequestURI();

        int status = HttpStatus.BAD_REQUEST.value();
        ErrorCode code = ErrorCode.BAD_REQUEST;
        String message = (ex.getMessage() != null && !ex.getMessage().isBlank()) ? ex.getMessage() : "Solicitud inválida.";
        ApiError body = ApiError.of(status, code, message, path);

        log.warn("400 BAD_REQUEST path={} errorId={}", path, body.errorId());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    //Exception al INSERT/UPDATE/DELETE y la db rechaza

    @ExceptionHandler({DataIntegrityViolationException.class, org.hibernate.exception.ConstraintViolationException.class})
    public ResponseEntity<ApiError> handleDataIntegrity(Exception ex, HttpServletRequest request){

        String path = request.getRequestURI();
        int status = HttpStatus.CONFLICT.value();
        ErrorCode code = ErrorCode.CONFLICT;
        String message = "Conflicto con el estado actual del recurso";
        ApiError body = ApiError.of(status, code, message, path);
        log.warn("409 CONFLICT path={} errorId={}", path, body.errorId());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);

    }

    //fallback 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request){

        String path = request.getRequestURI();
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        ErrorCode code = ErrorCode.INTERNAL_ERROR;
        String message = "Algo salió mal. Intenta más tarde.";
        ApiError body = ApiError.of(status, code, message, path);
        // 5xx → ERROR con stacktrace
        log.error("500 INTERNAL_ERROR path={} errorId={}", path, body.errorId(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }


    }


