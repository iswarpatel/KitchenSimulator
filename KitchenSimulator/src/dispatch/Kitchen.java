package dispatch;

import java.util.*;

/**
 * @author iswar patel
 * real-time system that simulates the fulfillment of delivery orders for a kitchen
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
	private static int dispatchedOrders;
	private static long courierWaitTime;
	private static long foodWaitTime;
	
	
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
	 * @param count number of orders
	 */
	static void setReceivedOrders(int count) {
		synchronized (counterLock) {
			receivedOrders = count;
		}
	}
	
	/**
	 * @return number of received orders
	 */
	static int getReceivedOrders() {
		synchronized (counterLock) {
			return receivedOrders;
		}
	}

	/**
	 * Get the number of dispatched orders
	 * @return number of dispatched orders
	 */
	static int getDispatchedOrders() {
		synchronized (counterLock) {
			return dispatchedOrders;
		}
	}	
	
	/**
	 * Update the courier wait time
	 * @param count the amount to increase
	 */
	static void updateCourierWaitTime(long count) {
		synchronized (counterLock) {
			courierWaitTime += count;
		}
	}

	static long getFoodWaitTime() {
		synchronized (counterLock) {
			return courierWaitTime;
		}
	}
	
	static void updateFoodWaitTime(long count) {
		synchronized (counterLock) {
			foodWaitTime += count;
		}
	}

	static long getCourierWaitTime() {
		synchronized (counterLock) {
			return foodWaitTime;
		}
	}

	// List of incoming unprepared orders
	private static LinkedList<Food> orderList = new LinkedList<Food>();

	static void placeOrder(String orderNum, Food order) {
		synchronized (orderList) {
			orderList.add(order);
		}
	}

	static Food orderAvailable() {
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
	private static Map<String, Food> ordersCooked = new HashMap<String, Food>();
	private static Queue<Food> cookedList = new LinkedList<>();

	static void updateCookedOrder(Food foodCooked) {
		synchronized (dispatchLock) {
			ordersCooked.put(foodCooked.id, foodCooked);
			cookedList.add(foodCooked);
			logEvent(LogEvent.cookFinishedFood(foodCooked.id));
		}
	}

	static boolean checkCookingStatus(String orderNum, Food food) {
		synchronized (dispatchLock) {
			if (ordersCooked.containsKey(orderNum)) {
				return true;
			} else {
				return false;
			}
		}

	}

	static void removeCompletedOrder(String orderNum) {
		synchronized (dispatchLock) {
			Food food = ordersCooked.remove(orderNum);
			cookedList.remove(food);
		}
	}

	static Food getFirstCompletedOrder() {
		synchronized (dispatchLock) {
			if (!cookedList.isEmpty()) {
				Food food = cookedList.remove();
				ordersCooked.remove(food.id);
				return food;
			}
			return null;
		}
	}
	
	static Queue<Food> getAllCompletedOrders() {
		synchronized (dispatchLock) {
			return cookedList;
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
				couriers.remove(courier.food.id);
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

	public static void runMatchedSimulation() throws InterruptedException {
		
		System.out.println("Starting Matched simulation");

		events = Collections.synchronizedList(new ArrayList<LogEvent>());
		chef = new Chef();
		dispatchType = DispatchType.Matched;
		dispatchManager = new DispatchManager();
		
		// Start cooking
		Thread cook = new Thread(new OrderManager());
		cook.start();

		// Start receiving orders.
		Thread customer = new Thread(new Customer());
		customer.start();
		
		// wait for all orders to finish
		while(getDispatchedOrders() == 0 || getDispatchedOrders() != getReceivedOrders()) {
			Thread.sleep(10000);
		}
		
		// Finish
		cook.interrupt();
		customer.interrupt();
	}
	
	public static void runFIFOSimulation() throws InterruptedException {

		System.out.println("Starting FIFO simulation");
		
		events = Collections.synchronizedList(new ArrayList<LogEvent>());
		chef = new Chef();
		dispatchType = DispatchType.FIFO;
		dispatchManager = new DispatchManager();

		// Start cooking
		Thread cook = new Thread(new OrderManager());
		cook.start();

		// Start receiving orders.
		Thread customer = new Thread(new Customer());
		customer.start();

		//Start dispatching orders.
		Thread dispatch = new Thread(new FIFODispatcher());
		dispatch.start();
		
		// wait for all orders to finish
		while(getDispatchedOrders() == 0 || getDispatchedOrders() != getReceivedOrders()) {
			Thread.sleep(10000);
		}
		
		// Finish
		cook.interrupt();
		customer.interrupt();
		dispatch.interrupt();
	}

	/**
	 * Entry point for the simulation.
	 */
	public static void main(String args[]) throws InterruptedException {
		
		Scanner input = new Scanner(System.in);
		System.out.println("Please type matched or fifo");
		String type = input.nextLine();
		input.close();
		
		if(type.equals("fifo")) {
			runFIFOSimulation();
		}
		else if(type.equals("matched")) {
			runMatchedSimulation();
		}
		else {
			System.out.println("Invalid input");
			System.exit(0);
		}

		// wait for all orders to finish
		while(getDispatchedOrders() == 0 || getDispatchedOrders() != getReceivedOrders()) {
			Thread.sleep(10000);
		}
		
		System.out.println("Average Courier Wait Time : " + getCourierWaitTime()/getDispatchedOrders() + " ms");
		System.out.println("Average Food Wait Time : " + getFoodWaitTime()/getDispatchedOrders() + " ms");
	}

}
