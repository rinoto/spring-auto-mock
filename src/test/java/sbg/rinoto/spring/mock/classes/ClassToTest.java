package sbg.rinoto.spring.mock.classes;

import javax.inject.Inject;
import javax.inject.Named;

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

	@Inject
	private ClassDefinedInConfig classDefinedInConfig;

	@Inject
	@Named("namedClassNotInCP")
	private ClassWithNamedNotInClasspath classWithNamedNotInClasspath;

	@Inject
	@Named("namedClassInCP")
	private ClassWithNamedInClasspath classWithNamedInClasspath;

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

	public ClassDefinedInConfig getClassDefinedInConfig() {
		return classDefinedInConfig;
	}

	public ClassWithNamedNotInClasspath getClassWithNamedNotInClasspath() {
		return classWithNamedNotInClasspath;
	}

	public ClassWithNamedInClasspath getClassWithNamedInClasspath() {
		return classWithNamedInClasspath;
	}

}
