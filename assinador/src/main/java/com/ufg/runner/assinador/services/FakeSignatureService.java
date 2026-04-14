package com.ufg.runner.assinador.services;

import com.ufg.runner.assinador.dto.outcome.IssueDTO;
import com.ufg.runner.assinador.dto.outcome.OperationOutcomeDTO;
import com.ufg.runner.assinador.dto.outcome.builder.OperationOutcomeBuilder;
import com.ufg.runner.assinador.dto.sign.SignRequestDTO;
import com.ufg.runner.assinador.dto.validate.ValidateRequestDTO;
import com.ufg.runner.assinador.validator.SignValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Service
public class FakeSignatureService implements SignatureService {

    private final Validator validator;
    private final SignValidator signValidator;

    public FakeSignatureService(Validator validator, SignValidator signValidator) {
        this.validator = validator;
        this.signValidator = signValidator;
    }

    @Override
    public OperationOutcomeDTO sign(SignRequestDTO request) {

        Set<ConstraintViolation<SignRequestDTO>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            ConstraintViolation<?> v = violations.iterator().next();

            return OperationOutcomeBuilder.error(
                    v.getPropertyPath() + " " + v.getMessage()
            );
        }

        OperationOutcomeDTO businessError = signValidator.validate(request);

        if (businessError != null) {
            return businessError;
        }

        String header = Base64.getEncoder().encodeToString(
                "{\"alg\":\"RS256\"}".getBytes()
        );

        String payload = Base64.getEncoder().encodeToString(
                ("{\"bundleSize\":\"" + request.getBundle().getEntries().size() + "\"}")
                        .getBytes()
        );

        String signature = Base64.getEncoder().encodeToString(
                "fake-signature".getBytes()
        );

        String jws = header + "." + payload + "." + signature;

        IssueDTO issue = new IssueDTO();
        issue.setSeverity("information");
        issue.setCode("SIGNATURE_CREATED");
        issue.setDiagnostics(jws);

        OperationOutcomeDTO outcome = new OperationOutcomeDTO();
        outcome.setIssue(List.of(issue));

        return outcome;
    }

    @Override
    public OperationOutcomeDTO validate(ValidateRequestDTO request) {
        Set<ConstraintViolation<ValidateRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()){
            ConstraintViolation<?> v = violations.iterator().next();
            return OperationOutcomeBuilder.error(
                    v.getPropertyPath() + " " + v.getMessage()
            );
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(request.getBase64());

            IssueDTO issue = new IssueDTO();
            issue.setSeverity("information");
            issue.setCode("SIGNATURE_VALID");
            issue.setDiagnostics("Assinatura válida com sucesso");

            OperationOutcomeDTO outcome = new OperationOutcomeDTO();
            outcome.setIssue(List.of(issue));

            return outcome;
        } catch (IllegalArgumentException e){
            return OperationOutcomeBuilder.error("Base64 inválido");
        }
    }
}