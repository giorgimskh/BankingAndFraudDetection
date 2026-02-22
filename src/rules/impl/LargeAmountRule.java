package rules.impl;

import domain.transaction.Transaction;
import rules.Decision;
import rules.FraudContext;
import rules.FraudRule;
import rules.RuleResult;
import util.Money;

public class LargeAmountRule implements FraudRule {
    private final Money threshold;
    private final Decision decisionOnHit;

    public LargeAmountRule(Money threshold, Decision decisionOnHit) {
        if(threshold==null)
            throw new IllegalArgumentException("Threshold cant be Null");
        if(!threshold.isPositive())
            throw new IllegalArgumentException("Threshold must be positive");
        if(decisionOnHit==null)
            throw new IllegalArgumentException("DecisionOnHit cant be Null");
        if(decisionOnHit==Decision.ALLOW)
            throw new IllegalArgumentException("DecisionOnHit should not be Allow");

        this.threshold = threshold;
        this.decisionOnHit = decisionOnHit;
    }


    @Override
    public RuleResult evaluate(Transaction tx, FraudContext ctx) {
        if(tx==null)
            throw new IllegalArgumentException("Transaction cant be Null");
        if(ctx==null)
            throw new IllegalArgumentException("FraudContext cant be Null");
        if(tx.getAmount().getCurrency()!=threshold.getCurrency())
            return RuleResult.allow();
        if(tx.getAmount().compareTo(threshold)>0){
            String msg = "Large Amount: " + tx.getAmount() + " > " + threshold;
            return (decisionOnHit == Decision.BLOCK) ? RuleResult.block(msg) : RuleResult.review(msg);
        }
        return RuleResult.allow();
    }

    public Money getThreshold() {
        return threshold;
    }

    public Decision getDecisionOnHit() {
        return decisionOnHit;
    }
}
