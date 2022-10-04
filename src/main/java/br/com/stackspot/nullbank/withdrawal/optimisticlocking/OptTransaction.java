package br.com.stackspot.nullbank.withdrawal.optimisticlocking;

import javax.persistence.*;

@Entity
public class OptTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private OptAccount account;

    private Double amount;
    private String description;

    @Deprecated
    public OptTransaction(){}

    public OptTransaction(OptAccount account, Double amount, String description) {
        this.account = account;
        this.amount = amount;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public OptAccount getAccount() {
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
        return "OptTransaction{" +
                "id=" + id +
                ", account=" + account +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}
