package domain.card;

import domain.account.Account;
import exception.CurrencyMismatchException;
import domain.customer.Customer;
import util.Money;
import java.util.UUID;
import java.time.LocalDate;

public abstract class Card {
    protected final UUID id;
    protected final Customer owner;
    protected final Account linkedAccount;
    protected  CardStatus cardStatus;
    protected final Money dailyLimit;
    protected  Money spentToday;
    protected  LocalDate spendDate;


    protected Card(Customer owner, Account linkedAccount, CardStatus cardStatus, Money dailyLimit, Money spentToday, LocalDate spendDate) {
        if(owner==null)
            throw new IllegalArgumentException("Owner cant be null");
        if(linkedAccount==null)
            throw new IllegalArgumentException("LinkedAccount cant be null");
        if(!linkedAccount.getOwner().getId().equals(owner.getId()))
            throw new IllegalArgumentException("Owner of Linked Account does not match");
        if(dailyLimit==null)
            throw new IllegalArgumentException("Daily limit cant be null");
        if(spentToday==null)
            throw new IllegalArgumentException("spent Today cant be null");
        if(cardStatus==null)
            throw new IllegalArgumentException("cardStatus cant be null");
        if(!dailyLimit.isPositive())
            throw new IllegalArgumentException("Daily limit must be positive");
        if(dailyLimit.getCurrency()!=linkedAccount.getCurrency())
            throw new CurrencyMismatchException("Currencies does not match");
        if(spentToday.getCurrency()!=linkedAccount.getCurrency())
            throw new CurrencyMismatchException("Currencies does not match");

        this.id = UUID.randomUUID();
        this.owner = owner;
        this.linkedAccount = linkedAccount;
        this.cardStatus = cardStatus;
        this.dailyLimit = dailyLimit;
        this.spentToday = spentToday;
        this.spendDate = (spendDate == null) ? LocalDate.now() : spendDate;
        }
    }
