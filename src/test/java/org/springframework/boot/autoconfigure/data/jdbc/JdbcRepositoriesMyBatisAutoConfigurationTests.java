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
import static org.springframework.boot.autoconfigure.data.jdbc.support.TestUtilities.prepareApplicationContext;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.autoconfigure.data.jdbc.support.PersonRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.jdbc.core.DataAccessStrategy;
import org.springframework.data.jdbc.mapping.model.NamingStrategy;
import org.springframework.data.jdbc.mybatis.MyBatisDataAccessStrategy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

/**
 * Test MyBatis-specific features of Spring Data JDBC.
 * 
 * @author Greg Turnquist
 */
public class JdbcRepositoriesMyBatisAutoConfigurationTests {

	private AnnotationConfigApplicationContext context;

	static private DataAccessStrategy dataAccessStrategy;
	static private NamingStrategy namingStrategy;

	@After
	public void tearDown() {
		this.context.close();
	}

	@Test
	public void myBatisDefaultsWork() {

		this.context = prepareApplicationContext(JdbcRepositoriesAutoConfigurationTests.TestConfiguration.class);

		assertThat(this.context.getBean(DataAccessStrategy.class)).isInstanceOf(MyBatisDataAccessStrategy.class);

		assertThat(this.context.getBean(NamedParameterJdbcOperations.class)).isNotNull();
		assertThat(this.context.getBean(PersonRepository.class)).isNotNull();
	}
}
