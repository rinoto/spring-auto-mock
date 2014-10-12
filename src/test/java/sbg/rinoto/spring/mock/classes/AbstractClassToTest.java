package sbg.rinoto.spring.mock.classes;

import javax.inject.Inject;

public class AbstractClassToTest {

	@Inject
	private ClassWithImpl classWithImpl;

	public ClassWithImpl getClassWithImplInParent() {
		return classWithImpl;
	}

}
