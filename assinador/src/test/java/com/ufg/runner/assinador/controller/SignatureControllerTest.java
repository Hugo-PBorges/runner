package com.ufg.runner.assinador.controller;

import com.ufg.runner.assinador.dto.outcome.OperationOutcomeBuilder;
import com.ufg.runner.assinador.exception.BusinessValidationException;
import com.ufg.runner.assinador.exception.CryptographicException;
import com.ufg.runner.assinador.service.SignatureService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SignatureController.class)
class SignatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SignatureService signatureService;

    @Test
    @DisplayName("POST /signature/sign com payload válido → 200 e resultado do service")
    void sign_validPayload_returnsOk() throws Exception {
        when(signatureService.sign(any())).thenReturn(
                OperationOutcomeBuilder.information("SIGNATURE_CREATED", "header.payload.signature")
        );

        mockMvc.perform(post("/signature/sign")
                        .contentType("application/json")
                        .content(validSignRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issue[0].severity").value("information"))
                .andExpect(jsonPath("$.issue[0].code").value("SIGNATURE_CREATED"));
    }

    @Test
    @DisplayName("POST /signature/sign com payload inválido (@Valid) → 400 e OperationOutcome de erro")
    void sign_invalidPayload_returnsBadRequest() throws Exception {
        String invalidJson = "{}";

        mockMvc.perform(post("/signature/sign")
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resourceType").value("OperationOutcome"))
                .andExpect(jsonPath("$.issue[0].severity").value("error"));
    }

    @Test
    @DisplayName("POST /signature/sign quando service lança BusinessValidationException → 422")
    void sign_businessValidationError_returnsUnprocessableContent() throws Exception {
        when(signatureService.sign(any()))
                .thenThrow(new BusinessValidationException("VALIDATION.TARGET-MISMATCH", "Quantidade de targets diferente do bundle"));

        mockMvc.perform(post("/signature/sign")
                        .contentType("application/json")
                        .content(validSignRequest()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.issue[0].code").value("VALIDATION.TARGET-MISMATCH"));
    }

    @Test
    @DisplayName("POST /signature/sign quando service lança CryptographicException → 500")
    void sign_cryptographicError_returnsInternalServerError() throws Exception {
        when(signatureService.sign(any()))
                .thenThrow(new CryptographicException("Erro ao acessar dispositivo criptográfico: falha"));

        mockMvc.perform(post("/signature/sign")
                        .contentType("application/json")
                        .content(validSignRequest()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.issue[0].severity").value("error"));
    }

    @Test
    @DisplayName("POST /signature/validate com payload válido → 200")
    void validate_validPayload_returnsOk() throws Exception {
        when(signatureService.validate(any())).thenReturn(
                OperationOutcomeBuilder.information("SIGNATURE_VALID", "Assinatura válida com sucesso")
        );

        String json = """
                {
                  "base64": "ZmFrZQ==",
                  "timestamp": 1751328001,
                  "policyURI": "http://example.com|1.0.0"
                }
                """;

        mockMvc.perform(post("/signature/validate")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issue[0].code").value("SIGNATURE_VALID"));
    }

    private String validSignRequest() {
        return """
                {
                  "bundle": { "entries": ["a", "b"] },
                  "provenance": { "target": ["a", "b"] },
                  "crypto": { "pin": "1234", "identifier": "key1", "slotId": 1, "tokenLabel": "SafeNet" },
                  "certificates": ["cert1"],
                  "timestamp": 1751328001,
                  "strategy": "iat",
                  "policy": "https://fhir.saude.go.gov.br|0.0.2"
                }
                """;
    }
}
