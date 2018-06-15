import javafx.scene.Group;

public enum ProjectileType {
	LASER,
	MISSILE,
	SNIPER,
	WAVE;

	public void fireMechanics(Group root) {

		switch (this) {
			case LASER:
			{
				for (int i = -12; i <= 12; i += 8) {
					int speed;
					int damage;
					Projectile p = Projectile.getAvailable();
					Projectile.inGameBullets.add(p);

					if (i == 12 || i == -12) {
						speed = 17;
						damage = 2;
					} else {
						speed = 20;
						damage = 5;
					}

					p.create(Player.Player(), i, null, speed, 0, damage, ProjectileType.LASER);
					p.body.setVisible(true);
					root.getChildren().add(p.body);
				}

				break;
			}
			case MISSILE:
			{
				for (int i = 0; i < 1; i++) {
					Projectile p = new Missile();
					Projectile.inGameBullets.add(p);
					Missile m = (Missile) p;
					m.create(Player.Player(), null, 5, /*7*/ 0);
					m.body.setVisible(true);
					root.getChildren().add(m.body);
				}
				break;
			}
			case SNIPER:
			{
				int oldAngle = Player.Player().visualAngle;
				for (int i = 0; i < 360; i++) {
					Player.Player().visualAngle = oldAngle + i;
					Projectile p = Projectile.getAvailable();
					Projectile.inGameBullets.add(p);
					p.create(Player.Player(), null, 40, 0, 50, ProjectileType.SNIPER);
					p.body.setVisible(true);
					root.getChildren().add(p.body);
				}
				Player.Player().visualAngle = oldAngle;
				break;
			}
			case WAVE:
			{
				for (int i = 45; i <= 315; i += 270) {
					Projectile p = Projectile.getAvailable();
					Projectile.inGameBullets.add(p);
					p.create(Player.Player(), i, null, Methods.randInt(4, 21), 45, 2, ProjectileType.WAVE);
					p.body.setVisible(true);
					root.getChildren().add(p.body);
				}

				break;
			}
			default:
				break;
		}
	}
}