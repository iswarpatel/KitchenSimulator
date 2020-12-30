package dispatch;

public class DispatchFactory {
	public enum DispatchType {
		Matched, FIFO
	}

	public static Runnable getDispatcher(DispatchType type) {
		switch (type) {
		case FIFO:
			return new FIFODispatcher();
		case Matched:
			return new MatchedDispatcher();
		default:
			return null;
		}
	}

	public static DispatchType getDispatchType(String type) throws Exception {
		if (type == null) {
			return null;
		}
		if (type.equalsIgnoreCase("fifo")) {
			return DispatchType.FIFO;
		} else if (type.equalsIgnoreCase("matched")) {
			return DispatchType.Matched;
		}
		throw new Exception();
	}
}
