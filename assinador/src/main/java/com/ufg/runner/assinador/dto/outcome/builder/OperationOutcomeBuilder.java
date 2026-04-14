package com.ufg.runner.assinador.dto.outcome.builder;

import com.ufg.runner.assinador.dto.outcome.IssueDTO;
import com.ufg.runner.assinador.dto.outcome.OperationOutcomeDTO;

import java.util.List;

public class OperationOutcomeBuilder {

    public static OperationOutcomeDTO error(String message) {

        IssueDTO issue = new IssueDTO();
        issue.setSeverity("error");
        issue.setCode("invalid");
        issue.setDiagnostics(message);

        OperationOutcomeDTO outcome = new OperationOutcomeDTO();
        outcome.setIssue(List.of(issue));

        return outcome;
    }
}