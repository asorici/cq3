package org.aimas.craftingquest.mapeditor;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
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
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JTextArea;

import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.objects.CraftedObjectType;
import org.aimas.craftingquest.state.resources.ResourceType;


@SuppressWarnings("serial")
public class EditorCanvas extends Canvas implements MouseListener, MouseMotionListener, AdjustmentListener {
	
	private static enum SelectionType {
		TERRAIN, RESOURCE
	}
	
	MapCell[][] cells;
	private int mapWidth;
	private int mapHeight;
	
	private CellType selectedTerrainType = CellType.Grass;
	private ResourceType selectedResourceType = ResourceType.GOLD;
	private int selectedResourceQuantity = 10;
	private SelectionType selectionType = SelectionType.TERRAIN;
	
	private Image rockTile;
	private Image grassTile;
	private Image unknown;
	
//	private Image waterTile;
//	private Image deepWaterTile;
//	private Image sandTile;
//	private Image snowTile;
//	private Image dirtTile;
//	private Image swampTile;
	
	
//	private Image goldRes;
//	private Image woodRes;
//	private Image stoneRes;
//	private Image leatherRes;
//	private Image bronzeRes;
//	private Image ironRes;
//	private Image titaniumRes;
	
	private Image tower;
//	private Image merchant;
	
	private Graphics bufferGraphics;
	private Image offscreenImage;
	private Dimension dim;
	
	private MiniEditorCanvas miniMap;
	private Scrollbar hs, vs;
	private JTextArea infoArea;
	
	private final int WIDTH = 700;
	private final int HEIGHT = 600;
	protected final int CELL_DIM = 50;
	
	protected int startX = 0, startY = 0;
	protected int offsetX = 0, offsetY = 0;
	
	private int cellStartX = 0, cellStartY = 0;
	private int cellFinalX, cellFinalY;
	
	private int crtCellX = 0, crtCellY = 0;
	private boolean mouseButton1Pressed = false;
	private boolean mouseButton2Pressed = false;

	HashMap<String, Image> entityToImage = new HashMap<String, Image>();
	
