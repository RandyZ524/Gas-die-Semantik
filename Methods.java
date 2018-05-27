import javafx.scene.Group;
import javafx.scene.Node;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.Random;

abstract public class Methods {
	public static final Random rand = new Random();
	
	public static int randInt(int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}
	
	public static void loadExistingSaves() {
		
		for (int i = 0; i < Chunk.allSaves.length; i++) {
			File f = new File("Save_" + i + ".txt");
			
			if (f.exists()) {
				Chunk.allSaves[i] = f;
			} else {
				return;
			}
		}
		
	}
	
	public static void saveGameState() {
		int saveNum = Chunk.getSaveNumber();
		
		if (saveNum == -1) {
			try {
				shiftSaves();
			} catch (IOException ioe) {
				System.out.println("Unable to shift existing saves!");
				return;
			}
			
			saveNum = Chunk.allSaves.length - 1;
		}
		
		int hours = (int) (Player.Player().inGameFrames / 216000);
		int minutes = (int) ((Player.Player().inGameFrames - (hours * 216000)) / 3600);
		int seconds = (int) ((Player.Player().inGameFrames - (hours * 216000) - (minutes * 3600)) / 60);
		createSaveFile(saveNum, "Save_" + saveNum + ".txt", hours, minutes, seconds);
	}
	
	public static void loadInitialChunks(int x, int y, Group root) {
		
		for (int i = -x; i <= x; i++) {
			Chunk.grid.add(new LinkedList<Chunk>());
			
			for (int j = -y; j <= y; j++) {
				Chunk c = new Chunk();
				c.create(i, j);
				Chunk.grid.getLast().add(c);
				Chunk.loadedChunks.add(c);
				root.getChildren().addAll(c.getNodes());
			}
		}
		
	}
	
	public static void offsetScreen(Player p) {
		
		if (p.body.getLayoutX() + (0.5 * p.body.getLayoutBounds().getWidth()) < 400) {
			Main.xOffset = p.xPos - 400;
		} else if (p.body.getLayoutX() + (0.5 * p.body.getLayoutBounds().getWidth()) > 600) {
			Main.xOffset = p.xPos - 600;
		}
		
		if (p.body.getLayoutY() + (0.5 * p.body.getLayoutBounds().getHeight()) < 200) {
			Main.yOffset = p.yPos - 200;
		} else if (p.body.getLayoutY() + (0.5 * p.body.getLayoutBounds().getHeight()) > 400) {
			Main.yOffset = p.yPos - 400;
		}
		
	}
	
	public static void setNodesVisible(boolean visible, Node... nodes) {
		for (Node n : nodes) n.setVisible(visible);
	}
	
	public static void nodesToFront(Node... nodes) {
		for (Node n : nodes) n.toFront();
	}
	
	public static void nodesToBack(Node... nodes) {
		for (Node n : nodes) n.toFront();
	}
	
	private static void shiftSaves() throws IOException {
		
		for (int i = 1; i < Chunk.allSaves.length; i++) {
			Path source = Paths.get("Save_" + i + ".txt");
			Files.move(source, source.resolveSibling("Save_" + (i - 1) + ".txt"), REPLACE_EXISTING);
		}
		
	}
	
	private static void createSaveFile(int saveNum, String fileName, int... time) {
		
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileName), "utf-8"))) {
			writer.write("Game seed: " + Chunk.seed + "\n");
			writer.write("Time in game:\n\tHours: " + time[0]);
			writer.write("\n\tMinutes: " + time[1]);
			writer.write("\n\tSeconds: " + time[2] + "\n\n");
			
			for (Chunk c : Chunk.loadedChunks)
				writer.write(c.xCoord + ", " + c.yCoord + "\t" + c.hash + "\n");
			
			Chunk.allSaves[saveNum] = new File(fileName);
		} catch (IOException ioe) {
			System.out.println("Unable to save!");
			return;
		}
		
	}
	
}