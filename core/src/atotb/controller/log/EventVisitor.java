package atotb.controller.log;

/**
 * Visitor interface for using the visitor pattern on events.
 * 
 * @author Ale Strooisma
 */
public interface EventVisitor {

	public void visitDeathEvent(DeathEvent event);

	public void visitMoveEvent(MoveEvent event);

	public void visitDashEvent(DashEvent event);

	public void visitChargeEvent(ChargeEvent event);
	
}
