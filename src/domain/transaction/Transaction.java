package domain.transaction;

import exception.CurrencyMismatchException;
import util.Money;

import java.time.Instant;
import java.util.UUID;

public abstract class Transaction {
    private final UUID id;
    private final Instant createdAt;
    private final Money amount;
    private  TransactionStatus status;
    private final String description;

    public Transaction(Money amount, String description) {
        if(amount==null)
            throw new IllegalStateException("Amount cannot be null");
        if(!amount.isPositive())
            throw new IllegalStateException("Amount must be positive!");

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.amount = amount;
        this.status = TransactionStatus.CREATED;
        this.description = description;
    }

    public abstract TransactionType type();
    public abstract void apply() throws CurrencyMismatchException;

    public void approve(){
        canBeModified();
        if(this.status!=TransactionStatus.CREATED)
            throw new IllegalStateException("This Transaction cannot be approved!");
        this.status=TransactionStatus.APPROVED;
    }

    public void MarkReview(){
        canBeModified();
        if(status != TransactionStatus.CREATED)
            throw new IllegalStateException("Only Created transaction can be  marked REVIEW!");
        this.status = TransactionStatus.REVIEW;
    }

    public void decline(){
        canBeModified();
        if(this.status!=TransactionStatus.CREATED)
            throw new IllegalStateException("Transaction is not created!It cant be declined!");
        this.status=TransactionStatus.DECLINED;
    }

    public void markPosted(){
        canBeModified();
        if(this.status!=TransactionStatus.APPROVED)
            throw new IllegalStateException("Only approved transaction are marked as posted!");
        this.status=TransactionStatus.POSTED;
    }

    public void canBeModified(){
        if(this.status==TransactionStatus.POSTED) {
            throw new IllegalStateException("Transaction  cannot be modified");
        }
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Money getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
