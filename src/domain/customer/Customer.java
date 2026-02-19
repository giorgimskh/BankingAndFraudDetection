package domain.customer;

import domain.account.Account;
import domain.card.Card;

import java.util.List;
import java.util.UUID;

public class Customer {
    UUID id;
    String fullName;
    List<Account> accounts;
    List<Card> cards;

}
