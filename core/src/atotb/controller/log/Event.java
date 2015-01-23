package atotb.controller.log;

/**
 * Something that happened during the game that should be logged.
 * 
 * @author Ale Strooisma
 */
public interface Event {
    public void visit(EventVisitor visitor);
}
