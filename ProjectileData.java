public class ProjectileData {
	int maxReload, currentReload;
	boolean shooting;
	
	public void create(int max) {
		maxReload = max;
		currentReload = 0;
		shooting = false;
	}
	
}