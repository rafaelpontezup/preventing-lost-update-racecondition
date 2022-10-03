package br.com.stackspot.nullbank.withdrawal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
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
