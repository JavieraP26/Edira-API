package com.edira.edira_api.shared.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para las excepciones personalizadas.
 * Verifica que las excepciones se construyan correctamente
 * y mantengan el mensaje y causa apropiados.
 * Se usa metodo Arrange/Act/Assert, pero se mantendra en español para mejor comprension de otros/as desarrolladores/as
 */
class CustomExceptionsTest {

    @Test
    void notFoundException_conMensaje_creaExcepcionCorrectamente() {
        // Preparar
        String mensaje = "Usuario no encontrado";

        // Ejecutar
        NotFoundException exception = new NotFoundException(mensaje);

        // Verificar
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void notFoundException_conMensajeYCausa_creaExcepcionCorrectamente() {
        // Preparar
        String mensaje = "Recurso no encontrado";
        Throwable causa = new RuntimeException("Error de base de datos");

        // Ejecutar
        NotFoundException exception = new NotFoundException(mensaje, causa);

        // Verificar
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertEquals(causa, exception.getCause());
    }

    @Test
    void notFoundException_esSubclaseDeRuntimeException() {
        // Ejecutar
        NotFoundException exception = new NotFoundException("Test");

        // Verificar
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void unauthorizedException_conMensaje_creaExcepcionCorrectamente() {
        // Preparar
        String mensaje = "Credenciales inválidas";

        // Ejecutar
        UnauthorizedException exception = new UnauthorizedException(mensaje);

        // Verificar
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void unauthorizedException_conMensajeYCausa_creaExcepcionCorrectamente() {
        // Preparar
        String mensaje = "Token expirado";
        Throwable causa = new SecurityException("Token inválido");

        // Ejecutar
        UnauthorizedException exception = new UnauthorizedException(mensaje, causa);

        // Verificar
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertEquals(causa, exception.getCause());
    }

    @Test
    void unauthorizedException_esSubclaseDeRuntimeException() {
        // Ejecutar
        UnauthorizedException exception = new UnauthorizedException("Test");

        // Verificar
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void forbiddenException_conMensaje_creaExcepcionCorrectamente() {
        // Preparar
        String mensaje = "Acceso denegado";

        // Ejecutar
        ForbiddenException exception = new ForbiddenException(mensaje);

        // Verificar
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void forbiddenException_conMensajeYCausa_creaExcepcionCorrectamente() {
        // Preparar
        String mensaje = "Permisos insuficientes";
        Throwable causa = new SecurityException("Rol no autorizado");

        // Ejecutar
        ForbiddenException exception = new ForbiddenException(mensaje, causa);

        // Verificar
        assertNotNull(exception);
        assertEquals(mensaje, exception.getMessage());
        assertEquals(causa, exception.getCause());
    }

    @Test
    void forbiddenException_esSubclaseDeRuntimeException() {
        // Ejecutar
        ForbiddenException exception = new ForbiddenException("Test");

        // Verificar
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void todasLasExcepciones_puedenSerLanzadas() {
        // Preparar, Ejecutar y Verificar
        // Verificar que las excepciones se pueden instanciar (no se lanzan automáticamente)
        assertDoesNotThrow(() -> {
            new NotFoundException("Test not found");
        });

        assertDoesNotThrow(() -> {
            new UnauthorizedException("Test unauthorized");
        });

        assertDoesNotThrow(() -> {
            new ForbiddenException("Test forbidden");
        });
    }

    @Test
    void excepciones_conMensajesVacios_sonValidas() {
        // Preparar, Ejecutar y Verificar
        assertDoesNotThrow(() -> {
            NotFoundException exception = new NotFoundException("");
            assertEquals("", exception.getMessage());
        });

        assertDoesNotThrow(() -> {
            UnauthorizedException exception = new UnauthorizedException("");
            assertEquals("", exception.getMessage());
        });

        assertDoesNotThrow(() -> {
            ForbiddenException exception = new ForbiddenException("");
            assertEquals("", exception.getMessage());
        });
    }

    @Test
    void excepciones_aceptanMensajesNull() {
        // Preparar, Ejecutar y Verificar
        assertDoesNotThrow(() -> {
            NotFoundException exception = new NotFoundException((String) null);
            assertNull(exception.getMessage());
        });

        assertDoesNotThrow(() -> {
            UnauthorizedException exception = new UnauthorizedException((String) null);
            assertNull(exception.getMessage());
        });

        assertDoesNotThrow(() -> {
            ForbiddenException exception = new ForbiddenException((String) null);
            assertNull(exception.getMessage());
        });
    }
}
