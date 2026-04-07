package com.ufg.runner.assinador.dto.sign;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ProvenanceDTO {

    @NotEmpty(message = "Provenance deve possuir targets")
    private List<String> target;

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }
}
