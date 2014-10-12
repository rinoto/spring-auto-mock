package sbg.rinoto.spring.mock.classes;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

@Service
public class ClassToTest extends AbstractClassToTest {

	@Inject
	private ClassWithImpl classWithImpl;

	// field with a different name than the impl
	@Inject
	private InterfaceWithImpl interfaceWithImpl;

	// field with the same name than the impl
	@Inject
	private InterfaceWithImpl implForInterfaceWithImpl;;

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

	public InterfaceWithImpl getImplForInterfaceWithImpl() {
		return implForInterfaceWithImpl;
	}

}
