package dispatch;

import java.util.Date;

/**
 * This class displays events that happens during the run
 */
public class LogEvent {
	public enum EventType {
		OrderReceived, OrderPrepared, OrderDispatched, CourierDispatched, CourierArrived
	};

	public final EventType event;

	public final String orderNumber;

	private final String courierNumber;

	private final Long foodWaitTime;

	private final Long courierWaitTime;

	private LogEvent(EventType event, String orderNumber, String courierNumber, Long foodWaitTime,
			Long courierWaitTime) {
		this.event = event;
		this.orderNumber = orderNumber;
		this.courierNumber = courierNumber;
		this.foodWaitTime = foodWaitTime;
		this.courierWaitTime = courierWaitTime;
	}

	public static LogEvent cookFinishedFood(String orderNumber) {
		return new LogEvent(EventType.OrderPrepared, orderNumber, null, null, null);
	}

	public static LogEvent customerPlacedOrder(String orderNumber) {
		return new LogEvent(EventType.OrderReceived, orderNumber, null, null, null);
	}

	public static LogEvent orderDispatched(String orderNumber, String courierId, Long foodWaitTime,
			Long courierWaitTime) {
		return new LogEvent(EventType.OrderDispatched, orderNumber, courierId, foodWaitTime, courierWaitTime);
	}
	
	public static LogEvent courierDispatched(String orderNumber) {
		return new LogEvent(EventType.CourierDispatched, orderNumber, null, null, null);
	}
	
	public static LogEvent courierArrived(String orderNumber) {
		return new LogEvent(EventType.CourierArrived, orderNumber, null, null, null);
	}

	public String toString() {
		switch (event) {

		case OrderPrepared:
			return " Finished " + " preparing order " + orderNumber + " at " + new Date().toString();

		case OrderReceived:
			return " Received order " + orderNumber + " at " + new Date().toString();

		case OrderDispatched:
			return "\n Order " + orderNumber + " dispatched by courier " + courierNumber + " at "
					+ new Date().toString() + "\n Food wait time " + foodWaitTime + " ms. Courier wait time "
					+ courierWaitTime + " ms\n";

		case CourierDispatched:
			return " Courier " + " dispatched for order " + orderNumber + " at " + new Date().toString();

		case CourierArrived:
			return " Courier " + " arrived for order " + orderNumber + " at " + new Date().toString();

		default:
			throw new Error("Invalid event");
		}
	}
}
