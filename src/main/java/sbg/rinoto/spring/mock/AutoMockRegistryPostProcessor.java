package sbg.rinoto.spring.mock;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.util.ClassUtils;

/**
 * It automagically creates mockito mocks for dependencies not found on the
 * classpath.
 * <p>
 * <h2>Usage</h2>
 * Register the <code>AutoMockRegistryPostProcessor</code> in your test like
 * this:
 * 
 * <pre>
 * &#064;ContextConfiguration(classes = { AutoMockRegistryPostProcessor.class, RestOfClasses.class, ... })
 * &#064;RunWith(SpringJUnit4ClassRunner.class)
 * public class YourTest {
 * ...
 * }
 * </pre>
 * <p>
 * Initially based on the work from Justin Ryan published on <a
 * href="http://java.dzone.com/tips/automatically-inject-mocks">DZone</a>
 */
public class AutoMockRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	// in order to avoid direct dependencies to javax.inject, we load the
	// classes via name
	private static final Class<? extends Annotation> JAVAX_INJECT_CLASS = initAnnotation("javax.inject.Inject");
	private static final Class<? extends Annotation> JAVAX_NAMED_CLASS = initAnnotation("javax.inject.Named");
	private static final Method VALUE_METHOD_FROM_NAMED = initMethod(JAVAX_NAMED_CLASS, "value");

	private static final Map<Class<?>, Object> MOCKS = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	private static Class<? extends Annotation> initAnnotation(String className) {
		try {
			return (Class<? extends Annotation>) ClassUtils.forName(className,
					AutoMockRegistryPostProcessor.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			// JSR-330 not available, it's ok
		}
		return null;
	}

	private static Method initMethod(Class<? extends Annotation> clazz, String methodName) {
		if (clazz == null) {
			return null;
		}
		try {
			return clazz.getMethod(methodName);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException("Annotation " + clazz.getName()
					+ " is present in classpath, but we cannot inspect the method " + methodName, e);
		}
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

		MOCKS.clear();

		for (String beanName : registry.getBeanDefinitionNames()) {
			final BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
			// final String beanClassName = beanDefinition.getBeanClassName();
			registerMocksForBean(registry, beanDefinition);
		}
	}

	private void registerMocksForBean(BeanDefinitionRegistry registry, final BeanDefinition beanDefinition) {
		Class<?> beanClass = getBeanClass(beanDefinition);
		registerMocksForClass(registry, beanClass);
	}

	private void registerMocksForClass(BeanDefinitionRegistry registry, Class<?> beanClass) {
		if (beanClass == null) {
			return;
		}
		for (final FieldDefinition fieldDef : findAllAutoWired(beanClass)) {
			if (!isBeanAlreadyRegistered(registry, fieldDef)) {
				registerMockFactoryBeanForField(registry, fieldDef);
			}
		}
		// the parents also need to be registered
		registerMocksForClass(registry, beanClass.getSuperclass());

	}

	private boolean isBeanAlreadyRegistered(BeanDefinitionRegistry registry, FieldDefinition fieldDef) {
		if (ListableBeanFactory.class.isInstance(registry)) {
			ListableBeanFactory listableBeanFactory = ListableBeanFactory.class.cast(registry);
			if (listableBeanFactory.getBeanNamesForType(fieldDef.type).length != 0) {
				return true;
			}
		} else if (registry.isBeanNameInUse(fieldDef.name)) {
			// if BeanDefinitionRegistry doesn't implement ListableBeanFactory,
			// fall back to name check
			return true;
		}
		return false;
	}

	private Class<?> getBeanClass(final BeanDefinition beanDefinition) {

		final String beanClassName = beanDefinition.getBeanClassName();
		if (beanClassName == null) {
			return getClassFromMethodMetadata(beanDefinition);
		}

		try {
			return Class.forName(beanClassName);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("Class not found for bean: " + beanClassName);
		}
	}

	/**
	 * Case when the Bean is being declared in a @Configuration, and we cannot
	 * get the BeanClassName directly from the BeanDefinition.
	 * <p>
	 * In this case we need to get the class from the IntrospectedMethod in the
	 * beanDefinition "source"
	 * 
	 * @param beanDefinition
	 * @return
	 */
	private Class<?> getClassFromMethodMetadata(final BeanDefinition beanDefinition) {
		final Object source = beanDefinition.getSource();
		if (source != null && StandardMethodMetadata.class.isInstance(source)) {
			final StandardMethodMetadata methodMetadata = StandardMethodMetadata.class.cast(source);
			final Method introspectedMethod = methodMetadata.getIntrospectedMethod();
			if (introspectedMethod != null) {
				return introspectedMethod.getReturnType();
			}
		}
		return null;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// nothing to do
	}

	private Set<FieldDefinition> findAllAutoWired(Class<?> targetBean) {
		// first finding all fields
		Set<FieldDefinition> nonAutowired = new HashSet<FieldDefinition>();
		List<Field> declaredFields = Arrays.asList(targetBean.getDeclaredFields());
		for (Field field : declaredFields) {
			if (!field.getType().isArray() && !field.getType().isPrimitive() && isAutowiringAnnotationPresent(field)) {
				String fieldName = field.getName();
				if (isNamedAnnotationPresent(field)) {
					fieldName = getValueFromNamedAnnotation(field);
				}
				nonAutowired.add(new FieldDefinition(fieldName, field.getType()));
			}
		}
		// now the constructors
		Constructor<?>[] constructors = targetBean.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			if (isAutowiringAnnotationPresent(constructor)) {
				Class<?>[] typeParameters = constructor.getParameterTypes();
				for (Class<?> typeParameter : typeParameters) {
					nonAutowired.add(new FieldDefinition(typeParameter.getSimpleName(), typeParameter));
				}
			}
		}
		return nonAutowired;
	}

	private String getValueFromNamedAnnotation(AnnotatedElement field) {
		if (VALUE_METHOD_FROM_NAMED == null) {
			// JSR-330 is not available - it's ok
			return null;
		}
		// field.getAnnotation(JAVAX_NAMED_CLASS).value();
		final Annotation namedAnnotation = field.getAnnotation(JAVAX_NAMED_CLASS);
		try {
			return (String) VALUE_METHOD_FROM_NAMED.invoke(namedAnnotation);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalStateException(
					"@Named annotation is present in classpath, but we cannot execute the value() method!", e);
		}
	}

	private boolean isNamedAnnotationPresent(AnnotatedElement field) {
		return JAVAX_NAMED_CLASS != null && VALUE_METHOD_FROM_NAMED != null
				&& field.isAnnotationPresent(JAVAX_NAMED_CLASS);
	}

	private boolean isAutowiringAnnotationPresent(AnnotatedElement field) {
		return field.isAnnotationPresent(Autowired.class)
				|| (JAVAX_INJECT_CLASS != null && field.isAnnotationPresent(JAVAX_INJECT_CLASS));
	}

	private void registerMockFactoryBeanForField(final BeanDefinitionRegistry registry, final FieldDefinition fieldDef) {
		GenericBeanDefinition mockFactoryBeanDefinition = new GenericBeanDefinition();
		mockFactoryBeanDefinition.setBeanClass(MockFactoryBean.class);
		MutablePropertyValues values = new MutablePropertyValues();
		values.addPropertyValue(new PropertyValue("type", fieldDef.type));
		mockFactoryBeanDefinition.setPropertyValues(values);

		registry.registerBeanDefinition(fieldDef.name, mockFactoryBeanDefinition);
	}

	/**
	 * Container class for name and type
	 * 
	 * @author ruben
	 */
	private class FieldDefinition {

		String name;
		Class<?> type;

		public FieldDefinition(String name, Class<?> type) {
			this.name = name;
			this.type = type;
		}

	}

	/**
	 * Factory that creates mock instances based on the <code>type</code>
	 */
	public static class MockFactoryBean implements FactoryBean<Object> {

		private Class<?> type;

		public MockFactoryBean() {

		}

		public void setType(final Class<?> type) {
			this.type = type;
		}

		@Override
		public Object getObject() throws Exception {
			final Object mock = Mockito.mock(type);
			MOCKS.put(type, mock);
			return mock;
		}

		@Override
		public Class<?> getObjectType() {
			return type;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

	/**
	 * It calls Mockito.reset in all mocks that have been created
	 */
	public static void initMocks() {
		for (Object mock : MOCKS.values()) {
			Mockito.reset(mock);
		}
	}

	/**
	 * It returned the created mocks
	 * 
	 * @return
	 */
	public static Map<Class<?>, Object> getCreatedMocks() {
		return MOCKS;
	}
}
