package dispatch;

import java.util.Random;

/**
 * A Courier delivers food to customer.
 * Arrives randomly 3 to 15 seconds after it is called.
 */

public class Courier implements Runnable {

	final Food food;
	long arrivalTime;

	Courier(Food food) {
	        this.food = food;
        }

	public void run() {
		try {
			Kitchen.logEvent(
					LogEvent.courierDispatched(food.id));
			// Randomly wait for 3 to 15 seconds
			Thread.sleep((new Random().nextInt(13) + 3) * 1000);
			Kitchen.logEvent(
					LogEvent.courierArrived(food.id));
			arrivalTime = System.currentTimeMillis();
			Kitchen.addCourier(this, food.id);

		} catch (InterruptedException e) {
			System.out.println("Courier thread interrupted.");
		}
	}
}