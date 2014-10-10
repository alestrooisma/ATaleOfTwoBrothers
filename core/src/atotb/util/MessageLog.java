package atotb.util;

/**
 * Used to log messages to standard out and to the screen.
 * 
 * @author Ale Strooisma
 */
public class MessageLog {

	private final int maxSize;
	private final String[] log;
	private int lastEntry;
	private int currentSize;

	public MessageLog(int maxSize) {
		this.maxSize = maxSize;
		log = new String[this.maxSize];
		lastEntry = -1;
		currentSize = 0;
	}

	public int getLogSize() {
		return currentSize;
	}

	public void push(String message) {
		System.out.println("Log: " + message);
		lastEntry = (lastEntry + 1) % maxSize;
		log[lastEntry] = message;
		if (currentSize < maxSize) {
			currentSize++;
		}
	}

	public String get(int messageNumber) {
		if (messageNumber < currentSize) {
			return log[((lastEntry - messageNumber) % maxSize + maxSize) % maxSize];
		} else {
			return null;
		}
	}
}
