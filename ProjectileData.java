public class ProjectileData {
	int maxReload, currentReload;
	boolean shooting;
	ProjectileType type;
	
	public void create(int max, ProjectileType type) {
		maxReload = max;
		currentReload = 0;
		shooting = false;
		this.type = type;
	}
	
}