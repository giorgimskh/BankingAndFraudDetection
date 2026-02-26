package domain.card;

import domain.account.Account;
import domain.customer.Customer;
import util.Money;

public class VirtualCard extends Card{
    public VirtualCard(Customer owner, Account linkedAccount, Money dailyLimit) {
        super(owner, linkedAccount, dailyLimit);
    }
}
