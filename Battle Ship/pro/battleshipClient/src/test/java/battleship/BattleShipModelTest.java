package battleship;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import battleship.model.Model;

public class BattleShipModelTest
{
	private Model model = new Model();

    @Test
    public void setShipTest() {
        assertTrue(model.setShip(3, 0, 0, 2, 0));
        assertTrue(model.setShip(2, 2, 7, 2, 6));
        assertTrue(model.setShip(1, 9, 9, 9, 9));   
    }

    @Test
    public void getShotTest() {
    	model.setShip(3, 0, 0, 2, 0);
    	model.setShip(1, 9, 9, 9, 9);

    	assertEquals(3, model.getShot(5, 5));
    	assertEquals(2, model.getShot(0, 0));
    	assertEquals(2, model.getShot(9, 9));
    }
}
