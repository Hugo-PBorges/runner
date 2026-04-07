package com.ufg.runner.assinador.dto.sign;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class BundleDTO {
    @NotEmpty(message = "Bundle deve possuir entries")
    private List<Object> entries;

    public List<Object> getEntries() {
        return entries;
    }

    public void setEntries(List<Object> entries) {
        this.entries = entries;
    }
}
