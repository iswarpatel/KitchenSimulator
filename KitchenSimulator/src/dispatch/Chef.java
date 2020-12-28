package dispatch;

/**
 * A Chef is used to cook Food order.
 * cooking food is simulated by THREAD.SLEEP
 */

public class Chef {

	synchronized boolean makeFood(Order food) throws InterruptedException {
        Thread cookThread = new Thread(new CookAnItem(food));
        cookThread.start();
        return true;
	}

	private class CookAnItem implements Runnable {
	    private final Order food;

	    CookAnItem(Order food) {
	        this.food = food;
        }

		public void run() {
			try {
                Thread.sleep(food.cookTimeS * 1000);
                food.prepTime = System.currentTimeMillis();
                Kitchen.updateCookedOrder(food);

			} catch(InterruptedException e) {
                System.out.println("Cooking thread interrupted.");
            }
		}
	}
}