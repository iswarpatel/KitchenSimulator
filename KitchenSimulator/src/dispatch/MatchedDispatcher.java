package dispatch;

public class MatchedDispatcher implements Runnable {
	/**
	 * Dispatch Manager matches cooked orders with couriers. Follows FIFO logic
	 * a courier picks up the next available order upon arrival
	 */
	private final Food food;
	
	MatchedDispatcher(Food food) {
        this.food = food;
    }
	
	public void run() {

		while (!Thread.interrupted()) {
			if (food != null) {
				Courier courier = null;
				do {
					courier = Kitchen.getCourier(food.id);
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
					break;
				}
			}
		}

	}

}
