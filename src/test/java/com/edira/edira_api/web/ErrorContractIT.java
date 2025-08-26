package com.edira.edira_api.web;

import com.edira.edira_api.shared.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc
class ErrorContractIT {

    @Autowired MockMvc mvc;

    @Configuration
    static class TestConfig {

        @Bean
        public ErrorTestController errorTestController() {
            return new ErrorTestController();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }

        @Bean
        public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
            return new MappingJackson2HttpMessageConverter();
        }
    }

    // Controlador de prueba para generar diferentes tipos de errores
    @RestController
    @RequestMapping("/test-errors")
    static class ErrorTestController {

        @GetMapping("/not-found")
        public String notFound() {
            throw new com.edira.edira_api.shared.error.NotFoundException("Recurso no encontrado");
        }

        @GetMapping("/bad-request")
        public String badRequest() {
            throw new IllegalArgumentException("Parámetro inválido");
        }

        @GetMapping("/conflict")
        public String conflict() {
            throw new RuntimeException("Conflicto de datos");
        }

        @PostMapping("/validation")
        public String validation(@Valid @RequestBody TestDto dto) {
            return "valid: " + dto.name();
        }

        @GetMapping("/internal-error")
        public String internalError() {
            throw new RuntimeException("Error interno del servidor");
        }
    }

    record TestDto(@NotNull(message = "El nombre es obligatorio") String name) {}

    // Test 1: Error 404 - NOT_FOUND
    @Test
    void notFound_devuelve404_yContratoCorrecto() throws Exception {
        mvc.perform(get("/test-errors/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/test-errors/not-found"))
                .andExpect(jsonPath("$.message").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // Test 2: Error 400 - BAD_REQUEST
    @Test
    void badRequest_devuelve400_yContratoCorrecto() throws Exception {
        mvc.perform(get("/test-errors/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/test-errors/bad-request"))
                .andExpect(jsonPath("$.errorId").exists());
    }

    // Test 3: Error 500 - INTERNAL_ERROR (RuntimeException se mapea a 500)
    @Test
    void conflict_devuelve500_yContratoCorrecto() throws Exception {
        mvc.perform(get("/test-errors/conflict"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.path").value("/test-errors/conflict"))
                .andExpect(jsonPath("$.errorId").exists());
    }

    // Test 4: Error 400 - VALIDATION_ERROR con detalles
    @Test
    void validationError_devuelve400_yListaDetalles() throws Exception {
        String bodyInvalido = """
            { "name": null }
        """;

        mvc.perform(post("/test-errors/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/test-errors/validation"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details.length()").value(1))
                .andExpect(jsonPath("$.details[0].field").value("name"))
                .andExpect(jsonPath("$.details[0].message").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.errorId").exists());
    }

    // Test 5: Error 500 - INTERNAL_ERROR
    @Test
    void internalError_devuelve500_yContratoCorrecto() throws Exception {
        mvc.perform(get("/test-errors/internal-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.path").value("/test-errors/internal-error"))
                .andExpect(jsonPath("$.errorId").exists());
    }

    // Test 6: Verificar estructura completa del contrato (sin details para errores no de validación)
    @Test
    void todosLosErrores_tienenEstructuraCompleta() throws Exception {
        mvc.perform(get("/test-errors/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errorId").exists());
        // Nota: $.details solo está presente en errores de validación
    }
}
