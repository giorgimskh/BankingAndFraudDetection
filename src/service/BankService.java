package service;

import domain.account.Account;
import domain.customer.Customer;
import domain.ledger.Ledger;
import domain.transaction.Transaction;
import rules.FraudEngine;

import java.util.*;

public class BankService {
    private final Ledger ledger;
    private final FraudEngine fraudEngine;
    private final Map<UUID, Customer> customers=new HashMap<>();
    private final Map<UUID, Account> accounts=new HashMap<>();
    private final List<Transaction> attempts=new ArrayList<>();


    public BankService(Ledger ledger, FraudEngine fraudEngine) {
        if(ledger==null)
            throw new IllegalArgumentException("Ledger should not be null");
        if(fraudEngine==null)
            throw new IllegalArgumentException("FraudEngine should not be null");

        this.ledger = ledger;
        this.fraudEngine = fraudEngine;
    }
}
