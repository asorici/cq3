package org.aimas.craftingquest.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.BasicUnit;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.UnitState;
import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.UnitState.UnitType;

public class PrettyCanvas extends JComponent {

	AffineTransform tx = new AffineTransform();
	double translateX;
	double translateY;
	double rotate;
	double scale = 1;
	// Graphics
	Graphics2D g2;
	
	private Graphics bufferGraphics;
	private Image offscreenImage;
	private Dimension dim;
	
	int xBall = 100;
	int yBall = 200;
	int dxBall = 5;
	int dyBall = 3;
	//
	BufferedImage grass;
	BufferedImage water;
	BufferedImage deepWater;
	BufferedImage sand;
	BufferedImage rock;
	BufferedImage unknown;

	BufferedImage crocodile;
	BufferedImage tazmanian;
	BufferedImage fox;
	BufferedImage enemy;

	//IPlayerActions player;
	//Scenario scenario;
	//PlayerState state;
	GameState game;
	HashMap<String, BufferedImage> entityToImage = new HashMap<String, BufferedImage>();

	public PrettyCanvas() {
		setOpaque(true);
		setDoubleBuffered(true);
		TranslateHandler input1 = new TranslateHandler(this);
		ScaleHandler input2 = new ScaleHandler(this);
		this.addMouseListener(input1);
		// this.addMouseListener(new MouseAdapter() {
		//
		// @Override
		// public void mouseMoved(MouseEvent e) {
		// super.mouseMoved(e);
		// }
		// });
		this.addMouseMotionListener(input1);
		this.addMouseWheelListener(input2);
		load();
	}

