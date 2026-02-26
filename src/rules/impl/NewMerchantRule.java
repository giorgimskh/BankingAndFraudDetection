package rules.impl;

import domain.transaction.Transaction;
import exception.CurrencyMismatchException;
import rules.Decision;
import rules.FraudContext;
import rules.FraudRule;
import rules.RuleResult;

public class NewMerchantRule {
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


}
