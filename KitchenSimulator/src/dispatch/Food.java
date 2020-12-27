package dispatch;

/**
 * Food is what is prepared by Cooks, and ordered by Customers.  Food
 * is defined by its name, and the amount of time it takes to prepare
 * by Machine.  It is an immutable class.
 */
public class Food {
	final String id;
	final String name;
	final long cookTimeS;
	long prepTime;

	Food(String id, String name, long cookTimeS) {
		this.id = id;
		this.name = name;
		this.cookTimeS = cookTimeS;
	}

	public String toString() {
		return name;
	}
}