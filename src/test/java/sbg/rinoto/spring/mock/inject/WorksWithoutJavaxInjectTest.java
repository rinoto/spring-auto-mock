package sbg.rinoto.spring.mock.inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sbg.rinoto.spring.mock.AutoMockRegistryPostProcessor;
import sbg.rinoto.spring.mock.ConfigurationTest.HelloComponent;
import sbg.rinoto.spring.mock.ConfigurationTest.HelloWorld;

/**
 * @author mibutec
 */
@RunWith(TestRunner.class)
public class WorksWithoutJavaxInjectTest {
	private HelloWorld helloWorld;

	private ClassLoader oldClassloader;

	private AnnotationConfigApplicationContext context;

	@Before
	public void setup() throws Exception {
		oldClassloader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		context = new AnnotationConfigApplicationContext(AutoMockRegistryPostProcessor.class, HelloComponent.class);
		helloWorld = context.getBean(HelloWorld.class);
		when(helloWorld.sayHello()).thenReturn("its working");
	}

	@After
	public void tearDown() {
		context.close();
		Thread.currentThread().setContextClassLoader(oldClassloader);
	}

	@Test
	public void shouldAutowireTypesDefinedInComponentFromBeanAnnotation() {
		assertThat(helloWorld.sayHello(), is("its working"));
	}

}
