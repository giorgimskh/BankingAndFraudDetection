package domain.ledger;

import domain.account.Account;
import domain.customer.Customer;
import domain.transaction.Transaction;
import domain.transaction.TransactionStatus;
import domain.transaction.Transfer;
import exception.CurrencyMismatchException;

import java.util.*;

public class Ledger {
    private final List<Transaction> history;

    public Ledger() {
       history=new ArrayList<>();
    }

    public void post(Transaction tx)  {
        if(tx==null)
            throw new IllegalArgumentException("Transaction cant be null");
        if(tx.getStatus()!= TransactionStatus.APPROVED)
            throw new IllegalStateException("Only approved transactions are posted!");
        if(isPosted(tx.getId()))
            throw new IllegalArgumentException("This transaction is already done");

        try {
            tx.apply();
            tx.markPosted();
            history.add(tx);
        } catch (CurrencyMismatchException e) {
            throw new RuntimeException(e);
        }
    }



    public List<Transaction> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public boolean isPosted(UUID txId) {
        if (txId == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }

        for (Transaction tx : history) {
            if (tx.getId().equals(txId))
                return true;
        }
        return false;
    }


    public List<Transaction> statementFor(Account account){
        if(account==null)
            throw new IllegalArgumentException("Account cannot be null");
        List<Transaction>involvedIn=new ArrayList<>();

        for(Transaction tx:history){
            if(tx.involves(account))
                involvedIn.add(tx);
        }
        return involvedIn;
    }

}
