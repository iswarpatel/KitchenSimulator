package dispatch;

public class FIFODispatcher implements Runnable {

	/**
	 * Dispatch Manager matches cooked orders with couriers. Follows FIFO logic
	 * a courier picks up the next available order upon arrival
	 */
	public void run() {

		while (!Thread.interrupted()) {
			Order order = Kitchen.getFirstCompletedOrder();
			if (order != null) {
				Courier courier = null;
				do {
					courier = Kitchen.getFirstCourier();
				} while (courier == null);

				if (courier != null) {
					long epoch = System.currentTimeMillis();
					long orderWaitTime = epoch - order.prepTime;
					long courierWaitTime = epoch - courier.arrivalTime;
					Kitchen.updateDispatchedOrders();
					Kitchen.updateOrderWaitTime(orderWaitTime);
					Kitchen.updateCourierWaitTime(courierWaitTime);
					Kitchen.logEvent(
							LogEvent.orderDispatched(order.id, courier.order.id, orderWaitTime, courierWaitTime, epoch));
				}
			}
		}

	}

}