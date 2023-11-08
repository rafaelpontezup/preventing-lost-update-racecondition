package br.com.stackspot.nullbank.withdrawal;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessimisticLockingWithAdvisoryLockInQueryATMService {

    @Autowired
    private AccountRepository repository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Retryable(
            value = AccountNotFoundOrLockNotAcquiredException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, random = true, multiplier = 2.0)
    )
    @Transactional
    public void withdraw(Long accountId, double amount) {

        Account account = repository.findByIdWithPessimisticAdvisoryLocking(accountId).orElseThrow(() -> {
            throw new AccountNotFoundOrLockNotAcquiredException("account does not exist: " + accountId);
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

    class AccountNotFoundOrLockNotAcquiredException extends RuntimeException {

        public AccountNotFoundOrLockNotAcquiredException(String message) {
            super(message);
        }
    }
}
