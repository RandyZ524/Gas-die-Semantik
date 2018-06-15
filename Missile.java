import java.util.Iterator;

public class Missile extends Projectile {
	double accel;
	boolean boosting;
	Enemy target;

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
		penetration = 10;
		maxLifeTime = 240;
		accel = 1.05;
		boosting = false;
		lifeTimeFrames = maxLifeTime;
		xPos = owner.xPos;
		yPos = owner.yPos;
		diagVelocity = speed;
		xVelocity = diagVelocity * Math.sin(Math.toRadians(angle));
		yVelocity = -diagVelocity * Math.cos(Math.toRadians(angle));
		alive = true;
		body.setImage(bulletImages[1]);
		body.setRotate(angle);
		home = source;
		target = null;
		findClosestTarget();
	}

	public void update(double xOffset, double yOffset) {

		if (target != null && target.body.isVisible()) {
			double tempTargetAngle = Math.atan2(target.yPos - yPos, target.xPos - xPos) + Math.PI / 2.0;
			int tempAngle = Math.floorMod((int) Math.round(Math.toDegrees(tempTargetAngle)), 360);
			int turnSpeed = 5;

			if (Math.abs(Math.floorMod(tempAngle - angle, 360)) <= turnSpeed) {
				angle = tempAngle;
				boosting = true;
			}

			if (Math.abs(tempAngle - angle) > 180) {
				angle -= angle < tempAngle ? turnSpeed : (angle > tempAngle ? -turnSpeed : 0);
			} else {
				angle += angle < tempAngle ? turnSpeed : (angle > tempAngle ? -turnSpeed : 0);
			}

			angle = Math.floorMod(angle, 360);
			xVelocity = diagVelocity * Math.sin(Math.toRadians(angle));
			yVelocity = -diagVelocity * Math.cos(Math.toRadians(angle));
			body.setRotate(angle);
		} else {
			diagVelocity = 5;
			xVelocity = diagVelocity * Math.sin(Math.toRadians(angle));
			yVelocity = -diagVelocity * Math.cos(Math.toRadians(angle));
			findClosestTarget();
		}
		
		if (boosting) {
			xVelocity *= accel;
			yVelocity *= accel;
		}
		
		xPos += xVelocity;
		yPos += yVelocity;

		if (Math.sqrt(Math.pow(xVelocity, 2) + Math.pow(yVelocity, 2)) > 50) {
			diagVelocity = 50;
		}

		body.setLayoutX(xPos - 0.5 * body.getLayoutBounds().getWidth() - xOffset);
		body.setLayoutY(yPos - 0.5 * body.getLayoutBounds().getHeight() - yOffset);
		lifeTimeFrames--;
	}

	private void findClosestTarget() {
		double minSquareDistance = Double.MAX_VALUE;
		target = null;

		for (Iterator<Enemy> itr = Enemy.inGameEnemies.iterator(); itr.hasNext(); ) {
			Enemy e = itr.next();
			double tempSquareDistance = Math.pow(xPos - e.xPos, 2) + Math.pow(yPos - e.yPos, 2);

			if (tempSquareDistance < minSquareDistance) {
				minSquareDistance = tempSquareDistance;
				target = e;
			}
		}

		if (target == null) {
			boosting = true;
		} else {
			Target t = Target.getAvailable();
			t.create((Enemy) target);
			t.target.setVisible(true);
			Target.inGameTargets.add(t);
			Main.root.getChildren().add(t.target);
		}
	}
}