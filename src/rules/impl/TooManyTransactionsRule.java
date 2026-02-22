package rules.impl;

import domain.transaction.Transaction;
import rules.Decision;
import rules.FraudContext;
import rules.FraudRule;
import rules.RuleResult;

public class TooManyTransactionsRule implements FraudRule {
    private final int maxCount;
    private final int windowMinutes;
    private final Decision decisionOnHit;

    public TooManyTransactionsRule(int maxCount, int windowMinutes,Decision decisionOnHit) {
        if(maxCount<=0)
            throw new IllegalArgumentException("maxCount should be more than 0");
        if(windowMinutes<=0)
            throw new IllegalArgumentException("WindowMinutes should be more than 0");
        if (decisionOnHit == null)
            throw new IllegalArgumentException("decisionOnHit can't be null");
        if (decisionOnHit == Decision.ALLOW)
            throw new IllegalArgumentException("decisionOnHit must be REVIEW or BLOCK");

        this.maxCount = maxCount;
        this.windowMinutes = windowMinutes;
        this.decisionOnHit=decisionOnHit;
    }


    @Override
    public RuleResult evaluate(Transaction tx, FraudContext ctx) {
        if (tx == null) throw new IllegalArgumentException("Transaction can't be null");
        if (ctx == null) throw new IllegalArgumentException("FraudContext can't be null");

        int count = ctx.countWithinMinutes(windowMinutes) + 1; // include current tx

        if (count > maxCount) {
            String msg = "High velocity: " + count + " tx in " + windowMinutes + " minutes";
            return (decisionOnHit == Decision.BLOCK)
                    ? RuleResult.block(msg)
                    : RuleResult.review(msg);
        }

        return RuleResult.allow();
    }
}
