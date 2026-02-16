package com.acc.local.dto.project.decision;

public record DecisionApplyInfo(
   boolean isApplied,
   String projectId,
   String reason
) {
    public static DecisionApplyInfo success(String succeedProjectId) {
        return new DecisionApplyInfo(true, succeedProjectId,null);
    }

    public static DecisionApplyInfo fail(String reason) {
        return new DecisionApplyInfo(false, null, reason);
    }
}
