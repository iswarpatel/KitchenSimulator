package dispatch;

import java.util.Random;

/**
 * A Courier delivers order to customer. Arrives randomly 3 to 15 seconds after
 * it is called.
 */

public class Courier implements Runnable, Comparable<Courier> {

	final Order order;
	long arrivalTime;

	Courier(Order order) {
		this.order = order;
	}

	public void run() {
		try {
			Kitchen.logEvent(LogEvent.courierDispatched(order.id, System.currentTimeMillis()));
			// Randomly wait for 3 to 15 seconds
			Thread.sleep((new Random().nextInt(13) + 3) * 1000);
			arrivalTime = System.currentTimeMillis();
			Kitchen.logEvent(LogEvent.courierArrived(order.id, arrivalTime));
			Kitchen.addCourier(this, order.id);

		} catch (InterruptedException e) {
			System.out.println("Courier thread interrupted.");
		}
	}

	@Override
	public int compareTo(Courier o) {
		if (arrivalTime < o.arrivalTime)
			return -1;
		else if (arrivalTime > o.arrivalTime)
			return 1;
		return 0;
	}
}