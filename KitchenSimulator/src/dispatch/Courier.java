package dispatch;

import java.util.Random;

/**
 * A Courier delivers order to customer.
 * Arrives randomly 3 to 15 seconds after it is called.
 */

public class Courier implements Runnable {

	final Order order;
	long arrivalTime;

	Courier(Order order) {
	        this.order = order;
        }

	public void run() {
		try {
			Kitchen.logEvent(
					LogEvent.courierDispatched(order.id, System.currentTimeMillis()));
			// Randomly wait for 3 to 15 seconds
			Thread.sleep((new Random().nextInt(13) + 3) * 1000);
			Kitchen.logEvent(
					LogEvent.courierArrived(order.id, System.currentTimeMillis()));
			Kitchen.addCourier(this, order.id);

		} catch (InterruptedException e) {
			System.out.println("Courier thread interrupted.");
		}
	}
}