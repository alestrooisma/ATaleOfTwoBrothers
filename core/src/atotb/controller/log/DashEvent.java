/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atotb.controller.log;

import atotb.model.Unit;
import com.badlogic.gdx.utils.Array;
import java.awt.Point;

/**
 * The event of a unit dashing.
 * 
 * @author Ale Strooisma
 */
public class DashEvent extends AbstractMoveEvent {

	public DashEvent(Unit unit, int fromX, int fromY, Array<Point> path, double distance) {
		super(unit, fromX, fromY, path, distance);
	}

	@Override
	public String toString() {
		return getUnit().getName() + " dashed to " + getDestinationX() + ", " + getDestinationY();
	}
	
	@Override
	public void visit(EventVisitor visitor) {
		visitor.visitDashEvent(this);
	}
	
}
