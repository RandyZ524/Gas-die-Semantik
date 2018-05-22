import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayDeque;
import java.util.Deque;

public class Missile {
	int angle, penetration, maxLifeTime, lifeTimeFrames;
	int damage = 20;
	double diagVelocity, xVelocity, yVelocity, xPos, yPos, accel;
	BulletSize size;
	ImageView body;
	Civilization home;
	
	public static Deque<Missile> unusedBullets;
	
	static {
		unusedBullets = new ArrayDeque<>();
	}
	
	public Missile() {
		size = null;
		body = new ImageView();
	}
	
	public void create(Ship owner, Civilization source, double speed, int spread, int damage) {
		
		if (spread == 0) {
			angle = owner.visualAngle;
		} else {
			angle = Methods.randInt(owner.visualAngle - spread, owner.visualAngle + spread);
		}
		
		penetration = 1;
		maxLifeTime = 240;
		accel = 1;
		lifeTimeFrames = maxLifeTime;
		xPos = owner.xPos;
		yPos = owner.yPos;
		diagVelocity = speed;
		xVelocity = diagVelocity * Math.sin(Math.toRadians(angle));
		yVelocity = -diagVelocity * Math.cos(Math.toRadians(angle));
		body.setImage(new Image("arrow_bullet.png"));
		body.setRotate(angle);
		home = source;
	}
	
	public void update(double xOffset, double yOffset) {
		xPos += xVelocity;
		yPos += yVelocity;
		xVelocity=accel*xVelocity;
		yVelocity=accel*yVelocity;
		accel+=0.002;
		body.setLayoutX(xPos - 0.5 * body.getLayoutBounds().getWidth() - xOffset);
		body.setLayoutY(yPos - 0.5 * body.getLayoutBounds().getHeight() - yOffset);
		lifeTimeFrames--;
	}
	
	public static Missile getAvailable() {
		
		if (Missile.unusedBullets.isEmpty()) {
			return new Missile();
		}
		
		return Missile.unusedBullets.pop();
	}
	
}