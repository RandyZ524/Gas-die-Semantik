import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class Effect {
	int frames;
	double xPos, yPos;
	ImageView explosion;
	
	static ArrayList<Effect> explArray;
	
	static {
		explArray = new ArrayList<>();
	}
	
	public Effect() {
		explosion = new ImageView(new Image("explosion.gif"));
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
	
}