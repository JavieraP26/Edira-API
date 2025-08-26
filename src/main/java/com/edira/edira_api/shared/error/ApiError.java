package com.edira.edira_api.shared.error;

import com.edira.edira_api.shared.validation.ValidationErrorDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;
import java.time.Instant;
import java.util.List;


import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Schema(name = "ApiError", description = "Contrato estándar de error de Edira-API")
public record ApiError(

        @Schema(description = "Instante del error en UTC")
        Instant timestamp,
        @Schema(description = "Ruta solicitada")
        String path,
        @Schema(description = "Código HTTP")
        int status,
        @Schema(description = "Código lógico de negocio")
        ErrorCode code,
        @Schema(description = "Mensaje personalizado para cada caso")
        String message,
        @Schema(description = "Detalle de validaciones por campo (si aplica)")
        @JsonInclude(NON_EMPTY)
        List<ValidationErrorDetail> details,
        @Schema(description = "Correlación del error para logs")
        UUID errorId
) {
    //Metodo vacio generico.
    public static ApiError of(int status, ErrorCode code, String message, String path, UUID errorId){
        return new ApiError(
                Instant.now(),
                path,
                status,
                code,
                message,
                List.of(),
                errorId
        );
    }

    //Validación vacia
    public static ApiError of(int status, ErrorCode code, String message, String path) {
        return of(status, code, message, path, java.util.UUID.randomUUID());
    }

    //validaciones pero con la lista de detalles.
    public static ApiError validation(int status, ErrorCode code, String message, String path,
                                      List<ValidationErrorDetail> details, UUID errorId) {
        return new ApiError(
                Instant.now(),
                path,
                status,
                code,
                message,
                details == null ? List.of() : List.copyOf(details), // null-safe + inmutable
                errorId
        );
    }
    // Overload que genera errorId automáticamente
    public static ApiError validation(int status, ErrorCode code, String message, String path,
                                      List<ValidationErrorDetail> details) {
        return validation(status, code, message, path, details, java.util.UUID.randomUUID());
    }

}
