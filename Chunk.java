import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.File;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeSet;

public class Chunk {
	static final int size = 1000;
	static LinkedList<LinkedList<Chunk>> grid;
	static TreeSet<Chunk> loadedChunks;
	static final String allCharacters;
	static SecureRandom r;
	static String seed;
	static File[] allSaves;
	
	static {
		grid = new LinkedList<>();
		loadedChunks = new TreeSet<Chunk>((c1, c2) -> {
			int sum1 = Math.abs(c1.xCoord) + Math.abs(c1.yCoord);
			int sum2 = Math.abs(c2.xCoord) + Math.abs(c2.yCoord);
			
			if (c1.xCoord == c2.xCoord && c1.yCoord == c2.yCoord)
				return 0;
			if (sum1 - sum2 != 0)
				return sum1 - sum2;
			
			if (c1.xCoord == c2.xCoord)
				return c1.yCoord - c2.yCoord;
			else
				return c1.xCoord - c2.xCoord;
			
		});
		
		allCharacters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		r = new SecureRandom();
		seed = randString(8);
		allSaves = new File[6];
		//Arrays.fill(allSaves, new File());
	}
	
	int xCoord, yCoord, hash;
	Rectangle border;
	Text coords, hashText;
	
	public Chunk() {
		border = new Rectangle();
		coords = new Text();
		hashText = new Text();
	}
	
	public void create(int x, int y) {
		xCoord = x;
		yCoord = y;
		border.setWidth(size);
		border.setHeight(size);
		border.setStroke(Color.BLACK);
		border.setFill(Color.TRANSPARENT);
		coords.setText("(" + xCoord + ", " + yCoord + ")");
		coords.setFont(Font.font("Verdana", 16));
		String conkyString = new String(seed + x + seed + y + seed);
		hash = Math.abs(conkyString.hashCode());
		hashText.setText(Integer.toString(hash));
		hashText.setFont(Font.font("Verdana", 16));
	}
	
	public void update(double xOffset, double yOffset) {
		border.setX((xCoord * size) - (size * 0.5) - xOffset);
		border.setY((yCoord * size) - (size * 0.5) - yOffset);
		coords.setX(xCoord * size - (coords.getLayoutBounds().getWidth() * 0.5) - xOffset);
		coords.setY(yCoord * size - yOffset);
		hashText.setX(xCoord * size - (hashText.getLayoutBounds().getWidth() * 0.5) - xOffset);
		hashText.setY(yCoord * size - yOffset + 50);
	}
	
	public Node[] getNodes() { return new Node[]{ border, coords, hashText }; }
	
	public static int shiftXAxis(double xPos, int xCurrent, int yCurrent, Group root) {
		
		if (xPos < (xCurrent * size) - (size * 0.5)) {
			grid.addFirst(new LinkedList<Chunk>());
			
			for (int i = yCurrent - 2; i <= yCurrent + 2; i++) {
				Chunk c = new Chunk();
				c.create(xCurrent - 3, i);
				grid.getFirst().add(c);
				root.getChildren().addAll(c.getNodes());
				loadedChunks.add(c);
			}
			
			for (Chunk c : grid.getLast()) {
				root.getChildren().removeAll(c.getNodes());
			}
			
			grid.removeLast();
			return -1;
		} else if (xPos > (xCurrent * size) + (size * 0.5)) {
			grid.addLast(new LinkedList<Chunk>());
			
			for (int i = yCurrent - 2; i <= yCurrent + 2; i++) {
				Chunk c = new Chunk();
				c.create(xCurrent + 3, i);
				grid.getLast().add(c);
				root.getChildren().addAll(c.getNodes());
				loadedChunks.add(c);
			}
			
			for (Chunk c : grid.getFirst()) {
				root.getChildren().removeAll(c.getNodes());
			}
			
			grid.removeFirst();
			return 1;
		}
		
		return 0;
	}
	
	public static int shiftYAxis(double yPos, int xCurrent, int yCurrent, Group root) {
		
		if (yPos < (yCurrent * size) - (size * 0.5)) {
			int xChunkStart = xCurrent - 2;
			
			for (LinkedList<Chunk> column : grid) {
				Chunk c = new Chunk();
				c.create(xChunkStart++, yCurrent - 3);
				column.addFirst(c);
				root.getChildren().addAll(c.getNodes());
				loadedChunks.add(c);
				
				root.getChildren().removeAll(column.getLast().getNodes());
				column.removeLast();
			}
			
			return -1;
		} else if (yPos > (yCurrent * size) + (size * 0.5)) {
			int xChunkStart = xCurrent - 2;
			
			for (LinkedList<Chunk> column : grid) {
				Chunk c = new Chunk();
				c.create(xChunkStart++, yCurrent + 3);
				column.addLast(c);
				root.getChildren().addAll(c.getNodes());
				loadedChunks.add(c);
				
				root.getChildren().removeAll(column.getFirst().getNodes());
				column.removeFirst();
			}
			
			return 1;
		}
		
		return 0;
	}
	
	public static String randString(int length) {
		StringBuilder sb = new StringBuilder(length);
		
		for(int i = 0; i < length; i++) {
			sb.append(allCharacters.charAt(r.nextInt(allCharacters.length())));
		}
			
		return sb.toString();
	}
	
	public static int getSaveNumber() {
		
		for (int i = 0; i < allSaves.length; i++) {
			
			if (allSaves[i] == null) {
				return i;
			}
			
		}
		
		return -1;
	}
	
	public static int numOfChunks() {
		int total = 0;
		
		for (LinkedList<Chunk> column : grid) {
			total += column.size();
		}
		
		return total;
	}
	
}