package Screens;

import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import Scenes.Hud;
import Sprites.Enemy;
import Sprites.Goomba;
import Sprites.Mario;
import Sprites.Items.Item;
import Sprites.Items.ItemDef;
import Sprites.Items.Mushroom;
import Tools.B2WorldCreator;
import Tools.WorldContactListener;
import interno.mygdx.game.Main;

public class PlayScreen implements Screen {

	private Main game;
	private TextureAtlas atlas;
	
	/*Variáveis de câmera e hud*/
	private Hud hud;
	private OrthographicCamera gameCam;
	private Viewport gamePort;
	
	/*Variáveis da geração do tilemap*/
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	
	/*Variáveis do Box2D*/
	private World world;
	private Box2DDebugRenderer b2dr;
	private B2WorldCreator creator;
	
	private Mario player;
	private Music music;
	
	private Array<Item> items;
	private LinkedBlockingQueue<ItemDef> itemsToSpawn;
	
	
	public PlayScreen(Main game) {
		atlas = new TextureAtlas("Mario_and_Enemies.pack");
		
		this.game = game;
		gameCam = new OrthographicCamera(); //Cria a câmera que acompanha o personagem
		gamePort = new FitViewport(Main.V_WIDTH / Main.PPM, Main.V_HEIGHT / Main.PPM, gameCam); //Configura a resolução e a vista do jogador
		hud = new Hud(game.batch); //Cria a HUD
		
		mapLoader = new TmxMapLoader();
		map = mapLoader.load("Level1.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / Main.PPM );
		gameCam.position.set(gamePort.getScreenWidth() / 2, gamePort.getScreenHeight() / 2, 0);
		
		world = new World(new Vector2(0, -10), true);
		b2dr = new Box2DDebugRenderer();
		
		creator = new B2WorldCreator(this);
		
		player = new Mario(this);
		
		world.setContactListener(new WorldContactListener());
		
		music = Main.manager.get("audio/music/groundTheme.mp3", Music.class);
		music.setLooping(true);
		//music.play();
		
		items = new Array<Item>();
		itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
	}
	
	public void spawnItem(ItemDef idef) {
		itemsToSpawn.add(idef);
	}
	
	public void handleSpawningItems() {
		if(!itemsToSpawn.isEmpty()) {
			ItemDef idef = itemsToSpawn.poll();
			if(idef.type == Mushroom.class) {
				items.add(new Mushroom(this, idef.position.x, idef.position.y));
			}
		}
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
		handleSpawningItems();
		
		world.step(1 / 60f, 6, 2);
		
		player.update(dt);
		for(Enemy enemy : creator.getGoombas()) {
			enemy.update(dt);
			if(enemy.getX() < player.getX() + 448 / Main.PPM) {
				enemy.b2body.setActive(true);
			}
		}
		
		for(Item item: items) {
			item.update(dt);
		}
		
		hud.update(dt);
		
		/*Câmera segue o personagem*/
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
		
		/*Renderiza o mapa do jogo*/
		renderer.render();
		b2dr.render(world, gameCam.combined);
		game.batch.setProjectionMatrix(gameCam.combined);
		
		game.batch.begin();
		player.draw(game.batch);
		for(Enemy enemy : creator.getGoombas()) {
			enemy.draw(game.batch);;
		}
		
		for(Item item: items) {
			item.draw(game.batch);
		}
		game.batch.end();
		
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		gamePort.update(width, height);
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	public World getWorld() {
		return world;
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
