package rules.impl;

import domain.transaction.Transaction;
import exception.CurrencyMismatchException;
import rules.FraudContext;
import rules.FraudRule;
import rules.RuleResult;
import util.Currency;
import util.Money;

public class VelocitySumRule implements FraudRule {
    private final Money threshold;
    private final int windowMinutes;

    public VelocitySumRule(Money threshold, int windowMinutes) {
        if(threshold==null)
            throw new IllegalArgumentException("Threshold should not be null");
        if(windowMinutes<1)
            throw new IllegalArgumentException("WindowMinutes should me more than 0");
        if(!threshold.isPositive())
            throw new IllegalArgumentException("threshold must be positive");

        this.threshold = threshold;
        this.windowMinutes = windowMinutes;
    }

    @Override
    public RuleResult evaluate(Transaction tx, FraudContext ctx) throws CurrencyMismatchException {
        if(tx==null)
            throw new IllegalArgumentException("Transaction cant be Null");
        if(ctx==null)
            throw new IllegalArgumentException("FraudContext cant be Null");
        if (tx.getAmount().getCurrency() != threshold.getCurrency()) {
            return RuleResult.allow();
        }

        Currency c=tx.getAmount().getCurrency();
        Money sum = ctx.sumWithinMinutes(windowMinutes,c);
        Money projected = sum.addMoney(tx.getAmount());

        if (projected.compareTo(threshold) > 0) {
            return RuleResult.review("High spend: " + projected + " in last " + windowMinutes + " minutes");
        }

        return RuleResult.allow();
    }
}
