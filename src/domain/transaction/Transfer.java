package domain.transaction;

import domain.account.Account;
import domain.account.AccountStatus;
import exception.CurrencyMismatchException;
import util.Money;

public class Transfer extends Transaction{
    private final Account from;
    private final Account to;

    public Transfer(Money amount, String description,Account fromAcc,Account toAcc) throws CurrencyMismatchException {
        super(amount, description);

        if(fromAcc==null)
            throw new IllegalArgumentException("Account transferring money from cant be null");
        if(toAcc==null)
            throw new IllegalArgumentException("Account which you sending money to cant be null");
        if(fromAcc.getId().equals(toAcc.getId()))
            throw new IllegalArgumentException("You cant make transfer on same account");
        if(amount.getCurrency()!=fromAcc.getCurrency() || amount.getCurrency()!=toAcc.getCurrency())
            throw new CurrencyMismatchException("Currencies must match!");

        this.from = fromAcc;
        this.to = toAcc;
    }


    @Override
    public TransactionType type() {
        return TransactionType.TRANSFER;
    }

    @Override
    public void apply() throws CurrencyMismatchException {
        if(getStatus()==TransactionStatus.APPROVED)
            throw new IllegalStateException("Transaction already approved");

        if (to.getAccountStatus() != AccountStatus.ACTIVE)
            throw new IllegalStateException("Target account must be ACTIVE");

        this.from.withdraw(this.getAmount());
        this.to.deposit(this.getAmount());

        this.setStatus(TransactionStatus.APPROVED);
    }

    public boolean involves(Account a) {
        if (a == null) return false;
        return a.getId().equals(from.getId()) || a.getId().equals(to.getId());
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "from=" + from.getId() +
                ", to=" + to.getId() +
                '}';
    }
}
