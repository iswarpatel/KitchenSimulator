package dispatch;

/**
 * A Chef is used to cook Food order. it can make
 * many food items in parallel
 */

public class Chef {

	synchronized boolean makeFood(Food food) throws InterruptedException {
        Thread cookThread = new Thread(new CookAnItem(food));
        cookThread.start();
        return true;
	}

	private class CookAnItem implements Runnable {
	    private final Food food;

	    CookAnItem(Food food) {
	        this.food = food;
        }

		public void run() {
			try {
                Thread.sleep(food.cookTimeS * 1000);
                food.prepTime = System.currentTimeMillis();
                Kitchen.dispatchManager.dispatch(food);

			} catch(InterruptedException e) {
                System.out.println("Cooking thread interrupted.");
            }
		}
	}
}