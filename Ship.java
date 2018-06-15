import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Ship {
	int angle,
		visualAngle,
		xCurrent, yCurrent,
		maxReload, reloadFrames,
		maxSpeed, turnSpeed;
	double	diagVelocity, diagAccel,
			xVelocity, yVelocity,
			xAccel, yAccel,
			xPos, yPos,
			resistAccel,
			health;
	boolean accelerating;
	ProjectileData[] projectileArray;
	ImageView body;
	Line dVLine, xVLine, yVLine;

	public Ship() {
		projectileArray = new ProjectileData[4];

		for (int i = 0; i < projectileArray.length; i++) {
			projectileArray[i] = new ProjectileData();
		}

		body = new ImageView();
		dVLine = new Line();
		xVLine = new Line();
		yVLine = new Line();
	}

	public void create() {
		body.setVisible(true);
		dVLine.setStroke(Color.RED);
	}

	public void calculateVelocityAngle() {
		double tempangle = Math.atan2(yVelocity, xVelocity) + Math.PI / 2.0;
		angle = (int) Math.round(Math.toDegrees(tempangle));
	}

	public void changeAccel(double magnitude, double angle) {
		xAccel += magnitude * Math.sin(Math.toRadians(angle));
		yAccel -= magnitude * Math.cos(Math.toRadians(angle));
		diagAccel = Math.sqrt(Math.pow(xAccel, 2) + Math.pow(yAccel, 2));
	}

	public double stopXVelocity() {
		double oldX = xVelocity;
		xVelocity += xAccel;

		if (xVelocity * oldX < 0 && !accelerating) {
			xAccel = 0;
			xVelocity = 0;
		}

		return oldX;
	}

	public double stopYVelocity() {
		double oldY = yVelocity;
		yVelocity += yAccel;

		if (yVelocity * oldY < 0 && !accelerating) {
			yAccel = 0;
			yVelocity = 0;
		}

		return oldY;
	}

	public void minMaxVelocity(double maxSpeed, double oldX, double oldY) {

		if (diagVelocity > maxSpeed) {
			xVelocity = (oldX + xAccel);
			yVelocity = (oldY + yAccel);
			calculateVelocityAngle();
			xVelocity = maxSpeed * Math.sin(Math.toRadians(angle));
			yVelocity = -maxSpeed * Math.cos(Math.toRadians(angle));
			xAccel = 0;
			yAccel = 0;
		} else if (diagVelocity < 1E-8 && !accelerating) {
			xVelocity = 0;
			yVelocity = 0;
			xAccel = 0;
			yAccel = 0;
		}
	}

	public void updateProjectilesCooldown() {

		for (ProjectileData pd : projectileArray) {
			if (pd.maxReload != -1 && pd.currentReload > 0) {
				pd.currentReload--;
			}
		}
	}

	public boolean fireBullet() {
		reloadFrames--;

		if (reloadFrames < 0) {
			reloadFrames = maxReload;
			return true;
		}

		return false;
	}

	public void update(double xOffset, double yOffset) {
		body.setLayoutX(xPos - 0.5 * body.getLayoutBounds().getWidth() - xOffset);
		body.setLayoutY(yPos - 0.5 * body.getLayoutBounds().getHeight() - yOffset);
		body.setRotate(visualAngle);

		dVLine.setStartX(xPos - xOffset);
		dVLine.setStartY(yPos - yOffset);
		dVLine.setEndX(xPos + 20 * xVelocity - xOffset);
		dVLine.setEndY(yPos + 20 * yVelocity - yOffset);

		xVLine.setStartX(xPos - xOffset);
		xVLine.setStartY(yPos - yOffset);
		xVLine.setEndX(xPos + 20 * xVelocity - xOffset);
		xVLine.setEndY(yPos - yOffset);

		yVLine.setStartX(xPos - xOffset);
		yVLine.setStartY(yPos - yOffset);
		yVLine.setEndX(xPos - xOffset);
		yVLine.setEndY(yPos + 20 * yVelocity - yOffset);
	}

	public Node[] getNodes() {
		return new Node[] {body, dVLine, xVLine, yVLine};
	}

	public void setDebug(boolean visible) {
		Methods.setNodesVisible(visible, dVLine, xVLine, yVLine);
	}
}