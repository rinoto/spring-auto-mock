package sbg.rinoto.spring.mock.matcher;

import static org.mockito.Mockito.mockingDetails;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

public class IsMock<T> extends BaseMatcher<T> {

	@Override
	public boolean matches(Object o) {
		if (o == null) {
			return false;
		}
		return mockingDetails(o).isMock();
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("a mock instance");
	}

	/**
	 * Creates a matcher that matches if examined object is a mock object
	 * created by mockito.
	 * <p/>
	 * For example:
	 * 
	 * <pre>
	 * assertThat(cheese, isMock())
	 * </pre>
	 * 
	 */
	@Factory
	public static Matcher<Object> isMock() {
		return new IsMock<Object>();
	}

}
