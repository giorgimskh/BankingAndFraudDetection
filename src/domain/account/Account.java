package domain.account;

import domain.customer.Customer;
import exception.CurrencyMismatchException;
import util.Currency;
import util.Money;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class  Account {
    private final UUID id;
    private final Customer owner;
    private Money balance;
    private final Currency currency;
    private AccountStatus accountStatus;
    private AccountType accountType;

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

    public Account(Customer owner,  Currency currency, AccountStatus accountStatus, AccountType accountType) {
        if(owner==null)
            throw new IllegalArgumentException("owner cant be null");
        if(balance.getCurrency()!=currency)
            throw new IllegalArgumentException("Currencies does not match");
        if(accountStatus==null)
            throw new IllegalArgumentException("account status cant be null");

        this.id = UUID.randomUUID();
        this.owner = owner;
        this.balance = new Money(BigDecimal.ZERO,currency);
        this.currency = currency;
        this.accountStatus = accountStatus;
        this.accountType = accountType;
    }

    public UUID getId() {
        return id;
    }

    public Customer getOwner() {
        return owner;
    }

    public Money getBalance() {
        return balance;
    }

    public Currency getCurrency() {
        return currency;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public void deposit(Money money) throws CurrencyMismatchException {
        if(this.accountStatus.equals(AccountStatus.CLOSED))
            throw new IllegalStateException("Account must not be closed");

        if(money.getCurrency()!=this.currency)
            throw new CurrencyMismatchException("Currencies mismatched");

        this.balance.add(money);
    }

    public void withdraw(Money money) throws CurrencyMismatchException {
        if(!accountStatus.equals(AccountStatus.CLOSED))
            throw new IllegalStateException("Account must not be closed ");
        if(this.balance.getCurrency()!=money.getCurrency())
            throw new CurrencyMismatchException("Currencies mismatched");
        if(!canWithdraw(money))
            throw new IllegalArgumentException("Invalid operation");

        this.balance.subtract(money);
    }

    protected abstract boolean canWithdraw(Money amount) throws CurrencyMismatchException;

    public void freeze(){
        if(this.accountStatus.equals(AccountStatus.ACTIVE))
            setAccountStatus(AccountStatus.FROZEN);
    }

    public void unfreeze(){
        if(this.accountStatus.equals(AccountStatus.FROZEN))
            setAccountStatus(AccountStatus.ACTIVE);
    }

    public void close(){
        if(!this.accountStatus.equals(AccountStatus.CLOSED) && this.getBalance().isZero())
            setAccountStatus(AccountStatus.CLOSED);
    }


}
