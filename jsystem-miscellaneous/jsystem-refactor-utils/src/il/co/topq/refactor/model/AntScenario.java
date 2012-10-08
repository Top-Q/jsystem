package il.co.topq.refactor.model;

public class AntScenario {

	private final String name;
	
	public AntScenario(String name) {
		super();
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		AntScenario that = (AntScenario) o;

		if (name != null ? !name.equals(that.name) : that.name != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

}
