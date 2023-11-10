package br.com.stackspot.nullbank.withdrawal;

import br.com.stackspot.nullbank.shared.lockmanager.LockKey;
import br.com.stackspot.nullbank.shared.lockmanager.PostgresLockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class PostgresAdvisoryLockATMService {

    private static final Duration MAX_TIMEOUT = Duration.ofSeconds(5);
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresAdvisoryLockATMService.class);

    @Autowired
    private AccountRepository repository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PostgresLockManager postgresLockManager;

    @Transactional
    public void withdraw(Long accountId, double amount) {

        LockKey key = LockKey.of("account", accountId);
        postgresLockManager.tryWithLock(key, MAX_TIMEOUT, () -> {

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
        });

    }
}
