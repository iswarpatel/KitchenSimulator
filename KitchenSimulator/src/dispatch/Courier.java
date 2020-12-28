package dispatch;

import java.util.Random;

/**
 * A Courier delivers order to customer. Arrives randomly 3 to 15 seconds after
 * it is called.
 */

public class Courier implements Runnable {

	Order order;
	long arrivalTime;

	Courier(Order order) {
		this.order = order;
	}

	public void run() {
		try {
			Kitchen.logEvent(LogEvent.courierDispatched(order.id));
			// Randomly wait for 3 to 15 seconds
			Thread.sleep((new Random().nextInt(13) + 3) * 1000);
			Kitchen.logEvent(LogEvent.courierArrived(order.id));
			arrivalTime = System.currentTimeMillis();
			while (!Thread.interrupted()) {
				Order completedOrder;
				if (Kitchen.dispatchType == Kitchen.DispatchType.FIFO) {
					completedOrder = Kitchen.getFirstCompletedOrder();
				} else {
					completedOrder = Kitchen.getCookedOrder(order.id);
				}
				if (completedOrder != null) {
					long epoch = System.currentTimeMillis();
					long orderWaitTime = epoch - completedOrder.prepTime;
					long courierWaitTime = epoch - arrivalTime;
					Kitchen.updateDispatchedOrders();
					Kitchen.updateOrderWaitTime(orderWaitTime);
					Kitchen.updateCourierWaitTime(courierWaitTime);
					Kitchen.logEvent(LogEvent.orderDispatched(completedOrder.id, order.id, orderWaitTime, courierWaitTime));
					break;
				}
			}

		} catch (InterruptedException e) {
			System.out.println("Courier thread interrupted.");
		}
	}
}