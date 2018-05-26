import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayDeque;
import java.util.Deque;

public class Missile extends Projectile {
	double accel;
	
	public Missile() {
		super();
	}
	
	public void create(Ship owner, Civilization source, double speed, int spread) {
		
		if (spread == 0) {
			angle = owner.visualAngle;
		} else {
			angle = Methods.randInt(owner.visualAngle - spread, owner.visualAngle + spread);
		}
		
		damage = 25;
		penetration = 1;
		maxLifeTime = 240;
		accel = 1.02;
		lifeTimeFrames = maxLifeTime;
		xPos = owner.xPos;
		yPos = owner.yPos;
		diagVelocity = speed;
		xVelocity = diagVelocity * Math.sin(Math.toRadians(angle));
		yVelocity = -diagVelocity * Math.cos(Math.toRadians(angle));
		body.setImage(new Image("missile.gif"));
		body.setRotate(angle);
		home = source;
	}
	
	public void update(double xOffset, double yOffset) {
		xPos += xVelocity;
		yPos += yVelocity;
		xVelocity *= accel;
		yVelocity *= accel;
		//accel+=0.002;
		if (Math.sqrt(Math.pow(xVelocity, 2)+ Math.pow(yVelocity, 2)) > 55) {
			accel = 1;
		}
		
		body.setLayoutX(xPos - 0.5 * body.getLayoutBounds().getWidth() - xOffset);
		body.setLayoutY(yPos - 0.5 * body.getLayoutBounds().getHeight() - yOffset);
		lifeTimeFrames--;
	}
	
}