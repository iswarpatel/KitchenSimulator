package dispatch;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * The customer places order. It reads all orders from the input file
 * and puts them in a queue for processing
 */
public class Customer implements Runnable {
	public void run() {
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader("dispatch_orders.json")) {
			Object obj = jsonParser.parse(reader);

			JSONArray orderList = (JSONArray) obj;

			for (int i = 0; i < orderList.size(); i++) {
				JSONObject order = (JSONObject) orderList.get(i);
				Order food = parseOrdereObject(order);
				// Customer orders
				Kitchen.logEvent(LogEvent.customerPlacedOrder(food.id, System.currentTimeMillis()));
				Kitchen.placeOrder(food);
				// Limit orders to 2 per second
				if (i % 2 == 1) {
					Thread.sleep(1000);
				}
			}
			Kitchen.setReceivedOrders(orderList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static Order parseOrdereObject(JSONObject order) {
		String name = (String) order.get("name");
		String id = (String) order.get("id");
		long time = (Long) order.get("prepTime");
		return new Order(id, name, time);
	}
}