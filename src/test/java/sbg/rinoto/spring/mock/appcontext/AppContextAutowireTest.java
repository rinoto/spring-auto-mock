package sbg.rinoto.spring.mock.appcontext;

import static org.hamcrest.MatcherAssert.assertThat;
import static sbg.rinoto.spring.mock.matcher.IsImplementation.isImplementation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sbg.rinoto.spring.mock.AutoMockRegistryPostProcessor;
import sbg.rinoto.spring.mock.classes.ClassToTest;

@ContextConfiguration(classes = { AutoMockRegistryPostProcessor.class, ClassToTest.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
public class AppContextAutowireTest {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void shouldNotMockAppContext() throws Exception {
		assertThat(applicationContext, isImplementation());
	}

}
