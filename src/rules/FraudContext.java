package rules;

import domain.customer.Customer;
import domain.transaction.Transaction;

import java.time.Instant;
import java.util.List;

public class FraudContext {
    private final Customer customer;
    private final List<Transaction> postedHistory;
    private final Instant now;

    public FraudContext(Customer customer, List<Transaction> postedHistory) {
        if(customer==null)
            throw new IllegalArgumentException("Customer field cant be null");
        if(postedHistory==null)
            throw new IllegalArgumentException("PostedHistory cant be null");

        this.customer = customer;
        this.postedHistory = List.copyOf(postedHistory);
        this.now = Instant.now();
    }

    public List<Transaction> lastN(int n){
        if(n<=0)
            throw new IllegalArgumentException("n must be positive");
        if(postedHistory.size()<n)
            return getPostedHistory();

        int size= getPostedHistory().size();
        int from=Math.max(0,size-n);

        return getPostedHistory().subList(from,size);
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
