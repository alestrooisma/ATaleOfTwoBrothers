package atotb.controller.log;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Maintains a list of events for logging purposes.
 * 
 * @author Ale Strooisma
 */
public class EventLog {
    private final LinkedList<Event> events = new LinkedList<Event>();
	private final LinkedList<EventLog> listeners = new LinkedList<EventLog>();
	
	/**
	 * Pushes an event to this event log. The event is added at the tail of the 
	 * log. The event is also added to all event logs that are registered as 
	 * listeners.
	 * 
	 * @param e the new event
	 */
    public void push(Event e) {
        events.add(e);
		for (EventLog log : listeners) {
			log.push(e);
		}
    }
	
	/**
	 * Removes and returns the head of the log. In other words, it returns the 
	 * oldest event in the log.
	 * Note that only this log is modified, not the registered listeners.
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
	public void register(EventLog log) {
		listeners.add(log);
	}

	/**
	 * Removes the given log from the list of registered logs.
	 * 
	 * @param log the log to deregister
	 * @return true if the log is successfully deregistered
	 */
	public boolean deregister(EventLog log) {
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
