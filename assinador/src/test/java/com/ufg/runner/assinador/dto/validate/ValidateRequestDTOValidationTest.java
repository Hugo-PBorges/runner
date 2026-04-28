package com.ufg.runner.assinador.dto.validate;

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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ValidateRequestDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("DTO válido não gera violações")
    void validDto_noViolations() {
        assertThat(violations(buildValid())).isEmpty();
    }

    @Nested
    @DisplayName("campo base64")
    class Base64Field {

        @Test
        @DisplayName("null → violação")
        void nullBase64_violation() {
            ValidateRequestDTO dto = buildValid();
            dto.setBase64(null);
            assertViolation(dto, "base64");
        }

        @Test
        @DisplayName("string vazia → sem violação (apenas @NotNull está presente)")
        void emptyString_noViolation() {
            // O DTO só tem @NotNull, portanto string vazia é válida segundo as constraints atuais
            ValidateRequestDTO dto = buildValid();
            dto.setBase64("");
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("string com conteúdo qualquer → sem violação")
        void anyString_noViolation() {
            ValidateRequestDTO dto = buildValid();
            dto.setBase64("qualquer-coisa");
            assertThat(violations(dto)).isEmpty();
        }
    }

    @Nested
    @DisplayName("campo timestamp")
    class TimestampField {

        @Test
        @DisplayName("null → violação")
        void nullTimestamp_violation() {
            ValidateRequestDTO dto = buildValid();
            dto.setTimestamp(null);
            assertViolation(dto, "timestamp");
        }

        @Test
        @DisplayName("valor mínimo exato (1751328000) → sem violação")
        void timestampAtMin_noViolation() {
            ValidateRequestDTO dto = buildValid();
            dto.setTimestamp(1751328000L);
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("valor máximo exato (4102444800) → sem violação")
        void timestampAtMax_noViolation() {
            ValidateRequestDTO dto = buildValid();
            dto.setTimestamp(4102444800L);
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("um abaixo do mínimo (1751327999) → violação")
        void timestampBelowMin_violation() {
            ValidateRequestDTO dto = buildValid();
            dto.setTimestamp(1751327999L);
            assertViolation(dto, "timestamp");
        }

        @Test
        @DisplayName("um acima do máximo (4102444801) → violação")
        void timestampAboveMax_violation() {
            ValidateRequestDTO dto = buildValid();
            dto.setTimestamp(4102444801L);
            assertViolation(dto, "timestamp");
        }

        @Test
        @DisplayName("valor zero → violação")
        void timestampZero_violation() {
            ValidateRequestDTO dto = buildValid();
            dto.setTimestamp(0L);
            assertViolation(dto, "timestamp");
        }

        @Test
        @DisplayName("valor negativo → violação")
        void timestampNegative_violation() {
            ValidateRequestDTO dto = buildValid();
            dto.setTimestamp(-1L);
            assertViolation(dto, "timestamp");
        }
    }

    @Nested
    @DisplayName("campo policyURI")
    class PolicyURIField {

        @Test
        @DisplayName("null → violação")
        void nullPolicy_violation() {
            ValidateRequestDTO dto = buildValid();
            dto.setPolicyURI(null);
            assertViolation(dto, "policyURI");
        }

        @ParameterizedTest(name = "inválida: \"{0}\"")
        @ValueSource(strings = {
                "",
                " ",
                "http://exemplo.com",
                "http://exemplo.com|",
                "http://exemplo.com|v1",
                "http://exemplo.com|1",
                "http://exemplo.com|1.0",
                "|1.0.0",
                "1.0.0",
        })
        @DisplayName("formato inválido → violação")
        void invalidPolicyFormat_violation(String policy) {
            ValidateRequestDTO dto = buildValid();
            dto.setPolicyURI(policy);
            assertThat(violations(dto))
                    .as("policyURI '%s' deveria gerar violação", policy)
                    .isNotEmpty();
        }

        @ParameterizedTest(name = "válida: \"{0}\"")
        @ValueSource(strings = {
                "http://example.com|1.0.0",
                "https://fhir.saude.go.gov.br|0.0.2",
                "ftp://qualquer|10.20.30",
                "qualquer-coisa|1.2.3",
        })
        @DisplayName("formato válido → sem violação")
        void validPolicyFormat_noViolation(String policy) {
            ValidateRequestDTO dto = buildValid();
            dto.setPolicyURI(policy);
            assertThat(violations(dto))
                    .as("policyURI '%s' não deveria gerar violação", policy)
                    .isEmpty();
        }
    }


    private ValidateRequestDTO buildValid() {
        ValidateRequestDTO dto = new ValidateRequestDTO();
        dto.setBase64("ZXlKaGJHY2lPaUpTVXpJMU5pSjkuZXlKaWRXNWtiR1ZVWlhOMElqb2lNaUo5LmZha2Utc2lnbmF0dXJl");
        dto.setTimestamp(1751328000L);
        dto.setPolicyURI("http://example.com|1.0.0");
        return dto;
    }

    private Set<ConstraintViolation<ValidateRequestDTO>> violations(ValidateRequestDTO dto) {
        return validator.validate(dto);
    }

    private void assertViolation(ValidateRequestDTO dto, String field) {
        Set<ConstraintViolation<ValidateRequestDTO>> v = violations(dto);
        assertThat(v).as("Esperava violação em '%s'", field).isNotEmpty();
        assertThat(v).anyMatch(cv -> cv.getPropertyPath().toString().equals(field));
    }
}
