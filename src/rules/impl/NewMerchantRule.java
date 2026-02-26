package rules.impl;

import domain.transaction.CardPayment;
import domain.transaction.Transaction;
import rules.Decision;
import rules.FraudContext;
import rules.FraudRule;
import rules.RuleResult;

public class NewMerchantRule implements FraudRule {
    private final Decision decisionOnHit;

    public NewMerchantRule(Decision decisionOnHit) {
        if(decisionOnHit==null)
            throw new IllegalArgumentException("DecisionOnHit cant be null");
        if(decisionOnHit==Decision.ALLOW)
            throw new IllegalArgumentException("DecisionOnHit cant be allow type");

        this.decisionOnHit = decisionOnHit;
    }

    public Decision getDecisionOnHit() {
        return decisionOnHit;
    }


    @Override
    public RuleResult evaluate(Transaction tx, FraudContext ctx) {
        if(tx==null)
            throw new IllegalArgumentException("Transaction cant be null");
        if(ctx==null)
            throw new IllegalArgumentException("fraudContext cant be null");

        if(!(tx instanceof CardPayment cp))
            return RuleResult.allow();

        if (!ctx.hasSeenMerchant(cp.getMerchant().getId())) {
            String msg = "New merchant: " + cp.getMerchant().getName();
            return (decisionOnHit == Decision.BLOCK)
                    ? RuleResult.block(msg)
                    : RuleResult.review(msg);
        }

        return RuleResult.allow();
    }
}
