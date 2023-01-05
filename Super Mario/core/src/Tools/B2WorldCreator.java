package Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import Screens.PlayScreen;
import Sprites.Brick;
import Sprites.Coin;
import Sprites.Goomba;
import interno.mygdx.game.Main;

public class B2WorldCreator {
	
	private Array<Goomba> goombas;
	
	public B2WorldCreator(PlayScreen screen) {
		
		World world = screen.getWorld();
		TiledMap map = screen.getMap();
		
		BodyDef bdef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fdef = new FixtureDef();
		Body body;
		
		//Cria o chão
		for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() / 2) / Main.PPM, (rect.getY() + rect.getHeight() / 2) / Main.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox(rect.getWidth() / 2 / Main.PPM, rect.getHeight() / 2 / Main.PPM);
			fdef.shape = shape;
			body.createFixture(fdef);
		}
		
		//Cria os canos
		for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() / 2)  / Main.PPM, (rect.getY() + rect.getHeight() / 2)  / Main.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox((rect.getWidth() / 2)  / Main.PPM, (rect.getHeight() / 2)  / Main.PPM);
			fdef.shape = shape;
			fdef.filter.categoryBits = Main.OBJECT_BIT;
			body.createFixture(fdef);
		}
		
		//Cria os blocos de tijolos
		for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			new Brick(screen, object);
		}
		
		//Cria os blocos de moeda
		for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
			
			new Coin(screen, object);
		}
		
		//Cria os goombas
		goombas = new Array<Goomba>();
		for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			goombas.add(new Goomba(screen, rect.getX() / Main.PPM, rect.getY() / Main.PPM));
		}
	}

	public Array<Goomba> getGoombas() {
		return goombas;
	}

}
