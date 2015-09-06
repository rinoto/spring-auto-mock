package sbg.rinoto.spring.mock.inject;

import java.net.URLClassLoader;

/**
 * Parent last classloader prohibiting access to javax.inject
 * 
 * @author mibutec
 *
 */
public class TestClassLoader extends URLClassLoader {
	public TestClassLoader() {
		super(((URLClassLoader) getSystemClassLoader()).getURLs());
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		// is class already loaded?
		Class<?> loadedClass = findLoadedClass(name);
		if (loadedClass != null) {
			return loadedClass;
		}

		// load bootstrap classes
		try {
			return getSystemClassLoader().getParent().loadClass(name);
		} catch (ClassNotFoundException cnfe) {
			// not a bootstrap class
		}

		// some parts of junit have already been loaded by systemclassloader
		if (name.startsWith("org.junit")) {
			return super.loadClass(name);
		}

		// the important part: prohibit access to javax.inject
		if (name.startsWith("javax.inject.")) {
			throw new ClassNotFoundException();
		}

		return findClass(name);
	}
}