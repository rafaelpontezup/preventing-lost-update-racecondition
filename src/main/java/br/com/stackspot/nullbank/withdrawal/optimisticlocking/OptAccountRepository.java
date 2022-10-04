package br.com.stackspot.nullbank.withdrawal.optimisticlocking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface OptAccountRepository extends JpaRepository<OptAccount, Long> {

    @Transactional
    @Query("""
           select c.balance
             from OptAccount c
            where c.id = :accountId
           """)
    public Double getBalance(Long accountId);

}
