package com.edira.edira_api.web;

import com.edira.edira_api.shared.error.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * Test de integración para la validación de datos.
 * Se usa Given-When-Then para la nomenclatura de los tests.
 */


@WebMvcTest(controllers = ValidationIT.ValidationDummyController.class)
@Import({GlobalExceptionHandler.class, ValidationIT.ValidationDummyController.class})
@AutoConfigureMockMvc(addFilters = false)
class ValidationIT {

    @Autowired MockMvc mvc;

    // Controlador de prueba interno para validación
    @RestController
    @RequestMapping("/validation")
    static class ValidationDummyController {

        @PostMapping("/echo")
        public String echo(@Valid @RequestBody PersonDto person) {
            return "valid: " + person.nombre() + " - " + person.email();
        }
    }

    // DTO de prueba con validaciones
    record PersonDto(
            @NotBlank(message = "El nombre no puede estar vacío")
            String nombre,

            @NotBlank(message = "El email no puede estar vacío")
            @Email(message = "El email debe tener un formato válido")
            String email
    ) {}

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void post_invalido_dispara400Validation_yListaDetails() throws Exception {
        // Given
        PersonDto person = new PersonDtoBuilder()
                .withEmptyName()
                .withInvalidEmail()
                .build();

        // When & Then
        performPostRequest(person)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details.length()").value(2))
                .andExpectAll(
                    jsonPath("$.details[?(@.field == 'nombre')].message")
                        .value(contains("El nombre no puede estar vacío")),
                    jsonPath("$.details[?(@.field == 'email')].message")
                        .value(contains("El email debe tener un formato válido"))
                );
    }

    @Test
    void post_valido_retorna200ConMensaje() throws Exception {
        // Given
        PersonDto person = new PersonDtoBuilder()
                .withName("Juan Pérez")
                .withEmail("juan.perez@ejemplo.com")
                .build();

        // When & Then
        performPostRequest(person)
                .andExpect(status().isOk())
                .andExpect(content().string("valid: Juan Pérez - juan.perez@ejemplo.com"));
    }

    @Test
    void post_nombreVacio_disparaErrorDeValidacion() throws Exception {
        // Given
        PersonDto person = new PersonDtoBuilder()
                .withEmptyName()
                .withValidEmail()
                .build();

        // When & Then
        performPostRequest(person)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@.field == 'nombre')].message")
                    .value(contains("El nombre no puede estar vacío")));
    }

    @Test
    void post_emailInvalido_disparaErrorDeValidacion() throws Exception {
        // Given
        PersonDto person = new PersonDtoBuilder()
                .withName("Ana García")
                .withInvalidEmail()
                .build();

        // When & Then
        performPostRequest(person)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@.field == 'email')].message")
                    .value(contains("El email debe tener un formato válido")));
    }

    private ResultActions performPostRequest(PersonDto person) throws Exception {
        return mvc.perform(post("/validation/echo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person)));
    }

    /**
     * Test data builder para PersonDto
     */
    private static class PersonDtoBuilder {
        private String name = "Default Name";
        private String email = "default@example.com";

        PersonDtoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        PersonDtoBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        PersonDtoBuilder withEmptyName() {
            this.name = "";
            return this;
        }

        PersonDtoBuilder withValidEmail() {
            this.email = "valid@example.com";
            return this;
        }

        PersonDtoBuilder withInvalidEmail() {
            this.email = "invalid-email";
            return this;
        }

        PersonDto build() {
            return new PersonDto(name, email);
        }
    }
}
