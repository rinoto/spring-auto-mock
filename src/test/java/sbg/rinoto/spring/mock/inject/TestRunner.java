package sbg.rinoto.spring.mock.inject;

import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * reload the testclass with own classloader
 * 
 * @author mibutec
 */
public class TestRunner extends BlockJUnit4ClassRunner {
	@SuppressWarnings("resource")
	public TestRunner(Class<?> testFileClass) throws Exception {
		super(new TestClassLoader().loadClass(testFileClass.getName()));
	}
}