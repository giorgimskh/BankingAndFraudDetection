package rules;

import domain.transaction.Transaction;

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


    public RuleResult evaluate(Transaction tx, FraudContext ctx){
        if(tx==null)
            throw new IllegalArgumentException("Transaction cant be null");
        if(ctx==null)
            throw new IllegalArgumentException("FraudContext cant be null");

        Decision strongest=Decision.ALLOW;
        String message=null;

        for(FraudRule rule : rules){
            RuleResult result=rule.evaluate(tx,ctx);
            if(result.getDecision()==Decision.BLOCK)
                return result;
            if(result.getDecision()==Decision.REVIEW){
               if(strongest==Decision.ALLOW){
                   strongest=Decision.REVIEW;
                   message=result.getReason();
               }
            }
        }

        return strongest==Decision.REVIEW ? RuleResult.review(message):RuleResult.allow();
    }
}
