package service;

import domain.account.*;
import domain.card.Card;
import domain.customer.Customer;
import domain.ledger.Ledger;
import domain.transaction.Deposit;
import domain.transaction.Transaction;
import domain.transaction.Transfer;
import domain.transaction.Withdrawal;
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
    private final Map<UUID, Card> cards=new HashMap<>();

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

    public List<Transaction> getLedger() {
        return ledger.getHistory();
    }

    public FraudEngine getFraudEngine() {
        return fraudEngine;
    }

    public List<Transaction> listAttempts() {
        return List.copyOf(attempts);
    }

    public Transaction deposit(UUID accountId, Money amount, String description){
        Account account=requireAccount(accountId);
        Transaction tx =new Deposit(account,amount,description);

        try{
            tx.approve();
            ledger.post(tx);
        }catch (RuntimeException e){
            attempts.add(tx);
            System.out.println(e.getMessage());
        }

        return tx;
    }

    public Transaction withdraw(UUID accountId,Money amount,String description){
        Account account=requireAccount(accountId);
        Transaction tx=new Withdrawal(amount,description,account);


        FraudContext ctx=new FraudContext(account.getOwner(),getCustomerHistory(account.getOwner()));

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

    public List<Transaction> statement(UUID accountId){
        return ledger.statementFor(requireAccount(accountId));
    }

    public void freezeAccount(UUID accountId){
        try{
            requireAccount(accountId).freeze();
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    public void closeAccount(UUID accountId) {
        try{
            requireAccount(accountId).close();
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    public void unfreezeAccount(UUID accountId){
        try {
            requireAccount(accountId).unfreeze();
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    public Transaction transfer(UUID fromAccountId,UUID toAccountId,Money amount,String description){
        Account to=requireAccount(toAccountId);
        Account from=requireAccount(fromAccountId);
        Transfer transfer=new Transfer(amount,description,from,to);

        try{
            FraudContext ctx=new FraudContext(from.getOwner(),getCustomerHistory(from.getOwner()));
            RuleResult rr = fraudEngine.assess(transfer,ctx);

            if(RuleResult.isAllow(rr)){
                transfer.approve();
                ledger.post(transfer);
            }else if(RuleResult.isReview(rr)){
                transfer.markReview();
                attempts.add(transfer);
            }else{
                transfer.decline();
                attempts.add(transfer);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return transfer;
    }

    public List<Transaction> getCustomerHistory(Customer customer){
        if (customer == null) throw new IllegalArgumentException("Customer can't be null");

        List<Transaction> result = new ArrayList<>();
        for (Transaction tx : ledger.getHistory()) {
            if (tx.involvesAnyAccountOf(customer)) {
                result.add(tx);
            }
        }
        return List.copyOf(result);
    }

    private Card requireCard(UUID id){
        if(id==null)
            throw new IllegalArgumentException("Id cant be null");
        if(!cards.containsKey(id))
            throw new IllegalArgumentException("Such id cant be found in cards");

        //check if card is null
        Card card=cards.get(id);
        if(card==null)
            throw new IllegalStateException("Card is null");

        return card;
    }
}
