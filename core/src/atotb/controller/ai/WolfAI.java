package atotb.controller.ai;

import atotb.TwoBrothersGame;
import atotb.model.Army;
import atotb.model.Battle;
import atotb.model.Unit;
import atotb.util.Enum.Direction;
import atotb.util.PathFinder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ale Strooisma
 */
public class WolfAI implements ArtificialIntelligence {

	@Override
	public void playTurn(TwoBrothersGame game, int player) {
		System.out.println("Start AI");

		// Getting some useful objects
		Battle battle = game.getModel().getBattle();
		Army army = battle.getArmy(player);
		Army enemyArmy = battle.getArmy((player + 1) % 2); //TODO multiple parties
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

				if (minDistance <= unit.getTotalMovesRemaining()) {
					game.targetUnit(unit, nearestEnemy, pf);
				}
			}
		}
		System.out.println("End AI");
	}
}