	void load() {
		// setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/package1/package2/dump.jpg")));
		
		String rootDir = "F:/Work/eclipse-workspace/aiwo-test";
		String grassLoc = rootDir + "/img/grass.png";
		String waterLoc = rootDir + "/img/water.png";
		String deepWaterLoc = rootDir + "/img/deepWater.png";
		String sandLoc = rootDir + "/img/sand.png";
		String rockLoc = rootDir + "/img/rock.png";
		String unknownLoc = rootDir + "/img/unknown.png";

		String crocodileLoc = rootDir + "/img/crocodile.png";
		String tazmanianLoc = rootDir + "/img/tazmanian.png";
		String foxLoc = rootDir + "/img/fox.png";
		String enemyLoc = rootDir + "/img/enemy.png";

		// grass= new BufferedImage =
		// Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/grass.png"));

		// Image img =
		// Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/grass.png"));
		try {
			//System.out.println(getClass().getResource(grassLoc));
			grass = ImageIO.read(new File(grassLoc));
			water = ImageIO.read(new File(waterLoc));
			deepWater = ImageIO.read(new File(deepWaterLoc));
			sand = ImageIO.read(new File(sandLoc));
			rock = ImageIO.read(new File(rockLoc));

			unknown = ImageIO.read(new File(unknownLoc));
			crocodile = ImageIO.read(new File(crocodileLoc));
			tazmanian = ImageIO.read(new File(tazmanianLoc));
			fox = ImageIO.read(new File(foxLoc));
			enemy = ImageIO.read(new File(enemyLoc));
			
			entityToImage.put(CellType.Grass.name(), grass);
			entityToImage.put(CellType.Water.name(), water);
			entityToImage.put(CellType.DeepWater.name(), deepWater);
			entityToImage.put(CellType.Sand.name(), sand);
			entityToImage.put(CellType.Rock.name(), rock);
			entityToImage.put(UnitType.Crocodile.name(), crocodile);
			entityToImage.put(UnitType.Tazmanian.name(), tazmanian);
			entityToImage.put(UnitType.Fox.name(), fox);
			entityToImage.put("unknown", unknown);
			
			// grass = ImageIO.read(new File(grassLoc));
			// water = ImageIO.read(new File(waterLoc));
			// deepWater = ImageIO.read(new File(deepWaterLoc));
			// sand = ImageIO.read(new File(sandLoc));
			// rock = ImageIO.read(new File(rockLoc));
			// unknown = ImageIO.read(new File(unknownLoc));
			//
			// crocodile = ImageIO.read(new File(crocodileLoc));
			// tazmanian = ImageIO.read(new File(tazmanianLoc));
			// fox = ImageIO.read(new File(foxLoc));
			// enemy = ImageIO.read(new File(enemyLoc));
		} catch (IOException ex) {
			Logger.getLogger(PrettyCanvas.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	@Override
	public void paint(Graphics g) {
		/*
		// do double buffering
		if (offscreenImage == null) {
			dim = getSize();
			offscreenImage = createImage(dim.width, dim.height);
			bufferGraphics = offscreenImage.getGraphics();
		}
		
		bufferGraphics.clearRect(0, 0, dim.width, dim.height);
		render(bufferGraphics);
		
		g.drawImage(offscreenImage, 0, 0, null);
		*/
		render(g);
	}

	void render(Graphics g) {
		g2 = (Graphics2D) g;
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		
		
		
		// clear transforms 
		tx.setToIdentity();

		// apply transforms 
		tx.translate(centerX, centerY); // center
		// tx.rotate(rotate);
		tx.scale(scale, scale); // scale
		tx.translate(-centerX, -centerY);
		tx.translate(translateX, translateY); // translate

		// clear background to white or transparent
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setTransform(tx);
		
		
		// draw some stuff
		//drawText();
		//drawBall();
		//drawPlayerState();
		drawGameState();
		drawCenter(centerX, centerY);

	}

	void drawText() {

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// g2.setColor(Color.BLACK);
		// g2.fillOval(100, 100, 100, 100);
		g2.drawString("Hello Razvan!", 0, 0);
	}

	void drawBall() {
		g2.setColor(Color.red);
		g2.fillOval(xBall, yBall, 50, 50);
	}

	void drawCenter(int centerX, int centerY) {

		int newX = 0;
		int newY = 0;

		Point2D source = new Point2D.Float(centerX, centerY);
		Point2D destination = null;
		try {
			destination = tx.inverseTransform(source, null);
		} catch (NoninvertibleTransformException ex) {
			Logger.getLogger(PrettyCanvas.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		if (destination != null) {
			newX = (int) destination.getX();
			newY = (int) destination.getY();

			g2.setColor(Color.red);
			g2.fillOval(newX, newY, 10, 10);
		}
	}

	void drawUnit(UnitState unit) {
		BufferedImage icon = null;
		switch (unit.type) {
		case Crocodile:
			icon = crocodile;
			break;
		case Tazmanian:
			icon = tazmanian;
			break;
		case Fox:
			icon = fox;
			break;
		default:
			icon = null;
			break;
		}
		if (icon != null) {
			// g2.setColor(Color.red);
			// g2.setXORMode(Color.red);
			// g2.setBackground(Color.yellow);
			// Paint p = new GradientPaint(new Point2D.Float(), Color.yellow,
			// new Point2D.Float(10, 10), Color.blue);
			// g2.setPaint(new Paint() {})
			// g2.setPaintMode();
			// g2.setPaint(p);
			// g2.d
			// g2.setXORMode(Color.red);
			// ColorModel cm = icon.getColorModel();
			// Graphics g = icon.getGraphics();
			// g.setColor(Color.red);
			g2.drawImage(icon, unit.pos.y * 33, unit.pos.x * 33, 32, 32, null, null);
			// g2.setXORMode(Color.red);
			// g2.setXORMode(Color.white);
		}
	}

	void drawCell(UnitState unit, CellState cell, int x, int y) {
		BufferedImage icon = null;
		BufferedImage extra = null;
		if (cell == null) {
			icon = unknown;
		} else {
			switch (cell.type) {
			case Grass:
				icon = grass;
				break;
			case Sand:
				icon = sand;
				break;
			case Water:
				icon = water;
				break;
			case DeepWater:
				icon = deepWater;
				break;
			case Rock:
				icon = rock;
				break;
			default:
				icon = unknown;
				break;
			}
		}
		if (icon != null)
			g2.drawImage(icon, x, y, 32, 32, null);

		if (cell != null) {
			
			for (BasicUnit bu : cell.cellUnits) {
				if (bu.playerID != unit.playerID) {
					extra = enemy;
				}
			}
			
		}
		if (extra != null) {
			g2.drawImage(extra, x, y, 32, 32, null);
		}

	}

	void drawSight(UnitState unit) {
		for (int i = 0; i < unit.sight.length; i++) {
			for (int j = 0; j < unit.sight[i].length; j++) {
				drawCell(unit, unit.sight[i][j],
						(unit.pos.x - unit.sight.length + j) * 33,
						(unit.pos.y - unit.sight.length + i) * 33);
			}
		}

	}

	//void drawTerrain(PlayerState state) {
	void drawTerrain() {
		// 00 01 02 03
		// 10 11 12 13
		for (int i = 0; i < GamePolicy.mapsize.x; i++) {
			for (int j = 0; j < GamePolicy.mapsize.y; j++) {
				BufferedImage terrainImage = entityToImage.get(game.map.cells[i][j].type.name());
				g2.drawImage(terrainImage, j * 33, i * 33, 32, 32, null);
			}
		}
		
		/*
		for (UnitState unit : state.units) {
			drawSight(unit);
		}
		*/
		
		for (PlayerState pState : game.playerStates) {
			for (UnitState unit : pState.units) {
				drawUnit(unit);
			}
		}

		// UnitState croco = state.units.get(0);

	}

	/*
	void drawPlayerState() {
		if (player == null)
			return;
		state = player.getPlayerState();
		//scenario = player.getScenario();
		if (state == null) {
			return;
		}

		drawTerrain(state);
	}
	*/
	
	void drawGameState() {
		if (game == null) {
			return;
		}
		
		drawTerrain();
	}
	
	void update() {
		if (xBall < 100) {
			dxBall = +5;
		}
		if (xBall > 500) {
			dxBall = -5;
		}

		if (yBall < 100) {
			dyBall = +7;
		}
		if (yBall > 600) {
			dyBall = -2;
		}

		xBall += dxBall;
		yBall += dyBall;

		rotate += 0.005;
	}
}

class TranslateHandler implements MouseListener, MouseMotionListener {

	PrettyCanvas canvas;
	private int lastOffsetX;
	private int lastOffsetY;

	public TranslateHandler(PrettyCanvas canvas) {
		this.canvas = canvas;
	}

	public void mousePressed(MouseEvent e) {
		// capture starting point
		lastOffsetX = e.getX();
		lastOffsetY = e.getY();
	}

	public void mouseDragged(MouseEvent e) {

		// new x and y are defined by current mouse location subtracted
		// by previously processed mouse location
		int newX = e.getX() - lastOffsetX;
		int newY = e.getY() - lastOffsetY;

		// increment last offset to last processed by drag event.
		lastOffsetX += newX;
		lastOffsetY += newY;

		// update the canvas locations
		canvas.translateX += newX;
		canvas.translateY += newY;

		// schedule a repaint.
		canvas.repaint();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}

class ScaleHandler implements MouseWheelListener {

	PrettyCanvas canvas;

	public ScaleHandler(PrettyCanvas canvas) {
		this.canvas = canvas;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			canvas.scale -= (.2 * e.getWheelRotation());
			canvas.scale = clip(0.5, 4, canvas.scale);
			canvas.repaint();
		}
	}

	double clip(double min, double max, double val) {
		if (val < min) {
			return min;
		} else if (val > max) {
			return max;
		}
		return val;
	}
}
