package atotb.controller.ai;

import atotb.TwoBrothersGame;
import atotb.model.Army;
import atotb.model.Battle;
import atotb.model.Unit;
import atotb.util.Enum.Direction;
import atotb.util.PathFinder;
import com.badlogic.gdx.utils.Array;
import java.awt.Point;

/**
 *
 * @author Ale Strooisma
 */
public class WolfAI implements ArtificialIntelligence {

	@Override
	public void playTurn(TwoBrothersGame game) {
		System.out.println("Start AI");

		// Getting some useful objects
		int player = game.getModel().getBattle().getCurrentPlayer();
		Battle battle = game.getModel().getBattle();
		Army army = battle.getArmy(player);
		Army enemyArmy = battle.getArmy((player + 1) % 2); //TODO multiple enemy armies
		PathFinder pf = game.getPathFinder();

		// Iterate over all units
		for (Unit unit : army.getUnits()) {
			if (unit.isAlive()) {
				game.selectUnit(unit);
				pf.calculateDistancesFrom(
						unit.getPosition().x, unit.getPosition().y);

				// Get nearest enemy
				Unit nearestEnemy = null;
				double minDistance = Double.MAX_VALUE;
				for (Unit enemyUnit : enemyArmy.getUnits()) {
					if (enemyUnit.isAlive() && !enemyUnit.isLockedIntoCombat()) {
						int tx = enemyUnit.getPosition().x;
						int ty = enemyUnit.getPosition().y;
						Direction dir = game.getChargingDirection(tx, ty, pf);
						double d = game.getChargingDistance(tx, ty, pf, dir);
						if (d < minDistance) {
							nearestEnemy = enemyUnit;
							minDistance = d;
						}
					}
				}

				// Charge at nearest enemy
				if (minDistance <= unit.getTotalMovesRemaining()) {
					game.targetUnit(unit, nearestEnemy, pf);
				} //TODO else move closer
			}
		}
		System.out.println("End AI");
	}
}
