package sbg.rinoto.spring.mock.classes;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

@Service
public class ClassToTest {

	@Inject
	private ClassWithImpl classWithImpl;

	@Inject
	private InterfaceWithImpl interfaceWithImpl;

	@Inject
	private InterfaceWithoutImpl interfaceWithoutImpl;

	public ClassWithImpl getClassWithImpl() {
		return classWithImpl;
	}

	public InterfaceWithImpl getInterfaceWithImpl() {
		return interfaceWithImpl;
	}

	public InterfaceWithoutImpl getInterfaceWithoutImpl() {
		return interfaceWithoutImpl;
	}

}
