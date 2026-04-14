package com.ufg.runner.assinador.dto.validate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ValidateRequestDTO {

    @NotNull(message = "Sequência em base64 obrigatória")
    private String base64;

    @NotNull(message = "Timestamp é obrigatório")
    @Min(value = 1751328000L, message = "Timestamp abaixo do permitido")
    @Max(value = 4102444800L, message = "Timestamp acima do permitido")
    private Long timestamp;

    @NotNull(message = "Política de assinatura obrigatória")
    @Pattern(
            regexp = ".+\\|\\d+\\.\\d+\\.\\d+",
            message = "Política deve seguir o formato: url|versao (ex: x|1.0.0)"
    )
    private String policyURI;


    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPolicyURI() {
        return policyURI;
    }

    public void setPolicyURI(String policyURI) {
        this.policyURI = policyURI;
    }
}
