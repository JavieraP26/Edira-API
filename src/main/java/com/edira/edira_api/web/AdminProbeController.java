package com.edira.edira_api.web;

import com.edira.edira_api.shared.error.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "admin-probe-controller")
public class AdminProbeController {

    @Operation(summary = "Ping admin", description = "Requiere rol ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "401",
                                    value = """
                    {
                      "timestamp": "2025-08-24T18:35:12.123Z",
                      "path": "/admin/ping",
                      "status": 401,
                      "code": "UNAUTHORIZED",
                      "message": "No autenticado. Inicia sesión.",
                      "details": [],
                      "errorId": "11111111-1111-1111-1111-111111111111"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(
                                    name = "403",
                                    value = """
                    {
                      "timestamp": "2025-08-24T18:35:12.123Z",
                      "path": "/admin/ping",
                      "status": 403,
                      "code": "FORBIDDEN",
                      "message": "Acceso denegado",
                      "details": [],
                      "errorId": "22222222-2222-2222-2222-222222222222"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @SecurityRequirement(name = "basicAuth")
    @GetMapping("/ping")
    public String ping() {
        return "todo ok admin";
    }

}
