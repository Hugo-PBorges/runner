package com.ufg.runner.assinador.dto.sign;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class SignRequestDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("DTO válido → sem violação")
    void validDto_noViolations() {
        assertThat(violations(buildValid())).isEmpty();
    }


    @Nested
    @DisplayName("campo bundle")
    class BundleField {

        @Test
        @DisplayName("bundle preenchido → sem violação")
        void bundle_filled_noViolation() {
            assertThat(violations(buildValid())).isEmpty();
        }

        @Test
        @DisplayName("bundle null → violação")
        void bundle_null_violation() {
            SignRequestDTO dto = buildValid();
            dto.setBundle(null);
            assertViolation(dto, "bundle", "Bundle é obrigatório");
        }
    }


    @Nested
    @DisplayName("campo provenance")
    class ProvenanceField {

        @Test
        @DisplayName("provenance preenchido → sem violação")
        void provenance_filled_noViolation() {
            assertThat(violations(buildValid())).isEmpty();
        }

        @Test
        @DisplayName("provenance null → violação")
        void provenance_null_violation() {
            SignRequestDTO dto = buildValid();
            dto.setProvenance(null);
            assertViolation(dto, "provenance", "Provenance é obrigatório");
        }
    }


    @Nested
    @DisplayName("campo crypto")
    class CryptoField {

        @Test
        @DisplayName("crypto preenchido → sem violação")
        void crypto_filled_noViolation() {
            assertThat(violations(buildValid())).isEmpty();
        }

        @Test
        @DisplayName("crypto null → violação")
        void crypto_null_violation() {
            SignRequestDTO dto = buildValid();
            dto.setCrypto(null);
            assertViolation(dto, "crypto", "Crypto é obrigatório");
        }
    }


    @Nested
    @DisplayName("campo certificates")
    class CertificatesField {

        @Test
        @DisplayName("certificates com um item → sem violação")
        void certificates_singleItem_noViolation() {
            SignRequestDTO dto = buildValid();
            dto.setCertificates(List.of("cert1"));
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("certificates com múltiplos itens → sem violação")
        void certificates_multipleItems_noViolation() {
            SignRequestDTO dto = buildValid();
            dto.setCertificates(List.of("cert1", "cert2", "cert3"));
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("certificates null → violação")
        void certificates_null_violation() {
            SignRequestDTO dto = buildValid();
            dto.setCertificates(null);
            assertViolation(dto, "certificates", "Certificates é obrigatório");
        }

        @Test
        @DisplayName("certificates vazio → violação")
        void certificates_empty_violation() {
            SignRequestDTO dto = buildValid();
            dto.setCertificates(List.of());
            assertViolation(dto, "certificates", "Certificates é obrigatório");
        }

        @Test
        @DisplayName("certificates com item em branco → violação")
        void certificates_blankItem_violation() {
            SignRequestDTO dto = buildValid();
            dto.setCertificates(List.of("cert1", "  "));
            assertThat(violations(dto)).anyMatch(cv ->
                    cv.getMessage().equals("Certificado não pode ser vazio")
            );
        }

        @Test
        @DisplayName("certificates com item vazio → violação")
        void certificates_emptyItem_violation() {
            SignRequestDTO dto = buildValid();
            dto.setCertificates(List.of("cert1", ""));
            assertThat(violations(dto)).anyMatch(cv ->
                    cv.getMessage().equals("Certificado não pode ser vazio")
            );
        }
    }


    @Nested
    @DisplayName("campo timestamp")
    class TimestampField {

        @Test
        @DisplayName("timestamp no mínimo exato (1751328000) → sem violação")
        void timestamp_atMin_noViolation() {
            SignRequestDTO dto = buildValid();
            dto.setTimestamp(1751328000L);
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("timestamp no máximo exato (4102444800) → sem violação")
        void timestamp_atMax_noViolation() {
            SignRequestDTO dto = buildValid();
            dto.setTimestamp(4102444800L);
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("timestamp null → violação")
        void timestamp_null_violation() {
            SignRequestDTO dto = buildValid();
            dto.setTimestamp(null);
            assertViolation(dto, "timestamp", "Timestamp é obrigatório");
        }

        @Test
        @DisplayName("timestamp abaixo do mínimo (1751327999) → violação")
        void timestamp_belowMin_violation() {
            SignRequestDTO dto = buildValid();
            dto.setTimestamp(1751327999L);
            assertViolation(dto, "timestamp", "Timestamp abaixo do permitido");
        }

        @Test
        @DisplayName("timestamp acima do máximo (4102444801) → violação")
        void timestamp_aboveMax_violation() {
            SignRequestDTO dto = buildValid();
            dto.setTimestamp(4102444801L);
            assertViolation(dto, "timestamp", "Timestamp acima do permitido");
        }
    }


    @Nested
    @DisplayName("campo strategy")
    class StrategyField {

        @ParameterizedTest(name = "strategy válida: \"{0}\"")
        @ValueSource(strings = {"iat", "tsa"})
        @DisplayName("strategy válida → sem violação")
        void strategy_valid_noViolation(String strategy) {
            SignRequestDTO dto = buildValid();
            dto.setStrategy(strategy);
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("strategy null → violação")
        void strategy_null_violation() {
            SignRequestDTO dto = buildValid();
            dto.setStrategy(null);
            assertViolation(dto, "strategy", "Strategy é obrigatória");
        }

        @Test
        @DisplayName("strategy em branco → violação")
        void strategy_blank_violation() {
            SignRequestDTO dto = buildValid();
            dto.setStrategy("  ");
            assertViolation(dto, "strategy", "Strategy é obrigatória");
        }

        @ParameterizedTest(name = "strategy inválida: \"{0}\"")
        @ValueSource(strings = {"rsa", "IAT", "TSA", "invalid", "iat tsa"})
        @DisplayName("strategy fora do padrão → violação")
        void strategy_invalid_violation(String strategy) {
            SignRequestDTO dto = buildValid();
            dto.setStrategy(strategy);
            assertViolation(dto, "strategy", "Strategy deve ser 'iat' ou 'tsa'");
        }
    }


    @Nested
    @DisplayName("campo policy")
    class PolicyField {

        @ParameterizedTest(name = "policy válida: \"{0}\"")
        @ValueSource(strings = {
                "https://fhir.saude.go.gov.br|0.0.2",
                "http://example.com|1.0.0",
                "qualquer-coisa|10.20.30"
        })
        @DisplayName("policy válida → sem violação")
        void policy_valid_noViolation(String policy) {
            SignRequestDTO dto = buildValid();
            dto.setPolicy(policy);
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("policy null → violação")
        void policy_null_violation() {
            SignRequestDTO dto = buildValid();
            dto.setPolicy(null);
            assertViolation(dto, "policy", "Policy é obrigatória");
        }

        @Test
        @DisplayName("policy em branco → violação")
        void policy_blank_violation() {
            SignRequestDTO dto = buildValid();
            dto.setPolicy("  ");
            assertViolation(dto, "policy", "Policy é obrigatória");
        }

        @ParameterizedTest(name = "policy inválida: \"{0}\"")
        @ValueSource(strings = {
                "http://example.com",
                "http://example.com|",
                "http://example.com|v1",
                "http://example.com|1.0",
                "|1.0.0",
                "1.0.0"
        })
        @DisplayName("policy fora do padrão → violação")
        void policy_invalid_violation(String policy) {
            SignRequestDTO dto = buildValid();
            dto.setPolicy(policy);
            assertViolation(dto, "policy", "Policy deve seguir o formato: url|versao (ex: x|1.0.0)");
        }
    }


    private SignRequestDTO buildValid() {
        BundleDTO bundle = new BundleDTO();
        bundle.setEntries(List.of("a", "b"));

        ProvenanceDTO provenance = new ProvenanceDTO();
        provenance.setTarget(List.of("a", "b"));

        CryptoDTO crypto = new CryptoDTO();
        crypto.setPin("1234");
        crypto.setIdentifier("key1");
        crypto.setSlotId(1);
        crypto.setTokenLabel("SafeNet");

        SignRequestDTO dto = new SignRequestDTO();
        dto.setBundle(bundle);
        dto.setProvenance(provenance);
        dto.setCrypto(crypto);
        dto.setCertificates(List.of("cert1"));
        dto.setTimestamp(1751328001L);
        dto.setStrategy("iat");
        dto.setPolicy("https://fhir.saude.go.gov.br|0.0.2");
        return dto;
    }

    private Set<ConstraintViolation<SignRequestDTO>> violations(SignRequestDTO dto) {
        return validator.validate(dto);
    }

    private void assertViolation(SignRequestDTO dto, String field, String expectedMessage) {
        Set<ConstraintViolation<SignRequestDTO>> v = violations(dto);
        assertThat(v).as("Esperava violação em '%s'", field).isNotEmpty();
        assertThat(v).anyMatch(cv ->
                cv.getPropertyPath().toString().equals(field) &&
                        cv.getMessage().equals(expectedMessage)
        );
    }
}