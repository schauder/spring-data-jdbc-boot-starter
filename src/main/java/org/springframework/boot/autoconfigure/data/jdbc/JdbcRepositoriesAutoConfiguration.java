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
package org.springframework.boot.autoconfigure.data.jdbc;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.CascadingDataAccessStrategy;
import org.springframework.data.jdbc.core.DataAccessStrategy;
import org.springframework.data.jdbc.core.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.core.DelegatingDataAccessStrategy;
import org.springframework.data.jdbc.core.SqlGeneratorSource;
import org.springframework.data.jdbc.mapping.model.ConversionCustomizer;
import org.springframework.data.jdbc.mapping.model.JdbcMappingContext;
import org.springframework.data.jdbc.mapping.model.NamingStrategy;
import org.springframework.data.jdbc.repository.config.JdbcRepositoryConfigExtension;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

/**
 * Autoconfigure Spring Data JDBC
 *
 * @author Jens Schauder
 * @author Greg Turnquist
 */
@Configuration
@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(JdbcRepositoryFactoryBean.class)
@ConditionalOnMissingBean({ JdbcRepositoryFactoryBean.class, JdbcRepositoryConfigExtension.class })
@ConditionalOnProperty( //
		prefix = "spring.data.jdbc.repositories", //
		name = "enabled", //
		havingValue = "true", //
		matchIfMissing = true)
@Import({ JdbcRepositoriesAutoconfigureRegistrar.class, JdbcRepositoriesMyBatisAutoConfiguration.class })
@AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
public class JdbcRepositoriesAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	JdbcMappingContext jdbcMappingContext(NamingStrategy namingStrategy, NamedParameterJdbcOperations operations,
			ConversionCustomizer conversionCustomizer) {
		return new JdbcMappingContext(namingStrategy, operations, conversionCustomizer);
	}

	@Bean
	@ConditionalOnMissingBean
	NamingStrategy namingStrategy() {
		return NamingStrategy.INSTANCE;
	}

	@Bean
	@ConditionalOnMissingBean
	ConversionCustomizer conversionCustomizer() {
		return conversionService -> {};
	}

	@Bean
	@ConditionalOnMissingBean
	SqlGeneratorSource sqlGeneratorSource(JdbcMappingContext context) {
		return new SqlGeneratorSource(context);
	}

	@Bean
	@ConditionalOnMissingBean
	DataAccessStrategy dataAccessStrategy(SqlGeneratorSource sqlGeneratorSource, JdbcMappingContext context) {
		return buildDataAccessStrategy(new DefaultDataAccessStrategy(sqlGeneratorSource, context));
	}

	static DataAccessStrategy buildDataAccessStrategy(DataAccessStrategy... accessStrategies) {

		DelegatingDataAccessStrategy delegatingDataAccessStrategy = new DelegatingDataAccessStrategy();

		CascadingDataAccessStrategy strategy = new CascadingDataAccessStrategy(Arrays.asList(accessStrategies));
		delegatingDataAccessStrategy.setDelegate(strategy);

		return strategy;
	}
}
