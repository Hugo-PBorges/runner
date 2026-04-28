package com.ufg.runner.assinador.dto.sign;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProvenanceDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("target com múltiplos itens → sem violação")
    void target_withMultipleItems_noViolation() {
        ProvenanceDTO dto = new ProvenanceDTO();
        dto.setTarget(List.of("a", "b"));

        assertThat(violations(dto)).isEmpty();
    }

    @Test
    @DisplayName("target com um único item → sem violação (limite mínimo exato)")
    void target_singleItem_noViolation() {
        ProvenanceDTO dto = new ProvenanceDTO();
        dto.setTarget(List.of("a"));

        assertThat(violations(dto)).isEmpty();
    }

    @Test
    @DisplayName("target null → violação")
    void target_null_violation() {
        ProvenanceDTO dto = new ProvenanceDTO();
        dto.setTarget(null);

        assertViolation(dto, "target", "Provenance deve possuir targets");
    }

    @Test
    @DisplayName("target vazio → violação")
    void target_empty_violation() {
        ProvenanceDTO dto = new ProvenanceDTO();
        dto.setTarget(List.of());

        assertViolation(dto, "target", "Provenance deve possuir targets");
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private Set<ConstraintViolation<ProvenanceDTO>> violations(ProvenanceDTO dto) {
        return validator.validate(dto);
    }

    private void assertViolation(ProvenanceDTO dto, String field, String expectedMessage) {
        Set<ConstraintViolation<ProvenanceDTO>> v = violations(dto);
        assertThat(v).as("Esperava violação em '%s'", field).isNotEmpty();
        assertThat(v).anyMatch(cv ->
                cv.getPropertyPath().toString().equals(field) &&
                        cv.getMessage().equals(expectedMessage)
        );
    }
}