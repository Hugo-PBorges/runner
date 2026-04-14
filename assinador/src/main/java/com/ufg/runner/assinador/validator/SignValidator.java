package com.ufg.runner.assinador.validator;

import com.ufg.runner.assinador.dto.outcome.IssueDTO;
import com.ufg.runner.assinador.dto.outcome.OperationOutcomeDTO;
import com.ufg.runner.assinador.dto.sign.SignRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SignValidator {

    public OperationOutcomeDTO validate(SignRequestDTO req) {

        int bundleSize = req.getBundle().getEntries().size();
        int targetSize = req.getProvenance().getTarget().size();

        if (bundleSize != targetSize) {

            IssueDTO issue = new IssueDTO();
            issue.setSeverity("error");
            issue.setCode("VALIDATION.TARGET-MISMATCH");
            issue.setDiagnostics("Quantidade de targets diferente do bundle");

            OperationOutcomeDTO outcome = new OperationOutcomeDTO();
            outcome.setIssue(List.of(issue));

            return outcome;
        }

        return null;
    }
}