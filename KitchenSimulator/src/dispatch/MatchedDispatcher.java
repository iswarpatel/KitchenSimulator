package dispatch;

public class MatchedDispatcher implements Runnable {
	/**
	 * Dispatch Manager matches cooked orders with couriers. Follows FIFO logic
	 * a courier picks up the next available order upon arrival
	 */
	private final Order order;
	
	MatchedDispatcher(Order order) {
        this.order = order;
    }
	
	public void run() {

		while (!Thread.interrupted()) {
			if (order != null) {
				Courier courier = null;
				do {
					courier = Kitchen.getCourier(order.id);
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
					break;
				}
			}
		}

	}

}