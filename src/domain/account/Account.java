package domain.account;

import domain.customer.Customer;
import util.Currency;
import util.Money;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {
    UUID id;
    Customer owner;
    Money balance;
    Currency currency;
    AccountStatus accountStatus;
    AccountType accountType;

    public Account(Customer owner, Money balance, Currency currency, AccountStatus accountStatus, AccountType accountType) {
        if(owner==null)
            throw new IllegalArgumentException("owner cant be null");
        if(balance.getCurrency()!=currency)
            throw new IllegalArgumentException("Currencies does not match");
        if(accountStatus==null)
            throw new IllegalArgumentException("account status cant be null");

        this.id=UUID.randomUUID();
        this.owner=owner;
        this.currency=currency;
        this.accountType=accountType;
        this.accountStatus=accountStatus;
    }

    public Account(UUID id, Customer owner,  Currency currency, AccountStatus accountStatus, AccountType accountType) {
        if(owner==null)
            throw new IllegalArgumentException("owner cant be null");
        if(balance.getCurrency()!=currency)
            throw new IllegalArgumentException("Currencies does not match");
        if(accountStatus==null)
            throw new IllegalArgumentException("account status cant be null");

        this.id = id;
        this.owner = owner;
        this.balance = new Money(BigDecimal.ZERO,currency);
        this.currency = currency;
        this.accountStatus = accountStatus;
        this.accountType = accountType;
    }


}
