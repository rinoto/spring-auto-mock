package sbg.rinoto.spring.mock;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sbg.rinoto.spring.mock.ConfigurationTest.SomeConfig;

/**
 * Test case provided by mibutec to solve #1
 * https://github.com/rinoto/spring-auto-mock/issues/1
 * 
 * @author ruben
 *
 */
@ContextConfiguration(classes = { AutoMockRegistryPostProcessor.class, SomeConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ConfigurationTest {
	@Autowired
	private HelloWorld helloWorld;

	@Before
	public void setup() {
		when(helloWorld.sayHello()).thenReturn("its working");
	}

	@Test
	public void shouldAutowireTypesDefinedInComponentFromBeanAnnotation() {
		assertThat(helloWorld.sayHello(), is("its working"));
	}

	@Configuration
	public static class SomeConfig {
		@Bean
		public HelloComponent helloComponent() {
			return new HelloComponent();
		}
	}

	public static interface HelloWorld {
		String sayHello();
	}

	public static class HelloComponent {
		@Autowired
		private HelloWorld helloWorld;
	}
}