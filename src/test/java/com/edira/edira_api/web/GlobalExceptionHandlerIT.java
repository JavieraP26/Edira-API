
package com.edira.edira_api.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de integración para el GlobalExceptionHandler.
 * Verifica que todas las excepciones se mapeen correctamente a códigos HTTP
 * y respuestas JSON apropiadas.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GlobalExceptionHandlerIT {

    @Autowired MockMvc mvc;

    /**
     * Test 1: BAD_REQUEST (400) - Argumento ilegal
     */
    @Test
    @WithMockUser
    void illegalArgument_devuelve400() throws Exception {
        mvc.perform(get("/global/illegal"))
           .andExpect(status().isBadRequest())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
           .andExpect(jsonPath("$.path").value("/global/illegal"))
           .andExpect(jsonPath("$.errorId").exists());
    }

    /**
     * Test 2: NOT_FOUND (404) - Recurso no encontrado
     */
    @Test
    @WithMockUser
    void notFound_devuelve404() throws Exception {
        mvc.perform(get("/global/not-found"))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value("NOT_FOUND"))
           .andExpect(jsonPath("$.path").value("/global/not-found"));
    }

    /**
     * Test 3: CONFLICT (409) - Violación de integridad de datos
     */
    @Test
    @WithMockUser
    void conflict_devuelve409() throws Exception {
        mvc.perform(get("/global/conflict"))
           .andExpect(status().isConflict())
           .andExpect(jsonPath("$.code").value("CONFLICT"))
           .andExpect(jsonPath("$.path").value("/global/conflict"));
    }

    /**
     * Test 4: INTERNAL_ERROR (500) - Error genérico (fallback)
     */
    @Test
    @WithMockUser
    void fallback_devuelve500() throws Exception {
        mvc.perform(get("/global/boom"))
           .andExpect(status().isInternalServerError())
           .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
           .andExpect(jsonPath("$.path").value("/global/boom"));
    }

    /**
     * Test 5: VALIDATION_ERROR (400) - Validación de parámetros con detalles
     */
    @Test
    @WithMockUser
    void constraintViolation_param_devuelve400_conDetails() throws Exception {
        mvc.perform(get("/global/param?page=0"))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
           .andExpect(jsonPath("$.details.length()", greaterThanOrEqualTo(1)))
           .andExpect(jsonPath("$.path").value("/global/param"));
    }
}
