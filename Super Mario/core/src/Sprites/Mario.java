package Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import Screens.PlayScreen;
import interno.mygdx.game.Main;

public class Mario extends Sprite {
	public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING}
	public State currentState;
	public State previousState;
	public World world;
	public Body b2body;
	private TextureRegion marioStand;
	private Animation marioRun;
	private TextureRegion marioJump;
	private TextureRegion bigMarioStand;
	private TextureRegion bigMarioJump;
	private Animation bigMarioRun;
	private Animation growMario;
	
	private float stateTimer;
	private boolean runningRight;
	private boolean marioIsBig;
	private boolean runGrowAnimation;
	
	public Mario(PlayScreen screen) {
		this.world = screen.getWorld();
		currentState = State.STANDING;
		previousState = State.STANDING;
		stateTimer = 0;
		runningRight = true;
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		
		for(int i = 1; i < 8; i++) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 24, 0, 24, 32));
		}
		
		marioRun = new Animation(0.1f, frames);
		
		frames.clear();
		
		for(int i = 1; i < 8; i++) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 32, 0, 32, 64));
		}
		
		bigMarioRun = new Animation(0.1f, frames);
		frames.clear();
		
		frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 32, 64));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 24, 32));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 32, 64));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 24, 32));
		growMario = new Animation(0.2f, frames);
		
		frames.clear();
		
		marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 192, 0, 24, 32);
		bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 256, 0, 32, 64);
		
		defineMario();
		marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 24, 32);
		bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 32, 64);
		setBounds(0, 0, 32 / Main.PPM, 32 / Main.PPM);
		setRegion(marioStand);
	}
	
	public void update(float dt) {
		setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
		setRegion(getFrame(dt));
	}

	public TextureRegion getFrame(float dt) {
		currentState = getState();
		
		TextureRegion region;
		switch (currentState) {
			case GROWING:
				region = (TextureRegion) growMario.getKeyFrame(stateTimer);
				if(growMario.isAnimationFinished(stateTimer)) {
					runGrowAnimation = false;
				}
				break;
			case JUMPING:
				region = marioIsBig ? bigMarioJump : marioJump;
				break;
			case RUNNING:
				region = (TextureRegion) (marioIsBig ? bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true));
				break;
			case FALLING:
			case STANDING:
			default:
				region = marioIsBig ? bigMarioStand : marioStand;
				break;
		}
		
		if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
			region.flip(true, false);
			runningRight = false;
		}
		else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
			region.flip(true, false);
			runningRight = true;
		}
		
		stateTimer = currentState == previousState ? stateTimer + dt : 0;
		previousState = currentState;
		return region;
	}
	
	public State getState() {
		if(runGrowAnimation) {
			return State.GROWING;
		}
		
		else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
			return State.JUMPING;
		}
		
		else if (b2body.getLinearVelocity().y < 0) {
			return State.FALLING;
		}
		
		else if (b2body.getLinearVelocity().x != 0) {
			return State.RUNNING;
		}
		else {
			return State.STANDING;
		}
	}
	
	public void defineMario() {
		// TODO Auto-generated method stub
		BodyDef bdef = new BodyDef();
		bdef.position.set(32 / Main.PPM, 32 / Main.PPM);
		bdef.type = BodyDef.BodyType.DynamicBody;
		b2body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(12 / Main.PPM);
		fdef.filter.categoryBits = Main.MARIO_BIT; //Define a categoria do body
		fdef.filter.maskBits = Main.GROUND_BIT | //Define com o que ele colide
				Main.COIN_BIT |
				Main.BRICK_BIT |
				Main.ENEMY_BIT |
				Main.OBJECT_BIT |
				Main.ENEMY_HEAD_BIT |
				Main.ITEM_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef);
		
		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2 / Main.PPM, 12 / Main.PPM), new Vector2(2 / Main.PPM, 12 / Main.PPM));
		fdef.shape = head;
		fdef.isSensor = true;
		
		b2body.createFixture(fdef).setUserData("head");
	}
	
	public void grow() {
		runGrowAnimation = true;
		marioIsBig = true;
		setBounds(getX(), getY(),getWidth(), getHeight() * 2);
		Main.manager.get("audio/sounds/powerup.wav");
	}

}