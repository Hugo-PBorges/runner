package com.ufg.runner.assinador.service;

import com.ufg.runner.assinador.config.Pkcs11Properties;
import com.ufg.runner.assinador.dto.outcome.OperationOutcomeDTO;
import com.ufg.runner.assinador.dto.sign.BundleDTO;
import com.ufg.runner.assinador.dto.sign.CryptoDTO;
import com.ufg.runner.assinador.dto.sign.ProvenanceDTO;
import com.ufg.runner.assinador.dto.sign.SignRequestDTO;
import com.ufg.runner.assinador.dto.validate.ValidateRequestDTO;
import com.ufg.runner.assinador.exception.BusinessValidationException;
import com.ufg.runner.assinador.exception.CryptographicException;
import com.ufg.runner.assinador.validator.SignValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Pkcs11SignatureServiceTest {

    private Pkcs11SignatureService service;

    @BeforeEach
    void setUp() {
        Pkcs11Properties properties = new Pkcs11Properties();
        properties.setLibraryPath("/caminho/inexistente/libsofthsm2.so");
        service = new Pkcs11SignatureService(new SignValidator(), properties);
    }

    @Test
    @DisplayName("validate com base64 válido → SIGNATURE_VALID")
    void validate_validBase64_returnsSignatureValid() {
        ValidateRequestDTO request = new ValidateRequestDTO();
        request.setBase64(Base64.getEncoder().encodeToString("conteudo".getBytes()));
        request.setTimestamp(1751328001L);
        request.setPolicyURI("http://example.com|1.0.0");

        OperationOutcomeDTO outcome = service.validate(request);

        assertThat(outcome.getIssue()).hasSize(1);
        assertThat(outcome.getIssue().get(0).getSeverity()).isEqualTo("information");
        assertThat(outcome.getIssue().get(0).getCode()).isEqualTo("SIGNATURE_VALID");
    }

    @Test
    @DisplayName("validate com base64 inválido → BusinessValidationException")
    void validate_invalidBase64_throwsBusinessValidationException() {
        ValidateRequestDTO request = new ValidateRequestDTO();
        request.setBase64("não-é-base64!!");
        request.setTimestamp(1751328001L);
        request.setPolicyURI("http://example.com|1.0.0");

        assertThatThrownBy(() -> service.validate(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Base64 inválido");
    }

    @Test
    @DisplayName("sign com bundle/provenance de tamanhos diferentes → BusinessValidationException")
    void sign_targetMismatch_throwsBusinessValidationException() {
        SignRequestDTO request = buildRequest(List.of("a", "b"), List.of("a"));

        assertThatThrownBy(() -> service.sign(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessage("Quantidade de targets diferente do bundle");
    }

    @Test
    @DisplayName("sign com biblioteca PKCS11 inexistente → CryptographicException")
    void sign_invalidPkcs11Library_throwsCryptographicException() {
        SignRequestDTO request = buildRequest(List.of("a", "b"), List.of("a", "b"));

        assertThatThrownBy(() -> service.sign(request))
                .isInstanceOf(CryptographicException.class)
                .hasMessageStartingWith("Erro ao acessar dispositivo criptográfico");
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
