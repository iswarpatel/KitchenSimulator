package dispatch;

public class DispatchManager {
	
	synchronized void dispatch(Food food) throws InterruptedException {
		if(Kitchen.dispatchType == Kitchen.DispatchType.FIFO) {
			Kitchen.updateCookedOrder(food);
		}
		else {
            Kitchen.logEvent(LogEvent.cookFinishedFood(food.id));
    		Thread dispatch = new Thread(new MatchedDispatcher(food));
    		dispatch.start();
		}
	}

}
