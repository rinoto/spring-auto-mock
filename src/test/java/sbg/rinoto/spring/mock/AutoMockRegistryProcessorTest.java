package sbg.rinoto.spring.mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static sbg.rinoto.spring.mock.matcher.IsMock.isMock;
import static sbg.rinoto.spring.mock.matcher.IsMock.isNotMock;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sbg.rinoto.spring.mock.classes.ClassToTest;
import sbg.rinoto.spring.mock.classes.ClassWithImpl;
import sbg.rinoto.spring.mock.classes.ImplForInterfaceWithImpl;

@ContextConfiguration(classes = { AutoMockRegistryPostProcessor.class, ClassToTest.class, ClassWithImpl.class,
		ImplForInterfaceWithImpl.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class AutoMockRegistryProcessorTest {

	@Inject
	private ClassToTest classToTest;

	@Test
	public void shouldUseMockIfImplementationOfInterfaceIsNotAvailable() {
		assertThat(classToTest.getInterfaceWithoutImpl(), isMock());
	}

	@Test
	public void shouldUseRealImplementationOfInterfaceIfAvailable() {
		assertThat(classToTest.getInterfaceWithImpl(), isNotMock());
	}

	@Test
	public void shouldUseRealImplementationOfClassIfAvailable() {
		assertThat(classToTest.getClassWithImpl(), isNotMock());
	}

}
