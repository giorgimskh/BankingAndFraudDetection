package domain.account;

import domain.customer.Customer;
import util.Currency;
import util.Money;


public class CheckingAccount extends Account {
    Money overdraftLimit;

    public CheckingAccount(Customer owner, Money balance, Currency currency, AccountStatus accountStatus, AccountType accountType,Money overdraftLimit) {
        super(owner, balance, currency, accountStatus, accountType);
        this.overdraftLimit=overdraftLimit;
    }

    public CheckingAccount(Customer owner, Currency currency, AccountStatus accountStatus, AccountType accountType,Money overdraftLimit) {
        super(owner, currency, accountStatus, accountType);
        this.overdraftLimit=overdraftLimit;
    }

    @Override
    protected boolean canWithdraw(Money amount) {
        if(this.getBalance().getAmount().add(this.overdraftLimit.getAmount()).compareTo(amount.getAmount())>=0)
            return true;

        return false;
    }
}
