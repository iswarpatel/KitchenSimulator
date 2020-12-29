package dispatch;

import java.util.*;

/**
 * @author iswar patel real-time system that simulates the fulfillment of
 *         delivery orders for a kitchen
 *
 */
public class Kitchen {

	enum DispatchType {
		Matched, FIFO
	}

	static DispatchType dispatchType;
	// List to track simulation events during simulation
	private static List<LogEvent> events;

	synchronized static void logEvent(LogEvent event) {
		events.add(event);
		System.out.println(event);
	}

	static Chef chef;
	static DispatchManager dispatchManager;

	// an instanceLock object for synchronizing on counters on wait time and orders
	private static final Object counterLock = new Object();

	/**
	 * total number of received orders
	 */
	private static int receivedOrders;
	/**
	 * total number of dispatched orders
	 */
	private static int dispatchedOrders;
	/**
	 * counter for total courier wait time wait time = arrival time - delivery time
	 */
	private static long courierWaitTime;
	/**
	 * counter for total order wait time wait time = delivery time - prep time
	 */
	private static long orderWaitTime;

	/**
	 * Increment the number of dispatched orders
	 */
	static void updateDispatchedOrders() {
		synchronized (counterLock) {
			dispatchedOrders++;
		}
	}

	/**
	 * Increment the number of received orders
	 * 
	 * @param count number of orders
	 */
	static void setReceivedOrders(int count) {
		synchronized (counterLock) {
			receivedOrders = count;
		}
	}

	/**
	 * Get total received orders
	 * 
	 * @return number of received orders
	 */
	static int getReceivedOrders() {
		synchronized (counterLock) {
			return receivedOrders;
		}
	}

	/**
	 * Get the number of dispatched orders
	 * 
	 * @return number of dispatched orders
	 */
	static int getDispatchedOrders() {
		synchronized (counterLock) {
			return dispatchedOrders;
		}
	}

	/**
	 * Update the courier wait time
	 * 
	 * @param count the amount to increase
	 */
	static void updateCourierWaitTime(long count) {
		synchronized (counterLock) {
			courierWaitTime += count;
		}
	}

	/**
	 * Get order wait time
	 * 
	 * @return total order wait time
	 */
	static long getOrderWaitTime() {
		synchronized (counterLock) {
			return orderWaitTime;
		}
	}

	/**
	 * update order wait time when an order is delivered
	 * 
	 * @param count the wait time of current order
	 */
	static void updateOrderWaitTime(long count) {
		synchronized (counterLock) {
			orderWaitTime += count;
		}
	}

	/**
	 * Get courier wait time
	 * 
	 * @return total courier wait time
	 */
	static long getCourierWaitTime() {
		synchronized (counterLock) {
			return courierWaitTime;
		}
	}

	// List of incoming unprepared orders
	private static LinkedList<Order> orderList = new LinkedList<Order>();

	/**
	 * Add order to queue to process
	 * 
	 * @param order the order object
	 */
	static void placeOrder(Order order) {
		synchronized (orderList) {
			orderList.add(order);
		}
	}

	/**
	 * Return FIFO order to be processed
	 * 
	 * @return order
	 */
	static Order orderAvailable() {
		synchronized (orderList) {
			if (!orderList.isEmpty()) {
				return orderList.pop();
			} else {
				return null;
			}
		}
	}

	// List prepared orders ready to be dispatched
	private static final Object dispatchLock = new Object();
	private static Map<String, Order> ordersCooked = new HashMap<String, Order>();
	private static Queue<Order> cookedList = new LinkedList<>();

	/**
	 * Update an order which has finished cooking
	 * 
	 * @param orderCooked order that has finished cooking
	 */
	static void updateCookedOrder(Order orderCooked) {
		synchronized (dispatchLock) {
			ordersCooked.put(orderCooked.id, orderCooked);
			cookedList.add(orderCooked);
			logEvent(LogEvent.cookFinishedOrder(orderCooked.id, System.currentTimeMillis()));
		}
	}

	/**
	 * Gets an order from the cooked queue
	 * 
	 * @param orderNum the order to be retrieved
	 * @return the order if present, null otherwise
	 */
	static Order getCookedOrder(String orderNum) {
		synchronized (dispatchLock) {
			if (ordersCooked.containsKey(orderNum)) {
				Order order = ordersCooked.remove(orderNum);
				cookedList.remove(order);
				return order;
			} else {
				return null;
			}
		}
	}

	/**
	 * Get an order in FIFO format
	 * 
	 * @return the first order in queue
	 */
	static Order getFirstCompletedOrder() {
		synchronized (dispatchLock) {
			if (!cookedList.isEmpty()) {
				Order order = cookedList.remove();
				ordersCooked.remove(order.id);
				return order;
			}
			return null;
		}
	}

	// List of couriers ready to deliver orders
	private static Map<String, Courier> couriers = new HashMap<String, Courier>();
	private static Queue<Courier> courierList = new LinkedList<>();

	static void addCourier(Courier courier, String id) {
		synchronized (dispatchLock) {
			courierList.add(courier);
			couriers.put(id, courier);
		}
	}

	static Courier getFirstCourier() {
		synchronized (dispatchLock) {
			if (!courierList.isEmpty()) {
				Courier courier = courierList.poll();
				couriers.remove(courier.order.id);
				return courier;
			}
			return null;
		}
	}

	static Courier getCourier(String id) {
		synchronized (dispatchLock) {
			if (couriers.containsKey(id)) {
				Courier courier = couriers.get(id);
				couriers.remove(id);
				courierList.remove(courier);
				return courier;
			}
			return null;
		}
	}

	public static List<LogEvent> runSimulation(DispatchType type) throws InterruptedException {

		events = Collections.synchronizedList(new ArrayList<LogEvent>());
		chef = new Chef();
		dispatchType = type;
		dispatchManager = new DispatchManager();

		// Start cooking
		Thread cook = new Thread(new OrderManager());
		cook.start();

		// Start receiving orders.
		Thread customer = new Thread(new Customer());
		customer.start();

		Thread dispatch = new Thread(new FIFODispatcher());
		// Start dispatching orders.
		if (type == DispatchType.FIFO) {
			dispatch.start();
		}

		// wait for all orders to finish
		while (getDispatchedOrders() == 0 || getDispatchedOrders() != getReceivedOrders()) {
			Thread.sleep(10000);
		}

		// Finish
		cook.interrupt();
		customer.interrupt();
		if (type == DispatchType.FIFO) {
			dispatch.interrupt();
		}

		Kitchen.logEvent(LogEvent.logAverageCourierWaitTime(getCourierWaitTime() / getDispatchedOrders()));
		Kitchen.logEvent(LogEvent.logAverageOrderWaitTime(getOrderWaitTime() / getDispatchedOrders()));
		
		return events;
	}

	/**
	 * Entry point for the simulation.
	 */
	public static void main(String args[]) throws InterruptedException {

		Scanner input = new Scanner(System.in);
		System.out.println("Please type matched or fifo");
		String type = input.nextLine();
		input.close();

		if (type.equals("fifo")) {
			System.out.println("\n Validation: " + Validate.validateSimulation(runSimulation(DispatchType.FIFO)));
		} else if (type.equals("matched")) {
			System.out.println("\n Validation: " + Validate.validateSimulation(runSimulation(DispatchType.Matched)));
		} else {
			System.out.println("Invalid input");
			System.exit(0);
		}
	}

}
