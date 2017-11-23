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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.boot.autoconfigure.data.jdbc.support.TestUtilities.prepareApplicationContext;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.support.PersonRepository;
import org.springframework.boot.testsupport.runner.classpath.ClassPathExclusions;
import org.springframework.boot.testsupport.runner.classpath.ModifiedClassPathRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.DataAccessStrategy;
import org.springframework.data.jdbc.core.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.mapping.model.ConversionCustomizer;
import org.springframework.data.jdbc.mapping.model.NamingStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

/**
 * Test the core features of Spring Data JDBC with MyBatis excluded from the testing classpath.
 *
 * @author Greg Turnquist
 */
@RunWith(ModifiedClassPathRunner.class)
@ClassPathExclusions("mybatis-*.jar")
public class JdbcRepositoriesAutoConfigurationTests {

	static private DataAccessStrategy dataAccessStrategy;
	static private NamingStrategy namingStrategy;
	static private ConversionCustomizer conversionCustomizer;

	private AnnotationConfigApplicationContext context;

	@After
	public void tearDown() {
		this.context.close();
	}

	@Test
	public void defaultDataAccessStrategyWhenNoOther() {

		this.context = prepareApplicationContext(TestConfiguration.class);

		assertThat(this.context.getBean(DataAccessStrategy.class)).isInstanceOf(DefaultDataAccessStrategy.class);

		assertThat(this.context.getBean(NamedParameterJdbcOperations.class)).isNotNull();
		assertThat(this.context.getBean(PersonRepository.class)).isNotNull();
	}

	@Test
	public void overrideStandardBeans() {

		dataAccessStrategy = mock(DataAccessStrategy.class);
		namingStrategy = mock(NamingStrategy.class);
		conversionCustomizer = mock(ConversionCustomizer.class);

		this.context = prepareApplicationContext(OverrideEverything.class);

		assertThat(this.context.getBean(DataAccessStrategy.class)).isEqualTo(dataAccessStrategy);
		assertThat(this.context.getBean(NamingStrategy.class)).isEqualTo(namingStrategy);
		assertThat(this.context.getBean(ConversionCustomizer.class)).isEqualTo(conversionCustomizer);
	}

	@Configuration
	@EnableAutoConfiguration
	static class TestConfiguration {

	}

	@Configuration
	@EnableAutoConfiguration
	static class OverrideEverything {

		@Bean
		DataAccessStrategy dataAccessStrategy() {
			return dataAccessStrategy;
		}

		@Bean
		NamingStrategy namingStrategy() {
			return namingStrategy;
		}

		@Bean
		ConversionCustomizer conversionCustomizer() {
			return conversionCustomizer;
		}
	}
}
