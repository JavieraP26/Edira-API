package com.edira.edira_api.shared.validation;

import jakarta.validation.ConstraintViolation;
import org.springframework.validation.FieldError;

/* DTO inmutable para un error de validación
field cumple la funcion de marcar la ruta del campo que fallo como el email
message: Mensaje interpolado por Bean Validation
 */

public record ValidationErrorDetail(String field, String message) {

    //Error de validacion de un @RequestBody @Valid que se encuentra en un controller
    public static ValidationErrorDetail from (FieldError fe){
        String path = fe.getField(); //ruta
        String msg = fe.getDefaultMessage(); //mensaje devuelta
        return new ValidationErrorDetail(path, msg);
    }

    //Falla de parametros sueltos con @Validated en un controller o en validación de metodo
    public static ValidationErrorDetail from (ConstraintViolation<?> cv ){
        String path = cv.getPropertyPath().toString();
        String msg = cv.getMessage();
        return new ValidationErrorDetail(path, msg);
    }
}
