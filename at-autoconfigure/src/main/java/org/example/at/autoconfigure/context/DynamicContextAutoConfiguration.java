package org.example.at.autoconfigure.context;

import org.example.at.utils.DynamicContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ConditionalOnClass(DynamicContext.class)
public class DynamicContextAutoConfiguration {
    @Bean
    @Scope("cucumber-glue")
    public DynamicContext context() {
        return new DynamicContext();
    }
}
