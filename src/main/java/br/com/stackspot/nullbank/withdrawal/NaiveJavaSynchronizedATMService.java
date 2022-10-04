package br.com.stackspot.nullbank.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NaiveJavaSynchronizedATMService {

    @Autowired
    private NaiveATMService originalService;

    /**
     * IMPORTANT: it works only on single JVM, I mean, this solution
     * does NOT work on clustered environment
     *
     * Another detail: @Transaction does NOT work well with synchronized, they should be used separately
     * - https://stackoverflow.com/questions/41767860/spring-transactional-with-synchronized-keyword-doesnt-work
     */
    public synchronized void withdraw(Long accountId, double amount) {
        originalService
                .withdraw(accountId, amount);
    }

}
