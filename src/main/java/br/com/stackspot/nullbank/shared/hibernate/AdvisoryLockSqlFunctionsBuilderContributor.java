package br.com.stackspot.nullbank.shared.hibernate;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * We must register it via {@code /resources/META-INF/services/org.hibernate.boot.model.FunctionContributor}
 * file with its fully qualified class name.
 *
 * For more details, this article can help:
 * https://aregall.tech/hibernate-6-custom-functions#heading-how-to-register-custom-functions-in-hibernate-6
 */
@Configuration
public class AdvisoryLockSqlFunctionsBuilderContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry()
                .register(
                        "pg_try_advisory_xact_lock",
                        new StandardSQLFunction(
                                "pg_try_advisory_xact_lock",
                                StandardBasicTypes.BOOLEAN
                        )
                );
    }

    /**
     * ⚠️ It does NOT work with Spring Boot 3.2.x
     * https://discourse.hibernate.org/t/migrate-hibernate-5-to-6-with-spring-boot-2-7-x-to-3/7787/2
     */
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties -> {
            hibernateProperties.put(
                    "hibernate.function_contributor", // 😭😭
                    AdvisoryLockSqlFunctionsBuilderContributor.class.getName()
            );
        };
    }

}
