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

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.DataAccessStrategy;
import org.springframework.data.jdbc.core.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.core.SqlGeneratorSource;
import org.springframework.data.jdbc.mapping.model.ConversionCustomizer;
import org.springframework.data.jdbc.mapping.model.DefaultNamingStrategy;
import org.springframework.data.jdbc.mapping.model.JdbcMappingContext;
import org.springframework.data.jdbc.mapping.model.NamingStrategy;
import org.springframework.data.jdbc.mybatis.MyBatisContext;
import org.springframework.data.jdbc.mybatis.MyBatisDataAccessStrategy;
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
@ConditionalOnMissingBean({JdbcRepositoryFactoryBean.class, JdbcRepositoryConfigExtension.class})
@ConditionalOnProperty(prefix = "spring.data.jdbc.repositories", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(JdbcRepositoriesAutoconfigureRegistrar.class)
@AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
public class JdbcRepositoriesAutoConfiguration {

	@Bean
	JdbcMappingContext jdbcMappingContext(NamingStrategy namingStrategy, ConversionCustomizer conversionCustomizer) {
		return new JdbcMappingContext(namingStrategy, conversionCustomizer);
	}

	@Bean
	@ConditionalOnMissingBean
	NamingStrategy namingStrategy() {
		return new DefaultNamingStrategy();
	}

	@Bean
	@ConditionalOnMissingBean
	ConversionCustomizer conversionCustomizer() {
		return conversionService -> {};
	}

	@Configuration
	@ConditionalOnClass(SqlSessionFactory.class)
	@AutoConfigureAfter(DataSourceAutoConfiguration.class)
	static class MyBatisConfiguration {

		@Bean
		SqlSessionFactoryBean createSessionFactory(DataSource dataSource) {

			org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
			configuration.getTypeAliasRegistry().registerAlias("MyBatisContext", MyBatisContext.class);

			// Unit tests in Spring Data JDBC register extra aliases and mappers. I took those out,
			// sure what the impact is.

			SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
			sqlSessionFactoryBean.setDataSource(dataSource);
			sqlSessionFactoryBean.setConfiguration(configuration);

			return sqlSessionFactoryBean;
		}

		@Bean
		@ConditionalOnMissingBean
		DataAccessStrategy myBatisDataAccessStrategy(SqlSessionFactory factory) {
			return new MyBatisDataAccessStrategy(factory);
		}
	}

	@Configuration
	@ConditionalOnMissingClass("org.apache.ibatis.session.SqlSessionFactory")
	@AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
	static class JdbcTemplateBasedConfiguration {

		@Bean
		SqlGeneratorSource sqlGeneratorSource(JdbcMappingContext context) {
			return new SqlGeneratorSource(context);
		}

		@Bean
		@ConditionalOnMissingBean
		DataAccessStrategy defaultDataAccessStrategy(NamedParameterJdbcOperations operations, SqlGeneratorSource sqlGeneratorSource, JdbcMappingContext context) {
			return new DefaultDataAccessStrategy(sqlGeneratorSource, operations, context);
		}
	}

}
