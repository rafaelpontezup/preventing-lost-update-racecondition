package br.com.stackspot.nullbank.shared.hibernate;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * it seems like it's not necessary. Hibernate can understand this function correctly in JPQL queries.
 */
@Configuration
public class AdvisoryLockSqlFunctionsBuilderContributor implements MetadataBuilderContributor {

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder.applySqlFunction(
                "pg_try_advisory_xact_lock",
                new StandardSQLFunction(
                        "pg_try_advisory_xact_lock",
                        StandardBasicTypes.BOOLEAN
                )
        );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties -> {
            hibernateProperties.put(
                    "hibernate.metadata_builder_contributor",
                    AdvisoryLockSqlFunctionsBuilderContributor.class.getName()
            );
        };
    }

}
