package domain.account;

import domain.customer.Customer;
import exception.CurrencyMismatchException;
import util.Currency;
import util.Money;


public class CheckingAccount extends Account {
    private final Money overdraftLimit;

    public CheckingAccount(Customer owner, Money balance, Currency currency, AccountStatus accountStatus, AccountType accountType,Money overdraftLimit) {
        super(owner, balance, currency, accountStatus, accountType);
        validateOverdraft(overdraftLimit,currency);
        this.overdraftLimit=overdraftLimit;
    }

    public CheckingAccount(Customer owner, Currency currency, AccountStatus accountStatus, AccountType accountType,Money overdraftLimit) {
        super(owner, currency, accountStatus, accountType);
        validateOverdraft(overdraftLimit,currency);
        this.overdraftLimit=overdraftLimit;
    }

    private void validateOverdraft(Money overdraftLimit, Currency currency) {
        if (overdraftLimit == null)
            throw new IllegalArgumentException("Overdraft limit cannot be null");

        if (!overdraftLimit.getCurrency().equals(currency))
            throw new IllegalArgumentException("Overdraft currency mismatch");

        if (overdraftLimit.isNegative())
            throw new IllegalArgumentException("Overdraft cannot be negative");
    }

    @Override
    protected boolean canWithdraw(Money amount) throws CurrencyMismatchException {
        if(amount==null)
            throw new IllegalArgumentException("Amount cannot be null");

        Money available=getBalance().addMoney(getOverdraftLimit());
        return available.compareTo(amount)>=0;
    }

    public Money getOverdraftLimit() {
        return overdraftLimit;
    }
}
