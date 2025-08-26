package com.edira.edira_api.shared.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el enum ErrorCode.
 * Verifica que todos los códigos de error estén definidos
 * y sean consistentes con los códigos HTTP correspondientes.
 * Se usa metodo Arrange/Act/Assert, pero se mantendra en español para mejor comprension de otros/as desarrolladores/as
 */
class ErrorCodeTest {

    @Test
    void errorCode_tieneTodosLosValoresEsperados() {
        // Preparar y ejecutar
        ErrorCode[] expectedCodes = {
                ErrorCode.VALIDATION_ERROR,
                ErrorCode.BAD_REQUEST,
                ErrorCode.NOT_FOUND,
                ErrorCode.CONFLICT,
                ErrorCode.UNAUTHORIZED,
                ErrorCode.FORBIDDEN,
                ErrorCode.INTERNAL_ERROR
        };

        // Verificar
        assertEquals(7, ErrorCode.values().length);
        
        for (ErrorCode code : expectedCodes) {
            assertNotNull(code);
        }
    }

    @Test
    void errorCode_valoresSonUnicos() {
        // Preparar
        ErrorCode[] codes = ErrorCode.values();
        
        // Ejecutar y verificar
        for (int i = 0; i < codes.length; i++) {
            for (int j = i + 1; j < codes.length; j++) {
                assertNotEquals(codes[i], codes[j], 
                    "Los códigos de error deben ser únicos: " + codes[i] + " y " + codes[j]);
            }
        }
    }

    @Test
    void errorCode_nombresSonConsistentes() {
        // Verificar
        assertEquals("VALIDATION_ERROR", ErrorCode.VALIDATION_ERROR.name());
        assertEquals("BAD_REQUEST", ErrorCode.BAD_REQUEST.name());
        assertEquals("NOT_FOUND", ErrorCode.NOT_FOUND.name());
        assertEquals("CONFLICT", ErrorCode.CONFLICT.name());
        assertEquals("UNAUTHORIZED", ErrorCode.UNAUTHORIZED.name());
        assertEquals("FORBIDDEN", ErrorCode.FORBIDDEN.name());
        assertEquals("INTERNAL_ERROR", ErrorCode.INTERNAL_ERROR.name());
    }

    @Test
    void errorCode_ordinalValuesSonConsistentes() {
        // Verificar
        assertEquals(0, ErrorCode.VALIDATION_ERROR.ordinal());
        assertEquals(1, ErrorCode.BAD_REQUEST.ordinal());
        assertEquals(2, ErrorCode.NOT_FOUND.ordinal());
        assertEquals(3, ErrorCode.CONFLICT.ordinal());
        assertEquals(4, ErrorCode.UNAUTHORIZED.ordinal());
        assertEquals(5, ErrorCode.FORBIDDEN.ordinal());
        assertEquals(6, ErrorCode.INTERNAL_ERROR.ordinal());
    }

    @Test
    void errorCode_valueOfFuncionaCorrectamente() {
        // Verificar
        assertEquals(ErrorCode.VALIDATION_ERROR, ErrorCode.valueOf("VALIDATION_ERROR"));
        assertEquals(ErrorCode.BAD_REQUEST, ErrorCode.valueOf("BAD_REQUEST"));
        assertEquals(ErrorCode.NOT_FOUND, ErrorCode.valueOf("NOT_FOUND"));
        assertEquals(ErrorCode.CONFLICT, ErrorCode.valueOf("CONFLICT"));
        assertEquals(ErrorCode.UNAUTHORIZED, ErrorCode.valueOf("UNAUTHORIZED"));
        assertEquals(ErrorCode.FORBIDDEN, ErrorCode.valueOf("FORBIDDEN"));
        assertEquals(ErrorCode.INTERNAL_ERROR, ErrorCode.valueOf("INTERNAL_ERROR"));
    }

    @Test
    void errorCode_mapeoACodigosHTTP() {
        // Este test documenta el mapeo esperado entre ErrorCode y códigos HTTP
        // Los códigos HTTP se manejan en GlobalExceptionHandler, no en ErrorCode
        
        // Verificar - Verifica que los nombres sean descriptivos para su uso en APIs
        assertTrue(ErrorCode.VALIDATION_ERROR.name().contains("VALIDATION"));
        assertTrue(ErrorCode.BAD_REQUEST.name().contains("BAD_REQUEST"));
        assertTrue(ErrorCode.NOT_FOUND.name().contains("NOT_FOUND"));
        assertTrue(ErrorCode.CONFLICT.name().contains("CONFLICT"));
        assertTrue(ErrorCode.UNAUTHORIZED.name().contains("UNAUTHORIZED"));
        assertTrue(ErrorCode.FORBIDDEN.name().contains("FORBIDDEN"));
        assertTrue(ErrorCode.INTERNAL_ERROR.name().contains("INTERNAL"));
    }

    @Test
    void errorCode_puedeSerUsadoEnSwitch() {
        // Este test verifica que ErrorCode funcione correctamente en statements switch
        ErrorCode testCode = ErrorCode.NOT_FOUND;
        
        String result = switch (testCode) {
            case VALIDATION_ERROR -> "validation";
            case BAD_REQUEST -> "bad_request";
            case NOT_FOUND -> "not_found";
            case CONFLICT -> "conflict";
            case UNAUTHORIZED -> "unauthorized";
            case FORBIDDEN -> "forbidden";
            case INTERNAL_ERROR -> "internal";
        };
        
        assertEquals("not_found", result);
    }

    @Test
    void errorCode_esComparable() {
        // Verificar - Verifica que los códigos se puedan comparar
        assertTrue(ErrorCode.VALIDATION_ERROR.compareTo(ErrorCode.BAD_REQUEST) < 0);
        assertTrue(ErrorCode.BAD_REQUEST.compareTo(ErrorCode.NOT_FOUND) < 0);
        assertTrue(ErrorCode.NOT_FOUND.compareTo(ErrorCode.CONFLICT) < 0);
        assertTrue(ErrorCode.CONFLICT.compareTo(ErrorCode.UNAUTHORIZED) < 0);
        assertTrue(ErrorCode.UNAUTHORIZED.compareTo(ErrorCode.FORBIDDEN) < 0);
        assertTrue(ErrorCode.FORBIDDEN.compareTo(ErrorCode.INTERNAL_ERROR) < 0);
    }
}
