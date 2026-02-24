package service;

import domain.account.*;
import domain.customer.Customer;
import domain.ledger.Ledger;
import domain.transaction.Deposit;
import domain.transaction.Transaction;
import domain.transaction.Withdrawal;
import exception.CurrencyMismatchException;
import rules.FraudContext;
import rules.FraudEngine;
import rules.RuleResult;
import util.Money;
import util.Currency;

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

    //Checks if customer is findable
    private Customer requireCustomer(UUID id){
        if(id==null)
            throw new IllegalArgumentException("Id cant be null");
        if(!customers.containsKey(id))
            throw new IllegalArgumentException("Customer not found");

        return customers.get(id);
    }

    //Checks if account is findable
    private Account requireAccount(UUID id){
        if(id==null)
            throw new IllegalArgumentException("Id cant be null");
        if(!accounts.containsKey(id))
            throw new IllegalArgumentException("Account not found");

        return accounts.get(id);
    }

    //Creates customer
    public Customer createCustomer(String fullName){
        if(fullName==null)
            throw new IllegalArgumentException("FullName cant be null");
        if(fullName.isBlank())
            throw new IllegalArgumentException("FullName cant be blank");

        Customer c=new Customer(fullName);
        customers.put(c.getId(),c);

        return c;
    }

    public Customer getCustomer(UUID customerId){
        return requireCustomer(customerId);
    }

    public List<Customer> listCustomers(){
        return List.copyOf(customers.values());
    }


    public CheckingAccount openChecking(UUID customerId, Currency currency, Money overdraftLimit){
        Customer owner=requireCustomer(customerId);
        CheckingAccount account=new CheckingAccount(owner,currency, AccountStatus.ACTIVE, AccountType.CHECKING,overdraftLimit);

        //adding account into list and then assigning to owner
        accounts.put(account.getId(),account);
        owner.addAccount(account);

        return account;
    }

    public SavingsAccount openSavings(UUID customerId,Currency currency,Money minimumBalance,int monthlyLimit){
        Customer owner=requireCustomer(customerId);
        SavingsAccount account = new SavingsAccount(owner,currency,AccountStatus.ACTIVE,AccountType.SAVINGS,monthlyLimit,minimumBalance);

        accounts.put(account.getId(),account);
        owner.addAccount(account);

        return account;
    }

    public Account getAccount(UUID accountId){
        return requireAccount(accountId);
    }

    public List<Account> listAccounts(){
        return List.copyOf(accounts.values());
    }

    public Ledger getLedger() {
        return ledger;
    }

    public FraudEngine getFraudEngine() {
        return fraudEngine;
    }

    public List<Transaction> listAttempts() {
        return List.copyOf(attempts);
    }

    public Transaction deposit(UUID accountId, Money amount, String description) throws CurrencyMismatchException {
        Account account=requireAccount(accountId);
        Transaction tx =new Deposit(account,amount,description);

        if(amount.getCurrency()!=account.getCurrency())
            throw new CurrencyMismatchException("Currency does not match!");

        tx.approve();
        ledger.post(tx);

        return tx;
    }

    public Transaction withdraw(UUID accountId,Money amount,String description) throws CurrencyMismatchException {
        Account account=requireAccount(accountId);
        Transaction tx=new Withdrawal(amount,description,account);
        FraudContext ctx=new FraudContext(account.getOwner(),ledger.statementFor(account));

        RuleResult rr=fraudEngine.assess(tx,ctx);

        if (RuleResult.isAllow(rr)) {
            tx.approve();
            ledger.post(tx);
        } else if (RuleResult.isReview(rr)) {
            tx.markReview();
            attempts.add(tx);
        } else { // BLOCK
            tx.decline();
            attempts.add(tx);
        }

        return tx;
    }
}
