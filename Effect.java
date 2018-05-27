import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayDeque;
import java.util.Deque;

public class Effect {
	int frames;
	double xPos, yPos;
	ImageView explosion;
	
	public static Image explosionImage;
	public static Deque<Effect> inGameEffects;
	public static Deque<Effect> unusedEffects;
	
	static {
		explosionImage = new Image("explosion.gif");
		inGameEffects = new ArrayDeque<>();
		unusedEffects = new ArrayDeque<>();
	}
	
	public Effect() {
		explosion = new ImageView(explosionImage);
	}
	
	public void create(double x, double y) {
		frames = 50;
		xPos = x;
		yPos = y;
	}
	
	public boolean update(double xOffset, double yOffset) {
		frames--;
		
		if (frames == 0) {
			return true;
		}
		
		explosion.setLayoutX(xPos - explosion.getLayoutBounds().getWidth() * 0.5 - xOffset);
		explosion.setLayoutY(yPos - explosion.getLayoutBounds().getHeight() * 0.5 - yOffset);
		return false;
	}
	
	public static Effect getAvailable() {
		
		if (Effect.unusedEffects.isEmpty()) {
			return new Effect();
		}
		
		return Effect.unusedEffects.pop();
	}
	
}