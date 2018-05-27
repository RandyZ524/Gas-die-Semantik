import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayDeque;
import java.util.Deque;

public class Projectile {
	int angle, penetration, maxLifeTime, lifeTimeFrames;
	int damage;
	double diagVelocity, xVelocity, yVelocity, xPos, yPos;
	boolean alive;
	ImageView body;
	Civilization home;
	
	public static Image[] bulletImages;
	public static Deque<Projectile> inGameBullets;
	public static Deque<Projectile> unusedBullets;
	
	static {
		bulletImages = new Image[4];
		bulletImages[0] = new Image("laser_bullet.png");
		bulletImages[1] = new Image("missile.gif");
		bulletImages[2] = new Image("sniper_bullet.png");
		bulletImages[3] = new Image("wave.png");
		inGameBullets = new ArrayDeque<>();
		unusedBullets = new ArrayDeque<>();
	}
	
	public Projectile() {
		body = new ImageView();
	}
	
	public void create(Ship owner, Civilization source, double speed, int spread, int damage, ProjectileType type) {
		
		if (spread == 0) {
			angle = owner.visualAngle;
		} else {
			angle = owner.visualAngle + Methods.randInt(-spread, spread);
		}

		this.damage = damage;
		penetration = 1;
		maxLifeTime = 120;
		lifeTimeFrames = maxLifeTime;
		xPos = owner.xPos;
		yPos = owner.yPos;
		diagVelocity = speed;
		xVelocity = diagVelocity * Math.sin(Math.toRadians(angle));
		yVelocity = -diagVelocity * Math.cos(Math.toRadians(angle));
		alive = true;
		body.setImage(bulletImages[type.ordinal()]);
		body.setRotate(angle);
		home = source;
	}

	public void create(Ship owner, int offSet, Civilization source, double speed, int spread, int damage, ProjectileType type) {
		int newAngle;
		
		if (spread == 0) {
			newAngle = owner.visualAngle + offSet;
		} else {
			newAngle = owner.visualAngle + offSet + Methods.randInt(-spread, spread);
		}
		
		create(owner, source, speed, spread, damage, type);
		angle = newAngle;
		xVelocity = diagVelocity * Math.sin(Math.toRadians(angle));
		yVelocity = -diagVelocity * Math.cos(Math.toRadians(angle));
		body.setRotate(angle);
	}
	
	public void update(double xOffset, double yOffset) {
		xPos += xVelocity;
		yPos += yVelocity;
		body.setLayoutX(xPos - 0.5 * body.getLayoutBounds().getWidth() - xOffset);
		body.setLayoutY(yPos - 0.5 * body.getLayoutBounds().getHeight() - yOffset);
		lifeTimeFrames--;
	}
	
	public static Projectile getAvailable() {
		
		if (Projectile.unusedBullets.isEmpty()) {
			return new Projectile();
		}
		
		return Projectile.unusedBullets.pop();
	}
	
}