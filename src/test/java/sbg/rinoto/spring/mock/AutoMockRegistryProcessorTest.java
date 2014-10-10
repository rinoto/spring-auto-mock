package sbg.rinoto.spring.mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sbg.rinoto.spring.mock.classes.ClassToTest;

@ContextConfiguration(classes={AutoMockRegistryPostProcessor.class, ClassToTest.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AutoMockRegistryProcessorTest {
	
	@Inject
	private ClassToTest classToTest;
	
	@Test
	public void shouldUseRealImplementationIfAvailable() {
//		assertThat(classToTest.getInterfaceWithoutImpl(), instanceOf(Mockito.class));
	}
	

}
