package br.com.stackspot.nullbank.withdrawal;

import org.hibernate.LockOptions;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Transactional
    @Query("""
           select c.balance
             from Account c
            where c.id = :accountId
           """)
    public Double getBalance(Long accountId);

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Account c where c.id = :accountId")
    public Optional<Account> findByIdWithPessimisticLocking(Long accountId);

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select c from Account c where c.id = :accountId")
    public Optional<Account> findByIdWithPessimisticReadLocking(Long accountId);

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Account c where c.id = :accountId")
    @QueryHints({
        @QueryHint(name = "javax.persistence.lock.timeout", value = (LockOptions.NO_WAIT + ""))
    })
    public Optional<Account> findByIdWithPessimisticNoWaitLocking(Long accountId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = """
                   update account
                      set balance = (balance - :amount)
                    where id = :accountId
                   """
    )
    public int update(Long accountId, Double amount);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
           value = """
                   update account
                      set balance = (balance - :amount)
                    where id = :accountId
                      and (balance - :amount) >= 0
                   """
    )
    public int updateWithValidation(Long accountId, Double amount);

    @Transactional
//    @Modifying
    @Query(nativeQuery = true,
            value = """
                   update account
                      set balance = (balance - :amount)
                    where id = :accountId
                returning balance
                   """
    )
    public Double updateWithReturning(Long accountId, Double amount);

}
