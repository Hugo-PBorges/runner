package com.ufg.runner.assinador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufg.runner.assinador.dto.outcome.IssueDTO;
import com.ufg.runner.assinador.dto.outcome.OperationOutcomeDTO;
import com.ufg.runner.assinador.dto.sign.SignRequestDTO;
import com.ufg.runner.assinador.dto.validate.ValidateRequestDTO;
import com.ufg.runner.assinador.services.SignatureService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CliRunner implements CommandLineRunner {

    private final SignatureService signatureService;
    private final ObjectMapper objectMapper;

    public CliRunner(SignatureService signatureService) {
        this.signatureService = signatureService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {

            return;
        }

        String command = args[0];

        try {
            if ("sign".equals(command)) {

                String inputPath = extractArg(args, "--input");

                if (inputPath == null) {
                    System.err.println("Erro: --input é obrigatório");
                    System.exit(1);
                }

                SignRequestDTO request = objectMapper.readValue(
                        new File(inputPath),
                        SignRequestDTO.class
                );

                OperationOutcomeDTO result = signatureService.sign(request);

                print(result);
                
                System.exit(0);

            } else if ("validate".equals(command)) {

                String inputPath = extractArg(args, "--input");

                if (inputPath == null) {
                    System.err.println("Erro: --input é obrigatório para validate");
                    System.exit(1);
                }

                ValidateRequestDTO request = objectMapper.readValue(
                        new File(inputPath),
                        ValidateRequestDTO.class
                );

                OperationOutcomeDTO result = signatureService.validate(request);

                print(result);
                System.exit(0);

            } else {
                System.err.println("Comando inválido. Use: sign ou validate");
                System.exit(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
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