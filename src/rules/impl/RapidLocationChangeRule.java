package rules.impl;

import domain.transaction.CardPayment;
import domain.transaction.Transaction;
import exception.CurrencyMismatchException;
import rules.Decision;
import rules.FraudContext;
import rules.FraudRule;
import rules.RuleResult;

import java.time.Duration;
import java.util.List;

public class RapidLocationChangeRule implements FraudRule {
    private final Duration maxTimeBetween;
    private final Decision decisionOnHit;

    public RapidLocationChangeRule(Duration maxTimeBetween, Decision decisionOnHit) {
        if(maxTimeBetween==null)
            throw new IllegalArgumentException("maxTimeBetween cant be null");
        if(decisionOnHit==null)
            throw new IllegalArgumentException("decisionOnHit cant be null");
        if(decisionOnHit==Decision.ALLOW)
            throw new IllegalArgumentException("decisionOnHit cant be allow");
        if(!maxTimeBetween.isPositive())
            throw new IllegalArgumentException("maxTimeBetween must be positive");

        this.maxTimeBetween = maxTimeBetween;
        this.decisionOnHit = decisionOnHit;
    }

    public Duration getMaxTimeBetween() {
        return maxTimeBetween;
    }

    public Decision getDecisionOnHit() {
        return decisionOnHit;
    }

    @Override
    public RuleResult evaluate(Transaction tx, FraudContext ctx) throws CurrencyMismatchException {
        if(!(tx instanceof CardPayment cp))
            return RuleResult.allow();
        if(ctx.getPostedHistory().isEmpty())
            return RuleResult.allow();

        List<Transaction> reversedList=ctx.getPostedHistory().reversed();
        for (Transaction transaction: reversedList){
            if(transaction instanceof CardPayment cardPayment){
                if (!cardPayment.getMerchant().getCountry().equals(cp.getMerchant().getCountry())) {
                    Duration difference = Duration.between(
                            cardPayment.getCreatedAt(),
                            cp.getCreatedAt()
                    );

                    if (!difference.isNegative() && difference.compareTo(maxTimeBetween) <= 0) {
                        String msg = "Different country spotted";
                        return decisionOnHit == Decision.REVIEW ? RuleResult.review(msg) : RuleResult.block(msg);
                    }
                }
                return RuleResult.allow();
            }
        }

        return RuleResult.allow();
    }
}
