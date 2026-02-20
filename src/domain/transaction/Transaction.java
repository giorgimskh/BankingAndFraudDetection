package domain.transaction;

import util.Money;

import java.time.Instant;
import java.util.UUID;

public abstract class Transaction {
    UUID id;
    Instant createdAt;
    Money amount;
    TransactionStatus status;
    String description;

    public Transaction(UUID id, Instant createdAt, Money amount, TransactionStatus status, String description) {
        this.id = id;
        this.createdAt = createdAt;
        this.amount = amount;
        this.status = status;
        this.description = description;
    }
}