	public EditorCanvas(MapCell[][] terrain, Scrollbar hs, Scrollbar vs) {
		this.cells = terrain;
		this.hs = hs;
		this.vs = vs;
		mapHeight = terrain.length * CELL_DIM;
		mapWidth = terrain[0].length * CELL_DIM;
		setSize(WIDTH, HEIGHT);
		addMouseListener(this);
		addMouseMotionListener(this);
		hs.addAdjustmentListener(this);
		vs.addAdjustmentListener(this);

		try {
			rockTile = ImageIO.read(new File("images/rock.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
			grassTile = ImageIO.read(new File("images/grass.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
			
//			waterTile = ImageIO.read(new File("images/water.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
//			deepWaterTile = ImageIO.read(new File("images/deepWater.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
//			snowTile = ImageIO.read(new File("images/snow.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
//			sandTile = ImageIO.read(new File("images/sand.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
//			swampTile = ImageIO.read(new File("images/swamp.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
//			dirtTile = ImageIO.read(new File("images/dirt.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
			
			
			
			tower = ImageIO.read(new File("images/tower.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
//			merchant = ImageIO.read(new File("images/merchant.jpg")).getScaledInstance(CELL_DIM, CELL_DIM, Image.SCALE_FAST);
		
			entityToImage.put(CellType.Grass.name(), grassTile);
			entityToImage.put(CellType.Rock.name(), rockTile);
			
//			entityToImage.put(CellType.Water.name(), waterTile);
//			entityToImage.put(CellType.DeepWater.name(), deepWaterTile);
//			entityToImage.put(CellType.Sand.name(), sandTile);
//			entityToImage.put(CellType.Swamp.name(), swampTile);
//			entityToImage.put(CellType.Dirt.name(), dirtTile);
//			entityToImage.put(CellType.Snow.name(), snowTile);
			
			entityToImage.put("unknown", unknown);
			entityToImage.put(CraftedObjectType.TOWER.toString(), tower);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		hs.setMaximum(terrain[0].length - WIDTH / CELL_DIM + 1);
		vs.setMaximum(terrain.length - HEIGHT / CELL_DIM + 1);
	}
	
	public void setTerrain(MapCell[][] terrain) {
		cells = terrain;
		mapHeight = terrain.length * CELL_DIM;
		mapWidth = terrain[0].length * CELL_DIM;
		
		miniMap.setTerrain(terrain);
	}
	
	
	public void setMiniMap(MiniEditorCanvas miniMap) {
		this.miniMap = miniMap;
	}
	
	public void setInfoArea(JTextArea infoArea) {
		this.infoArea = infoArea;
	}
	
	public String getCrtCellInfo() {
		String info = "";
		MapCell crtCell = cells[crtCellY][crtCellX];
		
		info += "Terrain type = " + crtCell.cellType + " [" + crtCellX + "," + crtCellY + "]\n";
		
		HashMap<ResourceType, Integer> cellResources = crtCell.cellResources;
		if (!cellResources.isEmpty()) {
			for (ResourceType rt : cellResources.keySet()) {
				info += "Cell resource = " + rt + "(" + cellResources.get(rt) + ")\n";
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
		
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				paintGridCell(i, j);
			}
		}
		bufferGraphics.setColor(Color.RED);
    	bufferGraphics.drawRect(crtCellX * CELL_DIM + offsetX, crtCellY * CELL_DIM + offsetY, CELL_DIM, CELL_DIM);
		
    	if (mouseButton2Pressed) {
    		int rectWidth = (cellFinalX - cellStartX) * CELL_DIM;
    		int rectHeight = (cellFinalY - cellStartY) * CELL_DIM;
    		
    		bufferGraphics.drawRect(cellStartX * CELL_DIM + offsetX, cellStartY * CELL_DIM + offsetY, rectWidth, rectHeight);
    	}
    	
		g.drawImage(offscreenImage, 0, 0, null);
	}
		
    public void paintGridCell(int i, int j) {
    	MapCell cell = cells[i][j];
    	Image tile = grassTile;
    	
    	HashMap<ResourceType, Integer> cellResources = cell.cellResources;
		if (!cellResources.isEmpty()) {
			String resString = "";
			for (ResourceType rt : cellResources.keySet()) {
				resString += rt.name().toUpperCase().charAt(0) + "(" + cellResources.get(rt) + ") ";
			}
			
			// draw resource type - white image with text for now
			bufferGraphics.setColor(Color.WHITE);
			bufferGraphics.fillRect(j * CELL_DIM + offsetX, i * CELL_DIM + offsetY, CELL_DIM, CELL_DIM);
			bufferGraphics.setColor(Color.BLACK);
			
			int midX = j * CELL_DIM + offsetX + CELL_DIM / 2;
			int midY = i * CELL_DIM + offsetY + CELL_DIM / 2;
			
			int w2 = bufferGraphics.getFontMetrics().stringWidth(resString) / 2;
	        int h2 = bufferGraphics.getFontMetrics().getDescent();
	        bufferGraphics.drawString(resString, midX - w2, midY + h2);
			
		}
		else {
	    	// draw terrain type
	    	tile = entityToImage.get(cell.cellType.name());
	    	bufferGraphics.drawImage(tile, j * CELL_DIM + offsetX, i * CELL_DIM + offsetY, null);
	
	    	bufferGraphics.setColor(Color.BLACK);
	    	bufferGraphics.drawRect(j * CELL_DIM + offsetX, i * CELL_DIM + offsetY, CELL_DIM, CELL_DIM);
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
	    
	    mouseButton2Pressed = false;
	    mouseButton1Pressed = false;
	    
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
	    else {
	    	if (e.getButton() == MouseEvent.BUTTON3) {
	    		mouseButton2Pressed = true;
	    		cellStartX = (x - offsetX) / CELL_DIM;
	    		cellStartY = (y - offsetY) / CELL_DIM;
	    		
	    		repaint();
	    	}
	    }
	    
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
		
		if (mouseButton2Pressed) {
			cellFinalX = (x - offsetX) / CELL_DIM;
			cellFinalY = (y - offsetY) / CELL_DIM;
			
			repaint();
			miniMap.repaint();
		}
		
	}	
	
	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		if (mouseButton2Pressed) {
			cellFinalX = (x - offsetX) / CELL_DIM;
			cellFinalY = (y - offsetY) / CELL_DIM;
			
			for (int yy = cellStartY; yy < cellFinalY; yy++) {
				for (int xx = cellStartX; xx < cellFinalX; xx++) {
					if (selectionType == SelectionType.TERRAIN) {
						// if we are in terrain mode set the terrain
						cells[yy][xx].cellType = selectedTerrainType;
					}
					else if (selectionType == SelectionType.RESOURCE) {
						// otherwise we are in resource selection mode
						Map<ResourceType, Integer> cellResources = cells[yy][xx].cellResources; 
						
						if (cellResources.get(selectedResourceType) == null) {
							cellResources.put(selectedResourceType, selectedResourceQuantity);
						}
						else {
							int prevQuantity = cellResources.get(selectedResourceType);
							cellResources.put(selectedResourceType, prevQuantity + selectedResourceQuantity);
						}
					}
				}
			}
			
			repaint();
			miniMap.repaint();
			
			mouseButton2Pressed = false;
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
		int x = e.getX();
	    int y = e.getY();
	    
	    if (e.getButton() == MouseEvent.BUTTON3) {
		    int xcoord = (x - offsetX) / CELL_DIM;
	    	int ycoord = (y - offsetY) / CELL_DIM;
	    	
	    	//cells[ycoord][xcoord].cellType = CellType.Grass;
	    	if (selectionType == SelectionType.TERRAIN) {
	    		cells[ycoord][xcoord].cellType = selectedTerrainType;
	    	}
	    	else if (selectionType == SelectionType.RESOURCE) {
	    		// otherwise we are in resource selection mode
				Map<ResourceType, Integer> cellResources = cells[ycoord][xcoord].cellResources; 
				
				if (cellResources.get(selectedResourceType) == null) {
					cellResources.put(selectedResourceType, selectedResourceQuantity);
				}
				else {
					int prevQuantity = cellResources.get(selectedResourceType);
					cellResources.put(selectedResourceType, prevQuantity + selectedResourceQuantity);
				}
	    	}
	    	
	    	repaint();
	    	miniMap.repaint();
	    }
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {	
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	public CellType getSelectedCellType() {
		return selectedTerrainType;
	}
	
	public ResourceType getSelectedResourceType() {
		return selectedResourceType;
	}

	public void setSelectedCellType(CellType selectedTerrainType) {
		this.selectedTerrainType = selectedTerrainType;
		
		selectionType = SelectionType.TERRAIN;
	}

	public void setSelectedResourceType(ResourceType selectedResourceType, int quantity) {
		this.selectedResourceType = selectedResourceType;
		this.selectedResourceQuantity = quantity;
		
		selectionType = SelectionType.RESOURCE;
	}

}
