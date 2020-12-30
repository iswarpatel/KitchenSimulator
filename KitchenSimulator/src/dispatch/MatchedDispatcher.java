package dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchedDispatcher implements Runnable, Dispatcher {

	/**
	 * Dispatch Manager matches cooked orders with couriers. Follows FIFO logic a
	 * courier picks up the next available order upon arrival
	 */
	public void run() {

		while (!Thread.interrupted()) {
			List<String> processed = new ArrayList<>();
			Map<String, Courier> couriers = Kitchen.getCouriers();
			if (couriers != null) {
				for (String id : couriers.keySet()) {
					Order order = Kitchen.getCookedOrder(id);
					if (order != null) {
						processed.add(id);
						long epoch = System.currentTimeMillis();
						long orderWaitTime = epoch - order.prepTime;
						long courierWaitTime = epoch - couriers.get(id).arrivalTime;
						Kitchen.updateDispatchedOrders();
						Kitchen.updateOrderWaitTime(orderWaitTime);
						Kitchen.updateCourierWaitTime(courierWaitTime);
						Kitchen.logEvent(LogEvent.orderDispatched(order.id, couriers.get(id).order.id, orderWaitTime,
								courierWaitTime, epoch));
					}
				}
				for (String id : processed) {
					Kitchen.getCourier(id);
				}
			}
		}
	}
}
