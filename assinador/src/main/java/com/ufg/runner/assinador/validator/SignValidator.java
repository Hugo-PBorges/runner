package com.ufg.runner.assinador.validator;

import com.ufg.runner.assinador.dto.sign.SignRequestDTO;
import com.ufg.runner.assinador.exception.BusinessValidationException;
import org.springframework.stereotype.Component;

@Component
public class SignValidator {

    public void validate(SignRequestDTO req) {

        int bundleSize = req.getBundle().getEntries().size();
        int targetSize = req.getProvenance().getTarget().size();

        if (bundleSize != targetSize) {
            throw new BusinessValidationException(
                    "VALIDATION.TARGET-MISMATCH",
                    "Quantidade de targets diferente do bundle"
            );
        }
    }
}
