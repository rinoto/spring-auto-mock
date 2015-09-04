package sbg.rinoto.spring.mock.classes;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigClassToTest {

	@Bean
	public ClassDefinedInConfig create() {
		return new ClassDefinedInConfig();
	}

	@Inject
	private ClassWithImpl classWithImpl;
	public static ClassWithImpl classWithImpl_static;

	// field with a different name than the impl
	@Inject
	private InterfaceWithImpl interfaceWithImpl;
	public static InterfaceWithImpl interfaceWithImpl_static;

	// field with the same name than the impl
	@Inject
	private InterfaceWithImpl implForInterfaceWithImpl;;
	public static InterfaceWithImpl implForInterfaceWithImpl_static;

	@Inject
	private InterfaceWithoutImpl interfaceWithoutImpl;
	public static InterfaceWithoutImpl interfaceWithoutImpl_static;

	@PostConstruct
	public void init() {
		classWithImpl_static = classWithImpl;
		interfaceWithImpl_static = interfaceWithImpl;
		implForInterfaceWithImpl_static = implForInterfaceWithImpl;
		interfaceWithoutImpl_static = interfaceWithoutImpl;
	}

}
