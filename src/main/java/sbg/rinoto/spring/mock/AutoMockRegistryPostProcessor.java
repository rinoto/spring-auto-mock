package sbg.rinoto.spring.mock;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;

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

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		for (String beanName : registry.getBeanDefinitionNames()) {
			final BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
			final String beanClassName = beanDefinition.getBeanClassName();
			registerMocksForBean(registry, beanClassName);
		}
	}

	private void registerMocksForBean(BeanDefinitionRegistry registry, final String beanClassName) {
		if (beanClassName == null) {
			return;
		}
		Class<?> beanClass = getBeanClass(beanClassName);
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

	private Class<?> getBeanClass(final String beanClassName) {
		Class<?> beanClass;
		try {
			beanClass = Class.forName(beanClassName);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException("Class not found for bean: " + beanClassName);
		}
		return beanClass;
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
			if (!field.getType().isArray() && !field.getType().isPrimitive()
					&& (field.isAnnotationPresent(Autowired.class) || field.isAnnotationPresent(Inject.class))) {
				String fieldName = field.getName();
				if (field.isAnnotationPresent(Named.class)) {
					fieldName = field.getAnnotation(Named.class).value();
				}
				nonAutowired.add(new FieldDefinition(fieldName, field.getType()));
			}
		}
		// now the constructors
		Constructor<?>[] constructors = targetBean.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			if (constructor.isAnnotationPresent(Autowired.class) || constructor.isAnnotationPresent(Inject.class)) {
				Class<?>[] typeParameters = constructor.getParameterTypes();
				for (Class<?> typeParameter : typeParameters) {
					nonAutowired.add(new FieldDefinition(typeParameter.getSimpleName(), typeParameter));
				}
			}
		}
		return nonAutowired;
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

		@Override
		public String toString() {
			return "[name=" + name + ", type=" + type + "]";
		}
	}

}
