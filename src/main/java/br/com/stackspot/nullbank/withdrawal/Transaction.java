package br.com.stackspot.nullbank.withdrawal;

import jakarta.persistence.*;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    private Double amount;
    private String description;

    @Deprecated
    public Transaction(){}

    public Transaction(Account account, Double amount, String description) {
        this.account = account;
        this.amount = amount;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public Double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", account=" + account +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}
