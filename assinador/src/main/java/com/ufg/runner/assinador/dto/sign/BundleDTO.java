package com.ufg.runner.assinador.dto.sign;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class BundleDTO {
    @NotEmpty(message = "Bundle deve possuir entries")
    private List<String> entries;

    public List<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }
}
