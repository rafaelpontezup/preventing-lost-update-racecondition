package br.com.stackspot.nullbank.shared.lockmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * A simple lock manager implementation
 */
@Component
public class PostgresLockManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresLockManager.class);

    private final JdbcTemplate jdbcTemplate;

    public PostgresLockManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Tries to acquire a distributed lock from the database before executing the operation
     * until the timeout expires
     */
    @Transactional
    public void tryWithLock(LockKey key, Duration timeout, Runnable operation) {
        lock(key.getKey(), timeout);
        operation.run();
    }

    private void lock(final String key, Duration timeout) {

        // creates and configure a RetryTemplate
        RetryTemplate retryTemplate
                = RetryTemplate.builder()
                    .maxAttempts(1000) // tries as much as possible
                    .exponentialBackoff(100, 2, timeout.toMillis(), true)
                    .retryOn(AdvisoryLockNotAcquiredException.class)
                    .traversingCauses()
                    .build();

        LOGGER.info("Acquiring pg_try_advisory_xact_lock() for key '{}'", key);

        // tries to acquire a lock until the timeout expires
        retryTemplate.execute(retryContext -> {
            boolean acquired = jdbcTemplate
                    .queryForObject("select pg_try_advisory_xact_lock(pg_catalog.hashtextextended(?, 0))", Boolean.class, key);

            if (!acquired) {
                throw new AdvisoryLockNotAcquiredException("Advisory lock not acquired for key '" + key + "'");
            }
            return null;
        });
    }

    /**
     * Exception thrown when it's not possible to acquire an advisory lock from database
     */
    class AdvisoryLockNotAcquiredException extends RuntimeException {

        public AdvisoryLockNotAcquiredException(String message) {
            super(message);
        }
    }
}
