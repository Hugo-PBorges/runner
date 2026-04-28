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

class BundleDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("entries com itens → sem violação")
    void entries_withItems_noViolation() {
        BundleDTO dto = new BundleDTO();
        dto.setEntries(List.of("a", "b"));

        assertThat(violations(dto)).isEmpty();
    }

    @Test
    @DisplayName("entries com um único item → sem violação")
    void entries_singleItem_noViolation() {
        BundleDTO dto = new BundleDTO();
        dto.setEntries(List.of("a"));

        assertThat(violations(dto)).isEmpty();
    }

    @Test
    @DisplayName("entries null → violação")
    void entries_null_violation() {
        BundleDTO dto = new BundleDTO();
        dto.setEntries(null);

        assertViolation(dto, "entries", "Bundle deve possuir entries");
    }

    @Test
    @DisplayName("entries vazio → violação")
    void entries_empty_violation() {
        BundleDTO dto = new BundleDTO();
        dto.setEntries(List.of());

        assertViolation(dto, "entries", "Bundle deve possuir entries");
    }

    private Set<ConstraintViolation<BundleDTO>> violations(BundleDTO dto) {
        return validator.validate(dto);
    }

    private void assertViolation(BundleDTO dto, String field, String expectedMessage) {
        Set<ConstraintViolation<BundleDTO>> v = violations(dto);
        assertThat(v).as("Esperava violação em '%s'", field).isNotEmpty();
        assertThat(v).anyMatch(cv ->
                cv.getPropertyPath().toString().equals(field) &&
                        cv.getMessage().equals(expectedMessage)
        );
    }
}