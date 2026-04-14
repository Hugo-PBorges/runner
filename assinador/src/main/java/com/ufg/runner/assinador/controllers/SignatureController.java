package com.ufg.runner.assinador.controllers;

import com.ufg.runner.assinador.dto.outcome.OperationOutcomeDTO;
import com.ufg.runner.assinador.dto.sign.SignRequestDTO;
import com.ufg.runner.assinador.dto.validate.ValidateRequestDTO;
import com.ufg.runner.assinador.services.SignatureService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/signature")
public class SignatureController {

    private final SignatureService signatureService;

    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @PostMapping("/sign")
    public ResponseEntity<OperationOutcomeDTO> sign(
            @Valid @RequestBody SignRequestDTO request
    ) {

        OperationOutcomeDTO response = signatureService.sign(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<OperationOutcomeDTO> validate(
            @Valid @RequestBody ValidateRequestDTO request
    ) {

        OperationOutcomeDTO response = signatureService.validate(request);

        return ResponseEntity.ok(response);
    }
}