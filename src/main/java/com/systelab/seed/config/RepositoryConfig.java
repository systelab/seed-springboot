package com.systelab.seed.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.systelab.seed.repository"},
        repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class RepositoryConfig {
}