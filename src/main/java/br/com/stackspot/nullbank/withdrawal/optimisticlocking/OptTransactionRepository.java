package br.com.stackspot.nullbank.withdrawal.optimisticlocking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptTransactionRepository extends JpaRepository<OptTransaction, Long> {

    public int countByAccount(OptAccount account);
}
