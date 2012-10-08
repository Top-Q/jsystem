package il.co.topq.refactor.model;

/**
 * This object represents a specific method annotated with JUnit @Test
 * 
 * IMPORTANT !!!!! it is compared only by its qualified name regardless of the
 * UUID given by JSystem The reason is that the same method can appear in a
 * scenario with different UUIDs and when refactoring its name or its parameters
 * the reference must be the qualified java name and not the UUID.
 * 
 * @author Itai Agmon
 * 
 */
public class MethodTest {

	// Represents the full name of a test including
	// packageName.className.methodName
	private final String qualifiedName;

	// Represents only the simple name of the test: methodName
	private final String simpleName;

	public MethodTest(String qualifiedName, String simpleName) {
		this.qualifiedName = qualifiedName;
		this.simpleName = simpleName;
	}

	@Override
	public String toString() {
		return qualifiedName;

	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public String getSimpleName() {
		return simpleName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		MethodTest that = (MethodTest) o;

		if (qualifiedName != null ? !qualifiedName.equals(that.qualifiedName) : that.qualifiedName != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return qualifiedName != null ? qualifiedName.hashCode() : 0;
	}
}
