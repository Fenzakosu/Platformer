package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;
import utils.LoadSave;

import static main.Game.TILES_SIZE;

public class LevelManager {

	private Game game;
	private BufferedImage[] levelSprites;
	private Level levelOne;

	public LevelManager(Game game) {
		this.game = game;
//		levelSprite = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
		importOutsideSprites();
		levelOne = new Level(LoadSave.GetLevelData());
	}

	private void importOutsideSprites() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
		levelSprites = new BufferedImage[48];

		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 12; i++) {
				int index = j * 12 + i;
				levelSprites[index] = img.getSubimage(i * 32, j * 32, 32, 32);
			}
		}
	}

	public void draw(Graphics g, int lvlOffset) {
		for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
			for (int i = 0; i < levelOne.getLvlData()[0].length; i++) {
				int index = levelOne.getSpriteIndex(i, j);
				g.drawImage(levelSprites[index], TILES_SIZE * i - lvlOffset,
						TILES_SIZE * j, TILES_SIZE, TILES_SIZE, null);
			}
		}
	}

	public void update() {

	}

	public Level getCurrentLevel() {
		return levelOne;
	}

}
