package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import Scenes.Hud;
import Sprites.Mario;
import Tools.B2WorldCreator;
import Tools.WorldContactListener;
import interno.mygdx.game.Main;

public class PlayScreen implements Screen {

	private Main game;
	private TextureAtlas atlas;
	private OrthographicCamera gameCam;
	private Viewport gamePort;
	private Hud hud;
	
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	
	private World world;
	private Box2DDebugRenderer b2dr;
	
	private Mario player;
	private Music music;
	
	public PlayScreen(Main game) {
		atlas = new TextureAtlas("Mario_and_Enemies.pack");
		
		this.game = game;
		gameCam = new OrthographicCamera();
		gamePort = new FitViewport(Main.V_WIDTH / Main.PPM, Main.V_HEIGHT / Main.PPM, gameCam);
		hud = new Hud(game.batch);
		
		mapLoader = new TmxMapLoader();
		map = mapLoader.load("Level1.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / Main.PPM );
		gameCam.position.set(gamePort.getScreenWidth() / 2, gamePort.getScreenHeight() / 2, 0);
		
		world = new World(new Vector2(0, -10), true);
		b2dr = new Box2DDebugRenderer();
		
		new B2WorldCreator(world, map);
		
		player = new Mario(world, this);
		
		world.setContactListener(new WorldContactListener());
		
		music = Main.manager.get("audio/music/groundTheme.mp3", Music.class);
		music.setLooping(true);
		music.play();
	}
	
	public TextureAtlas getAtlas() {
		return atlas;
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
	
	public void update(float dt) {
		handleInput(dt);
		
		world.step(1 / 60f, 6, 2);
		
		player.update(dt);
		hud.update(dt);
		gameCam.position.x = player.b2body.getPosition().x;
		gameCam.update();
		renderer.setView(gameCam);
	}

	private void handleInput(float dt) {
		// TODO Auto-generated method stub
		if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			player.b2body.applyLinearImpulse(new Vector2(0, 6f), player.b2body.getWorldCenter(), true);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2) {
			player.b2body.applyLinearImpulse(new Vector2(0.2f, 0), player.b2body.getWorldCenter(), true);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2) {
			player.b2body.applyLinearImpulse(new Vector2(-0.2f, 0), player.b2body.getWorldCenter(), true);
		}
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		update(delta);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		renderer.render();
		b2dr.render(world, gameCam.combined);
		game.batch.setProjectionMatrix(gameCam.combined);
		
		game.batch.begin();
		player.draw(game.batch);
		game.batch.end();
		
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		gamePort.update(width, height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
	}

}
