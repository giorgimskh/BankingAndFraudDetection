package domain.transaction;


import domain.account.Account;
import exception.CurrencyMismatchException;
import util.Money;

public class Withdrawal extends Transaction {
    private final Account from;

    public Withdrawal(Money amount, String description, Account fromAcc) {
        super(amount,description);

        if(fromAcc==null) throw new IllegalArgumentException("Target account cannot be null!");
        if(!fromAcc.getCurrency().equals(amount.getCurrency())) throw new IllegalStateException("Currency mismatch!");

        this.from = fromAcc;
    }

    public Account getFromAccount() {
        return this.from;
    }

    @Override
    public TransactionType type() {
        return TransactionType.WITHDRAWAL;
    }

    @Override
    public void apply() throws CurrencyMismatchException {
        from.withdraw(getAmount());
    }
}