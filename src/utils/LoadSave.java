package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import entity.Crabby;
import main.Game;

import static utils.Constants.EnemyConstants.CRABBY;

public class LoadSave {

	public static final String PLAYER_ATLAS = "player_images/player_sprites.png";
	public static final String LEVEL_ATLAS = "tiles/outside_sprites.png";
//	public static final String LEVEL_ONE_DATA = "levels/level_one_data.png";
	public static final String LEVEL_ONE_DATA = "levels/level_one_data_long.png";
	public static final String MENU_BUTTONS = "menu/button_atlas.png";
	public static final String MENU_BACKGROUND = "menu/menu_background.png";
	public static final String PAUSE_BACKGROUND = "menu/pause_menu.png";
	public static final String SOUND_BUTTONS = "menu/sound_button.png";
	public static final String URM_BUTTONS = "menu/urm_buttons.png";
	public static final String VOLUME_BUTTONS = "menu/volume_buttons.png";
	public static final String MENU_BACKGROUND_IMG = "menu/background_menu.png";
	public static final String PLAYING_BG_IMG = "playing_bg/playing_bg_img.png";
	public static final String BIG_CLOUDS = "playing_bg/big_clouds.png";
	public static final String SMALL_CLOUDS = "playing_bg/small_clouds.png";
	public static final String CRABBY_SPRITE = "enemy_images/crabby_sprite.png";
	public static final String STATUS_BAR = "status_bar/health_power_bar.png";

	public static BufferedImage GetSpriteAtlas(String fileName) {
		BufferedImage img = null;
		InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);

		try {
			img = ImageIO.read(is);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return img;
	}

	public static ArrayList<Crabby> GetCrabs() {
		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
		ArrayList<Crabby> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == CRABBY) {					
					list.add(new Crabby(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
				}
			}
		}
		return list;
	}

	public static int[][] GetLevelData() {
		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
		int[][] lvlData = new int[img.getHeight()][img.getWidth()];

		for (int j = 0; j < img.getHeight(); j++) {
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getRed();
				if (value >= 48) {
					value = 0;
				}
				lvlData[j][i] = value;
			}
		}
		return lvlData;
	}

}
