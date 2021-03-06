package org.aimas.craftingquest.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JTextArea;

import org.aimas.craftingquest.state.BasicUnit;
import org.aimas.craftingquest.state.CellState;
import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.objects.Tower;
import org.aimas.craftingquest.state.resources.ResourceType;
import org.aimas.craftingquest.state.objects.*;
//import org.aimas.craftingquest.state.UnitState.UnitType;



@SuppressWarnings("serial")
public class MapCanvas extends Canvas implements MouseListener, MouseMotionListener, AdjustmentListener {
	
	private GameState game;
	private int mapWidth;
	private int mapHeight;
	
	private String selectedResource = "ALL";
	
	private Image rockTile;
	private Image grassTile;
	private Image unknown;
	
	private Image tower;
	private Image trap;
	private Image resource;
	private Image standardUnitImage;	
	
	private Graphics bufferGraphics;
	private Image offscreenImage;
	private Dimension dim;
	
	private MiniMapCanvas miniMap;
	private Scrollbar hs, vs;
	private JTextArea infoArea;
	
	private final int WIDTH = 700;
	private final int HEIGHT = 600;
	protected final int CELL_DIM = 50;
	
	protected int startX = 0, startY = 0;
	protected int offsetX = 0, offsetY = 0;
	private int crtCellX = 0, crtCellY = 0;
	private boolean mouseButton1Pressed;

	HashMap<String, Image> entityToImage = new HashMap<String, Image>();
	
