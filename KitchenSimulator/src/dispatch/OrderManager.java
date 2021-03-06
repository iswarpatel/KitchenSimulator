package dispatch;

/**
 * Order manager attempts to retrieve orders and process them by sending them to
 * Chef and calling a Courier for pickup
 */
public class OrderManager implements Runnable {

	public void run() {

		try {
			while (!Thread.interrupted()) {
				Order food = Kitchen.orderAvailable();
				if (food != null) {
					Kitchen.chef.makeFood(food);
					Thread dispatchThread = new Thread(new Courier(food));
					dispatchThread.start();
				}
			}
		} catch (InterruptedException e) {
			System.out.println("OrderManager thread interrupted.");
		}

	}
}