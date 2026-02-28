package rules;

import java.util.List;

public class FraudPipeline {
    private final List<FraudRule> rules;

    public FraudPipeline(List<FraudRule> rules) {
        if(rules==null)
            throw new IllegalArgumentException("rules can't be null");
        if(rules.contains(null))
            throw new IllegalArgumentException("Rules list should not contain null");
        this.rules = List.copyOf(rules);
    }
}
