package br.com.stackspot.nullbank.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessimisticLockingWithAdvisoryLockInSmartQueryATMService {

    @Autowired
    private AccountRepository repository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Retryable(
            value = FailedToAcquireLockForAccountException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, random = true, multiplier = 2.0)
    )
    @Transactional
    public void withdraw(Long accountId, double amount) {

        // We load the entity even if a lock is not acquired
        LockableAccount lockedAccount = repository.findByIdWithPessimisticAdvisoryLockingInSelectClause(accountId).orElseThrow(() -> {
            throw new IllegalStateException("account does not exist: " + accountId);
        });

        // But the business logic is executed only if the lock was acquired for the account
        Account account = lockedAccount.getAccountIfLockedOrElseThrow();

        double newBalance = (account.getBalance() - amount);
        if (newBalance < 0) {
            throw new IllegalStateException("there's not enough balance");
        }

        account.setBalance(newBalance);
        repository.save(account);

        transactionRepository
                .save(new Transaction(account, amount, "withdraw"));
    }

    /**
     * Represents an account that may be locked or not
     */
    public static class LockableAccount {
        private final Account account;
        private final boolean locked;

        public LockableAccount(Account account, boolean locked) {
            this.account = account;
            this.locked = locked;
        }

        /**
         * Returns the actual account if it was locked or else throws an {@code FailedToAcquireLockForAccountException}
         */
        public Account getAccountIfLockedOrElseThrow() {
            if (!locked) {
                throw new FailedToAcquireLockForAccountException("Account already locked by another user");
            }
            return account;
        }

        public boolean isLocked() {
            return locked;
        }
    }

    public static class FailedToAcquireLockForAccountException extends RuntimeException {

        public FailedToAcquireLockForAccountException(String message) {
            super(message);
        }
    }
}
