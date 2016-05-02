package sbg.rinoto.spring.mock.reset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sbg.rinoto.spring.mock.AutoMockRegistryPostProcessor;
import sbg.rinoto.spring.mock.classes.ClassToTest;
import sbg.rinoto.spring.mock.classes.InterfaceWithoutImpl;

@ContextConfiguration(classes = { AutoMockRegistryPostProcessor.class, ClassToTest.class })
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext
public class ResetMocksTest {

	@Inject
	private InterfaceWithoutImpl mockObject;

	@Before
	public void setup() {
		AutoMockRegistryPostProcessor.initMocks();
	}

	@Test
	public void a_shouldMockFirstTime() throws Exception {
		// given
		when(mockObject.emptyMethod()).thenReturn("hi");

		// when - then
		assertThat(mockObject.emptyMethod(), is("hi"));
	}

	@Test
	public void b_shouldHaveCleanedUpMock() throws Exception {
		assertThat(mockObject.emptyMethod(), nullValue());
	}

}
