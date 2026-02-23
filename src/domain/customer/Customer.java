package domain.customer;

import domain.account.Account;
import domain.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Customer {
    private final UUID id;
    private final String fullName;
    private final List<Account>accounts;
    private final List<Card> cards=new ArrayList<>();

    public Customer(String fullName, List<Account> accounts) {
        if(fullName==null)
            throw new IllegalArgumentException("Full name must be non-empty");
        if(fullName.isEmpty())
            throw new IllegalArgumentException("Full name must not be blank");

        this.id = UUID.randomUUID();
        this.fullName = fullName;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account){
        if(account==null)
            throw new IllegalArgumentException("Account cant be null");
        if(!account.getOwner().equals(this))
            throw new IllegalArgumentException("Owners must match");
        if(accounts.contains(account))
            throw new IllegalArgumentException("Account is already added!");

        accounts.add(account);
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Card> getCards() {
        return cards;
    }
}
