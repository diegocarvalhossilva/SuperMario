package Sprites.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import Screens.PlayScreen;
import Sprites.Mario;
import interno.mygdx.game.Main;

public class Mushroom extends Item {

	public Mushroom(PlayScreen screen, float x, float y) {
		super(screen, x, y);
		setRegion(screen.getAtlas().findRegion("Mushroom"), 0, 0, 32, 32);
		velocity = new Vector2(0.7f, 0);
	}

	@Override
	public void defineItem() {
		// TODO Auto-generated method stub
		BodyDef bdef = new BodyDef();
		bdef.position.set(getX(), getY());
		bdef.type = BodyDef.BodyType.DynamicBody;
		body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(12 / Main.PPM);
		fdef.filter.categoryBits = Main.ITEM_BIT;
		fdef.filter.maskBits = Main.MARIO_BIT |
				Main.OBJECT_BIT |
				Main.GROUND_BIT |
				Main.COIN_BIT |
				Main.BRICK_BIT;
		
		fdef.shape = shape;
		body.createFixture(fdef).setUserData(this);
		
	}

	@Override
	public void use(Mario mario) {
		// TODO Auto-generated method stub
		destroy();
		mario.grow();
		
	}
	
	public void update(float dt) {
		super.update(dt);
		setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
		velocity.y = body.getLinearVelocity().y;
		body.setLinearVelocity(velocity);
	}
}