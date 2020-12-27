package dispatch;

public class FIFODispatcher implements Runnable {

	/**
	 * Dispatch Manager matches cooked orders with couriers. Follows FIFO logic
	 * a courier picks up the next available order upon arrival
	 */
	public void run() {

		while (!Thread.interrupted()) {
			Food food = Kitchen.getFirstCompletedOrder();
			if (food != null) {
				Courier courier = null;
				do {
					courier = Kitchen.getFirstCourier();
				} while (courier == null);

				if (courier != null) {
					long epoch = System.currentTimeMillis();
					long foodWaitTime = epoch - food.prepTime;
					long courierWaitTime = epoch - courier.arrivalTime;
					Kitchen.updateDispatchedOrders();
					Kitchen.updateFoodWaitTime(foodWaitTime);
					Kitchen.updateCourierWaitTime(courierWaitTime);
					Kitchen.logEvent(
							LogEvent.orderDispatched(food.id, courier.food.id, foodWaitTime, courierWaitTime));
				}
			}
		}

	}

}
