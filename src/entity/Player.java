package entity;

import static utils.Constants.PlayerConstants.ATTACK;
import static utils.Constants.PlayerConstants.JUMP;
import static utils.Constants.PlayerConstants.GetSpriteAmount;
import static utils.Constants.PlayerConstants.IDLE;
import static utils.Constants.PlayerConstants.RUNNING;
import static utils.Constants.PlayerConstants.FALLING;
import static utils.HelpMethods.GetEntityXPosNextToWall;
import static utils.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor;
import static utils.HelpMethods.IsEntityOnFloor;

import static utils.HelpMethods.CanMoveHere;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import gamestates.Playing;
import main.Game;
import utils.LoadSave;

public class Player extends Entity {

	private BufferedImage[][] animations;
	private int aniTick, aniIndex, aniSpeed = 25;
	private int playerAction = IDLE;

	private boolean moving = false, attacking = false;
	private boolean left, up, right, down, jump;
	private float playerSpeed = 1.0f * Game.SCALE;

	private int[][] lvlData;
	// X OFFSET COORDINATE OF HITBOX
	private float xDrawOffset = 21 * Game.SCALE;
	// Y OFFSET COORDINATE OF HITBOX
	private float YDrawOffset = 4 * Game.SCALE;

	// JUMPING, GRAVITY

	// SPEED OF TRAVELING THROUGH THE AIR
	private float airSpeed = 0F;
	// LOWER THE GRAVITY, THE HIGHER THE JUMP
	private float gravity = 0.04F * Game.SCALE;
	// JUMP SPEED IS NEGATIVE (GOING UP IN Y DIRECTION)
	private float jumpSpeed = -2.25F * Game.SCALE;
	// IN CASE PLAYER IS HITTING THE ROOF, WE ARE
	// SETTING A NEW VALUE FOR AIR SPEED
	private float fallSpeedAfterCollision = 0.5F * Game.SCALE;
	private boolean inAir = false;

	// STATUS BAR UI
	private BufferedImage statusBarImg;

	private int statusBarWidth = (int) (192 * Game.SCALE);
	private int statusBarHeight = (int) (58 * Game.SCALE);
	private int statusBarX = (int) (10 * Game.SCALE);
	private int statusBarY = (int) (10 * Game.SCALE);

	private int healthBarWidth = (int) (150 * Game.SCALE);
	private int healthBarHeight = (int) (4 * Game.SCALE);
	private int healthBarXStart = (int) (34 * Game.SCALE);
	private int healthBarYStart = (int) (14 * Game.SCALE);

	// HEALTH
	private int maxHealth = 100;
	private int currentHealth = maxHealth;
	private int healthWidth = healthBarWidth;

	// ATTACK BOX
	private Rectangle2D.Float attackBox;
	private int flipX = 0;
	private int flipW = 1;

