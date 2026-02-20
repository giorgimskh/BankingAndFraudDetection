package domain.transaction;

import domain.account.Account;
import exception.CurrencyMismatchException;
import util.Money;

public class Deposit extends Transaction {
    private  Account to;

    public Deposit(Account account,Money amount,  String description) throws CurrencyMismatchException {
        super(amount,description);
        if(account ==null)
            throw new IllegalArgumentException("account cannot be null");
        if(to.getCurrency()!=amount.getCurrency())
            throw new CurrencyMismatchException("Currencies must match");
        this.to=account;
    }


    @Override
    public TransactionType type() {
        return TransactionType.DEPOSIT;
    }

    @Override
    public void apply() throws CurrencyMismatchException {
        to.deposit(getAmount());
    }
}
