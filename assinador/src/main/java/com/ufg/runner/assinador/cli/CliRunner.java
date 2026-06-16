package com.ufg.runner.assinador.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufg.runner.assinador.dto.outcome.IssueDTO;
import com.ufg.runner.assinador.dto.outcome.OperationOutcomeBuilder;
import com.ufg.runner.assinador.dto.outcome.OperationOutcomeDTO;
import com.ufg.runner.assinador.dto.sign.SignRequestDTO;
import com.ufg.runner.assinador.dto.validate.ValidateRequestDTO;
import com.ufg.runner.assinador.exception.BusinessValidationException;
import com.ufg.runner.assinador.exception.CryptographicException;
import com.ufg.runner.assinador.service.SignatureService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Set;

@Component
public class CliRunner implements CommandLineRunner {

    private final SignatureService signatureService;
    private final Validator validator;
    private final ObjectMapper objectMapper;

    public CliRunner(SignatureService signatureService, Validator validator) {
        this.signatureService = signatureService;
        this.validator = validator;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            return;
        }

        String command = args[0];

        try {
            switch (command) {
                case "sign" -> runSign(args);
                case "validate" -> runValidate(args);
                default -> {
                    System.err.println("Comando inválido. Use: sign ou validate");
                    System.exit(1);
                }
            }
        } catch (BusinessValidationException | CryptographicException e) {
            print(OperationOutcomeBuilder.error(e.getMessage()));
            System.exit(1);
        } catch (Exception e) {
            print(OperationOutcomeBuilder.error("Erro inesperado: " + e.getMessage()));
            System.exit(1);
        }
    }

    private void runSign(String[] args) throws Exception {
        String inputPath = requireInput(args);

        SignRequestDTO request = objectMapper.readValue(new File(inputPath), SignRequestDTO.class);

        Set<ConstraintViolation<SignRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            print(toOutcome(violations));
            System.exit(1);
            return;
        }

        OperationOutcomeDTO result = signatureService.sign(request);
        print(result);
        System.exit(0);
    }

    private void runValidate(String[] args) throws Exception {
        String inputPath = requireInput(args);

        ValidateRequestDTO request = objectMapper.readValue(new File(inputPath), ValidateRequestDTO.class);

        Set<ConstraintViolation<ValidateRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            print(toOutcome(violations));
            System.exit(1);
            return;
        }

        OperationOutcomeDTO result = signatureService.validate(request);
        print(result);
        System.exit(0);
    }

    private String requireInput(String[] args) {
        String inputPath = extractArg(args, "--input");

        if (inputPath == null) {
            System.err.println("Erro: --input é obrigatório");
            System.exit(1);
        }

        return inputPath;
    }

    private <T> OperationOutcomeDTO toOutcome(Set<ConstraintViolation<T>> violations) {
        ConstraintViolation<?> v = violations.iterator().next();
        return OperationOutcomeBuilder.error(v.getPropertyPath() + " " + v.getMessage());
    }

    private void print(OperationOutcomeDTO outcome) {
        System.out.println("resourceType: " + outcome.getResourceType());

        for (IssueDTO issue : outcome.getIssue()) {
            System.out.println(issue.getSeverity() + " - " +
                    issue.getCode() + ": " +
                    issue.getDiagnostics());
        }
    }

    private String extractArg(String[] args, String key) {
        for (String arg : args) {
            if (arg.startsWith(key + "=")) {
                return arg.substring((key + "=").length());
            }
        }
        return null;
    }
}
