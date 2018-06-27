import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class Collision {
	long entity1, entity2;
	
	public static Deque<Collision> inGameCollisions;
	public static Deque<Collision> unusedCollisions;
	
	static {
		inGameCollisions = new ArrayDeque<>();
		unusedCollisions = new ArrayDeque<>();
	}
	
	public Collision() {
		entity1 = -1;
		entity2 = -1;
	}
	
	public void create(long id1, long id2) {
		entity1 = id1;
		entity2 = id2;
	}
	
	public static boolean checkUnique(long id1, long id2) {
		for (Iterator<Collision> itr = inGameCollisions.iterator(); itr.hasNext(); ) {
			Collision c = itr.next();
			
			if ((id1 == c.entity1 && id2 == c.entity2) || (id1 == c.entity2 && id2 == c.entity1)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static void addCollision(long id1, long id2) {
		Collision newCollision;
		
		if (unusedCollisions.isEmpty()) {
			newCollision = new Collision();
		} else {
			newCollision = unusedCollisions.pop();
		}
		
		newCollision.create(id1, id2);
		inGameCollisions.add(newCollision);
	}
	
	public static void removeEntityCollisions(long id) {
		for (Iterator<Collision> itr = inGameCollisions.iterator(); itr.hasNext(); ) {
			Collision c = itr.next();
			
			if (id == c.entity1 || id == c.entity2) {
				itr.remove();
			}
		}
	}
	
}