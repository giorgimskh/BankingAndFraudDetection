# UML Class Diagram

High-level class diagram of the domain, rules, and service layers, showing key fields/methods and relationships (inheritance, composition, association, and interface implementation).

```mermaid
classDiagram
    class Customer {
        +UUID id
        +String fullName
        +addAccount(Account)
        +addCard(Card)
    }

    class Account {
        <<abstract>>
        +UUID id
        +Money balance
        +Currency currency
        +AccountStatus accountStatus
        +deposit(Money)
        +withdraw(Money)
        +canWithdraw(Money) bool*
    }
    class CheckingAccount {
        +Money overdraftLimit
    }
    class SavingsAccount {
        +Money minimumBalance
        +int monthlyWithdrawalLimit
    }

    class Card {
        <<abstract>>
        +UUID id
        +CardStatus cardStatus
        +Money dailyLimit
        +Money spentToday
        +canAuthorize(Money) bool
        +recordSpend(Money)
    }
    class DebitCard
    class VirtualCard

    class Transaction {
        <<abstract>>
        +UUID id
        +Money amount
        +TransactionStatus status
        +apply() void*
        +involves(Account) bool*
    }
    class Deposit
    class Withdrawal
    class Transfer
    class CardPayment

    class Merchant {
        +UUID id
        +String name
        +String countryCode
    }

    class Ledger {
        +post(Transaction)
        +statementFor(Account) List~Transaction~
    }

    class FraudRule {
        <<interface>>
        +evaluate(Transaction, FraudContext) RuleResult
    }
    class FraudEngine {
        +assess(Transaction, FraudContext) RuleResult
    }
    class RuleResult {
        +Decision decision
        +String reason
    }
    class LargeAmountRule
    class DailySpendLimitRule
    class VelocitySumRule
    class TooManyTransactionsRule
    class NewMerchantRule
    class RapidLocationChangeRule

    class BankService {
        +deposit(...)
        +withdraw(...)
        +transfer(...)
        +payByCard(...)
        +issueDebitCard(...)
    }

    Account <|-- CheckingAccount
    Account <|-- SavingsAccount
    Card <|-- DebitCard
    Card <|-- VirtualCard
    Transaction <|-- Deposit
    Transaction <|-- Withdrawal
    Transaction <|-- Transfer
    Transaction <|-- CardPayment

    Customer "1" *-- "many" Account
    Customer "1" *-- "many" Card
    Card --> Account : linkedAccount
    CardPayment --> Merchant
    Ledger --> Transaction : history

    FraudRule <|.. LargeAmountRule
    FraudRule <|.. DailySpendLimitRule
    FraudRule <|.. VelocitySumRule
    FraudRule <|.. TooManyTransactionsRule
    FraudRule <|.. NewMerchantRule
    FraudRule <|.. RapidLocationChangeRule
    FraudEngine o-- FraudRule
    FraudEngine --> RuleResult

    BankService --> Ledger
    BankService --> FraudEngine
    BankService --> Customer
    BankService --> Account
    BankService --> Card
```
