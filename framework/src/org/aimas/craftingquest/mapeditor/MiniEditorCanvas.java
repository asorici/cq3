package org.aimas.craftingquest.mapeditor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.aimas.craftingquest.state.StrategicResource.StrategicResourceType;

@SuppressWarnings("serial")
public class MiniEditorCanvas extends Canvas implements MouseListener, MouseMotionListener {
	
	private MapCell[][] terrain;	
	private EditorCanvas map;
	
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

	public MiniEditorCanvas(MapCell[][] terrain, EditorCanvas map) {
		this.terrain = terrain;
		this.map = map;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		CELL_DIM = WIDTH / terrain[0].length;
		HEIGHT = CELL_DIM * terrain.length;
		setSize(WIDTH, HEIGHT);
		/* width = number of cells covered * width of a cell on the minimap (CELL_DIM) */
		viewAreaWidth = (map.getWidth() / map.CELL_DIM) * CELL_DIM;
		/* height = number of cells covered * height of a cell on the minimap (CELL_DIM) */
		viewAreaHeight = (map.getHeight() / map.CELL_DIM) * CELL_DIM;
	}
	
	public void setTerrain(MapCell[][] terrain) {
		this.terrain = terrain;
	}
	
	protected void createMiniMapImage() {
		
		mapImage = createImage(dim.width, dim.height);
		Graphics g = mapImage.getGraphics();
		for (int i = 0; i < terrain.length; i++) {
			for (int j = 0; j < terrain[i].length; j++) {
				paintGridCell(g, i, j);
			}
		}
	}
	
    public void paintGridCell(Graphics g, int i, int j) {
    	MapCell cell = terrain[i][j];
    	Color terrainColor = Color.BLACK;
    	
    	switch (cell.cellType) {
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
    	StrategicResourceType strRes = cell.strategicResType;
    	if (strRes != null) {
    		if (strRes == StrategicResourceType.Tower) {
    			g.drawString("T", j * CELL_DIM, i * CELL_DIM); // T = observation Tower
    		}
    		else {
    			g.drawString("M", j * CELL_DIM, i * CELL_DIM); // P = trading Post
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
		}
		
		createMiniMapImage();
		
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
