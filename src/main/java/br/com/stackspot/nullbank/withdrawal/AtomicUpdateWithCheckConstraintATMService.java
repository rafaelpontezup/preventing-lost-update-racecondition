package br.com.stackspot.nullbank.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtomicUpdateWithCheckConstraintATMService extends NaiveAtomicUpdateATMService {

    /**
     * Important: don't forget to apply this DDL below to enable CHECK constraint
     * in the table before run the test:
     *
     *      ALTER TABLE account ADD CONSTRAINT balance_check CHECK (balance >= 0)
     */
    @Transactional
    @Override
    public void withdraw(Long accountId, double amount) {
        super.withdraw(accountId, amount);
    }
}
