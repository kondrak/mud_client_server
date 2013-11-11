package mud_gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JApplet;
import javax.swing.DefaultListModel;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import javax.swing.JList;
import java.util.Vector;
import java.awt.Font;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import mud_networking.*;
import java.rmi.registry.*;
import java.rmi.*;
import java.text.*;
import java.util.*;


/**
 * This is the main client class responsible for running the applet and building
 * the user interface. It interacts with the remote game server by the means of
 * a MUDPlayer object called "Player".
 * 
 * @author Krzysztof Kondrak
 * @version 1.0
 *
 */

public class MUDClient extends JApplet {

	private Vector cmd_buffer = null;  //  @jve:decl-index=0:
	private MUDPlayer Player = null;  //  @jve:decl-index=0:
	private int buffer_iterator = -1;
	private int login_stage_bound = 7;
	private String locationNorth = "0";
	private String locationSouth = "0";
	private String locationEast = "0";
	private String locationWest = "0";
	private DefaultListModel ItemsModel = null;
	private DefaultListModel PlayersModel = null;
	private DefaultListModel TrackerModel = null;
	private Frame MapWindow = null;  //  @jve:decl-index=0:visual-constraint="10,630"
	
	private JPanel MainPanel = null;
	private DrawingPanel drawing_panel = null;
	private DrawingPanel the_map = null;
	private JPanel ImagePanel = null;
	private JScrollPane WorldPanel = null;
	private JTextArea world_panel = null;
	private JTextField command_line = null;
	private JPanel CompassPanel = null;
	private JButton ButtonNorth = null;
	private JButton ButtonSouth = null;
	private JButton ButtonEast = null;
	private JButton ButtonWest = null;
	private JPanel PanelRight = null;
	private JScrollPane ItemsPanel = null;
	private JList ItemsList = null;
	private JPanel MenuPanel = null;
	private JScrollPane ActionsPanel = null;
	private JList ActionsList = null;
	private JPanel PanelLeft = null;
	private JButton ButtonConnect = null;
	private JButton ButtonInventory = null;
	private JButton ButtonMap = null;
	private JTextPane LocationName = null;
	private JLabel ActionsLabel = null;
	private JLabel ItemsLabel = null;
	private JLabel MenuLabel = null;
	private JLabel CompassLabel = null;
	private JButton ButtonOK = null;
	private JLabel PlayersLabel = null;
	private JScrollPane PlayersPanel = null;
	private JList PlayersList = null;
	private JLabel TrackerLabel = null;
	private JScrollPane TrackerScrollPane = null;
	private JList TrackerList = null;
	/**
	 * This is the xxx default constructor
	 */
	public MUDClient() {
		super();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void init() {
		TrackerLabel = new JLabel();
		TrackerLabel.setBounds(new Rectangle(541, 29, 153, 17));
		TrackerLabel.setText("Player Tracker:");
		this.setSize(810, 610);
		this.setContentPane(getMainPanel());
		  MapWindow = new Frame("Map");
	      
	      Image map = getImage(getCodeBase(),"resources/map.jpg");
		    the_map =  new DrawingPanel(map);
		    the_map.setBounds(new Rectangle(4, 23, 527, 434));
		   // MainPanel.add(getImagePanel(), null);
		    MapWindow.setLayout(null);
	      
	      MapWindow.setSize(757, 461);
	      MapWindow.setBackground(new Color(238, 238, 238));
	      MapWindow.add(the_map, null);
	      MapWindow.add(TrackerLabel, null);
	      MapWindow.add(getTrackerScrollPane(), null);
	      MapWindow.addWindowListener(new java.awt.event.WindowAdapter() {   
	      	public void windowOpened(java.awt.event.WindowEvent e) {
	      		updateTrackerList();
	      		System.out.println("windowOpened()"); // TODO Auto-generated Event stub windowOpened()
	      	}
	      	public void windowClosing(java.awt.event.WindowEvent e) {
	      		MapWindow.setVisible(false);
	      		System.out.println("windowClosing()"); // TODO Auto-generated Event stub windowClosing()
	      	}
	      });
		Player = new MUDPlayer(this);
	}

	/**
	 * This method initializes MainPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (MainPanel == null) {
			MainPanel = new JPanel();
			MainPanel.setLayout(null);						
			MainPanel.add(getPanelRight(), null);
			MainPanel.add(getPanelLeft(), null);
		}
		return MainPanel;
	}

	/**
	 * This method initializes ImagePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getImagePanel() {
		if (ImagePanel == null) {
			ImagePanel = new JPanel();
			ImagePanel.setLayout(new BorderLayout());
			ImagePanel.setBounds(new Rectangle(8, 7, 350, 250));
			
			Image img = getImage(getCodeBase(),"resources/LOTR_logo.jpg");
		    
			// Create an instanceof DrawingPanel
		    drawing_panel =  new DrawingPanel(img);
		    ImagePanel.add(drawing_panel, BorderLayout.CENTER);
 
		    MainPanel.add(getImagePanel(), null);
			
		}
		return ImagePanel;
	}

	/**
	 * This method initializes WorldPanel	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getWorldPanel() {
		if (WorldPanel == null) {
			WorldPanel = new JScrollPane();
			WorldPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			WorldPanel.setLocation(new Point(9, 286));
			WorldPanel.setSize(new Dimension(350, 278));
			WorldPanel.setViewportView(getWorld_panel());
			WorldPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}
		return WorldPanel;
	}

	/**
	 * This method initializes world_panel	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getWorld_panel() {
		if (world_panel == null) {
			world_panel = new JTextArea();
			world_panel.setText("LOTR GUI v. 1.0");
			world_panel.setBackground(Color.black);
			world_panel.setFont(new Font("Dialog", Font.PLAIN, 12));
			world_panel.setForeground(Color.lightGray);
			world_panel.setEditable(false);
		}
		return world_panel;
	}

	/**
	 * This method initializes command_line	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCommand_line() {
		if (command_line == null) {
			command_line = new JTextField();
			command_line.setBackground(Color.black);
			command_line.setLocation(new Point(7, 568));
			command_line.setSize(new Dimension(298, 21));
			command_line.setEnabled(false);
			command_line.setForeground(Color.lightGray);
			command_line.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					if(command_line.getText().length() > 0)
					{					
						System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
						if(Player.getLoginStage() < login_stage_bound && Player.getLoginStage() > 0)
							acceptInput(Player.performLoginStage(command_line.getText()));
						else
						acceptInput("");
					}
				}
			});
			command_line.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {	
					
					System.out.println("keyPressed()"); // TODO Auto-generated Event stub keyPressed()
				    
					switch(e.getKeyCode())
					{
					case 40: searchBuffer(0);
							 break;
					case 38: searchBuffer(1);
							 break;
					}
				}
			});
		}
		return command_line;
	}

	/**
	 * This method initializes CompassPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCompassPanel() {
		if (CompassPanel == null) {
			CompassPanel = new JPanel();
			CompassPanel.setLayout(null);
			CompassPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			CompassPanel.setBounds(new Rectangle(219, 24, 200, 161));
			CompassPanel.add(getButtonNorth(), null);
			CompassPanel.add(getButtonSouth(), null);
			CompassPanel.add(getButtonEast(), null);
			CompassPanel.add(getButtonWest(), null);
		}
		return CompassPanel;
	}

	/**
	 * This method initializes ButtonNorth	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonNorth() {
		if (ButtonNorth == null) {
			ButtonNorth = new JButton();
			ButtonNorth.setBounds(new Rectangle(68, 8, 71, 41));
			ButtonNorth.setEnabled(false);
			ButtonNorth.setText("North");
			ButtonNorth.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Player.moveToLocation(locationNorth);
					updateCompass();
					updateLocationInfo();
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					//LocationName.setText("Inverted Logo");
					
				}
			});
		}
		return ButtonNorth;
	}

	/**
	 * This method initializes ButtonSouth	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonSouth() {
		if (ButtonSouth == null) {
			ButtonSouth = new JButton();
			ButtonSouth.setText("South");
			ButtonSouth.setLocation(new Point(67, 110));
			ButtonSouth.setEnabled(false);
			ButtonSouth.setSize(new Dimension(71, 41));
			ButtonSouth.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Player.moveToLocation(locationSouth);
					updateCompass();
					updateLocationInfo();
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return ButtonSouth;
	}

	/**
	 * This method initializes ButtonEast	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonEast() {
		if (ButtonEast == null) {
			ButtonEast = new JButton();
			ButtonEast.setBounds(new Rectangle(123, 60, 71, 41));
			ButtonEast.setEnabled(false);
			ButtonEast.setText("East");
			ButtonEast.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Player.moveToLocation(locationEast);
					updateCompass();
					updateLocationInfo();
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return ButtonEast;
	}

	/**
	 * This method initializes ButtonWest	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonWest() {
		if (ButtonWest == null) {
			ButtonWest = new JButton();
			ButtonWest.setBounds(new Rectangle(8, 59, 71, 41));
			ButtonWest.setEnabled(false);
			ButtonWest.setText("West");
			ButtonWest.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Player.moveToLocation(locationWest);
					updateCompass();
					updateLocationInfo();
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return ButtonWest;
	}

	/**
	 * This method initializes PanelRight	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPanelRight() {
		if (PanelRight == null) {
			PanelRight = new JPanel();
			PanelRight.setLayout(null);
			PanelRight.setLocation(new Point(435, 9));
			PanelRight.setSize(new Dimension(366, 596));
			PanelRight.add(getImagePanel(), null);
			PanelRight.add(getWorldPanel(), null);
			PanelRight.add(getCommand_line(), null);
			PanelRight.add(getLocationName(), null);
			PanelRight.add(getButtonOK(), null);
		}
		return PanelRight;
	}

	/**
	 * This method initializes ItemsPanel	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getItemsPanel() {
		if (ItemsPanel == null) {
			ItemsPanel = new JScrollPane();
			ItemsPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ItemsPanel.setLocation(new Point(219, 209));
			ItemsPanel.setSize(new Dimension(200, 190));
			ItemsPanel.setViewportView(getItemsList());
			ItemsPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}
		return ItemsPanel;
	}

	/**
	 * This method initializes ItemsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getItemsList() {
		if (ItemsList == null) {
			//netkoji: tymczasowe przedmioty
			//tmp_items.add("Item 1");
			//tmp_items.add("Item 2");
			ItemsModel = new DefaultListModel();
			ItemsModel.addElement("Item 1");
			ItemsModel.addElement("Item 4");
			//ItemsList = new JList(tmp_items);
			ItemsList = new JList(ItemsModel);
			ItemsList.setBackground(Color.black);
			ItemsList.setForeground(Color.lightGray);
			ItemsList.setEnabled(false);
			ItemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			ItemsList.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					System.out.println("mousePressed()"); // TODO Auto-generated Event stub mousePressed()
				  if(ItemsList.isEnabled())
				  {
					  	int actionIndex = ActionsList.getSelectedIndex();
					  	if(actionIndex != -1)
						  command_line.setText((String)ActionsList.getSelectedValue() + " " + (String)ItemsList.getSelectedValue());
					  	else
							command_line.setText("Look " + (String)ItemsList.getSelectedValue());							
				  }
				}
			});
		}
		return ItemsList;
	}

	/**
	 * This method initializes MenuPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMenuPanel() {
		if (MenuPanel == null) {
			MenuPanel = new JPanel();
			MenuPanel.setLayout(null);
			MenuPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			MenuPanel.setSize(new Dimension(203, 161));
			MenuPanel.setLocation(new Point(6, 24));
			MenuPanel.add(getButtonConnect(), null);
			MenuPanel.add(getButtonInventory(), null);
			MenuPanel.add(getButtonMap(), null);
		}
		return MenuPanel;
	}

	/**
	 * This method initializes ActionsPanel	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getActionsPanel() {
		if (ActionsPanel == null) {
			ActionsPanel = new JScrollPane();
			ActionsPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ActionsPanel.setLocation(new Point(6, 209));
			ActionsPanel.setSize(new Dimension(202, 379));
			ActionsPanel.setViewportView(getActionsList());
			ActionsPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}
		return ActionsPanel;
	}

	/**
	 * This method initializes ActionsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getActionsList() {
		if (ActionsList == null) {
			Vector tmp_actions = new Vector();
			// netkoji: tymczasowe akcje
			tmp_actions.add("Drop");
			tmp_actions.add("Give");
			tmp_actions.add("Look");
			tmp_actions.add("Say");
			tmp_actions.add("Take");
			ActionsList = new JList(tmp_actions);
			ActionsList.setBackground(Color.black);
			ActionsList.setForeground(Color.lightGray);
			ActionsList.setEnabled(false);
			ActionsList.setFont(new Font("Dialog", Font.BOLD, 12));
			ActionsList.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					System.out.println("mousePressed()"); // TODO Auto-generated Event stub mousePressed()
				if(ActionsList.isEnabled())
					command_line.setText((String)ActionsList.getSelectedValue());
				}
			});
		}
		return ActionsList;
	}

	/**
	 * This method initializes PanelLeft	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPanelLeft() {
		if (PanelLeft == null) {
			PlayersLabel = new JLabel();
			PlayersLabel.setText("Visible Players:");
			PlayersLabel.setLocation(new Point(222, 406));
			PlayersLabel.setSize(new Dimension(140, 12));
			CompassLabel = new JLabel();
			CompassLabel.setText("Compass");
			CompassLabel.setLocation(new Point(222, 7));
			CompassLabel.setSize(new Dimension(148, 14));
			MenuLabel = new JLabel();
			MenuLabel.setText("Menu");
			MenuLabel.setLocation(new Point(9, 7));
			MenuLabel.setSize(new Dimension(123, 16));
			ItemsLabel = new JLabel();
			ItemsLabel.setText("Visible Items:");
			ItemsLabel.setLocation(new Point(222, 192));
			ItemsLabel.setSize(new Dimension(172, 16));
			ActionsLabel = new JLabel();
			ActionsLabel.setText("Action List:");
			ActionsLabel.setLocation(new Point(9, 192));
			ActionsLabel.setSize(new Dimension(159, 14));
			PanelLeft = new JPanel();
			PanelLeft.setLayout(null);
			PanelLeft.setLocation(new Point(4, 9));
			PanelLeft.setSize(new Dimension(426, 596));
			PanelLeft.add(getItemsPanel(), null);
			PanelLeft.add(getMenuPanel(), null);
			PanelLeft.add(getActionsPanel(), null);
			PanelLeft.add(getCompassPanel(), null);
			PanelLeft.add(ActionsLabel, null);
			PanelLeft.add(ItemsLabel, null);
			PanelLeft.add(MenuLabel, null);
			PanelLeft.add(CompassLabel, null);
			PanelLeft.add(PlayersLabel, null);
			PanelLeft.add(getPlayersPanel(), null);
		}
		return PanelLeft;
	}

	/**
	 * This method initializes ButtonConnect	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonConnect() {
		if (ButtonConnect == null) {
			ButtonConnect = new JButton();
			ButtonConnect.setBounds(new Rectangle(37, 11, 129, 39));
			ButtonConnect.setText("Connect");
			ButtonConnect.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
					if(Player.isConnected() == false)
					{ 
						if(Player.Connect())
					      {
						     world_panel.setText(Player.getWelcomeText());
						     ButtonConnect.setText("Disconnect");
					     }
					}
					else
					{
						 Player.Disconnect();
					     world_panel.setText("Disconnected");
						 ButtonConnect.setText("Connect");
					}
				}
			});
		}
		return ButtonConnect;
	}

	/**
	 * This method initializes ButtonInventory	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonInventory() {
		if (ButtonInventory == null) {
			ButtonInventory = new JButton();
			ButtonInventory.setText("Inventory");
			ButtonInventory.setSize(new Dimension(129, 39));
			ButtonInventory.setEnabled(false);
			ButtonInventory.setLocation(new Point(38, 61));
			ButtonInventory.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Player.getInventory();
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return ButtonInventory;
	}

	/**
	 * This method initializes ButtonMap	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonMap() {
		if (ButtonMap == null) {
			ButtonMap = new JButton();
			ButtonMap.setText("Map");
			ButtonMap.setSize(new Dimension(129, 39));
			ButtonMap.setEnabled(false);
			ButtonMap.setLocation(new Point(38, 110));
			ButtonMap.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				      MapWindow.setVisible(!MapWindow.isVisible());
					System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
				}
			});
		}
		return ButtonMap;
	}

	/**
	 * This method initializes LocationName	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getLocationName() {
		if (LocationName == null) {
			LocationName = new JTextPane();
			LocationName.setFont(new Font("Dialog", Font.BOLD, 12));
			LocationName.setEditable(false);
			LocationName.setForeground(Color.white);
			LocationName.setBackground(Color.black);
			LocationName.setLocation(new Point(10, 257));
			LocationName.setSize(new Dimension(348, 22));
			LocationName.setText("Location Name");
		}
		return LocationName;
	}

	/**
	 * This method paints the location pictures	
	 * 	
	 * @return void	
	 */

	public void updateLocationInfo() {
				
		drawing_panel.RedrawPicture(getImage(getCodeBase(),"resources/" + Player.getLocationPicture()));
		LocationName.setText(Player.getLocationName());
		world_panel.setText(world_panel.getText() + "\n> " + LocationName.getText() + "\n\n" + Player.getLocationDescription());

		updateItemList();
		updatePlayersList();
		updateTrackerList();

		MainPanel.repaint();
	}

	/**
	 * This method initializes ButtonOK	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getButtonOK() {
		if (ButtonOK == null) {
			ButtonOK = new JButton();
			ButtonOK.setText("OK");
			ButtonOK.setSize(new Dimension(51, 21));
			ButtonOK.setEnabled(false);
			ButtonOK.setLocation(new Point(307, 568));
			ButtonOK.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//Player.getDesc();
					if(command_line.getText().length() > 0)
					{				   
						System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
						if(Player.getLoginStage() < login_stage_bound && Player.getLoginStage() > 0)	
							acceptInput(Player.performLoginStage(command_line.getText()));
						else
						acceptInput("");
					}
				}
			});
		}
		return ButtonOK;
	}

	/**
	 * This method updates the command line buffer with new data
	 * 	
	 * @return void	
	 */
	private void updateBuffer() {
		if(cmd_buffer == null)
			cmd_buffer = new Vector();
		
		cmd_buffer.add((String)command_line.getText());
		buffer_iterator = -1;
	} // updateBuffer


	/**
	 * This method searches the command line buffer in the reverse order
	 * 
	 * @param mode The directon of searching the buffer. 0 - forward; 1 - backward.
	 * 	
	 * @return void	
	 */
	private void searchBuffer(int mode) {
	  if(cmd_buffer != null)
	  {   

		  switch(mode)
		  {
		  	case 0: buffer_iterator++;
		  			if(buffer_iterator == cmd_buffer.size())
		  			  buffer_iterator = 0;
		  			break;
		  	case 1: buffer_iterator--;
		  			break;
		  }
		  		  
		  if(buffer_iterator < 0)
		    {  
			  switch(mode)
			  {
			  	case 0: buffer_iterator = 0;
			  			break;
			  	case 1: buffer_iterator = cmd_buffer.size()-1;
			  			break;
			  }
		    }
		  
		  command_line.setText((String)cmd_buffer.get(buffer_iterator));

	  }
	} // searchBuffer

	/**
	 * This method updates the the main world panel with the user's input.
	 * 
	 * @param input - Set to a value only during the connection process in order to
	 * determine whether users wants to create a character, load it from the database
	 * etc. Otherwise it's set to a null string and by default it updates the world
	 * panel with user's input from the command line.
	 * 
	 * @return void	
	 */
	
	private void acceptInput(String input) {
		if(input.length() > 0)
			world_panel.setText(world_panel.getText() + "\n> " + input);
		else
		{
			world_panel.setText(world_panel.getText() + "\n> " + command_line.getText());
			if(verifyCommand(command_line.getText()))
			{
				updateBuffer();
				performCommand(command_line.getText());
			}
			else
			  world_panel.setText(world_panel.getText() + "\nInvalid command. Try again.");
		}
		command_line.setText("");
	}
	
	/**
	 * This method checks whether the command given by the user is correct.
	 * 
	 * @param command - the command to be checked 
	 * 
	 * @return boolean
	 */	
	
	private boolean verifyCommand(String command) {
		
		String userInput = command.toLowerCase() + " "; //in case there is no space given
		String checkedCommand = userInput.substring(0, userInput.indexOf(" "));
		String commands[] = {"take", "look", "drop", "say", "give"};
		
		for(int i=0; i<commands.length; i++)
			if(checkedCommand.equals(commands[i]))
				return true;
		
		return false;
	}
	
	/**
	 * This method executes the command given by the user is correct.
	 * 
	 * @param command - the command to be executed 
	 * 
	 * @return void
	 */	
	
	private void performCommand(String command) {
		
		String userInput = command.toLowerCase() + " ";
		String action = userInput.substring(0, userInput.indexOf(" "));
		String action_subject = userInput.substring(userInput.indexOf(" "));
		action_subject = action_subject.trim();
		
			if(action_subject.length() > 1)
				Player.performCommand(action, action_subject);
			else
				world_panel.setText(world_panel.getText() + "\nWhat do you want to " + action + "?");			
	}

	/**
	 * This method enables/disables all the input buttons on the screen, depending on the
	 * value given as parameter.
	 * 
	 * @param state enable or disable the controls
	 * 
	 * @return void	
	 */
	
	public void toggleGameInput(boolean state) {
		 ActionsList.setEnabled(state);
	     ItemsList.setEnabled(state);
	     PlayersList.setEnabled(state);
	     ButtonInventory.setEnabled(state);
	     ButtonMap.setEnabled(state);
	     ButtonNorth.setEnabled(state);
	     ButtonSouth.setEnabled(state);
	     ButtonEast.setEnabled(state);
	     ButtonWest.setEnabled(state);
	}

	/**
	 * This method enables/disables the command line and OK button, depending on the
	 * value given as parameter.
	 * 
	 * @param state enable or disable the controls
	 * 
	 * @return void	
	 */
	
	public void toggleBasicInput(boolean state) {
	     ButtonOK.setEnabled(state);
	     command_line.setEnabled(state);
	}
	
	/**
	 * This method updates the World Panel with any relevant message received from the game server.
	 * 
	 * @param message the server message to be displayed on the world panel
	 * 
	 * @return void	
	 */
	
	public void updateWorldPanel(String message) {
		if(message.length() > 0)
			world_panel.setText(world_panel.getText() + "\n" + message);		
	} //updateWorldPanel

	/**
	 * This method updates the compass according to the player's new position.
	 * 
	 * @return void	
	 */
	
	public void updateCompass() {
		String adjacentLocations[] = Player.getAdjacentLocations();
		locationNorth = adjacentLocations[0];
		locationSouth = adjacentLocations[1];
		locationEast = adjacentLocations[2];
		locationWest = adjacentLocations[3];
		
		if(locationNorth.equals("0"))
			ButtonNorth.setEnabled(false);
		else
			ButtonNorth.setEnabled(true);

		if(locationSouth.equals("0"))
			ButtonSouth.setEnabled(false);
		else
			ButtonSouth.setEnabled(true);
		
		if(locationEast.equals("0"))
			ButtonEast.setEnabled(false);
		else
			ButtonEast.setEnabled(true);
		
		if(locationWest.equals("0"))
			ButtonWest.setEnabled(false);
		else
			ButtonWest.setEnabled(true);
		
	} //updateCompass()

	/**
	 * This method updates the list of visible items in the given area.
	 * 
	 * @return void	
	 */
	
	public void updateItemList() {
		
		Vector LocationItems = Player.getLocationItems();
		Vector InventoryItems = Player.getInventoryItemsList();

		ItemsModel.removeAllElements();
		for(int i=0; i<LocationItems.size(); i++)
			ItemsModel.addElement(LocationItems.get(i));

		for(int i=0; i<InventoryItems.size(); i++)
			ItemsModel.addElement(InventoryItems.get(i));
		
		
	} //updateItemList()

	/**
	 * This method updates the list of visible players in the given area.
	 * 
	 * @return void	
	 */
	
	public void updatePlayersList() {
		
		Vector LocationPlayers = Player.getLocationPlayers();

		PlayersModel.removeAllElements();
		for(int i=0; i<LocationPlayers.size(); i++)
			PlayersModel.addElement(LocationPlayers.get(i));
		
	} //updateItemList()

	/**
	 * This method updates the list of player positions on the map.
	 * 
	 * @return void	
	 */
	
	public void updateTrackerList() {
		
		Vector PlayerLocations = Player.getPlayerPositions();

		TrackerModel.removeAllElements();
		for(int i=0; i<PlayerLocations.size(); i++)
			TrackerModel.addElement(PlayerLocations.get(i));
	
		
	} //updateTrackerList()
	
	/**
	 * This method initializes PlayersPanel	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getPlayersPanel() {
		if (PlayersPanel == null) {
			PlayersPanel = new JScrollPane();
			PlayersPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			PlayersPanel.setSize(new Dimension(200, 167));
			PlayersPanel.setLocation(new Point(219, 421));
			PlayersPanel.setViewportView(getPlayersList());
			PlayersPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return PlayersPanel;
	}

	/**
	 * This method initializes PlayersList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getPlayersList() {
		if (
			PlayersList == null) {
			
			PlayersModel = new DefaultListModel();

			PlayersList = new JList(PlayersModel);
			
			//PlayersList = new JList();
			PlayersList.setForeground(Color.lightGray);
			PlayersList.setBounds(new Rectangle(0, 0, 182, 187));
			PlayersList.setEnabled(false);
			PlayersList.setBackground(Color.black);
			PlayersList.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					System.out.println("mousePressed()"); // TODO Auto-generated Event stub mousePressed()
				if(PlayersList.isEnabled())
				{
				  int actionIndex = ActionsList.getSelectedIndex();
				  	if(actionIndex != -1)
						command_line.setText((String)ActionsList.getSelectedValue() + " " + (String)PlayersList.getSelectedValue());					
				  	else
						command_line.setText("Look " + (String)PlayersList.getSelectedValue());					
				  		
				}

				}
			});
		}
		return PlayersList;
	}

	/**
	 * This method initializes TrackerScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getTrackerScrollPane() {
		if (TrackerScrollPane == null) {
			TrackerScrollPane = new JScrollPane();
			TrackerScrollPane.setBounds(new Rectangle(541, 53, 208, 399));
			TrackerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			TrackerScrollPane.setForeground(Color.lightGray);
			TrackerScrollPane.setViewportView(getTrackerList());
		}
		return TrackerScrollPane;
	}

	/**
	 * This method initializes TrackerList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getTrackerList() {
		if (TrackerList == null) {
			
			TrackerModel = new DefaultListModel();

			TrackerList = new JList(TrackerModel);
			TrackerList.setBackground(Color.black);
			TrackerList.setForeground(Color.lightGray);
		}
		return TrackerList;
	}
	
}
