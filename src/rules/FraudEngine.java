package rules;

import domain.transaction.Transaction;

import java.util.List;

public class FraudEngine{
    private final List<FraudRule> rules;

    public FraudEngine(List<FraudRule> rules) {
        if(rules.isEmpty())
            throw new IllegalArgumentException("Rules cant  be null");
        if(rules.contains(null))
            throw new IllegalArgumentException("Rules list contains null");
        this.rules = List.copyOf(rules);
    }

    public RuleResult assess(Transaction tx,FraudContext ctx){
        if(tx==null)
            throw new IllegalArgumentException("Transaction cant be null");
        if(ctx==null)
            throw new IllegalArgumentException("FraudContext cant be null");
        RuleResult best=RuleResult.allow();

        for(FraudRule rule:rules){
            RuleResult result=rule.evaluate(tx,ctx);

            if(result.getDecision()==Decision.BLOCK)
                return result;
            if(result.getDecision()==Decision.REVIEW && result.getDecision()==Decision.ALLOW)
                best=result;
        }

        return  best;
    }
}
