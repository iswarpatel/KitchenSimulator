package dispatch;

/**
 * Prepared by Chef, and ordered by Customer.
 */
public class Order {
	final String id;
	final String name;
	final long cookTimeS;
	long prepTime;

	Order(String id, String name, long cookTimeS) {
		this.id = id;
		this.name = name;
		this.cookTimeS = cookTimeS;
	}

	public String toString() {
		return name;
	}
}