	private boolean attackChecked;
	private Playing playing;

	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		loadAnimations();
		initHitbox(x, y, (int) (20 * Game.SCALE), (int) (27 * Game.SCALE));
		initAttackBox();
	}

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (20 * Game.SCALE),
				(int) (20 * Game.SCALE));
	}

	public void update() {

		updateHealthBar();

		if (currentHealth <= 0) {
			playing.setGameOver(true);
			return;
		}

		updateAttackBox();

		updatePos();
		if (attacking) {
			checkAttack();
		}
		updateAnimationTick();
		setAnimation();
	}

	private void checkAttack() {
		if (attackChecked || aniIndex != 1) {
			return;
		}
		attackChecked = true;
		playing.checkEnemyHit(attackBox);
	}

	private void updateAttackBox() {
		if (right) {
			attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 10);
		} else if (left) {
			attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 10);
		}
		attackBox.y = hitbox.y + (int) (Game.SCALE * 10);
	}

	private void updateHealthBar() {
		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
	}

	public void render(Graphics g, int lvlOffset) {
		g.drawImage(animations[playerAction][aniIndex],
				(int) (hitbox.x - xDrawOffset) - lvlOffset + flipX,
				(int) (hitbox.y - YDrawOffset), width * flipW, height, null);
//		drawHitbox(g,lvlOffset);

//		drawAttackBox(g, lvlOffset);

		drawUI(g);
	}

	private void drawAttackBox(Graphics g, int lvlOffsetX) {
		g.setColor(Color.red);
		g.drawRect((int) attackBox.x - lvlOffsetX, (int) attackBox.y,
				(int) attackBox.width, (int) attackBox.height);
	}

	// NO LVL OFFSET IS NEEDED HERE , STATUS BAR IS ALWAYS
	// DRAWN AT THE SAME POSITION
	private void drawUI(Graphics g) {
		// DRAW STATUS BAR
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth,
				statusBarHeight, null);
		// DRAW HEALTH
		g.setColor(Color.red);
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY,
				healthWidth, healthBarHeight);
	}

	private void setAnimation() {

		int startAni = playerAction;

		if (moving) {
			playerAction = RUNNING;
		} else {
			playerAction = IDLE;
		}

		if (inAir) {
			// GOING UP IN THE AIR (JUMPING)
			if (airSpeed < 0) {
				playerAction = JUMP;
			} else {
				playerAction = FALLING;
			}
		}

		if (attacking) {
			playerAction = ATTACK;
			if (startAni != ATTACK) {
				aniIndex = 1;
				aniTick = 0;
				return;
			}
		}

		if (startAni != playerAction) {
			resetAniTick();
		}
	}

	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
	}

	private void updatePos() {

		moving = false;

		if (jump) {
			jump();
		}

		// CHECK IF WE ARE MOVING AT ALL
		// OR IN THE AIR
		// (PRESSING MOVEMENT KEYS)
//		if (!left && !right && !inAir) {
//			return;
//		}

		if (!inAir) {
			if ((!left && !right) || (left && right)) {
				return;
			}
		}

		// TEMP STORAGE OF SPEED
		float xSpeed = 0;

		if (left) {
			xSpeed -= playerSpeed;
			flipX = width;
			flipW = -1;
		}
		if (right) {
			xSpeed += playerSpeed;
			flipX = 0;
			flipW = 1;
		}

		if (!inAir) {
			if (!IsEntityOnFloor(hitbox, lvlData)) {
				inAir = true;
			}
		}

		if (inAir) {
			// CHECK BOTH X & Y POSITION FOR COLLISION (IF IN AIR)
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width,
					hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += gravity;
				updateXPos(xSpeed);
			} else {
				// IF WE CANNOT MOVE (HITTING FLOOR OR ROOF)
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);

				if (airSpeed > 0) {
					// WE HIT THE FLOOR
					resetInAir();
				} else {
					// WE HIT THE ROOF
					airSpeed = fallSpeedAfterCollision;
				}
				updateXPos(xSpeed);
			}
		} else {
			// CHECK FOR X POSITION ONLY (IF NOT IN AIR)
			updateXPos(xSpeed);
		}
		moving = true;
	}

	private void jump() {
		if (inAir) {
			return;
		}
		inAir = true;
		airSpeed = jumpSpeed;
	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
	}

	private void updateXPos(float xSpeed) {
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height,
				lvlData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
		}
	}

	public void changeHealth(int value) {
		currentHealth += value;
		if (currentHealth <= 0) {
			currentHealth = 0;
			// TODO: gameOver()
		} else if (currentHealth >= maxHealth) {
			currentHealth = maxHealth;
		}
	}

	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= aniSpeed) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(playerAction)) {
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
			}

		}
	}

	private void loadAnimations() {

		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
		animations = new BufferedImage[7][8];

		for (int j = 0; j < animations.length; j++) {
			for (int i = 0; i < animations[j].length; i++) {
				animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40);
			}
		}

		statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);

	}

	public void loadLevelData(int[][] lvlData) {
		this.lvlData = lvlData;
		// CHECK IF PLAYER IS ALREADY IN AIR
		if (!IsEntityOnFloor(hitbox, lvlData)) {
			inAir = true;
		}
	}

	public void resetDirBooleans() {
		left = false;
		up = false;
		right = false;
		down = false;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public void resetAll() {
		resetDirBooleans();
		inAir = false;
		attacking = false;
		moving = false;
		playerAction = IDLE;
		currentHealth = maxHealth;

		// RESET PLAYER POSITION
		hitbox.x = x;
		hitbox.y = y;

		if (!IsEntityOnFloor(hitbox, lvlData)) {
			inAir = true;
		}
	}

}
