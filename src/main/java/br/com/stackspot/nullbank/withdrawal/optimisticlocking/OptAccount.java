package br.com.stackspot.nullbank.withdrawal.optimisticlocking;

import javax.persistence.*;

@Entity
public class OptAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String holderName;

    @Column(nullable = false, precision = 9, scale = 2)
    private Double balance;

    /**
     * JPA/Hibernate optimistic locking version column
     */
    @Version
    private long version;

    @Deprecated
    public OptAccount(){}

    public OptAccount(String holderName, Double balance) {
        this.holderName = holderName;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getHolderName() {
        return holderName;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "OptAccount{" +
                "id=" + id +
                ", holderName='" + holderName + '\'' +
                ", balance=" + balance +
                '}';
    }
}
