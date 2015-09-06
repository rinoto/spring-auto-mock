package sbg.rinoto.spring.mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static sbg.rinoto.spring.mock.matcher.IsImplementation.isImplementation;
import static sbg.rinoto.spring.mock.matcher.IsMock.isMock;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sbg.rinoto.spring.mock.classes.ClassToTest;
import sbg.rinoto.spring.mock.classes.ClassWithImpl;
import sbg.rinoto.spring.mock.classes.ClassWithNamedInClasspath;
import sbg.rinoto.spring.mock.classes.ConfigClassToTest;
import sbg.rinoto.spring.mock.classes.ImplForInterfaceWithImpl;
import sbg.rinoto.spring.mock.classes.InterfaceWithoutImpl;

@ContextConfiguration(classes = { AutoMockRegistryPostProcessor.class, ClassToTest.class, ClassWithImpl.class,
		ImplForInterfaceWithImpl.class, ConfigClassToTest.class, ClassWithNamedInClasspath.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class AutoMockRegistryProcessorTest {

	@Inject
	private ClassToTest classToTest;

	@Inject
	private InterfaceWithoutImpl interfaceWithoutImpl;

	@Test
	public void shouldUseMockIfImplementationOfInterfaceIsNotAvailable() {
		assertThat(classToTest.getInterfaceWithoutImpl(), isMock());
	}

	@Test
	public void shouldUseRealImplementationOfInterfaceIfAvailableWhenFieldHasADifferentNameThanTheImpl() {
		assertThat(classToTest.getInterfaceWithImpl(), isImplementation());
	}

	@Test
	public void shouldUseRealImplementationOfInterfaceIfAvailableWhenFieldHasSameNameThanTheImpl() {
		assertThat(classToTest.getImplForInterfaceWithImpl(), isImplementation());
	}

	@Test
	public void shouldUseRealImplementationOfClassIfAvailable() {
		assertThat(classToTest.getClassWithImpl(), isImplementation());
	}

	@Test
	public void shouldUseRealImplementationOfClassIfAvailableInParent() {
		assertThat(classToTest.getClassWithImplInParent(), isImplementation());
	}

	@Test
	public void shouldInjectMockAlsoInTest() {
		assertThat(interfaceWithoutImpl, isMock());
	}

	@Test
	public void mockInjectedInTestShouldBeTheSameThanInjectedInDependentClass() {
		assertThat(interfaceWithoutImpl, sameInstance(classToTest.getInterfaceWithoutImpl()));
	}

	@Test
	public void shouldUseMockIfImplementationOfInterfaceIsNotAvailableInConfigClass() {
		assertThat(ConfigClassToTest.interfaceWithoutImpl_static, isMock());
	}

	@Test
	public void shouldUseRealImplementationOfClassIfAvailableInConfigClass() {
		assertThat(ConfigClassToTest.classWithImpl_static, isImplementation());
	}

	@Test
	public void shouldUseRealImplementationOfClassDefinedInConfigurationIfAvailable() {
		assertThat(classToTest.getClassDefinedInConfig(), isImplementation());
	}

	@Test
	public void shouldUseRealImplementationForClassWithNamedAnnotationInClasspath() {
		assertThat(classToTest.getClassWithNamedInClasspath(), isImplementation());
	}

	@Test
	public void shouldUseMockForClassWithNamedAnnotationNotInClasspath() {
		assertThat(classToTest.getClassWithNamedNotInClasspath(), isMock());
	}

}
