package rules.impl;

import domain.transaction.Transaction;
import rules.Decision;
import rules.FraudContext;
import rules.FraudRule;
import rules.RuleResult;
import util.Money;

public class LargeAmountRule implements FraudRule {
    private final Money threshold;
    private final Decision DecisionOnHit;

    public LargeAmountRule(Money threshold, Decision decisionOnHit) {
        if(threshold==null)
            throw new IllegalArgumentException("Threshold cant be Null");
        if(!threshold.isPositive())
            throw new IllegalArgumentException("Threshold must be negative");
        if(decisionOnHit==null)
            throw new IllegalArgumentException("DecisionOnHit cant be Null");
        if(decisionOnHit==Decision.ALLOW)
            throw new IllegalArgumentException("DecisionOnHit should not be Allow");

        this.threshold = threshold;
        this.DecisionOnHit = decisionOnHit;
    }


    @Override
    public RuleResult evaluate(Transaction tx, FraudContext ctx) {
        if(tx==null)
            throw new IllegalArgumentException("Transaction cant be Null");
        if(ctx==null)
            throw new IllegalArgumentException("FraudContext cant be Null");
        if(tx.getAmount().getCurrency()!=threshold.getCurrency())
            return RuleResult.review("LargeAmountRule skipped:Currency mismatched");
        if(tx.getAmount().compareTo(threshold)>0){
            if(DecisionOnHit==Decision.BLOCK)
                return RuleResult.block("Large Amount: "+tx.getAmount()+" > "+threshold);
            else
                return RuleResult.review("Large Amount: "+tx.getAmount()+" > "+threshold);
        }
        return RuleResult.allow();
    }

    public Money getThreshold() {
        return threshold;
    }

    public Decision getDecisionOnHit() {
        return DecisionOnHit;
    }
}
