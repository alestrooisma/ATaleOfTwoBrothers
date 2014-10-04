package atotb.controller.events;

/**
 * An abstract class for abstracting various types of input events and using 
 * them as list items.
 * 
 * @author Ale Strooisma
 * 
 * @todo pooling
 */
public abstract class InputEvent {
	private InputEvent next;

	public InputEvent() {
		next = null;
	}
	
	public static class List {
		InputEvent head;
		InputEvent tail;
		
		public InputEvent next() {
			InputEvent rv = head;
			if (head != null) {
				head = head.next;
				if (head == null) {
					tail = null;
				}
			}
			return rv;
		}
		
		public void add(InputEvent e) {
			if (tail == null) {
				head = e;
			} else {
				tail.next = e;
			}
			tail = e;
		}
	}
}
