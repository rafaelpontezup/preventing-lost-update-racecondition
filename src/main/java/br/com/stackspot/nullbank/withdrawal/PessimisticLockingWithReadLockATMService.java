package br.com.stackspot.nullbank.withdrawal;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessimisticLockingWithReadLockATMService {

    @Autowired
    private AccountRepository repository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Retryable(
            value = {
                LockAcquisitionException.class, // hibernate
                CannotAcquireLockException.class // spring
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 400, maxDelay = 8000, random = true) // UniformRandomBackOffPolicy
//            backoff = @Backoff(delay = 100, random = true, multiplier = 2.0) // ExponentialRandomBackOffPolicy
    )
    @Transactional
    public void withdraw(Long accountId, double amount) {

        Account account = repository.findByIdWithPessimisticReadLocking(accountId).orElseThrow(() -> {
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
