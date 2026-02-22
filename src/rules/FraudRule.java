package rules;

import domain.transaction.Transaction;
import exception.CurrencyMismatchException;

public interface FraudRule {
    RuleResult evaluate(Transaction tx, FraudContext ctx) throws CurrencyMismatchException;
}
