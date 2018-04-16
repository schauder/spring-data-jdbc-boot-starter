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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.support.PersonRepository;
import org.springframework.boot.autoconfigure.data.jdbc.support.TestUtilities;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.CascadingDataAccessStrategy;
import org.springframework.data.jdbc.core.DataAccessStrategy;
import org.springframework.data.jdbc.core.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.mapping.model.ConversionCustomizer;
import org.springframework.data.jdbc.mapping.model.NamingStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

/**
 * Test the core features of Spring Data JDBC with MyBatis excluded from the testing classpath.
 *
 * @author Greg Turnquist
 * @author Jens Schauder
 */
public class JdbcRepositoriesAutoConfigurationTests {

	static private DataAccessStrategy dataAccessStrategy;
	static private NamingStrategy namingStrategy;
	static private ConversionCustomizer conversionCustomizer;

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withClassLoader(new FilteredClassLoader(SqlSessionFactory.class));

	@Test
	public void defaultDataAccessStrategyWhenNoOther() {

		contextRunner.withConfiguration(AutoConfigurations.of(TestConfiguration.class)).run(context -> { //
			assertThat(context) //
					.hasSingleBean(NamedParameterJdbcOperations.class) //
					.hasSingleBean(PersonRepository.class) //
					.getBean(DataAccessStrategy.class) //
					.isOfAnyClassIn(CascadingDataAccessStrategy.class) //
					.satisfies(das -> { //
						assertThat(TestUtilities.<List> getField(das, "strategies")) //
								.extracting(Object::getClass) //
								.containsExactly(DefaultDataAccessStrategy.class); //
					}); //
		});

	}

	@Test
	public void overrideStandardBeans() {

		dataAccessStrategy = mock(DataAccessStrategy.class);
		namingStrategy = mock(NamingStrategy.class);
		conversionCustomizer = mock(ConversionCustomizer.class);

		contextRunner.withConfiguration(AutoConfigurations.of(OverrideEverything.class)) //
				.run(context -> { //
					assertThat(context).getBean(DataAccessStrategy.class).isEqualTo(dataAccessStrategy); //
					assertThat(context).getBean(NamingStrategy.class).isEqualTo(namingStrategy); //
					assertThat(context).getBean(ConversionCustomizer.class).isEqualTo(conversionCustomizer); //
				});
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
