import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.concurrent.ThreadLocalRandom;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class Main extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	static String[] stra = new String[4];
	
	public static double xMousePos, yMousePos;
	public static double xOffset = -500;
	public static double yOffset = -300;
	
	public void start(Stage primaryStage) throws Exception {
		stra[0] = "laser_bullet.png";
		stra[1] = "sniper_bullet.png";
		stra[2] = "small_bullet.png";
		stra[3] = "wave.png";

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
				
				Player.Player().updateProjectilesCooldown();
				
				if (Player.Player().projectileArray[0].shooting && Player.Player().projectileArray[0].currentReload <= 0) {
					for (int i = -12; i <= 12; i += 8) {
						int speed;
						int damage;
						Projectile p = Projectile.getAvailable();
						bulletArray.add(p);
						
						if (i == 12 || i == -12) {
							speed = 17;
							damage = 2;
						} else {
							speed = 20;
							damage = 5;
						}
						
						p.create(Player.Player(), i, null, speed, 0, damage, stra[0]);
						p.body.setVisible(true);
						root.getChildren().add(p.body);
					}
					Player.Player().projectileArray[0].currentReload = Player.Player().projectileArray[0].maxReload;
				}
				
				if (Player.Player().projectileArray[1].shooting && Player.Player().projectileArray[1].currentReload <= 0) {
					Projectile p = new Missile();
					bulletArray.add(p);
					Missile m = (Missile) p;
					m.create(Player.Player(), null, 5, 7);
					m.body.setVisible(true);
					root.getChildren().add(m.body);
					Player.Player().projectileArray[1].currentReload = Player.Player().projectileArray[1].maxReload;
				}

				if (Player.Player().projectileArray[2].shooting && Player.Player().projectileArray[2].currentReload <= 0) {
					Projectile p = Projectile.getAvailable();
					bulletArray.add(p);
					p.create(Player.Player(), null, 40, 0, 50, stra[1]);
					p.body.setVisible(true);
					root.getChildren().add(p.body);
					Player.Player().projectileArray[2].currentReload = Player.Player().projectileArray[2].maxReload;
				}
				
				if (Player.Player().projectileArray[3].shooting && Player.Player().projectileArray[3].currentReload <= 0) {
					Player.Player().projectileArray[3].currentReload = Player.Player().projectileArray[3].maxReload;
				}

				if (Player.Player().projectileArray[3].currentReload >= 60) {
					Projectile p = Projectile.getAvailable();
					Projectile q = Projectile.getAvailable();
					bulletArray.add(q);
					bulletArray.add(p);
					p.create(Player.Player(), 45, null, 4 + Math.floor(Math.random() * Math.floor(17)), 45, 2, stra[3]);
					q.create(Player.Player(), 315, null, 4 + Math.floor(Math.random() * Math.floor(17)), 45, 2, stra[3]);
					p.body.setVisible(true);
					q.body.setVisible(true);
					root.getChildren().addAll(q.body,p.body);
				}
				
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

				/*for (int i = missileArray.size() - 1; i >= 0; i--) {
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
							enemyArray[j].health -= current.damage;
							
							if(enemyArray[j].health - current.damage <= 0){
								Methods.setNodesVisible(false, enemyArray[j].getNodes());
								enemyArray[j] = null;
								Enemy.currentEnemies--;
							}
							
							current.body.setVisible(false);
							root.getChildren().remove(current.body);
							Missile.unusedBullets.push(current);
							missileArray.remove(i);
							break;
						}
					}
				}*/

				for (int i = bulletArray.size() - 1; i >= 0; i--) {
					Projectile current = bulletArray.get(i);
					current.update(xOffset, yOffset);
					current.body.toBack();
					
					if ((current.body.getBoundsInParent().intersects(Player.Player().body.getBoundsInParent()) && current.home != null) || current.lifeTimeFrames == 0) {
						if((current.body.getBoundsInParent().intersects(Player.Player().body.getBoundsInParent()) && current.home != null)){
							Player.Player().health -= current.damage;
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
							enemyArray[j].health -= current.damage;
							
							if(enemyArray[j].health <= 0) {
								Methods.setNodesVisible(false, enemyArray[j].getNodes());
								Effect explo = new Effect();
								explo.create(enemyArray[j].xPos, enemyArray[j].yPos);
								Effect.explArray.add(explo);
								root.getChildren().add(explo.explosion);
								enemyArray[j] = null;
								Enemy.currentEnemies--;
							}
							
							current.body.setVisible(false);
							root.getChildren().remove(current.body);
							Projectile.unusedBullets.push(current);
							bulletArray.remove(i);
							break;
						}
					}
				}
				
				for (int i = Effect.explArray.size() - 1; i >= 0; i--) {
					if (Effect.explArray.get(i).update(xOffset, yOffset)) {
						Effect.explArray.get(i).explosion.setVisible(false);
						root.getChildren().remove(Effect.explArray.get(i).explosion);
						Effect.explArray.remove(i);
					}
				}
				
			}
		};
		
		timer.start();
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
}