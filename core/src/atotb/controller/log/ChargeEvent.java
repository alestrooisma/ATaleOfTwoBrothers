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
public class ChargeEvent extends AbstractMoveEvent {
	private final Unit opponent;
	
	public ChargeEvent(Unit unit, Unit opponent, 
			int fromX, int fromY, int destX, int destY, double distance) {
		super(unit, fromX, fromY, destX, destY, distance);
		this.opponent = opponent;
	}

	public Unit getOpponent() {
		return opponent;
	}

	@Override
	public String toString() {
		return unit.getName() + " dashed to " + destX + ", " + destY;
	}
	
	@Override
	public void visit(EventVisitor visitor) {
		visitor.visitChargeEvent(this);
	}
	
}
