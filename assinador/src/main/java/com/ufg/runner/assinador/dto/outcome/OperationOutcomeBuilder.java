package com.ufg.runner.assinador.dto.outcome;

import java.util.List;

public class OperationOutcomeBuilder {

    private static final String SEVERITY_ERROR = "error";
    private static final String SEVERITY_INFORMATION = "information";
    private static final String DEFAULT_ERROR_CODE = "invalid";

    private OperationOutcomeBuilder() {
    }

    public static OperationOutcomeDTO error(String message) {
        return error(DEFAULT_ERROR_CODE, message);
    }

    public static OperationOutcomeDTO error(String code, String message) {
        return build(SEVERITY_ERROR, code, message);
    }

    public static OperationOutcomeDTO information(String code, String diagnostics) {
        return build(SEVERITY_INFORMATION, code, diagnostics);
    }

    private static OperationOutcomeDTO build(String severity, String code, String diagnostics) {
        IssueDTO issue = new IssueDTO();
        issue.setSeverity(severity);
        issue.setCode(code);
        issue.setDiagnostics(diagnostics);

        OperationOutcomeDTO outcome = new OperationOutcomeDTO();
        outcome.setIssue(List.of(issue));

        return outcome;
    }
}
