package dispatch;

/**
 * A Chef is used to cook Food order.
 * cooking order is simulated by THREAD.SLEEP
 */

public class Chef {

	synchronized boolean makeFood(Order order) throws InterruptedException {
        Thread cookThread = new Thread(new CookAnItem(order));
        cookThread.start();
        return true;
	}

	private class CookAnItem implements Runnable {
	    private final Order order;

	    CookAnItem(Order order) {
	        this.order = order;
        }

		public void run() {
			try {
                Thread.sleep(order.cookTimeS * 1000);
                order.prepTime = System.currentTimeMillis();
                Kitchen.dispatchManager.dispatch(order);

			} catch(InterruptedException e) {
                System.out.println("Cooking thread interrupted.");
            }
		}
	}
}