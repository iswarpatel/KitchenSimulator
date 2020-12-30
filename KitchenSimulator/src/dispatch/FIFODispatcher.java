package dispatch;

public class FIFODispatcher implements Runnable, Dispatcher {

	/**
	 * Dispatch Manager matches cooked orders with couriers. Follows FIFO logic a
	 * courier picks up the next available order upon arrival
	 */
	public void run() {

		while (!Thread.interrupted()) {
			Courier courier = Kitchen.getFirstCourier();
			if (courier != null) {
				Order order;
				do {
					order = Kitchen.getFirstCompletedOrder();
				} while (order == null);
				if (order != null) {
					long epoch = System.currentTimeMillis();
					long orderWaitTime = epoch - order.prepTime;
					long courierWaitTime = epoch - courier.arrivalTime;
					Kitchen.updateDispatchedOrders();
					Kitchen.updateOrderWaitTime(orderWaitTime);
					Kitchen.updateCourierWaitTime(courierWaitTime);
					Kitchen.logEvent(LogEvent.orderDispatched(order.id, courier.order.id, orderWaitTime,
							courierWaitTime, epoch));
				}
			}
		}

	}

}