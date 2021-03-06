import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player extends Ship {

	int health = 1000000;

	long inGameFrames;
	boolean shooting, missileShooting, shootingSniper, turnLeft, turnRight, shootingRapid;
	static boolean keyboardMode = true;
	Rectangle bounds;

	private static Player instance = null;

	public static Player Player() {

		if (instance == null) {
			instance = new Player();
		}

		return instance;
	}

	private Player() {
		super();
		body.setImage(new Image("ship.png"));
		bounds = new Rectangle();
	}

	public void create() {
		super.create();
		maxReload = 0;
		reloadFrames = maxReload;
		maxSpeed = 5;

		projectileArray[0].create(10, ProjectileType.LASER);
		projectileArray[1].create(25, ProjectileType.MISSILE);
		projectileArray[2].create(30, ProjectileType.SNIPER);
		projectileArray[3].create(90, ProjectileType.WAVE);

		bounds.setX(400);
		bounds.setY(200);
		bounds.setWidth(200);
		bounds.setHeight(200);
		bounds.setStroke(Color.BLUE);
		bounds.setFill(Color.TRANSPARENT);
	}

	public void calculateVisualAngle(double xMouse, double yMouse) {

		if (!keyboardMode) {
			double tempTargetAngle =
			  Math.atan2(
			    yMouse - body.getLayoutY() - (body.getLayoutBounds().getHeight() * 0.5),
			    xMouse - body.getLayoutX() - (body.getLayoutBounds().getWidth() * 0.5))
			  + Math.PI / 2.0;
			int tempAngle = Math.floorMod((int) Math.round(Math.toDegrees(tempTargetAngle)), 360);

			if (Math.abs(Math.floorMod(tempAngle - visualAngle, 360)) <= 3) {
				visualAngle = tempAngle;
			}

			if (Math.abs(tempAngle - visualAngle) > 180) {
				visualAngle -= visualAngle < tempAngle ? 3 : (visualAngle > tempAngle ? -3 : 0);
			} else {
				visualAngle += visualAngle < tempAngle ? 3 : (visualAngle > tempAngle ? -3 : 0);
			}

			visualAngle = Math.floorMod(visualAngle, 360);
		}
	}

	public Node[] getNodes() {
		Node[] oldNodes = super.getNodes();
		Node[] newNodes = new Node[oldNodes.length + 1];

		for (int i = 0; i < oldNodes.length; i++) {
			newNodes[i] = oldNodes[i];
		}

		newNodes[4] = bounds;
		return newNodes;
	}

	public void setDebug(boolean visible) {
		super.setDebug(visible);
		bounds.setVisible(visible);
	}

	public void addMouseEvents(Scene scene) {

		scene.addEventHandler(
				MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (event.getButton() == MouseButton.PRIMARY && !keyboardMode) {
					Player().accelerating = true;
				} else if (event.getButton() == MouseButton.SECONDARY && !keyboardMode) {
					Player().shooting = true;
				}
			}
		});

		scene.addEventHandler(
				MouseEvent.MOUSE_RELEASED,
				new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (!keyboardMode) {
					if (event.getButton() == MouseButton.PRIMARY) {
						Player().accelerating = false;
					}
				}
			}
		});

		scene.addEventHandler(
				MouseEvent.MOUSE_MOVED,
				new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (!keyboardMode) {
					Main.xMousePos = event.getX();
					Main.yMousePos = event.getY();
				}
			}
		});

		scene.addEventHandler(
				MouseEvent.MOUSE_DRAGGED,
				new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (!keyboardMode) {
					Main.xMousePos = event.getX();
					Main.yMousePos = event.getY();
				}
			}
		});

		scene.setOnKeyPressed(
				new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
					case S:
						Methods.saveGameState();
						break;
					case R:
						for (int i = 0; i < Chunk.allSaves.length; i++) {
							if (Chunk.allSaves[i] != null) {
								Chunk.allSaves[i].delete();
								Chunk.allSaves[i] = null;
							}
						}

						break;
					case LEFT:
						if (Player().keyboardMode) {
							Player().turnLeft = true;
						}

						break;
					case RIGHT:
						if (Player().keyboardMode) {
							Player().turnRight = true;
						}

						break;
					case UP:
						if (Player().keyboardMode) {
							Player().accelerating = true;
						}

						break;
					case Z:
						Player().projectileArray[0].shooting = true;
						break;
					case X:
						Player().projectileArray[1].shooting = true;
						break;
					case C:
						Player().projectileArray[2].shooting = true;
						break;
					case V:
						Player().projectileArray[3].shooting = true;
						break;
					case K:
						keyboardMode = !keyboardMode;
						break;
				}
			}
		});

		scene.setOnKeyReleased(
				new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				switch (event.getCode()) {
					case LEFT:
						if (Player().keyboardMode) {
							Player().turnLeft = false;
						}

						break;
					case RIGHT:
						if (Player().keyboardMode) {
							Player().turnRight = false;
						}

						break;
					case UP:
						if (Player().keyboardMode) {
							Player().accelerating = false;
						}

						break;
					case Z:
						Player().projectileArray[0].shooting = false;
						break;
					case X:
						Player().projectileArray[1].shooting = false;
						break;
					case C:
						Player().projectileArray[2].shooting = false;
						break;
					case V:
						Player().projectileArray[3].shooting = false;
						break;
				}
			}
		});
	}
}