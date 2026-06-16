package com.ufg.runner.assinador.service;

import com.ufg.runner.assinador.dto.outcome.OperationOutcomeDTO;
import com.ufg.runner.assinador.dto.sign.SignRequestDTO;
import com.ufg.runner.assinador.dto.validate.ValidateRequestDTO;

public interface SignatureService {
    OperationOutcomeDTO sign(SignRequestDTO request);

    OperationOutcomeDTO validate(ValidateRequestDTO request);
}
