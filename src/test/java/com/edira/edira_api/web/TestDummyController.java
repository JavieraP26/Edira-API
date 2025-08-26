package com.edira.edira_api.web;

import com.edira.edira_api.shared.error.NotFoundException;
import com.edira.edira_api.shared.error.UnauthorizedException;
import com.edira.edira_api.shared.error.ForbiddenException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;

/**
 * Controlador de prueba que genera diferentes tipos de excepciones
 * para verificar su manejo correcto por el GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/global")
@Validated // para validar parámetros (ConstraintViolationException)
public class TestDummyController {

    @GetMapping("/illegal")
    public String illegal() { 
        throw new IllegalArgumentException("Solicitud inválida."); 
    }

    @GetMapping("/not-found")
    public String notFound() { 
        throw new NotFoundException("Recurso no encontrado."); 
    }

    @GetMapping("/conflict")
    public String conflict() { 
        throw new DataIntegrityViolationException("duplicado"); 
    }

    @GetMapping("/boom")
    public String boom() { 
        throw new RuntimeException("kaboom"); 
    }

    @GetMapping("/param")
    public String param(@RequestParam @Min(1) int page, @RequestParam(required = false) @Min(1) Integer size) { 
        return "ok"; 
    }

    @PostMapping("/validation")
    public String validation(@Valid @RequestBody TestDto dto) {
        return "valid: " + dto.name();
    }

    @GetMapping("/unauthorized")
    public String unauthorized() {
        throw new UnauthorizedException("Credenciales inválidas");
    }

    @GetMapping("/forbidden")
    public String forbidden() {
        throw new ForbiddenException("No tienes permisos para acceder a este recurso");
    }

    // DTO de prueba con validaciones
    record TestDto(@NotBlank(message = "El nombre es obligatorio") String name) {}
}
