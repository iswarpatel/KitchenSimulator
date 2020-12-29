package dispatch;

public class DispatchManager {
	
	synchronized void dispatch(Order order) throws InterruptedException {
		if(Kitchen.dispatchType == Kitchen.DispatchType.FIFO) {
			Kitchen.updateCookedOrder(order);
		}
		else {
            Kitchen.logEvent(LogEvent.cookFinishedOrder(order.id, System.currentTimeMillis()));
    		Thread dispatch = new Thread(new MatchedDispatcher(order));
    		dispatch.start();
		}
	}

}