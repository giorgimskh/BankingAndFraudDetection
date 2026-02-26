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
        if(card==null)
            throw new IllegalArgumentException("Card cant be null");
        if(merchant==null)
            throw new IllegalArgumentException("Merchant cant be null");
        if(amount.getCurrency()!=card.getLinkedAccount().getCurrency())
            throw new CurrencyMismatchException("Currencies does not match");
        if(card.getCardStatus()!= CardStatus.ACTIVE)
            throw new IllegalArgumentException("Card Status must be active");

        super(amount,description);
        this.card = card;
        this.merchant = merchant;
    }

    @Override
    public TransactionType type() {
        return TransactionType.CARD_PAYMENT;
    }

    @Override
    public void apply(){
        try{
            card.getLinkedAccount().withdraw(getAmount());
            card.recordSpend(getAmount());
        }catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean involves(Account account) {
        return account==card.getLinkedAccount();
    }

    public Card getCard() {
        return card;
    }

    public Merchant getMerchant() {
        return merchant;
    }
}
