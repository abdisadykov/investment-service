package com.abdisadykov.tinkoffservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.tinkoff.piapi.core.InvestApi;


@Configuration
@EnableAsync
public class ApplicationConfig {

    @Bean
    public InvestApi api() {
        var ssoToken = System.getenv("ssoToken");
        return InvestApi.createSandbox(ssoToken);
    }

}
