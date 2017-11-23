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
package org.springframework.boot.autoconfigure.data.jdbc.support;

import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Greg Turnquist
 */
public final class TestUtilities {

	public static AnnotationConfigApplicationContext prepareApplicationContext(Class<?>... configurationClasses) {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

		if (configurationClasses.length > 0) {
			context.register(configurationClasses);
		}

		context.register(EmbeddedDataSourceConfiguration.class, //
			JdbcRepositoriesAutoConfiguration.class, //
			PropertyPlaceholderAutoConfiguration.class);

		context.refresh();

		return context;
	}
}