import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.Deque;

public class Target {
	double xPos, yPos;
	ImageView target;
	Enemy targetEnemy;
	
	public static Image redCross;
	public static Deque<Target> inGameTargets;
	public static Deque<Target> unusedTargets;
	
	static {
		redCross = new Image("red_cross.png");
		inGameTargets = new ArrayDeque<>();
		unusedTargets = new ArrayDeque<>();
	}
	
	public Target() {
		target = new ImageView(redCross);
	}
	
	public void create(final Enemy targetEnemy) {
		Target thisObj = this;
		
		final Animation animation = new Transition() {
			{
				setCycleDuration(Duration.millis(2000));
			}
			
			protected void interpolate(double t) {
				target.setRotate(t * 450);
				if (t < 0.25) {
					target.setFitWidth(((0.5 - t) / 0.25) * redCross.getWidth());
					target.setFitHeight(((0.5 - t) / 0.25) * redCross.getHeight());
					target.setOpacity(t / 0.25);
				} else if (t > 0.75) {
					target.setOpacity((1 - t) / 0.25);
					target.setFitWidth(((1 - t) / 0.25) * redCross.getWidth());
					target.setFitHeight(((1 - t) / 0.25) * redCross.getHeight());
				}
				
				if (t == 1.0 || !targetEnemy.body.isVisible()) {
					target.setVisible(false);
					thisObj.targetEnemy = null;
					inGameTargets.remove(thisObj);
					unusedTargets.push(thisObj);
					Main.root.getChildren().remove(target);
					this.stop();
				}
				
				target.setLayoutX(targetEnemy.xPos - 0.5 * target.getLayoutBounds().getWidth() - Main.xOffset);
				target.setLayoutY(targetEnemy.yPos - 0.5 * target.getLayoutBounds().getHeight() - Main.yOffset);
			}
			
		};
		
		animation.playFromStart();
	}
	
	public static Target getAvailable() {
		Target t;

		if (Target.unusedTargets.isEmpty()) {
			t = new Target();
		} else {
			t = Target.unusedTargets.removeLast();
		}
		
		t.target.setLayoutX(2000);
		t.target.setLayoutY(1200);
		return t;
	}
	
}