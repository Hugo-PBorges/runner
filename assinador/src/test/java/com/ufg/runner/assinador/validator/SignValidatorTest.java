package com.ufg.runner.assinador.validator;

import com.ufg.runner.assinador.dto.sign.BundleDTO;
import com.ufg.runner.assinador.dto.sign.CryptoDTO;
import com.ufg.runner.assinador.dto.sign.ProvenanceDTO;
import com.ufg.runner.assinador.dto.sign.SignRequestDTO;
import com.ufg.runner.assinador.exception.BusinessValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

class SignValidatorTest {

    private final SignValidator validator = new SignValidator();

    @Test
    @DisplayName("bundle e provenance com mesmo tamanho → não lança exceção")
    void sameSize_doesNotThrow() {
        SignRequestDTO request = buildRequest(List.of("a", "b"), List.of("a", "b"));

        assertThatNoException().isThrownBy(() -> validator.validate(request));
    }

    @Test
    @DisplayName("bundle e provenance com tamanhos diferentes → lança BusinessValidationException")
    void differentSize_throwsBusinessValidationException() {
        SignRequestDTO request = buildRequest(List.of("a", "b"), List.of("a"));

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Quantidade de targets diferente do bundle")
                .satisfies(e -> assertThat(((BusinessValidationException) e).getCode())
                        .isEqualTo("VALIDATION.TARGET-MISMATCH"));
    }

    private SignRequestDTO buildRequest(List<String> entries, List<String> targets) {
        BundleDTO bundle = new BundleDTO();
        bundle.setEntries(entries);

        ProvenanceDTO provenance = new ProvenanceDTO();
        provenance.setTarget(targets);

        CryptoDTO crypto = new CryptoDTO();
        crypto.setPin("1234");
        crypto.setIdentifier("key1");
        crypto.setSlotId(1);

        SignRequestDTO request = new SignRequestDTO();
        request.setBundle(bundle);
        request.setProvenance(provenance);
        request.setCrypto(crypto);
        request.setCertificates(List.of("cert1"));
        request.setTimestamp(1751328001L);
        request.setStrategy("iat");
        request.setPolicy("https://fhir.saude.go.gov.br|0.0.2");
        return request;
    }
}
