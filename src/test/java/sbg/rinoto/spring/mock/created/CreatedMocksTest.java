package sbg.rinoto.spring.mock.created;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import java.util.Map;

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
public class CreatedMocksTest {

	@Inject
	private InterfaceWithoutImpl mockObject;

	@Before
	public void doSomethingWithMockObject() {
		when(mockObject.emptyMethod()).thenReturn("hi");
	}

	@Test
	public void shouldReturnCreatedMocks() throws Exception {
		final Map<Class<?>, Object> createdMocks = AutoMockRegistryPostProcessor.getCreatedMocks();

		assertThat(createdMocks.keySet(), hasSize(7));
		assertThat(createdMocks.keySet(), hasItem(InterfaceWithoutImpl.class));
	}
}
