package Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import Scenes.Hud;
import Screens.PlayScreen;
import Sprites.Items.ItemDef;
import Sprites.Items.Mushroom;
import interno.mygdx.game.Main;

public class Coin extends InteractiveTileObject{
	
	private static TiledMapTileSet tileSet;
	private final int BLANK_COIN = 11;
	
	public Coin(PlayScreen screen, MapObject object) {
		super(screen, object);
		tileSet = map.getTileSets().getTileSet("Tileset");
		fixture.setUserData(this);
		setCategoryFilter(Main.COIN_BIT);
		
	}

	@Override
	public void onHeadHit() {
		// TODO Auto-generated method stub
		if(getCell().getTile().getId() == BLANK_COIN) {
			Main.manager.get("audio/sounds/bump.wav", Sound.class).play();
		}
		else {
			if(object.getProperties().containsKey("mushroom")) {
				screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 32 / Main.PPM), Mushroom.class));
				Main.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
			}
			else {
				Main.manager.get("audio/sounds/coin.wav", Sound.class).play();
			}
		}
		getCell().setTile(tileSet.getTile(BLANK_COIN));
		Hud.addScore(100);
	}

}
