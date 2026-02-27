package rules.impl;

import rules.Decision;
import rules.RuleResult;

import java.time.Duration;

public class RapidLocationChangeRule {
    private Duration maxTimeBetween;
    private final Decision decisionOnHit;

    public RapidLocationChangeRule(Duration maxTimeBetween, Decision decisionOnHit) {
        if(maxTimeBetween==null)
            throw new IllegalArgumentException("maxTimeBetween cant be null");
        if(decisionOnHit==null)
            throw new IllegalArgumentException("decisionOnHit cant be null");
        if(decisionOnHit==Decision.ALLOW)
            throw new IllegalArgumentException("decisionOnHit cant be allow");
        if(!maxTimeBetween.isPositive())
            throw new IllegalArgumentException("maxTimeBetween must be positive");

        this.maxTimeBetween = maxTimeBetween;
        this.decisionOnHit = decisionOnHit;
    }

    public Duration getMaxTimeBetween() {
        return maxTimeBetween;
    }

    public Decision getDecisionOnHit() {
        return decisionOnHit;
    }
}
