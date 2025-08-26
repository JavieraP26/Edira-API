package com.edira.edira_api.web;

import com.edira.edira_api.security.SecurityConfig;
import com.edira.edira_api.security.ApiErrorAccessDeniedHandler;
import com.edira.edira_api.security.ApiErrorAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * Test de integración para la seguridad de las rutas públicas y administrativas.
 */

@WebMvcTest(controllers = {PublicProbeController.class, AdminProbeController.class})
@Import({SecurityConfig.class, ApiErrorAccessDeniedHandler.class, ApiErrorAuthenticationEntryPoint.class})
class ProbeSecurityIT {

    @Autowired MockMvc mvc;

    @Test
    void apiPing_sinAuth_devuelve401_yContrato() throws Exception {
        mvc.perform(get("/ping"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.path").value("/ping"))
                .andExpect(jsonPath("$.errorId").exists());
    }

    @Test
    @WithMockUser // rol USER por defecto
    void apiPing_conAuth_devuelve200_yPong() throws Exception {
        mvc.perform(get("/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    @Test
    @WithMockUser // rol USER por defecto
    void adminPing_conUserRole_devuelve403_yContrato() throws Exception {
        mvc.perform(get("/admin/ping"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.path").value("/admin/ping"))
                .andExpect(jsonPath("$.errorId").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPing_conAdminRole_devuelve200_yOk() throws Exception {
        mvc.perform(get("/admin/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("todo ok admin"));
    }
}
