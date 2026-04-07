package com.ufg.runner.assinador.dto.sign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class SignRequestDTO {

    @NotNull(message = "Bundle é obrigatório")
    @Valid
    private BundleDTO bundle;

    @NotNull(message = "Provenance é obrigatório")
    @Valid
    private ProvenanceDTO provenance;

    @NotNull(message = "Crypto é obrigatório")
    @Valid
    private CryptoDTO crypto;

    @NotEmpty(message = "Certificates é obrigatório")
    private List<
            @NotBlank(message = "Certificado não pode ser vazio")
                    String
            > certificates;

    @NotNull(message = "Timestamp é obrigatório")
    @Min(value = 1751328000L, message = "Timestamp abaixo do permitido")
    @Max(value = 4102444800L, message = "Timestamp acima do permitido")
    private Long timestamp;

    @NotBlank(message = "Strategy é obrigatória")
    @Pattern(
            regexp = "iat|tsa",
            message = "Strategy deve ser 'iat' ou 'tsa'"
    )
    private String strategy;

    @NotBlank(message = "Policy é obrigatória")
    @Pattern(
            regexp = ".+\\|\\d+\\.\\d+\\.\\d+",
            message = "Policy deve seguir o formato: url|versao (ex: x|1.0.0)"
    )
    private String policy;

    public BundleDTO getBundle() {
        return bundle;
    }

    public void setBundle(BundleDTO bundle) {
        this.bundle = bundle;
    }

    public ProvenanceDTO getProvenance() {
        return provenance;
    }

    public void setProvenance(ProvenanceDTO provenance) {
        this.provenance = provenance;
    }

    public CryptoDTO getCrypto() {
        return crypto;
    }

    public void setCrypto(CryptoDTO crypto) {
        this.crypto = crypto;
    }

    public List<String> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<String> certificates) {
        this.certificates = certificates;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

}