package Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

import Scenes.Hud;
import Screens.PlayScreen;
import interno.mygdx.game.Main;

public class Brick extends InteractiveTileObject{

	public Brick(PlayScreen screen, MapObject object) {
		super(screen, object);
		// TODO Auto-generated constructor stub
		fixture.setUserData(this);
		setCategoryFilter(Main.BRICK_BIT);
		
		
	}

	@Override
	public void onHeadHit() {
		// TODO Auto-generated method stub
		Gdx.app.log("Brick", "Collision");
		setCategoryFilter(Main.DESTROYED_BIT);
		getCell().setTile(null);
		Hud.addScore(200);
		Main.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
	}

}
