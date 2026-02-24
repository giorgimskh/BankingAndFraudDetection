package service;

import domain.account.Account;
import domain.account.AccountStatus;
import domain.account.AccountType;
import domain.account.CheckingAccount;
import domain.customer.Customer;
import domain.ledger.Ledger;
import domain.transaction.Transaction;
import rules.FraudEngine;
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
        if(fullName.isEmpty())
            throw new IllegalArgumentException("FullName cant be blank");

        Customer c=new Customer(fullName);
        customers.put(c.getId(),c);

        return c;
    }

    public Customer getCustoemr(UUID customerId){
        return requireCustomer(customerId);
    }

    public List<Customer> listCustomers(){
        return List.copyOf(customers.values());
    }


    public CheckingAccount openChecking(UUID customerId, Currency currency, Money overdraftLimit){
        Customer owner=requireCustomer(customerId);
        CheckingAccount account=new CheckingAccount(owner,currency, AccountStatus.ACTIVE, AccountType.CHECKING,overdraftLimit);

        //adding account into list and then assigning to owner
        accounts.put(owner.getId(),account);
        owner.addAccount(account);

        return account;
    }

    public Ledger getLedger() {
        return ledger;
    }

    public FraudEngine getFraudEngine() {
        return fraudEngine;
    }
}
