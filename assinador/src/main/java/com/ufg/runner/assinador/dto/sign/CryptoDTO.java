package com.ufg.runner.assinador.dto.sign;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CryptoDTO {

    @NotBlank(message = "PIN é obrigatório")
    private String pin;

    @NotBlank(message = "Identifier é obrigatório")
    private String identifier;

    @Min(value = 0, message = "SlotId não pode ser negativo")
    private Integer slotId;

    @Size(max = 32, message = "TokenLabel deve ter no máximo 32 caracteres")
    private String tokenLabel;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public String getTokenLabel() {
        return tokenLabel;
    }

    public void setTokenLabel(String tokenLabel) {
        this.tokenLabel = tokenLabel;
    }
}