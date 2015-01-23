/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atotb.controller.log;

import atotb.model.Unit;

/**
 * The event of a unit dashing.
 * 
 * @author Ale Strooisma
 */
public class DashEvent extends AbstractMoveEvent {

	public DashEvent(Unit unit, int fromX, int fromY, int destX, int destY) {
		super(unit, fromX, fromY, destX, destY);
	}

	@Override
	public String toString() {
		return unit.getName() + " dashed to " + destX + ", " + destY;
	}
	
	@Override
	public void visit(EventVisitor visitor) {
		visitor.visitDashEvent(this);
	}
	
}
