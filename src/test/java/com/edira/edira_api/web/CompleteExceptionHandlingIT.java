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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test integral que verifica todas las excepciones implementadas en el sistema.
 * Este test asegura que cada tipo de excepción se maneje correctamente y devuelva
 * el código HTTP apropiado con la estructura de respuesta esperada.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompleteExceptionHandlingIT {

    @Autowired MockMvc mvc;

    /**
     * Test 1: VALIDATION_ERROR (400) - Validación de body con detalles completos
     */
    @Test
    @WithMockUser
    void validationError_devuelve400_conDetallesCompletos() throws Exception {
        mvc.perform(post("/global/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"age\":-5}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.path").value("/global/validation"))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details.length()", greaterThanOrEqualTo(1)));
    }

    /**
     * Test 2: VALIDATION_ERROR (400) - Validación de parámetros con detalles completos
     */
    @Test
    @WithMockUser
    void paramValidationError_devuelve400_conDetallesCompletos() throws Exception {
        mvc.perform(get("/global/param?page=0&size=-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.path").value("/global/param"))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details.length()", greaterThanOrEqualTo(1)));
    }

    /**
     * Test 3: BAD_REQUEST (400) - Argumento ilegal con contrato correcto
     */
    @Test
    @WithMockUser
    void illegalArgument_devuelve400_conContratoCorrecto() throws Exception {
        mvc.perform(get("/global/illegal"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.path").value("/global/illegal"))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Test 4: UNAUTHORIZED (401) - Autenticación fallida con contrato correcto
     */
    @Test
    void unauthorized_devuelve401_conContratoCorrecto() throws Exception {
        mvc.perform(get("/global/unauthorized"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.path").value("/global/unauthorized"))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Test 5: FORBIDDEN (403) - Autorización fallida con contrato correcto
     */
    @Test
    @WithMockUser
    void forbidden_devuelve403_conContratoCorrecto() throws Exception {
        mvc.perform(get("/global/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.path").value("/global/forbidden"))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Test 6: NOT_FOUND (404) - Recurso no encontrado con contrato correcto
     */
    @Test
    @WithMockUser
    void notFound_devuelve404_conContratoCorrecto() throws Exception {
        mvc.perform(get("/global/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/global/not-found"))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Test 7: CONFLICT (409) - Violación de integridad con contrato correcto
     */
    @Test
    @WithMockUser
    void conflict_devuelve409_conContratoCorrecto() throws Exception {
        mvc.perform(get("/global/conflict"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("CONFLICT"))
                .andExpect(jsonPath("$.path").value("/global/conflict"))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Test 8: INTERNAL_ERROR (500) - Error interno con contrato correcto
     */
    @Test
    @WithMockUser
    void internalError_devuelve500_conContratoCorrecto() throws Exception {
        mvc.perform(get("/global/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.path").value("/global/boom"))
                .andExpect(jsonPath("$.errorId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Test 9: Verificación de estructura común en todos los errores
     */
    @Test
    @WithMockUser
    void todosLosErrores_tienenEstructuraComun() throws Exception {
        // Verificar que todos los endpoints de error devuelvan la estructura común
        String[] endpoints = {
                "/global/illegal",
                "/global/not-found",
                "/global/conflict",
                "/global/boom"
        };

        for (String endpoint : endpoints) {
            mvc.perform(get(endpoint))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.path").value(endpoint))
                    .andExpect(jsonPath("$.errorId").exists())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.code").exists())
                    .andExpect(jsonPath("$.message").exists());
        }
    }
}
