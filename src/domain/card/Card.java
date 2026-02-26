package domain.card;

import domain.account.Account;
import exception.CurrencyMismatchException;
import domain.customer.Customer;
import util.Money;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import java.time.LocalDate;

public abstract class Card {
    protected final UUID id;
    protected final Customer owner;
    protected final Account linkedAccount;
    protected  CardStatus cardStatus;
    protected  Money dailyLimit;
    protected  Money spentToday;
    protected  LocalDate spendDate;

    public Card(Customer owner, Account linkedAccount, Money dailyLimit) {
        if(owner==null)
            throw new IllegalArgumentException("Owner cant be null");
        if(linkedAccount==null)
            throw new IllegalArgumentException("LinkedAccount cant be null");
        if(!linkedAccount.getOwner().getId().equals(owner.getId()))
            throw new IllegalArgumentException("Owner of Linked Account does not match");
        if(dailyLimit==null)
            throw new IllegalArgumentException("Daily limit cant be null");
        if(!dailyLimit.isPositive())
            throw new IllegalArgumentException("Daily limit must be positive");
        if(dailyLimit.getCurrency()!=linkedAccount.getCurrency())
            throw new CurrencyMismatchException("Currencies does not match");

        this.id = UUID.randomUUID();
        this.owner = owner;
        this.linkedAccount = linkedAccount;
        this.cardStatus = CardStatus.ACTIVE;
        this.dailyLimit = dailyLimit;
        this.spentToday = new Money(BigDecimal.ZERO,linkedAccount.getCurrency());
        this.spendDate = LocalDate.now();
        }


        //Freezing only Active cards
    public void freeze(){
        if(cardStatus!=CardStatus.ACTIVE)
            throw new IllegalStateException("CardStatus must be Active in order to freeze");

        cardStatus=CardStatus.FROZEN;
    }

    public void unfreeze(){
        if(cardStatus!=CardStatus.FROZEN)
            throw new IllegalStateException("CardStatus must be frozen to unfreeze");

        cardStatus=CardStatus.ACTIVE;
    }

    public void close(){
        cardStatus=CardStatus.CLOSED;
    }

    public boolean canAuthorize(Money amount) {
        if (amount == null || !amount.isPositive()) return false;
        if (cardStatus != CardStatus.ACTIVE) return false;
        if (amount.getCurrency() != linkedAccount.getCurrency()) return false;

        restoreDate();

        Money projected = spentToday.addMoney(amount);
        return projected.compareTo(dailyLimit) <= 0;
    }


    public void recordSpend(Money amount){
        if(amount==null)
            throw new IllegalArgumentException("Amount cant be null");
        if(!amount.isPositive())
            throw new IllegalArgumentException("Amount must be positive");
        if(amount.getCurrency()!=linkedAccount.getCurrency())
            throw new CurrencyMismatchException("Currencies does not match in recordSpend operation");

        restoreDate();

        Money projected = spentToday.addMoney(amount);
        if (projected.compareTo(dailyLimit) > 0)
            throw new IllegalStateException("Daily limit exceeded");

        spentToday = projected;
    }

    public void restoreDate(){
        LocalDate today = LocalDate.now();

        if (!today.equals(spendDate)) {
            spentToday = Money.zero(linkedAccount.getCurrency());
            spendDate=today;
        }
    }

    public UUID getId() {
        return id;
    }

    public Customer getOwner() {
        return owner;
    }

    public Account getLinkedAccount() {
        return linkedAccount;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public Money getDailyLimit() {
        return dailyLimit;
    }

    public Money getSpentToday() {
        return spentToday;
    }

    public LocalDate getSpendDate() {
        return spendDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(getId(), card.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
