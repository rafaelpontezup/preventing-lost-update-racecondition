package br.com.stackspot.nullbank.withdrawal;

import br.com.stackspot.nullbank.base.SpringBootIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PessimisticLockingWithReadLockATMServiceTest extends SpringBootIntegrationTest {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PessimisticLockingWithReadLockATMService pessimisticLockingReadLockATMService;

    private Account ACCOUNT;

    @BeforeEach
    public void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        this.ACCOUNT = accountRepository
                .save(new Account("Jordi", 100.0));
    }

    @Test
    @DisplayName("should withdraw money from account")
    public void t1() throws InterruptedException {

        pessimisticLockingReadLockATMService
                .withdraw(ACCOUNT.getId(), 20.0);

        assertEquals(80.0, accountRepository.getBalance(ACCOUNT.getId()), "account balance");
        assertEquals(1, transactionRepository.countByAccount(ACCOUNT), "number of transactions");
    }

    /**
     * Although it prevents Lost Update anomaly, it has a high rate of errors even with retries. Most of the
     * transactions are aborted due to deadlocks.
     *
     * Retries can help here, but they need long and random back-off periods to maximize the success rate. This article
     * can help to configure Spring Retry properly:
     * https://medium.com/@vmoulds01/springboot-retry-random-backoff-136f41a3211a
     *
     * It might work with fewer concurrent threads with a proper retry policy. But the more concurrent threads,
     * the worse it is.
     */
    @Test
    @DisplayName("⚠️ | should withdraw money from account concurrently")
    public void t2() throws InterruptedException {
        /**
         * The more concurrent threads, the worse success rate.
         */
        int numberOfThreads = 4;

        doSyncAndConcurrently(numberOfThreads, s -> {
            pessimisticLockingReadLockATMService
                    .withdraw(ACCOUNT.getId(), 25.0);
        });

        assertAll("account and transaction states",
            () -> assertEquals(0.0, accountRepository.getBalance(ACCOUNT.getId()), "account balance"),
            () -> assertEquals(numberOfThreads, transactionRepository.countByAccount(ACCOUNT), "number of transactions")
        );
    }

}