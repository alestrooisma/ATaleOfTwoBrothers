package atotb.controller.ai;

import atotb.controller.BattleController;
import atotb.model.Army;
import atotb.model.Battle;
import atotb.model.Unit;
import atotb.util.Enum.Direction;
import atotb.util.PathFinder;
import com.badlogic.gdx.utils.Array;
import java.awt.Point;

/**
 * Very simple AI intended to be used for a pack of wolves.
 * 
 * @todo units fail to move if there is no enemy unit with a neighbouring accessible tile 
 * 
 * @author Ale Strooisma
 */
public class WolfAI implements ArtificialIntelligence {

	@Override
	public void playTurn(BattleController controller) {

		// Getting some useful objects
		Battle battle = controller.getBattle();
		int player = battle.getCurrentPlayer();
		Army army = battle.getArmy(player);
		Army enemyArmy = battle.getArmy(battle.otherPlayer(player)); //TODO multiple enemy armies
		PathFinder pf = controller.getPathFinder();

		// Iterate over all units
		for (Unit unit : army.getUnits()) {
			if (unit.isAlive()) {
				pf.calculateDistancesFrom(
						unit.getPosition().x, unit.getPosition().y);

				// Get nearest enemy
				boolean charge = true;
				Unit nearestEnemy = getNearestEnemy(controller, enemyArmy, pf, charge);
				if (nearestEnemy == null) {
					charge = false;
					nearestEnemy = getNearestEnemy(controller, enemyArmy, pf, charge);
				}

				// Act
				if (nearestEnemy != null) {
					int tx = nearestEnemy.getPosition().x;
					int ty = nearestEnemy.getPosition().y;
					Direction dir = controller.getChargingDirection(tx, ty, pf);
					double d = controller.getChargingDistance(tx, ty, pf, dir);

					if (charge && d <= unit.getTotalMovesRemaining()) {
						controller.targetUnit(unit, nearestEnemy, null, pf);
					} else {
						Array<Point> path = pf.getPathTo(
								dir.getX(tx),
								dir.getY(ty),
								unit.getTotalMovesRemaining());
						if (path.size != 0) {
							controller.moveUnit(unit, path.first().x, path.first().y, pf);
						}
					}
				}
			}
		}
	}

	private Unit getNearestEnemy(BattleController controller, Army enemyArmy, PathFinder pf, boolean charge) {
		Unit nearestEnemy = null;
		double minDistance = Double.MAX_VALUE;
		for (Unit enemyUnit : enemyArmy.getUnits()) {
			if (enemyUnit.isAlive() && !(charge && enemyUnit.isLockedIntoCombat())) {
				int tx = enemyUnit.getPosition().x;
				int ty = enemyUnit.getPosition().y;
				Direction dir = controller.getChargingDirection(tx, ty, pf);
				if (dir != null) {
					double d = controller.getChargingDistance(tx, ty, pf, dir);
					if (d < minDistance) {
						nearestEnemy = enemyUnit;
						minDistance = d;
					}
				}
			}
		}
		return nearestEnemy;
	}
}
