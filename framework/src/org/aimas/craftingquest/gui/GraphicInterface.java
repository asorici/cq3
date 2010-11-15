package org.aimas.craftingquest.gui;

import java.awt.BorderLayout;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.aimas.craftingquest.core.GamePolicy;
import org.aimas.craftingquest.state.GameState;
import org.aimas.craftingquest.state.PlayerState;
import org.aimas.craftingquest.state.CraftedObject.BasicResourceType;


@SuppressWarnings("serial")
public class GraphicInterface extends JFrame implements ActionListener {

	private GameState game;
	private int crtTurn = 0;
	private InfoProvider infoProvider;
	
	private MapCanvas mapCanvas;
	private MiniMapCanvas miniMapCanvas;
	private Scrollbar hs, vs;
	private JButton mapInfoBtn, cellInfoBtn, playerInfoBtn, resInfoBtn;
	private JTextArea infoArea;
	private JComboBox resourceSelector;
	private JComboBox playerSelector;
	
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 660;
	
	public GraphicInterface(GameState game) {
		setLayout(new BorderLayout(10, 10));
		setSize(WIDTH, HEIGHT);		
		
		this.game = game;
		
		JPanel mapPanel = new JPanel(new BorderLayout());
		hs = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, 1);
		vs = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 1);
		
		mapCanvas = new MapCanvas(game, hs, vs);
		mapPanel.add(BorderLayout.CENTER, mapCanvas);
		mapPanel.add(BorderLayout.EAST, vs);
		mapPanel.add(BorderLayout.SOUTH, hs);

		JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
		miniMapCanvas = new MiniMapCanvas(game, mapCanvas);
		mapCanvas.setMiniMap(miniMapCanvas);
		rightPanel.add(BorderLayout.NORTH, miniMapCanvas);
		
		JPanel controlPanel = new JPanel(new BorderLayout());
		JPanel btnPnl = new JPanel();
		mapInfoBtn = new JButton("Map Info");
		mapInfoBtn.addActionListener(this);
		btnPnl.add(mapInfoBtn);
		cellInfoBtn = new JButton("Cell Info");
		cellInfoBtn.addActionListener(this);
		btnPnl.add(cellInfoBtn);
		
		JPanel selectorPanel = new JPanel();
		Integer[] playerIDs = game.getPlayerIds().toArray(new Integer[0]);
		
		String[] resourceNames = new String[BasicResourceType.values().length + 2];
		resourceNames[0] = "ALL";
		resourceNames[1] = "NONE";
		for (int i = 0; i < resourceNames.length - 2; i++) {
			resourceNames[i + 2] = GamePolicy.getResTypeByOrdinal(i).name();
		}
		resourceSelector = new JComboBox(resourceNames);
		
		playerSelector = new JComboBox(playerIDs);
		playerInfoBtn = new JButton("Select player");
		playerInfoBtn.addActionListener(this);
		resInfoBtn = new JButton("Select resource");
		resInfoBtn.addActionListener(this);
		
		selectorPanel.add(playerSelector);
		selectorPanel.add(playerInfoBtn);
		selectorPanel.add(resourceSelector);
		selectorPanel.add(resInfoBtn);
		
		controlPanel.add(BorderLayout.NORTH, btnPnl);
		controlPanel.add(BorderLayout.SOUTH, selectorPanel);
		
		rightPanel.add(BorderLayout.SOUTH, controlPanel);

		infoArea = new JTextArea();
		infoArea.setEditable(false);
		infoProvider = new MapInfoProvider();
		JScrollPane infoPanel = new JScrollPane(infoArea);
		
		mapCanvas.setInfoArea(infoArea);
		//rightPanel.add(BorderLayout.CENTER, infoArea);
		rightPanel.add(BorderLayout.CENTER, infoPanel);
		
		add(BorderLayout.CENTER, mapPanel);
		add(BorderLayout.EAST, rightPanel);
		
        addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent we) {
                System.exit(0);
            }
        });
        
		setTitle("Environment");
		//setVisible(true);
		//printDebugInfo();
	}
	
	public void updateState() {
		crtTurn = game.getTurn();
		mapCanvas.repaint();
		miniMapCanvas.createMiniMapImage();
		miniMapCanvas.repaint();
		if (infoProvider != null && infoProvider.needsUpdate()) {
			infoArea.setText(infoProvider.getInfo());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(mapInfoBtn)) {
			//infoArea.setText(getMapInfo());
			infoProvider = new MapInfoProvider();
		}

		if (e.getSource().equals(cellInfoBtn)) {
			//infoArea.setText(mapCanvas.getCrtCellInfo());
			infoProvider = new CellInfoProvider();
		}
		
		if (e.getSource().equals(playerInfoBtn)) {
			Integer playerID = (Integer)playerSelector.getSelectedItem();
			infoProvider = new PlayerInfoProvider(playerID);
		}
		
		if (e.getSource().equals(resInfoBtn)) {
			String selectedRes = (String)resourceSelector.getSelectedItem();
			infoProvider = new ResInfoProvider(selectedRes);
			mapCanvas.setSelectedResource(selectedRes);
		}
	}
	
	private String getMapInfo() {
		String info = "";
		info += "Current turn = " + crtTurn + "\n";
		info += "Width = " + game.map.cells[0].length + "\n";
		info += "Height = " + game.map.cells.length + "\n";
		return info;
	}

	interface InfoProvider {
		public boolean needsUpdate();
		public String getInfo();
	}

	private class MapInfoProvider implements InfoProvider {
		
		@Override
		public String getInfo() {
			return getMapInfo();
		}

		@Override
		public boolean needsUpdate() {
			return true;
		}
	}
	
	private class CellInfoProvider implements InfoProvider {
		private boolean wasPrinted = false;
		
		@Override
		public String getInfo() {
			return mapCanvas.getCrtCellInfo();
		}

		@Override
		public boolean needsUpdate() {
			if (wasPrinted) {
				return false;
			}
			else {
				wasPrinted = true;
				return true;
			}
		}
	}
	
	private class PlayerInfoProvider implements InfoProvider {
		private int playerID;
		
		public PlayerInfoProvider(int playerID) {
			this.playerID = playerID;
		}
		
		@Override
		public String getInfo() {
			PlayerState ps = game.playerStates.get(playerID);
			return ps.toString();
		}

		@Override
		public boolean needsUpdate() {
			return true;
		}
	}
	
	private class ResInfoProvider implements InfoProvider {
		private String selectedRes;
		
		public ResInfoProvider(String selectedRes) {
			this.selectedRes = selectedRes;
		}
		
		@Override
		public String getInfo() {
			String info = "Selected resource: " + selectedRes + "\n";
			if (!selectedRes.equals("NONE")) {
				if (!selectedRes.equals("ALL")){
					info += "Total quantity: " + game.resourceAmountsByType.get(BasicResourceType.valueOf(selectedRes));
				}
				else {
					int amount = 0;
					for (BasicResourceType br : BasicResourceType.values()) {
						amount += game.resourceAmountsByType.get(br);
					}
					info += "Total quantity: " + amount;
				}
			}
			
			return info;
		}

		@Override
		public boolean needsUpdate() {
			return true;
		}
	}
}