	public MapCanvas(GameState game, Scrollbar hs, Scrollbar vs) {
		this.game = game;
		this.hs = hs;
		this.vs = vs;
		mapHeight = game.map.cells[0].length * CELL_DIM;
		mapWidth = game.map.cells.length * CELL_DIM;
		setSize(WIDTH, HEIGHT);
		addMouseListener(this);
		addMouseMotionListener(this);
		hs.addAdjustmentListener(this);
		vs.addAdjustmentListener(this);

		try {
			rockTile = ImageIO.read(new File("images/rock.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
			grassTile = ImageIO.read(new File("images/grass.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
			
			tower = ImageIO.read(new File("images/tower.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
			trap = ImageIO.read(new File("images/trap.png")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
			resource = ImageIO.read(new File("images/resource.gif")).getScaledInstance(CELL_DIM / 2, CELL_DIM / 2, Image.SCALE_FAST);
			standardUnitImage = ImageIO.read(new File("images/devil.png")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
		
			entityToImage.put(CellType.Grass.name(), grassTile);
			entityToImage.put(CellType.Rock.name(), rockTile);
			
			entityToImage.put("unknown", unknown);
			entityToImage.put(CraftedObjectType.TOWER.toString(), tower);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		hs.setMaximum(game.map.cells[0].length - WIDTH / CELL_DIM + 1);
		vs.setMaximum(game.map.cells.length - HEIGHT / CELL_DIM + 1);
	}
	
	public void setGameState(GameState game) {
		this.game = game;
	}
	
	public void setSelectedResource(String selectedResource) {
		this.selectedResource = selectedResource;
	}
	
	public String getSelectedResource() {
		return selectedResource;
	}
	
	public void setMiniMap(MiniMapCanvas miniMap) {
		this.miniMap = miniMap;
	}
	
	public void setInfoArea(JTextArea infoArea) {
		this.infoArea = infoArea;
	}
	
	public String getCrtCellInfo() {
		String info = "";
		CellState crtCell = game.map.cells[crtCellY][crtCellX];
		info += "Terrain type = " + crtCell.type + " [" + crtCellX + "," + crtCellY + "]\n";
		
		info += "Cell units: \n";
		for (BasicUnit bu : crtCell.cellUnits) {
    		info += "\t" + "[" + bu.unitId + "]" + "(" + bu.playerID +")\n";
		}
		
		IStrategic res = crtCell.strategicObject;
		if (res != null) {
			info += "Strategic resource = " + res + "\n";
		}
		
		if (!crtCell.resources.isEmpty()) {
			info += "Resources: \n";
			Iterator<ResourceType> resIt = crtCell.resources.keySet().iterator();
			while(resIt.hasNext()) {
				ResourceType resType = resIt.next();
				info += "\t" + resType.toString() + ": " + crtCell.resources.get(resType) + "\n";
			}
		}
		
		if (!crtCell.visibleResources.isEmpty()) {
			info += "Visible resources: \n";
			for(ResourceType resType : crtCell.visibleResources.keySet()) {
				info += "\t" + resType.toString() + ": " + crtCell.visibleResources.get(resType) + "\n";
			}
		}
		
		return info;
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
		
		bufferGraphics.clearRect(0, 0, dim.width, dim.height);
		
		for (int i = 0; i < game.map.cells.length; i++) {
			for (int j = 0; j < game.map.cells[i].length; j++) {
				paintGridCell(i, j);
			}
		}
		bufferGraphics.setColor(Color.RED);
    	bufferGraphics.drawRect(crtCellX * CELL_DIM + offsetX, crtCellY * CELL_DIM + offsetY, CELL_DIM, CELL_DIM);
		
		g.drawImage(offscreenImage, 0, 0, null);
	}
		
    public void paintGridCell(int i, int j) {
    	CellState cell = game.map.cells[i][j];
    	
    	Image tile = grassTile;
    	Image unitImage = null;
    	
    	if (cell.cellUnits.isEmpty()) {
	    	IStrategic strRes = cell.strategicObject;
	    	if (strRes != null) {
	    		if (strRes instanceof Tower) {
	    			bufferGraphics.drawImage(tower, j * CELL_DIM + offsetX, i * CELL_DIM + offsetY, null);
	    		}
	    		else {
	    			bufferGraphics.drawImage(trap, j * CELL_DIM + offsetX, i * CELL_DIM + offsetY, null);
	    		}
	    		return;
	    	}
    	}
    	
    	// draw terrain type
    	tile = entityToImage.get(cell.type.name());
    	bufferGraphics.drawImage(tile, j * CELL_DIM + offsetX, i * CELL_DIM + offsetY, null);
    	  	
    	if (!cell.cellUnits.isEmpty()) {
    		BasicUnit unit = cell.cellUnits.get(0);
    		
    		unitImage = standardUnitImage;
    		bufferGraphics.drawImage(unitImage, j * CELL_DIM + offsetX, i * CELL_DIM + offsetY, null);
    		
    		bufferGraphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
    		bufferGraphics.setColor(Color.BLACK);
			bufferGraphics.drawString("" + unit.playerID, j * CELL_DIM + offsetX, i * CELL_DIM + offsetY);
    	}

    	bufferGraphics.setColor(Color.BLACK);
    	bufferGraphics.drawRect(j * CELL_DIM + offsetX, i * CELL_DIM + offsetY, CELL_DIM, CELL_DIM);
    	
    	boolean noResources = true;
    	for (Integer quant : cell.resources.values()) {
    		if (quant > 0) {
    			noResources = false;
    			break;
    		}
    	}
    	
    	for (Integer quant : cell.visibleResources.values()) {
    		if (quant > 0) {
    			noResources = false;
    			break;
    		}
    	}
    	
    	if (!selectedResource.equals("NONE") && !noResources) {
    		if (selectedResource.equals("ALL")) {
    			bufferGraphics.drawImage(resource, j * CELL_DIM + offsetX, i * CELL_DIM + offsetY, null);
        	}
    		else {
    			ResourceType selectedType = ResourceType.valueOf(ResourceType.class, selectedResource);
        		if (cell.resources.containsKey(selectedType)) {
        			bufferGraphics.drawImage(resource, j * CELL_DIM + offsetX, i * CELL_DIM + offsetY, null);
        		}
    		}
    	}
    }	
    
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		int value = e.getValue() * CELL_DIM;
		if (e.getSource().equals(hs)) {
			offsetX = -value;
			miniMap.crtX = e.getValue();
		}
		else {
			offsetY = -value;
			miniMap.crtY = e.getValue();
		}
		
		repaint();
		miniMap.repaint();
	}    
    
	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
	    int y = e.getY();
	    
	    if (e.getButton() == MouseEvent.BUTTON1) {
	    	startX = x;
	    	startY = y;
	    	mouseButton1Pressed = true;
	    	crtCellX = (x - offsetX) / CELL_DIM;
	    	crtCellY = (y - offsetY) / CELL_DIM;
	    	infoArea.setText(getCrtCellInfo());
	    	repaint();
	    	return;
	    }
	    
	    mouseButton1Pressed = false;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int newOffsetX, newOffsetY;
		
		if (mouseButton1Pressed) {
			newOffsetX = offsetX + x - startX;
			newOffsetY = offsetY + y - startY;
			
			setOffsetX(newOffsetX);
			setOffsetY(newOffsetY);
			
			startX = x;
			startY = y;
			
			/* update minimap */
			miniMap.crtX = -offsetX / CELL_DIM;
			miniMap.crtY = -offsetY / CELL_DIM;
			
			repaint();
			miniMap.repaint();
		}		
	}	
	
	protected boolean setOffsetX(int newOffsetX) {
		if (-newOffsetX >= 0 && -newOffsetX <= mapWidth - getWidth()) {
			offsetX = newOffsetX;
			hs.setValue(-newOffsetX / CELL_DIM);
			return true;
		}			
		return false;
	}
	
	protected boolean setOffsetY(int newOffsetY) {
		if (-newOffsetY >= 0 && -newOffsetY <= mapHeight - getHeight()) {
			offsetY = newOffsetY;
			vs.setValue(-newOffsetY / CELL_DIM);
			return true;
		}		
		return false;
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
