import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayDeque;
import java.util.Deque;

public class Projectile {
	int angle, penetration, maxLifeTime, lifeTimeFrames;
	int damage = 2;
	double diagVelocity, xVelocity, yVelocity, xPos, yPos;
	BulletSize size;
	ImageView body;
	Civilization home;
	
	public static Deque<Projectile> unusedBullets;
	
	static {
		unusedBullets = new ArrayDeque<>();
	}
	
	public Projectile() {
		size = null;
		body = new ImageView();
	}
	
	public void create(Ship owner, Civilization source, double speed, int spread, int damage, String src) {
		
		if (spread == 0) {
			angle = owner.visualAngle;
		} else {
			angle = Methods.randInt(owner.visualAngle - spread, owner.visualAngle + spread);
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
		body.setImage(new Image(src));
		body.setRotate(angle);
		home = source;
	}

	public void create(Ship owner, int offSet, Civilization source, double speed, int spread, int damage, String src) {
		
		if (spread == 0) {
			angle = owner.visualAngle + offSet;
		} else {
			angle = Methods.randInt(owner.visualAngle + offSet - spread, owner.visualAngle + offSet + spread);
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
		body.setImage(new Image(src));
		body.setRotate(angle);
		home = source;
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