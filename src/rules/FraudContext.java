package rules;

import domain.customer.Customer;
import domain.transaction.Transaction;
import exception.CurrencyMismatchException;
import util.Currency;
import util.Money;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FraudContext {
    private final Customer customer;
    private final List<Transaction> postedHistory;
    private final Instant now;

    public FraudContext(Customer customer, List<Transaction> postedHistory, Instant now) {
        if(customer==null)
            throw new IllegalArgumentException("Customer field cant be null");
        if(postedHistory==null)
            throw new IllegalArgumentException("PostedHistory cant be null");
        if(now==null)
            throw new IllegalArgumentException("time cant be null");

        this.customer = customer;
        this.now = now;

        List<Transaction> copy = new ArrayList<>(postedHistory);
        copy.sort(Comparator.comparing(Transaction::getCreatedAt));
        this.postedHistory = List.copyOf(copy);
    }

    public FraudContext(Customer customer, List<Transaction> postedHistory) {
        if(customer==null)
            throw new IllegalArgumentException("Customer field cant be null");
        if(postedHistory==null)
            throw new IllegalArgumentException("PostedHistory cant be null");

        this.customer = customer;
        this.now = Instant.now();

        List<Transaction> copy = new ArrayList<>(postedHistory);
        copy.sort(Comparator.comparing(Transaction::getCreatedAt));
        this.postedHistory = List.copyOf(copy);
    }

    public List<Transaction> lastN(int n){
        if(n<=0)
            throw new IllegalArgumentException("n must be positive");

        int size= getPostedHistory().size();
        int from=Math.max(0,size-n);

        return List.copyOf(postedHistory.subList(from,size));
    }

    public List<Transaction> withinMinutes(int minutes){
        if(minutes<=0)
            throw new IllegalArgumentException("Minutes must be positive");

        List<Transaction> withinMinutesList=new ArrayList<>();
        Instant cutOff=now.minus(Duration.ofMinutes(minutes));

        for(Transaction tx:postedHistory){
           if(!tx.getCreatedAt().isBefore(cutOff))
               withinMinutesList.add(tx);
        }
        return List.copyOf(withinMinutesList);

    }

    public Money sumWithinMinutes(int minutes, Currency currency) throws CurrencyMismatchException {
        if(currency==null)
            throw new IllegalArgumentException("Currency cant be null");

        List<Transaction> txs=withinMinutes(minutes);
        Money total=Money.zero(currency);


            for (Transaction tx : txs) {
                Money a=tx.getAmount();
                if(a.getCurrency()==currency)
                    total=total.addMoney(a);
            }

         return total;
    }

    public int countWithinMinutes(int minutes){
        return withinMinutes(minutes).size();
    }

    public boolean hasAnyTransaction(){
        return !postedHistory.isEmpty();
    }

    public Transaction mostRecent(){
       if(postedHistory.isEmpty())
           throw new IllegalStateException("No transactions in history");
       return postedHistory.getLast();
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Transaction> getPostedHistory() {
        return postedHistory;
    }

    public Instant getNow() {
        return now;
    }
}
