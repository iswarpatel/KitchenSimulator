package dispatch;

import java.util.*;

import dispatch.Kitchen.DispatchType;
import dispatch.LogEvent.EventType;

public class Validate {
	private static class InvalidSimulationException extends Exception {
		public InvalidSimulationException() {
		}
	}
	
	static class ValueComparator implements Comparator<Map.Entry<String, Long>> {
		public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
			return (o1.getValue()).compareTo(o2.getValue());
		}
	}

	// Helper method for validating the simulation
	private static void check(boolean check, String message) throws InvalidSimulationException {
		if (!check) {
			System.err.println("SIMULATION INVALID : " + message);
			throw new Validate.InvalidSimulationException();
		}
	}

	/**
	 * Validates the given list of events is a valid simulation. Returns true if the
	 * simulation is valid, false otherwise.
	 *
	 * @param events - a list of events generated by the simulation in the order
	 *               they were generated.
	 *
	 * @returns res - whether the simulation was valid or not
	 */
	public static boolean validateSimulation(List<LogEvent> events) {
		try {
			Map<String, Long> orderReceived = new HashMap<>();
			Map<String, Long> orderPrepared = new HashMap<>();
			Map<String, Long> orderDispatched = new HashMap<>();
			Map<String, Long> orderDispatchedByCourier = new HashMap<>();
			Map<String, Long> courierCalled = new HashMap<>();
			Map<String, Long> courierArrived = new HashMap<>();

			for (LogEvent e : events) {
				switch (e.event) {
				case OrderReceived:
					orderReceived.put(e.orderNumber, e.timeStamp);
					break;
				case OrderPrepared:
					orderPrepared.put(e.orderNumber, e.timeStamp);
					break;
				case OrderDispatched:
					orderDispatched.put(e.orderNumber, e.timeStamp);
					orderDispatchedByCourier.put(e.courierNumber, e.timeStamp);
					break;
				case CourierCalled:
					courierCalled.put(e.courierNumber, e.timeStamp);
					break;
				case CourierArrived:
					courierArrived.put(e.courierNumber, e.timeStamp);
					break;
				default:
					break;

				}
			}

			// Order prepared == Order received == courier dispatched == courier arrived ==
			// order dispatched
			check(orderReceived.size() == orderPrepared.size(), "Order Received != Order Preparated");
			check(orderReceived.size() == orderDispatched.size(), "Order Received != Order Dispatched");
			check(orderReceived.size() == courierCalled.size(), "Order Received != Courier Dispatched");
			check(orderReceived.size() == courierArrived.size(), "Order Received != Courier Arrived");

			// For Matched type dispatch, Order ID should be same as courier ID
			if (Kitchen.dispatchType == DispatchType.Matched) {
				for (LogEvent e : events) {
					if (e.event == EventType.OrderDispatched) {
						check(e.orderNumber.equals(e.courierNumber),
								"Order and Courier are not matched for matched type dispatch");
					}
				}
			}

			// Time stamp OrderReceived < OrderPrepared < OrderDispatched
			for (String order : orderReceived.keySet()) {
				check(orderReceived.get(order) <= orderPrepared.get(order),
						"Order " + order + " received time is not earlier than prepared time");
				check(orderPrepared.get(order) <= orderDispatched.get(order),
						"Order " + order + " prepared time is not earlier than dispatch time");
			}

			// Time stamp CourierDispatched < CourierArrived < OrderDispatchedByCourier
			for (String courier : courierCalled.keySet()) {
				check(courierCalled.get(courier) <= courierArrived.get(courier),
						"Courier " + courier + " dispatch time is not earlier than arrival time");
				check(courierArrived.get(courier) <= orderDispatchedByCourier.get(courier),
						"Courier " + courier + " arrival time is not earlier than order dispatch time");
			}
			
			// For FIFO, first prder prepared = first order dispatched
			/* if (Kitchen.dispatchType == DispatchType.FIFO) {
				List<Map.Entry<String, Long>> orderPreparedList = new ArrayList<Map.Entry<String, Long>>(orderPrepared.entrySet());
				Collections.sort(orderPreparedList, new ValueComparator());
				List<Map.Entry<String, Long>> orderDispatchedList = new ArrayList<Map.Entry<String, Long>>(orderDispatched.entrySet());
				Collections.sort(orderDispatchedList, new ValueComparator());
				for(int i=0; i<orderPreparedList.size(); i++) {
					System.out.print(i);
					check(orderPreparedList.get(i).getKey().equals(orderDispatchedList.get(i).getKey()), "FIFO not followed in order dispatch");
				}
				
			} */

			// For FIFO, first prder prepared = first order dispatched
			/* if (Kitchen.dispatchType == DispatchType.FIFO) {
				List<Map.Entry<String, Long>> courierArrivedList = new ArrayList<Map.Entry<String, Long>>(courierArrived.entrySet());
				Collections.sort(courierArrivedList, new ValueComparator());
				List<Map.Entry<String, Long>> orderDispatchedByCourierList = new ArrayList<Map.Entry<String, Long>>(orderDispatchedByCourier.entrySet());
				Collections.sort(orderDispatchedByCourierList, new ValueComparator());
				for(int i=0; i<courierArrivedList.size(); i++) {
					System.out.print(i);
					check(courierArrivedList.get(i).getKey().equals(orderDispatchedByCourierList.get(i).getKey()), "FIFO not followed in order dispatch");
				}
				
			} */

			return true;
		} catch (InvalidSimulationException e) {
			return false;
		}
	}
}
