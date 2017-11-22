/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.schauderhaft.spring.data.jdbc.boot;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.DataAccessStrategy;
import org.springframework.data.jdbc.core.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.core.SqlGeneratorSource;
import org.springframework.data.jdbc.mapping.model.JdbcMappingContext;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * Spring Boot Autoconfiguration class for configuring beans for Spring Data Jdbc
 *
 * @author Jens Schauder
 */
@Configuration
@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(JdbcRepositoryFactoryBean.class)
@ConditionalOnMissingBean(JdbcRepositoryFactoryBean.class)
@Import({
		JdbcRepositoryAutoconfigurationRegistrar.class,
		JdbcTemplateAutoConfiguration.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class SpringDataJdbcAutoconfiguration {

	@Bean
	String justABean() {
		return "Hello Autoconfig";
	}


	@Bean
	JdbcMappingContext jdbcMappingContext() {
		return new JdbcMappingContext();
	}

	@Bean
	SqlGeneratorSource sqlGeneratorSource(JdbcMappingContext context) {
		return new SqlGeneratorSource(context);
	}

	@Bean
	DataAccessStrategy dataAccessStrategy(NamedParameterJdbcTemplate operations, SqlGeneratorSource sqlGeneratorSource, JdbcMappingContext context) {
		System.out.println("creating a data access strategy");
		return new DefaultDataAccessStrategy(sqlGeneratorSource, operations, context);
	}

}
