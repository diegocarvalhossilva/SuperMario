package Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import Screens.PlayScreen;
import interno.mygdx.game.Main;

public class Goomba extends Enemy{

	private float stateTime;
	private Animation walkAnimation;
	private Array<TextureRegion> frames;
	private boolean setToDestroy;
	private boolean destroyed;
	
	public Goomba(PlayScreen screen, float x, float y) {
		super(screen, x, y);
		frames = new Array<TextureRegion>();
		for(int i = 0; i < 5; i++) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 32, 0, 32, 32));
		}
		walkAnimation = new Animation(0.1f, frames); /*Animações "PingPong" não são possíveis, mas é possível emular isso*/
		stateTime = 0;
		setBounds(getX(), getY(), 32 / Main.PPM, 32 / Main.PPM);
		setToDestroy = false;
		destroyed = false;
	}
	
	public void update(float dt) {
		stateTime += dt;
		if(setToDestroy && !destroyed) {
			world.destroyBody(b2body);
			destroyed = true;
			setRegion (new TextureRegion(screen.getAtlas().findRegion("goomba"), 160, 0, 32, 32));
		}
		
		else if (!destroyed) {
			b2body.setLinearVelocity(velocity);
			setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
			setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime, true));
		}
		
	}

	@Override
	protected void defineEnemy() {
		// TODO Auto-generated method stub
		BodyDef bdef = new BodyDef();
		bdef.position.set(getX(), getY());
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(12 / Main.PPM);
		fdef.filter.categoryBits = Main.ENEMY_BIT;
		fdef.filter.maskBits = Main.GROUND_BIT |
				Main.COIN_BIT |
				Main.BRICK_BIT |
				Main.ENEMY_BIT |
				Main.OBJECT_BIT |
				Main.MARIO_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		
		PolygonShape head = new PolygonShape();
		Vector2[] vertice = new Vector2[4];
		vertice[0] = new Vector2(-10, 16).scl(1/ Main.PPM);
		vertice[1] = new Vector2(10, 16).scl(1/ Main.PPM);
		vertice[2] = new Vector2(-6, 6).scl(1/ Main.PPM);
		vertice[3] = new Vector2(6, 6).scl(1/ Main.PPM);
		head.set(vertice);
		
		fdef.shape = head;
		fdef.restitution = 0.5f;
		fdef.filter.categoryBits = Main.ENEMY_HEAD_BIT;
		
		b2body.createFixture(fdef).setUserData(this);
		
	}

	public void draw(Batch batch) {
		if(!destroyed || stateTime < 1) {
			super.draw(batch);
		}
		
	}
	
	@Override
	public void hitOnHead() {
		// TODO Auto-generated method stub
		setToDestroy = true;
	}

}
