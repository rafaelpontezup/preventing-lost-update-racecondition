package br.com.stackspot.nullbank.withdrawal.optimisticlocking;

import br.com.stackspot.nullbank.base.SpringBootIntegrationTest;
import br.com.stackspot.nullbank.withdrawal.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OptimisticLockingATMServiceTest  extends SpringBootIntegrationTest {

    @Autowired
    private OptAccountRepository accountRepository;
    @Autowired
    private OptTransactionRepository transactionRepository;

    @Autowired
    private OptimisticLockingATMService optimisticLockingATMService;

    private OptAccount ACCOUNT;

    @BeforeEach
    public void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        this.ACCOUNT = accountRepository
                .save(new OptAccount("Jordi", 100.0));
    }

    @Test
    @DisplayName("should withdraw money from account")
    public void t1() throws InterruptedException {

        optimisticLockingATMService
                .withdraw(ACCOUNT.getId(), 20.0);

        assertEquals(80.0, accountRepository.getBalance(ACCOUNT.getId()), "account balance");
        assertEquals(1, transactionRepository.countByAccount(ACCOUNT), "number of transactions");
    }

    @Test
    @DisplayName("should withdraw money from account concurrently")
    public void t2() throws InterruptedException {

        doSyncAndConcurrently(10, s -> {
            optimisticLockingATMService
                    .withdraw(ACCOUNT.getId(), 20.0);
        });

        assertAll("account and transaction states",
                () -> assertEquals(0.0, accountRepository.getBalance(ACCOUNT.getId()), "account balance"),
                () -> assertEquals(5, transactionRepository.countByAccount(ACCOUNT), "number of transactions")
        );
    }

}