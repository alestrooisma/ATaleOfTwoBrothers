package atotb.controller.log;

/**
 * EventProcessor instances can be added as a listener to EventLogs.
 * 
 * @author Ale Strooisma
 */
public interface EventProcessor {
	public void push(Event e);
}
