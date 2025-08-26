package com.edira.edira_api.shared.error;

import com.edira.edira_api.shared.validation.ValidationErrorDetail;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la clase ApiError.
 * Verifica que todos los métodos de construcción funcionen correctamente
 * y que la estructura de datos sea consistente.
 * Se usa metodo Arrange/Act/Assert, pero se mantendrá en español para mejor comprensión de otros/as desarrolladores/as
 */
class ApiErrorTest {

    @Test
    void of_conTodosLosParametros_creaErrorCompleto() {
        // Preparación
        int status = 400;
        ErrorCode code = ErrorCode.BAD_REQUEST;
        String message = "Solicitud inválida";
        String path = "/api/test";
        UUID errorId = UUID.randomUUID();

        // Ejecutar
        ApiError error = ApiError.of(status, code, message, path, errorId);

        // Verificar
        assertNotNull(error);
        assertEquals(status, error.status());
        assertEquals(code, error.code());
        assertEquals(message, error.message());
        assertEquals(path, error.path());
        assertEquals(errorId, error.errorId());
        assertNotNull(error.timestamp());
        assertTrue(error.timestamp().isBefore(Instant.now().plusSeconds(1)));
        assertTrue(error.timestamp().isAfter(Instant.now().minusSeconds(1)));
        assertTrue(error.details().isEmpty());
    }

    @Test
    void of_sinErrorId_generaErrorIdAutomaticamente() {
        // Preparar
        int status = 404;
        ErrorCode code = ErrorCode.NOT_FOUND;
        String message = "Recurso no encontrado";
        String path = "/api/users/999";

        // Ejecutar
        ApiError error = ApiError.of(status, code, message, path);

        // Verificar
        assertNotNull(error);
        assertEquals(status, error.status());
        assertEquals(code, error.code());
        assertEquals(message, error.message());
        assertEquals(path, error.path());
        assertNotNull(error.errorId());
        assertTrue(error.details().isEmpty());
    }

    @Test
    void validation_conDetalles_creaErrorConDetalles() {
        // Preparar
        int status = 400;
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        String message = "Datos inválidos";
        String path = "/api/users";
        UUID errorId = UUID.randomUUID();
        
        List<ValidationErrorDetail> details = List.of(
                new ValidationErrorDetail("email", "El email es obligatorio"),
                new ValidationErrorDetail("name", "El nombre es obligatorio")
        );

        // Ejecutar
        ApiError error = ApiError.validation(status, code, message, path, details, errorId);

        // Verificar
        assertNotNull(error);
        assertEquals(status, error.status());
        assertEquals(code, error.code());
        assertEquals(message, error.message());
        assertEquals(path, error.path());
        assertEquals(errorId, error.errorId());
        assertEquals(2, error.details().size());
        assertEquals("email", error.details().get(0).field());
        assertEquals("El email es obligatorio", error.details().get(0).message());
        assertEquals("name", error.details().get(1).field());
        assertEquals("El nombre es obligatorio", error.details().get(1).message());
    }

    @Test
    void validation_sinErrorId_generaErrorIdAutomaticamente() {
        // Preparar
        int status = 400;
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        String message = "Validación fallida";
        String path = "/api/orders";
        
        List<ValidationErrorDetail> details = List.of(
                new ValidationErrorDetail("quantity", "La cantidad debe ser mayor a 0")
        );

        // Ejecutar
        ApiError error = ApiError.validation(status, code, message, path, details);

        // Verificar
        assertNotNull(error);
        assertEquals(status, error.status());
        assertEquals(code, error.code());
        assertEquals(message, error.message());
        assertEquals(path, error.path());
        assertNotNull(error.errorId());
        assertEquals(1, error.details().size());
        assertEquals("quantity", error.details().get(0).field());
    }

    @Test
    void validation_conDetallesNull_creaErrorConListaVacia() {
        // Preparar
        int status = 400;
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        String message = "Error de validación";
        String path = "/api/test";
        UUID errorId = UUID.randomUUID();

        // Ejecutar
        ApiError error = ApiError.validation(status, code, message, path, null, errorId);

        // Verificar
        assertNotNull(error);
        assertTrue(error.details().isEmpty());
    }

    @Test
    void validation_conDetallesVacios_creaErrorConListaVacia() {
        // Preparar
        int status = 400;
        ErrorCode code = ErrorCode.VALIDATION_ERROR;
        String message = "Error de validación";
        String path = "/api/test";
        UUID errorId = UUID.randomUUID();
        List<ValidationErrorDetail> details = List.of();

        // Ejecutar
        ApiError error = ApiError.validation(status, code, message, path, details, errorId);

        // Verificar
        assertNotNull(error);
        assertTrue(error.details().isEmpty());
    }

    @Test
    void errorId_esUnicoParaCadaInstancia() {
        // Preparar y Ejecutar
        ApiError error1 = ApiError.of(400, ErrorCode.BAD_REQUEST, "Error 1", "/api/test1");
        ApiError error2 = ApiError.of(404, ErrorCode.NOT_FOUND, "Error 2", "/api/test2");

        // Verificar
        assertNotEquals(error1.errorId(), error2.errorId());
        assertNotNull(error1.errorId());
        assertNotNull(error2.errorId());
    }

    @Test
    void timestamp_esReciente() {
        // Preparar
        Instant antes = Instant.now().minusSeconds(1);

        // Ejecutar
        ApiError error = ApiError.of(500, ErrorCode.INTERNAL_ERROR, "Error interno", "/api/test");

        // Verificar
        assertTrue(error.timestamp().isAfter(antes));
        assertTrue(error.timestamp().isBefore(Instant.now().plusSeconds(1)));
    }

    @Test
    void detalles_sonInmutables() {
        // Preparar
        List<ValidationErrorDetail> detallesOriginales = List.of(
                new ValidationErrorDetail("field1", "Error 1"),
                new ValidationErrorDetail("field2", "Error 2")
        );

        // Ejecutar
        ApiError error = ApiError.validation(400, ErrorCode.VALIDATION_ERROR, "Error", "/api/test", detallesOriginales);

        // Verificar
        assertEquals(2, error.details().size());
        
        // Intentar modificar la lista original no debe afectar el error
        detallesOriginales = List.of(new ValidationErrorDetail("field3", "Error 3"));
        assertEquals(2, error.details().size());
    }
}
