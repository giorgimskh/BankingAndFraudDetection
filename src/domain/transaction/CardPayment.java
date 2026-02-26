package domain.transaction;

import domain.account.Account;
import domain.card.Card;
import domain.card.CardStatus;
import domain.merchant.Merchant;
import exception.CurrencyMismatchException;
import util.Money;

public class CardPayment extends Transaction{
    private final Card card;
    private final Merchant merchant;


    public CardPayment(Card card, Merchant merchant, Money amount,String description) {
        super(amount,description);
        if(card==null)
            throw new IllegalArgumentException("Card cant be null");
        if(merchant==null)
            throw new IllegalArgumentException("Merchant cant be null");
        if(card.getLinkedAccount()==null)
            throw new IllegalArgumentException("Card Account is null");
        if(amount.getCurrency()!=card.getLinkedAccount().getCurrency())
            throw new CurrencyMismatchException("Currencies does not match");
        if(card.getCardStatus()!= CardStatus.ACTIVE)
            throw new IllegalArgumentException("Card Status must be active");

        this.card = card;
        this.merchant = merchant;
    }

    @Override
    public TransactionType type() {
        return TransactionType.CARD_PAYMENT;
    }

    @Override
    public void apply(){
        if (card.getCardStatus() != CardStatus.ACTIVE)
            throw new IllegalStateException("Card not active");
        if(card.canAuthorize(getAmount()))
            throw new IllegalStateException("Card cant authorize transaction");

        card.recordSpend(getAmount());
        card.getLinkedAccount().withdraw(getAmount());
    }

    @Override
    public boolean involves(Account account) {
        return account.getId().equals(card.getLinkedAccount().getId());
    }

    public Card getCard() {
        return card;
    }

    public Merchant getMerchant() {
        return merchant;
    }
}
