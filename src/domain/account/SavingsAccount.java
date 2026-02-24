package domain.account;

import domain.customer.Customer;
import exception.CurrencyMismatchException;
import util.Currency;
import util.Money;


public class SavingsAccount extends Account{
    private final Money minimumBalance;
    int withdrawalsThisMonth;
    int monthlyWithdrawalLimit;




    public SavingsAccount(Customer owner, Money balance, Currency currency, AccountStatus accountStatus, AccountType accountType,  int monthlyWithdrawalLimit,  Money minimumBalance1) {
        super(owner, balance, currency, accountStatus, accountType);
        this.monthlyWithdrawalLimit = monthlyWithdrawalLimit;
        this.withdrawalsThisMonth = 0;
        this.minimumBalance = minimumBalance1;
    }

    public SavingsAccount(Customer owner, Currency currency, AccountStatus accountStatus, AccountType accountType,  int monthlyWithdrawalLimit, Money minimumBalance1) {
        if(minimumBalance1==null || minimumBalance1.isNegative())
            throw new IllegalArgumentException("minimum balance should not be null or negative");
        if(monthlyWithdrawalLimit<=0)
            throw new IllegalArgumentException("Monthly withdrawal limit should be positive");

        super(owner, currency, accountStatus, accountType);
        this.minimumBalance = minimumBalance1;
        this.monthlyWithdrawalLimit = monthlyWithdrawalLimit;
        this.withdrawalsThisMonth = 0;
    }

    @Override
    protected boolean canWithdraw(Money amount) throws CurrencyMismatchException {
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");

        if (withdrawalsThisMonth >= monthlyWithdrawalLimit) {
            return false;
        }

        Money after = getBalance().subtractMoney(amount);
        return after.compareTo(minimumBalance) >= 0;
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setWithdrawalsThisMonth(int withdrawalsThisMonth) {
        this.withdrawalsThisMonth = withdrawalsThisMonth;
    }

    public void setMonthlyWithdrawalLimit(int monthlyWithdrawalLimit) {
        this.monthlyWithdrawalLimit = monthlyWithdrawalLimit;
    }

    public int getWithdrawalsThisMonth() {
        return withdrawalsThisMonth;
    }

    public int getMonthlyWithdrawalLimit() {
        return monthlyWithdrawalLimit;
    }
}

