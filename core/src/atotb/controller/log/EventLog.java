package atotb.controller.log;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Maintains a list of events for logging purposes.
 *
 * @author Ale Strooisma
 */
public class EventLog implements EventProcessor {

	private final LinkedList<Event> queue = new LinkedList<Event>();
	private final LinkedList<Event> events = new LinkedList<Event>();
	private final LinkedList<EventProcessor> listeners = new LinkedList<EventProcessor>();
	private boolean processing = false;

	/**
	 * Pushes an event to this event log. The event is added at the tail of the
	 * log. The event is also added to all event logs that are registered as
	 * listeners.
	 *
	 * @param e the new event
	 */
	@Override
	public void push(Event e) {
		queue.add(e);

		if (!processing) {
			// Prevent other calls to process the events
			processing = true;
			
			// Process all events in the queue
			while (!queue.isEmpty()) {
				Event queuedEvent = queue.pollFirst();
				
				// Add it to the local log
				events.add(queuedEvent);
				
				// Send the event to all listeners
				for (EventProcessor ep : listeners) {
					ep.push(queuedEvent);
				}
			}
			
			// Finished for now, allow processing for the next new event
			processing = false;
		}
	}

	/**
	 * Removes and returns the head of the log. In other words, it returns the
	 * oldest event in the log. Note that only this log is modified, not the
	 * registered listeners.
	 *
	 * @return the head of the log
	 */
	public Event pull() {
		return events.remove();
	}

	/**
	 * Adds the given log to the registered event logs. This means that any
	 * event added to this log will also be added to the given log.
	 *
	 * @param log
	 */
	public void register(EventProcessor log) {
		listeners.add(log);
	}

	/**
	 * Removes the given log from the list of registered logs.
	 *
	 * @param log the log to deregister
	 * @return true if the log is successfully deregistered
	 */
	public boolean deregister(EventProcessor log) {
		return listeners.remove(log);
	}

	/**
	 * Returns an iterator over the list of events.
	 *
	 * @return a list iterator
	 */
	public ListIterator<Event> iterator() {
		return events.listIterator();
	}

	/**
	 * Returns whether there are any events in the log.
	 *
	 * @return true if there are no events in the log
	 */
	public boolean isEmpty() {
		return events.isEmpty();
	}
}
