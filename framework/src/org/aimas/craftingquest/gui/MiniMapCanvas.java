package org.aimas.craftingquest.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.StrategicObject;
import org.aimas.craftingquest.state.StrategicResource;
import org.aimas.craftingquest.state.Tower;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;
import org.aimas.craftingquest.state.StrategicResource.StrategicResourceType;

@SuppressWarnings("serial")
public class MiniMapCanvas extends Canvas implements MouseListener, MouseMotionListener {
	
	private GameState game;	
	private MapCanvas map;
	
	private final int WIDTH = 400;
	private final int HEIGHT;
	private int CELL_DIM;
	protected int viewAreaWidth, viewAreaHeight;
	protected int crtX = 0, crtY = 0;
	
	private Graphics bufferGraphics;
	private Image offscreenImage;
	private Image mapImage;
	private Dimension dim;
	
	private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 12);

	public MiniMapCanvas(GameState game, MapCanvas map) {
		this.game = game;
		this.map = map;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		CELL_DIM = WIDTH / game.map.cells[0].length;
		HEIGHT = CELL_DIM * game.map.cells.length;
		setSize(WIDTH, HEIGHT);
		/* width = number of cells covered * width of a cell on the minimap (CELL_DIM) */
		viewAreaWidth = (map.getWidth() / map.CELL_DIM) * CELL_DIM;
		/* height = number of cells covered * height of a cell on the minimap (CELL_DIM) */
		viewAreaHeight = (map.getHeight() / map.CELL_DIM) * CELL_DIM;
	}
	
	public void setGameState(GameState game) {
		this.game = game;
		
	}
	
	protected void createMiniMapImage() {
		mapImage = createImage(dim.width, dim.height);
		Graphics g = mapImage.getGraphics();
		for (int i = 0; i < game.map.cells.length; i++) {
			for (int j = 0; j < game.map.cells[i].length; j++) {
				paintGridCell(g, i, j);
			}
		}
	}
	
    public void paintGridCell(Graphics g, int i, int j) {
    	CellState cell = game.map.cells[i][j];
    	Color terrainColor = Color.BLACK;
    	
    	
    	if (!cell.cellUnits.isEmpty()) {
    		g.setFont(font);
    		g.setColor(Color.BLACK);
    		
			g.drawString("U", j * CELL_DIM, i * CELL_DIM);
			
    		return;
    	}   
    	
    	switch (cell.type) {
    	case Grass:
    		terrainColor = Color.GREEN;
    		break;
    	case Rock:
    		terrainColor = Color.DARK_GRAY;
    		break;
    	case Water:
    		terrainColor = Color.CYAN;
    		break;
    	case DeepWater:
    		terrainColor = Color.BLUE;
    		break;
    	case Sand:
    		terrainColor = Color.YELLOW;
    		break;
    	case Snow:
    		terrainColor = Color.WHITE;
    		break;
    	case Dirt:
    		terrainColor = Color.LIGHT_GRAY;
    		break;
    	case Swamp:
    		terrainColor = Color.ORANGE;
    		break;
    	}
    	
    	
    	g.setColor(terrainColor);
    	g.fillRect(j * CELL_DIM, i * CELL_DIM, CELL_DIM, CELL_DIM);
    	
    	g.setFont(font);
    	g.setColor(Color.RED);
    	StrategicObject strRes = cell.strategicObject;
    	if (strRes != null) {
    		if (strRes instanceof Tower) {
    			g.drawString("T", j * CELL_DIM, i * CELL_DIM); // T = observation Tower
    		}
    		else {
    			g.drawString("M", j * CELL_DIM, i * CELL_DIM); // P = trading Post
    		}
    	}
    	
    	boolean noResources = true;
    	for (Integer quant : cell.resources.values()) {
    		if (quant > 0) {
    			noResources = false;
    			break;
    		}
    	}
    	
    	if (!map.getSelectedResource().equals("NONE") && !noResources) {
    		g.setFont(font);
    		g.setColor(Color.RED);
    		
    		if (map.getSelectedResource().equals("ALL")) {
    			g.drawString("R", j * CELL_DIM, i * CELL_DIM);
    		}
    		else {
    			BasicResourceType selectedType = BasicResourceType.valueOf(map.getSelectedResource());
    			if (cell.resources.containsKey(selectedType)) {
    				g.drawString("R", j * CELL_DIM, i * CELL_DIM);
    			}
    		}
    	}
    }
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	@Override
	public void paint(Graphics g) {
		/* do double buffering */
		if (offscreenImage == null) {
			dim = getSize();
			offscreenImage = createImage(dim.width, dim.height);
			bufferGraphics = offscreenImage.getGraphics();
			createMiniMapImage();
		}
		
		bufferGraphics.clearRect(0, 0, dim.width, dim.height);
		bufferGraphics.drawImage(mapImage, 0, 0, null);
		bufferGraphics.setColor(Color.RED);
		bufferGraphics.drawRect(crtX * CELL_DIM, crtY * CELL_DIM, viewAreaWidth, viewAreaHeight);
		
		g.drawImage(offscreenImage, 0, 0, null);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if (x >= 0 && x <= getWidth()- viewAreaWidth) {
			crtX = e.getX() / CELL_DIM;
			map.setOffsetX(-crtX * map.CELL_DIM);
		}
		if (y >= 0 && y <= getHeight() - viewAreaHeight) {
			crtY = e.getY() / CELL_DIM;
			map.setOffsetY(-crtY * map.CELL_DIM);
		}
		
		repaint();
		map.repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if (x >= 0 && x <= getWidth()- viewAreaWidth) {
			crtX = e.getX() / CELL_DIM;
			map.setOffsetX(-crtX * map.CELL_DIM);
		}
		if (y >= 0 && y <= getHeight() - viewAreaHeight) {
			crtY = e.getY() / CELL_DIM;
			map.setOffsetY(-crtY * map.CELL_DIM);
		}
		
		repaint();
		map.repaint();
	}	

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {	
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
