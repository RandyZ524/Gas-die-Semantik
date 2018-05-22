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
	public static int mcd = 0;
	public static int pcd = 0;
	public static int scd = 0;
	
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
				if(Player.Player().health <= 0) System.exit(0);
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
					if(pcd<=0){
						Projectile p = Projectile.getAvailable();
						Projectile q = Projectile.getAvailable();
						Projectile r = Projectile.getAvailable();
						Projectile s = Projectile.getAvailable();
						bulletArray.add(p);
						bulletArray.add(q);
						bulletArray.add(r);
						bulletArray.add(s);
						p.create(Player.Player(), 12, null, 17, 0, 2, "laser_bullet.png");
						q.create(Player.Player(), 4, null, 20, 0,5, "laser_bullet.png");
						r.create(Player.Player(), -4, null, 20, 0,5, "laser_bullet.png");
						s.create(Player.Player(), -12, null, 17, 0,2, "laser_bullet.png");
						p.body.setVisible(true);
						q.body.setVisible(true);
						r.body.setVisible(true);
						s.body.setVisible(true);
						root.getChildren().addAll(p.body, q.body, r.body, s.body);
						pcd=10;
					}
				}

				if (Player.Player().fireBullet() && Player.Player().shootingSniper) {
					if(scd<=0){
						Projectile p = Projectile.getAvailable();
						bulletArray.add(p);
						p.create(Player.Player(), null, 40, 0, 50, "sniper_bullet.png");
						p.body.setVisible(true);
						root.getChildren().add(p.body);
						scd=30;
					}
				}

				if (Player.Player().fireBullet() && Player.Player().shootingRapid) {
					Projectile p = Projectile.getAvailable();
					bulletArray.add(p);
					p.create(Player.Player(), null, 13+Math.floor(Math.random() * Math.floor(6)), 8, 1, "wave.png");
					p.body.setVisible(true);
					root.getChildren().add(p.body);
				}

				if (Player.Player().fireBullet() && Player.Player().missileShooting) {
					if(mcd<=0){
						Missile p = Missile.getAvailable();
						missileArray.add(p);
						p.create(Player.Player(), null, 10, 7, 20);
						p.body.setVisible(true);
						root.getChildren().add(p.body);
						mcd=10;
					}
				}

				if(mcd>-3)mcd--;
				if(pcd>-3)pcd--;
				if(scd>-3)scd--;
				
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
						
						if (e.lockedToPlayer() & e.fireBullet() & e.ecd <=0) {
							Projectile p = Projectile.getAvailable();
							bulletArray.add(p);
							p.create(e, new Civilization(), 10, 5, 2, "laser_bullet.png");
							p.body.setVisible(true);
							root.getChildren().add(p.body);
							e.ecd = e.cooldown;
						}

						if(e.ecd > -5) e.ecd--;
						
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
										e.cooldown = 8;
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
						if((current.body.getBoundsInParent().intersects(Player.Player().body.getBoundsInParent()) && current.home != null)){
							Player.Player().health-=current.damage;
							System.out.println(Player.Player().health);
						}
						current.body.setVisible(false);
						root.getChildren().remove(current.body);
						Missile.unusedBullets.push(current);
						missileArray.remove(i);
						continue;
					}
					for (int j = enemyArray.length - 1; j >= 0; j--) {
						if (enemyArray[j] != null && current.body.getBoundsInParent().intersects(enemyArray[j].body.getBoundsInParent()) && current.home == null) {
							
							if(enemyArray[j].health - current.damage <= 0){
								Methods.setNodesVisible(false, enemyArray[j].getNodes());
								enemyArray[j] = null;
								Enemy.currentEnemies--;
							}else
								enemyArray[j].health-=current.damage;
							current.body.setVisible(false);
							root.getChildren().remove(current.body);
							Missile.unusedBullets.push(current);
							missileArray.remove(i);
//							System.out.println(Enemy.currentEnemies);
							break;
						}
					}
				}

				for (int i = bulletArray.size() - 1; i >= 0; i--) {
					Projectile current = bulletArray.get(i);
					current.update(xOffset, yOffset);
					current.body.toBack();
					if ((current.body.getBoundsInParent().intersects(Player.Player().body.getBoundsInParent()) && current.home != null) || current.lifeTimeFrames == 0) {
						if((current.body.getBoundsInParent().intersects(Player.Player().body.getBoundsInParent()) && current.home != null)){
							Player.Player().health-=current.damage;
							System.out.println(Player.Player().health);
						}
						current.body.setVisible(false);
						root.getChildren().remove(current.body);
						Projectile.unusedBullets.push(current);
						bulletArray.remove(i);
						continue;
					}
					for (int j = enemyArray.length - 1; j >= 0; j--) {
						if (enemyArray[j] != null && current.body.getBoundsInParent().intersects(enemyArray[j].body.getBoundsInParent()) && current.home == null) {
							
							if(enemyArray[j].health - current.damage <= 0){
								Methods.setNodesVisible(false, enemyArray[j].getNodes());
								enemyArray[j] = null;
								Enemy.currentEnemies--;
							}else
								enemyArray[j].health-=current.damage;
							current.body.setVisible(false);
							root.getChildren().remove(current.body);
							Projectile.unusedBullets.push(current);
							bulletArray.remove(i);
//							System.out.println(Enemy.currentEnemies);
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