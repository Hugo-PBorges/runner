package com.ufg.runner.assinador.dto.sign;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CryptoDTOValidationTest {

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
    @DisplayName("campo pin")
    class PinField {

        @Test
        @DisplayName("pin preenchido → sem violação")
        void pin_filled_noViolation() {
            CryptoDTO dto = buildValid();
            dto.setPin("1234");
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("pin null → violação")
        void pin_null_violation() {
            CryptoDTO dto = buildValid();
            dto.setPin(null);
            assertViolation(dto, "pin", "PIN é obrigatório");
        }

        @Test
        @DisplayName("pin vazio → violação")
        void pin_empty_violation() {
            CryptoDTO dto = buildValid();
            dto.setPin("");
            assertViolation(dto, "pin", "PIN é obrigatório");
        }

        @Test
        @DisplayName("pin só com espaços → violação")
        void pin_blank_violation() {
            CryptoDTO dto = buildValid();
            dto.setPin("   ");
            assertViolation(dto, "pin", "PIN é obrigatório");
        }
    }


    @Nested
    @DisplayName("campo identifier")
    class IdentifierField {

        @Test
        @DisplayName("identifier preenchido → sem violação")
        void identifier_filled_noViolation() {
            CryptoDTO dto = buildValid();
            dto.setIdentifier("key1");
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("identifier null → violação")
        void identifier_null_violation() {
            CryptoDTO dto = buildValid();
            dto.setIdentifier(null);
            assertViolation(dto, "identifier", "Identifier é obrigatório");
        }

        @Test
        @DisplayName("identifier vazio → violação")
        void identifier_empty_violation() {
            CryptoDTO dto = buildValid();
            dto.setIdentifier("");
            assertViolation(dto, "identifier", "Identifier é obrigatório");
        }

        @Test
        @DisplayName("identifier só com espaços → violação")
        void identifier_blank_violation() {
            CryptoDTO dto = buildValid();
            dto.setIdentifier("   ");
            assertViolation(dto, "identifier", "Identifier é obrigatório");
        }
    }


    @Nested
    @DisplayName("campo slotId")
    class SlotIdField {

        @Test
        @DisplayName("slotId zero → sem violação (limite mínimo exato)")
        void slotId_zero_noViolation() {
            CryptoDTO dto = buildValid();
            dto.setSlotId(0);
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("slotId positivo → sem violação")
        void slotId_positive_noViolation() {
            CryptoDTO dto = buildValid();
            dto.setSlotId(5);
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("slotId negativo → violação")
        void slotId_negative_violation() {
            CryptoDTO dto = buildValid();
            dto.setSlotId(-1);
            assertViolation(dto, "slotId", "SlotId não pode ser negativo");
        }

        @Test
        @DisplayName("slotId null → sem violação (@Min não se aplica a null)")
        void slotId_null_noViolation() {

            CryptoDTO dto = buildValid();
            dto.setSlotId(null);
            assertThat(violations(dto)).isEmpty();
        }
    }

    @Nested
    @DisplayName("campo tokenLabel")
    class TokenLabelField {

        @Test
        @DisplayName("tokenLabel null → sem violação (@Size permite null)")
        void tokenLabel_null_noViolation() {
            // @Size não rejeita null — adicionar @NotNull se for obrigatório
            CryptoDTO dto = buildValid();
            dto.setTokenLabel(null);
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("tokenLabel vazio → sem violação")
        void tokenLabel_empty_noViolation() {
            CryptoDTO dto = buildValid();
            dto.setTokenLabel("");
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("tokenLabel com 32 caracteres → sem violação (limite máximo exato)")
        void tokenLabel_32chars_noViolation() {
            CryptoDTO dto = buildValid();
            dto.setTokenLabel("A".repeat(32));
            assertThat(violations(dto)).isEmpty();
        }

        @Test
        @DisplayName("tokenLabel com 33 caracteres → violação")
        void tokenLabel_33chars_violation() {
            CryptoDTO dto = buildValid();
            dto.setTokenLabel("A".repeat(33));
            assertViolation(dto, "tokenLabel", "TokenLabel deve ter no máximo 32 caracteres");
        }

        @Test
        @DisplayName("tokenLabel com 1 caractere → sem violação")
        void tokenLabel_1char_noViolation() {
            CryptoDTO dto = buildValid();
            dto.setTokenLabel("A");
            assertThat(violations(dto)).isEmpty();
        }
    }


    private CryptoDTO buildValid() {
        CryptoDTO dto = new CryptoDTO();
        dto.setPin("1234");
        dto.setIdentifier("key1");
        dto.setSlotId(1);
        dto.setTokenLabel("SafeNet");
        return dto;
    }

    private Set<ConstraintViolation<CryptoDTO>> violations(CryptoDTO dto) {
        return validator.validate(dto);
    }

    private void assertViolation(CryptoDTO dto, String field, String expectedMessage) {
        Set<ConstraintViolation<CryptoDTO>> v = violations(dto);
        assertThat(v).as("Esperava violação em '%s'", field).isNotEmpty();
        assertThat(v).anyMatch(cv ->
                cv.getPropertyPath().toString().equals(field) &&
                        cv.getMessage().equals(expectedMessage)
        );
    }
}