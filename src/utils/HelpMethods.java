package utils;

import java.awt.geom.Rectangle2D;

import main.Game;

public class HelpMethods {

	public static boolean CanMoveHere(float x, float y, float width, float height,
			int[][] lvlData) {

		// TOP LEFT POINT
		if (!IsSolid(x, y, lvlData)) {

			// BOTTOM RIGHT POINT (NESTED)
			if (!IsSolid(x + width, y + height, lvlData)) {

				// TOP RIGHT POINT (NESTED)
				if (!IsSolid(x + width, y, lvlData)) {

					// BOTTOM LEFT POINT (NESTED)
					if (!IsSolid(x, y + height, lvlData)) {
						// IF ALL OF THESE ARE FALSE , RETURN TRUE
						// PLAYER CAN MOVE
						return true;
					}
				}
			}
		}

		// IF ANY OF THESE CONDITIONS ARE TRUE ( isSolid(...) == true) ,
		// RETURN FALSE (PLAYER CANNOT MOVE)
		return false;
	}

	private static boolean IsSolid(float x, float y, int[][] lvlData) {
		if (x < 0 || x >= Game.GAME_WIDTH) {
			// RETURN TRUE IF SOLID
			return true;
		}
		if (y < 0 || y >= Game.GAME_HEIGHT) {
			return true;
		}

		float xIndex = x / Game.TILES_SIZE;
		float yIndex = y / Game.TILES_SIZE;

		int value = lvlData[(int) yIndex][(int) xIndex];

		// CHECK IF VALUE IS A TILE (48 SPRITES)
		// OR
		// VALUE IS BELLOW ZERO (FOR SOME UNEXPECTED REASON)
		// OR
		// VALUE IS NOT EQUAL TO 11 (12TH SPRITE IS TRANSPARENT)
		if (value >= 48 || value < 0 || value != 11) {
			return true;
		}
		return false;

	}

	public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox,
			float xSpeed) {

		int currentTile = (int) (hitbox.x / Game.TILES_SIZE);

		if (xSpeed > 0) {
			// Right
			int tileXPos = currentTile * Game.TILES_SIZE;
			int xOffset = (int) (Game.TILES_SIZE - hitbox.width);
			return tileXPos + xOffset - 1;
		} else {
			// Left
			return currentTile * Game.TILES_SIZE;
		}

	}

	public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox,
			float airSpeed) {

		int currentTile = (int) (hitbox.y / Game.TILES_SIZE);

		if (airSpeed > 0) {
			// FALLING (TOUCHING FLOOR)
			int tileYPos = currentTile * Game.TILES_SIZE;
			int yOffset = (int) (Game.TILES_SIZE - hitbox.height);
			return tileYPos + yOffset - 1;
		} else {
			// JUMPING
			return currentTile * Game.TILES_SIZE;
		}

	}

	public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox,
			int[][] lvlData) {
		
		// CHECK THE PIXEL BELLOW BOTTOMLEFT AND BOTTOMRIGHT
		if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData)) {
			if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1,
					lvlData)) {
				return false;
			}
		}
		return true;
	}

}
