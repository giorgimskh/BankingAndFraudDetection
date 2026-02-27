package domain.customer;

import domain.account.Account;
import domain.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Customer {
    private final UUID id;
    private final String fullName;
    private final List<Account>accounts;
    private final List<Card> cards;

    public Customer(String fullName) {
        if(fullName==null)
            throw new IllegalArgumentException("Full name must be non-empty");
        if(fullName.isEmpty())
            throw new IllegalArgumentException("Full name must not be blank");

        this.id = UUID.randomUUID();
        this.fullName = fullName;
        this.accounts = new ArrayList<>();
        this.cards=new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                '}';
    }

    public void addAccount(Account account){
        if(account==null)
            throw new IllegalArgumentException("Account cant be null");
        if(!account.getOwner().getId().equals(this.id))
            throw new IllegalArgumentException("Owners must match");
        if(accounts.contains(account))
            throw new IllegalArgumentException("Account is already added!");

        accounts.add(account);
    }

    public Account findAccount(UUID accountId){
        if(accountId==null)
            throw new IllegalArgumentException("Account cant be null");

        for(Account acc:accounts){
            if(acc.getId().equals(accountId))
                return acc;
        }
        throw new IllegalArgumentException("Account has not be found");
    }

    public void addCard(Card card) {
        if (card == null)
            throw new IllegalArgumentException("Card can't be null");

        if (!card.getLinkedAccount().getOwner().getId().equals(this.id))
            throw new IllegalArgumentException("Card owner must match customer");

        if (cards.contains(card))
            throw new IllegalArgumentException("Card already added");

        cards.add(card);
    }



    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public List<Account> getAccounts() {
        return List.copyOf(accounts);
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }
}
