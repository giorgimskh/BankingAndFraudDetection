package rules.impl;

import domain.card.Card;
import domain.transaction.CardPayment;
import domain.transaction.Transaction;
import rules.Decision;
import rules.FraudContext;
import rules.FraudRule;
import rules.RuleResult;
import util.Money;

public class DailySpendLimitRule implements FraudRule {
    private final Decision decisionOnHit;

    public DailySpendLimitRule(Decision decisionOnHit) {
        if(decisionOnHit==null)
            throw new IllegalArgumentException("DecisionOnHit cant be null");
        if(decisionOnHit==Decision.ALLOW)
            throw new IllegalArgumentException("DecisionOnHit cant be Allow");

        this.decisionOnHit = decisionOnHit;
    }

    @Override
    public RuleResult evaluate(Transaction tx, FraudContext ctx) {
        if(tx==null)
            throw new IllegalArgumentException("Transaction cant be null for evaluating");
        if(ctx==null)
            throw new IllegalArgumentException("Fraud context cant be null");
        if(!(tx instanceof CardPayment cp))
            return RuleResult.allow();

        Card card = cp.getCard();

        if(!card.canAuthorize(cp.getAmount())){
            Money projected = card.getSpentToday().addMoney(cp.getAmount());
            String msg="Daily limit exceeded: projected "+projected.getAmount()+ " > Limit " + card.getDailyLimit().getAmount();
            return decisionOnHit==Decision.REVIEW ?
                    RuleResult.review(msg):
                    RuleResult.block(msg);
        }

        return RuleResult.allow();
    }
}
