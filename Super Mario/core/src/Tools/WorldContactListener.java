package Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import Sprites.Enemy;
import Sprites.InteractiveTileObject;
import Sprites.Mario;
import Sprites.Items.Item;
import interno.mygdx.game.Main;

public class WorldContactListener implements ContactListener{

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
		
		if(fixA.getUserData() == "head" || fixB.getUserData() == "head") {
			Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
			Fixture object = head == fixA ? fixB : fixA;
			
			if(object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass())) {
				((InteractiveTileObject) object.getUserData()).onHeadHit();
			}
		}
		
		/*Da comandos caso uma colisão específica aconteça*/
		switch (cDef) {
			case Main.ENEMY_HEAD_BIT | Main.MARIO_BIT:
				if(fixA.getFilterData().categoryBits == Main.ENEMY_HEAD_BIT) {
					((Enemy)fixA.getUserData()).hitOnHead();
				}
				
				else {
					((Enemy)fixA.getUserData()).hitOnHead();
				}
				break;
			case Main.ENEMY_BIT | Main.OBJECT_BIT:
				if(fixA.getFilterData().categoryBits == Main.ENEMY_BIT) {
					((Enemy)fixA.getUserData()).reverseVelocity(true, false);
				}
				
				else {
					((Enemy)fixB.getUserData()).reverseVelocity(true, false);
				}
				break;
			case Main.MARIO_BIT | Main.ENEMY_BIT:
				break;
			case Main.ENEMY_BIT | Main.ENEMY_BIT:
				((Enemy)fixA.getUserData()).reverseVelocity(true, false);
				((Enemy)fixB.getUserData()).reverseVelocity(true, false);
				break;
			case Main.ITEM_BIT | Main.OBJECT_BIT:
				if(fixA.getFilterData().categoryBits == Main.ITEM_BIT) {
					((Item)fixA.getUserData()).reverseVelocity(true, false);
				}
				
				else {
					((Item)fixB.getUserData()).reverseVelocity(true, false);
				}
				break;
			case Main.ITEM_BIT | Main.MARIO_BIT:
				 if(fixA.getFilterData().categoryBits == Main.ITEM_BIT) {
					 ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
				 }
				 
				 else {
					 ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
				 }
	             break;
		}
		
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
