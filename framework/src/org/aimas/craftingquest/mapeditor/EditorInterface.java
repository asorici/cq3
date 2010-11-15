package org.aimas.craftingquest.mapeditor;

import java.awt.BorderLayout;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.aimas.craftingquest.state.CellState.CellType;
import org.aimas.craftingquest.state.StrategicResource.StrategicResourceType;


@SuppressWarnings("serial")
public class EditorInterface extends JFrame implements ActionListener {

	private MapCell[][] terrain;
	private int mapHeight = 60;
	private int mapWidth = 60;
	
	private EditorCanvas editorCanvas;
	private MiniEditorCanvas miniEditorCanvas;
	private Scrollbar hs, vs;
	private JButton mirrorBtn, openMapBtn, saveMapBtn, strategicInfoBtn, cellTypeInfoBtn;
	private JTextArea infoArea;
	private JComboBox terrainSelector;
	private JComboBox strategicSelector;
	private JFileChooser fileChooser = new JFileChooser("maps");
	
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 660;
	
	public EditorInterface() {
		setLayout(new BorderLayout(10, 10));
		setSize(WIDTH, HEIGHT);		
		
		JPanel mapPanel = new JPanel(new BorderLayout());
		hs = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, 1);
		vs = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 1);
		
		terrain = new MapCell[mapHeight][mapWidth];
		for (int i = 0; i < mapHeight; i++) {
			for (int j = 0; j < mapWidth; j++) {
				terrain[i][j] = new MapCell(CellType.Grass, null);
			}
		}
		
		editorCanvas = new EditorCanvas(terrain, hs, vs);
		mapPanel.add(BorderLayout.CENTER, editorCanvas);
		mapPanel.add(BorderLayout.EAST, vs);
		mapPanel.add(BorderLayout.SOUTH, hs);

		JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
		miniEditorCanvas = new MiniEditorCanvas(terrain, editorCanvas);
		editorCanvas.setMiniMap(miniEditorCanvas);
		rightPanel.add(BorderLayout.NORTH, miniEditorCanvas);
		
		JPanel controlPanel = new JPanel(new BorderLayout());
		JPanel btnPnl = new JPanel();
		openMapBtn = new JButton("Open Map");
		openMapBtn.addActionListener(this);
		btnPnl.add(openMapBtn);
		saveMapBtn = new JButton("Save Map");
		saveMapBtn.addActionListener(this);
		btnPnl.add(saveMapBtn);
		mirrorBtn = new JButton("Mirror Map");
		mirrorBtn.addActionListener(this);
		btnPnl.add(mirrorBtn);
		
		JPanel selectorPanel = new JPanel();
		
		terrainSelector = new JComboBox(CellType.values());
		terrainSelector.addActionListener(this);
		strategicSelector = new JComboBox(new StrategicResourceType[] {StrategicResourceType.Merchant});
		
		strategicInfoBtn = new JButton("Select strategic");
		strategicInfoBtn.addActionListener(this);
		cellTypeInfoBtn = new JButton("Select cell type");
		cellTypeInfoBtn.addActionListener(this);
		
		selectorPanel.add(strategicSelector);
		selectorPanel.add(strategicInfoBtn);
		selectorPanel.add(terrainSelector);
		selectorPanel.add(cellTypeInfoBtn);
		
		controlPanel.add(BorderLayout.NORTH, btnPnl);
		controlPanel.add(BorderLayout.SOUTH, selectorPanel);
		
		rightPanel.add(BorderLayout.SOUTH, controlPanel);

		infoArea = new JTextArea();
		infoArea.setEditable(false);
		editorCanvas.setInfoArea(infoArea);
		rightPanel.add(BorderLayout.CENTER, infoArea);
		
		add(BorderLayout.CENTER, mapPanel);
		add(BorderLayout.EAST, rightPanel);
		
        addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent we) {
                System.exit(0);
            }
        });
        
		setTitle("Map editor");
		//setVisible(true);
		//printDebugInfo();
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(saveMapBtn)) {
			int returnVal = fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                //This is where a real application would save the file.
                
                MapWriter mapWriter = new MapWriter(file, terrain);
                mapWriter.writeMap();
            }
		}
		
		if (e.getSource().equals(openMapBtn)) {
			int returnVal = fileChooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
                MapReader.readMap(file);
                
                mapWidth = MapReader.mapWidth;
                mapHeight = MapReader.mapHeight;
                int n = mapHeight;
                
                for (int i = 0; i < n; i++) {
    				for (int j = 0; j < n; j++) {
    					terrain[i][j].cellType = MapReader.cells[i][j].cellType;
    					terrain[i][j].strategicResType = MapReader.cells[i][j].strategicResType;
    				}
    			}
                
                editorCanvas.repaint();
    			miniEditorCanvas.repaint();
            } 
		}
		
		if (e.getSource().equals(cellTypeInfoBtn)) {
			editorCanvas.setSelectedCellType((CellType)terrainSelector.getSelectedItem());
			editorCanvas.setSelectedStrategic(null);
		}
				
		if (e.getSource().equals(strategicInfoBtn)) {
			editorCanvas.setSelectedStrategic((StrategicResourceType)strategicSelector.getSelectedItem());
		}
		
		if (e.getSource().equals(terrainSelector)) {
			editorCanvas.setSelectedStrategic(null);
			editorCanvas.setSelectedCellType((CellType)terrainSelector.getSelectedItem());
		}
		
		if (e.getSource().equals(mirrorBtn)) {
			int n = terrain.length;
			
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n - i; j++) {
					terrain[n - 1 - i][n - 1 - j].cellType = terrain[i][j].cellType;
					terrain[n - 1 - i][n - 1 - j].strategicResType = terrain[i][j].strategicResType;
				}
			}
			
			editorCanvas.repaint();
			miniEditorCanvas.repaint();
		}
	}

	interface InfoProvider {
		public String getInfo();
	}

	public int getMapHeight() {
		return mapHeight;
	}


	public void setMapHeight(int mapHeight) {
		this.mapHeight = mapHeight;
	}


	public int getMapWidth() {
		return mapWidth;
	}


	public void setMapWidth(int mapWidth) {
		this.mapWidth = mapWidth;
	}
	
	public static void main(String args[]) {
		final EditorInterface editInterface = new EditorInterface();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				editInterface.setVisible(true);
			}
		});
	}
}