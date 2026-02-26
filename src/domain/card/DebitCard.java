package domain.card;

import domain.account.Account;
import domain.customer.Customer;
import util.Money;

public class DebitCard extends Card{
    public DebitCard(Customer owner, Account linkedAccount, Money dailyLimit) {
        super(owner, linkedAccount, dailyLimit);
    }
}
