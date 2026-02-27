package rules.impl;

import rules.Decision;

public class DailySpendLimitRule {
    private final Decision decisionOnHit;

    public DailySpendLimitRule(Decision decisionOnHit) {
        if(decisionOnHit==null)
            throw new IllegalArgumentException("DecisionOnHit cant be null");
        if(decisionOnHit==Decision.ALLOW)
            throw new IllegalArgumentException("DecisionOnHit cant be Allow");

        this.decisionOnHit = decisionOnHit;
    }
}
