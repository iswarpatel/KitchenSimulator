package dispatch;

import java.util.Date;

/**
 * This class displays events that happens during the run
 */
public class LogEvent {
	public enum EventType {
		OrderReceived, OrderPrepared, OrderDispatched, CourierCalled, CourierArrived, AverageCourierWaitTime,
		AverageOrderWaitTime
	};

	public final EventType event;

	public final String orderNumber;

	public final String courierNumber;

	public final Long orderWaitTime;

	public final Long courierWaitTime;

	public final Long timeStamp;

	private LogEvent(EventType event, String orderNumber, String courierNumber, Long orderWaitTime,
			Long courierWaitTime, Long timeStamp) {
		this.event = event;
		this.orderNumber = orderNumber;
		this.courierNumber = courierNumber;
		this.orderWaitTime = orderWaitTime;
		this.courierWaitTime = courierWaitTime;
		this.timeStamp = timeStamp;
	}

	public static LogEvent cookFinishedOrder(String orderNumber, Long timeStamp) {
		return new LogEvent(EventType.OrderPrepared, orderNumber, null, null, null, timeStamp);
	}

	public static LogEvent customerPlacedOrder(String orderNumber, Long timeStamp) {
		return new LogEvent(EventType.OrderReceived, orderNumber, null, null, null, timeStamp);
	}

	public static LogEvent orderDispatched(String orderNumber, String courierId, Long orderWaitTime,
			Long courierWaitTime, Long timeStamp) {
		return new LogEvent(EventType.OrderDispatched, orderNumber, courierId, orderWaitTime, courierWaitTime,
				timeStamp);
	}

	public static LogEvent courierDispatched(String courierNumber, Long timeStamp) {
		return new LogEvent(EventType.CourierCalled, null, courierNumber, null, null, timeStamp);
	}

	public static LogEvent courierArrived(String courierNumber, Long timeStamp) {
		return new LogEvent(EventType.CourierArrived, null, courierNumber, null, null, timeStamp);
	}

	public static LogEvent logAverageCourierWaitTime(long courierWaitTime) {
		return new LogEvent(EventType.AverageCourierWaitTime, null, null, null, courierWaitTime, null);
	}

	public static LogEvent logAverageOrderWaitTime(long orderWaitTime) {
		return new LogEvent(EventType.AverageOrderWaitTime, null, null, orderWaitTime, null, null);
	}

	public String toString() {
		switch (event) {

		case OrderPrepared:
			return " Finished " + " preparing order " + orderNumber + " at " + new Date(timeStamp).toString();

		case OrderReceived:
			return " Received order " + orderNumber + " at " + new Date(timeStamp).toString();

		case OrderDispatched:
			return "\n Order " + orderNumber + " dispatched by courier " + courierNumber + " at "
					+ new Date(timeStamp).toString() + "\n Order wait time " + orderWaitTime + " ms. Courier wait time "
					+ courierWaitTime + " ms\n";

		case CourierCalled:
			return " Courier " + " dispatched for order " + courierNumber + " at " + new Date(timeStamp).toString();

		case CourierArrived:
			return " Courier " + " arrived for order " + courierNumber + " at " + new Date(timeStamp).toString();

		case AverageCourierWaitTime:
			return " Average Courier Wait Time " + courierWaitTime + " ms";

		case AverageOrderWaitTime:
			return " Average Order Wait Time " + orderWaitTime + " ms";

		default:
			throw new Error("Invalid event");
		}
	}
}
