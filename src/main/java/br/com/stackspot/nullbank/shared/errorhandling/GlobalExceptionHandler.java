package br.com.stackspot.nullbank.shared.errorhandling;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.StatusType;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import java.net.URI;

/**
 * Default application exception handler. This handler is based on Zalando Problem Library, which
 * follows the RFC7807 Problem.
 *
 * If you need to customize any kind of error/exception, you can read the documentation here:
 * https://github.com/zalando/problem-spring-web#customization
 *
 * TODO:
 *  - https://github.com/zalando/problem-spring-web#stack-traces-and-causal-chains
 */
@ControllerAdvice
public class GlobalExceptionHandler implements ProblemHandling {

    /**
     * Prints a better detail message when catching a <code>ResponseStatusException</code>
     */
    @Override
    public ProblemBuilder prepare(Throwable throwable, StatusType status, URI type) {
        ProblemBuilder builder = ProblemHandling.super.prepare(throwable, status, type);
        if (throwable instanceof ResponseStatusException exception) {
            return builder
                    .withDetail(exception.getReason()); // it's better than exception.getMessage()
        }
        return builder;
    }

}
