package br.com.stackspot.nullbank.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NaiveNonTransactionalATMService {

    @Autowired
    private AccountRepository repository;
    @Autowired
    private TransactionRepository transactionRepository;

    public void withdraw(Long accountId, double amount) {

        Account account = repository.findById(accountId).orElseThrow(() -> {
            throw new IllegalStateException("account does not exist: " + accountId);
        });

        double newBalance = (account.getBalance() - amount);
        if (newBalance < 0) {
            throw new IllegalStateException("there's not enough balance");
        }

        account.setBalance(newBalance);
        repository.save(account);

        transactionRepository
                .save(new Transaction(account, amount, "withdraw"));
    }

}
