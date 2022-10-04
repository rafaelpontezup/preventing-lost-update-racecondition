package br.com.stackspot.nullbank.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NaiveAtomicUpdateATMService {

    @Autowired
    private AccountRepository repository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public void withdraw(Long accountId, double amount) {

        Double currentBalance = repository.getBalance(accountId);
        if (currentBalance < 0) {
            throw new IllegalStateException("there's not enough balance");
        };

        repository.update(accountId, amount);

        transactionRepository
                .save(new Transaction(
                        repository.findById(accountId).get(),
                        amount, "withdraw")
                );
    }

}
