package com.ufg.runner.assinador.dto.outcome;

import java.util.List;

public class OperationOutcomeDTO {

    private String resourceType = "OperationOutcome";
    private List<IssueDTO> issue;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public List<IssueDTO> getIssue() {
        return issue;
    }

    public void setIssue(List<IssueDTO> issue) {
        this.issue = issue;
    }
}
