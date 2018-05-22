import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class Main extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	public static double xMousePos, yMousePos;
	public static double xOffset = -500;
	public static double yOffset = -300;
	public static int cd = 0;
	
	public void start(Stage primaryStage) throws Exception {
		Group root = new Group();
		Scene scene = new Scene(root, 1000, 600);
		
		Player.Player().create();
		Player.Player().setDebug(false);
		root.getChildren().addAll(Player.Player().getNodes());
		
		Methods.loadExistingSaves();
		Player.Player().addMouseEvents(scene);
		
		Enemy[] enemyArray = new Enemy[Enemy.maxEnemies];
		ArrayList<Projectile> bulletArray = new ArrayList<>();
		ArrayList<Missile> missileArray = new ArrayList<>();
		
		for (int i = 0; i < enemyArray.length; i++) {
			Enemy e = new Enemy();
			enemyArray[i] = e;
			e.setClassAttributes("Interceptor");
			e.create();
			e.xPos = Methods.randInt(-200, 200) * 10;
			e.yPos = Methods.randInt(-200, 200) * 10;
			e.setDebug(false);
			root.getChildren().addAll(e.getNodes());
		}
		
		Methods.loadInitialChunks(2, 2, root);
		
		AnimationTimer timer = new AnimationTimer() {
			@Override
			
			public void handle(long now) {
				Player.Player().inGameFrames++;
				Player.Player().calculateVelocityAngle();
				
				if (Player.Player().keyboardMode) {
					if (Player.Player().turnLeft) {
						Player.Player().visualAngle -= 3;
					}
					if (Player.Player().turnRight) {
						Player.Player().visualAngle += 3;
					}
				} else {
					if(!Player.keyboardMode)
						Player.Player().calculateVisualAngle(xMousePos, yMousePos);
				}
				if (Player.Player().accelerating) {
					Player.Player().changeAccel(0.005, Player.Player().visualAngle);
				}
				
				Player.Player().resistAccel = 0.001 * Math.abs(Player.Player().diagVelocity);
				Player.Player().changeAccel(Player.Player().resistAccel, Player.Player().angle + 180);
				double oldX = Player.Player().stopXVelocity();
				double oldY = Player.Player().stopYVelocity();
				
				Player.Player().diagVelocity = Math.sqrt(Math.pow(Player.Player().xVelocity, 2) + Math.pow(Player.Player().yVelocity, 2));
				Player.Player().minMaxVelocity(Player.Player().maxSpeed, oldX, oldY);
				
				Player.Player().xPos += Player.Player().xVelocity;
				Player.Player().yPos += Player.Player().yVelocity;
				
				Player.Player().update(xOffset, yOffset);
				Methods.offsetScreen(Player.Player());
				
				if (Player.Player().fireBullet() && Player.Player().shooting) {
					if(cd<=0){
						Missile p = Missile.getAvailable();
						missileArray.add(p);
						p.create(Player.Player(), null, 10, 0);
						p.body.setVisible(true);
						root.getChildren().add(p.body);
						cd=10;
					}
				}

				if(cd>-3)cd--;
				
				while (true) {
					int tempX = Chunk.shiftXAxis(Player.Player().xPos, Player.Player().xCurrent, Player.Player().yCurrent, root);
					int tempY = Chunk.shiftYAxis(Player.Player().yPos, Player.Player().xCurrent, Player.Player().yCurrent, root);
					if (tempX == 0 && tempY == 0) {
						break;
					}
					Player.Player().xCurrent += tempX;
					Player.Player().yCurrent += tempY;
				}
				
				for (LinkedList<Chunk> column : Chunk.grid) {
					for (Chunk c : column) {
						c.update(xOffset, yOffset);
					}
				}
				
				Player.Player().calculateVelocityAngle();
				
				for (Enemy e : enemyArray) {
					if (e != null) {
						e.changeAccel(e.turnToPlayer(), e.visualAngle);
						
						e.resistAccel = 0.001 * Math.abs(e.diagVelocity);
						e.changeAccel(e.resistAccel, e.angle + 180);
						
						oldX = e.stopXVelocity();
						oldY = e.stopYVelocity();
						
						e.diagVelocity = Math.sqrt(Math.pow(e.xVelocity, 2) + Math.pow(e.yVelocity, 2));
						e.minMaxVelocity(e.maxSpeed, oldX, oldY);
						
						e.xPos += e.xVelocity;
						e.yPos += e.yVelocity;
						
						e.update(xOffset, yOffset);
						
						if (e.lockedToPlayer() & e.fireBullet()) {
							Projectile p = Projectile.getAvailable();
							bulletArray.add(p);
							p.create(e, new Civilization(), 10, 5);
							p.body.setVisible(true);
							root.getChildren().add(p.body);
						}
						
						if (Player.Player().inGameFrames % 600 == 0) {
							e.activateAbilities();
						}
						
						for (EnemyAbility ea : e.abilities) {
							if (e.abilityFrames[ea.ordinal()] != ea.maxFrames) {
								e.abilityFrames[ea.ordinal()]--;
							}
							
							if (e.abilityFrames[ea.ordinal()] <= 0) {
								e.abilityFrames[ea.ordinal()] = ea.maxFrames;
								
								switch (ea) {
									case SPEED_BOOST:
										e.maxSpeed >>= 1;
										e.turnSpeed >>= 1;
										e.body.setImage(new Image("enemy.png"));
										break;
								}
							}
						}
					}
				}

				for (int i = missileArray.size() - 1; i >= 0; i--) {
					Missile current = missileArray.get(i);
					current.update(xOffset, yOffset);
					current.body.toBack();
					if ((current.body.getBoundsInParent().intersects(Player.Player().body.getBoundsInParent()) && current.home != null) || current.lifeTimeFrames == 0) {
						current.body.setVisible(false);
						root.getChildren().remove(current.body);
						Missile.unusedBullets.push(current);
						missileArray.remove(i);
						continue;
					}
					for (int j = enemyArray.length - 1; j >= 0; j--) {
						if (enemyArray[j] != null && current.body.getBoundsInParent().intersects(enemyArray[j].body.getBoundsInParent()) && current.home == null) {
							Methods.setNodesVisible(false, enemyArray[j].getNodes());
							enemyArray[j] = null;
							current.body.setVisible(false);
							root.getChildren().remove(current.body);
							Missile.unusedBullets.push(current);
							missileArray.remove(i);
							Enemy.currentEnemies--;
							System.out.println(Enemy.currentEnemies);
							break;
						}
					}
				}

				for (int i = bulletArray.size() - 1; i >= 0; i--) {
					Projectile current = bulletArray.get(i);
					current.update(xOffset, yOffset);
					current.body.toBack();
					if ((current.body.getBoundsInParent().intersects(Player.Player().body.getBoundsInParent()) && current.home != null) || current.lifeTimeFrames == 0) {
						current.body.setVisible(false);
						root.getChildren().remove(current.body);
						Projectile.unusedBullets.push(current);
						bulletArray.remove(i);
						continue;
					}
					for (int j = enemyArray.length - 1; j >= 0; j--) {
						if (enemyArray[j] != null && current.body.getBoundsInParent().intersects(enemyArray[j].body.getBoundsInParent()) && current.home == null) {
							Methods.setNodesVisible(false, enemyArray[j].getNodes());
							enemyArray[j] = null;
							current.body.setVisible(false);
							root.getChildren().remove(current.body);
							Projectile.unusedBullets.push(current);
							bulletArray.remove(i);
							Enemy.currentEnemies--;
							System.out.println(Enemy.currentEnemies);
							break;
						}
					}
				}
			}
		};
		
		timer.start();
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
}