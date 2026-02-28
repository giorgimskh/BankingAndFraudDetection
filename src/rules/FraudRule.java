package rules;

import domain.transaction.Transaction;

public interface FraudRule {
    RuleResult evaluate(Transaction tx, FraudContext ctx);
}
