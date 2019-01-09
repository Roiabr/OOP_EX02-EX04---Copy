package GUI;


import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import Coords.MyCoords;
import File_format.*;
import Geom.*;
import Map.Map;
import Robot.Play;
import GIS.*;
import Game.Block;
import Game.Fruit;
import Game.Game;
import Game.Ghost;
import Game.Packman;


public class MainWindow extends JFrame implements MouseListener 
{

	private BufferedImage myImage ,myImage1,myImage2,myImage3,myImage4;
	private Game GuiGame;
	private Play play1;
	private boolean BeforeTheGameStarted=false;
	private boolean PlaceMyPackmen=false;
	private double angle;
	private boolean GameRun=false;



	public MainWindow() 
	{
		GuiGame = new Game();
		initGUI();		
		this.addMouseListener(this); 
	}


	private void initGUI() 
	{

	
		
		JPanel northPanel = new JPanel(new GridLayout(6,2)); //deteminte the long of space beetween the panel user name 

		JMenuBar menuBar = new JMenuBar(); 
		JMenu menu1 = new JMenu("File");

		JMenu Player = new JMenu("Player");
		JMenuBar P = new JMenuBar();
		JMenuItem newgame = new JMenuItem("NewGame");
		JMenuItem save = new JMenuItem("Save Game");
		JMenuItem load = new JMenuItem("Load Game");

		JMenuItem placeme = new JMenuItem("Place me");
		JMenuItem runserver = new JMenuItem("Run manual");
		JMenuItem AutoRun = new JMenuItem("Auto Run");
		JMenuItem end = new JMenuItem("end");


		//  menuItem /////
		menu1.add(newgame);
		menu1.add(load);
	
		Player.add(end);
		Player.add(runserver);
		Player.add(placeme);
		Player.add(AutoRun);
		menuBar.add(menu1);
		menuBar.add(Player);
		newgame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("New Game");
				GuiGame.getPack().removeAll(GuiGame.getPack());
				GuiGame.getFruit().removeAll(GuiGame.getFruit());
				GuiGame.getGhost().removeAll(GuiGame.getGhost());
				GuiGame.getBlock().removeAll(GuiGame.getBlock());
				repaint();

			}
		});

		load.addActionListener(new ActionListener() {  //reset all the arraylist and loadfile csv
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("load game"); 
				loadFile();
				repaint();
			}
		});
		
		placeme.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Place The player");

				PlaceMyPackmen=true;
				BeforeTheGameStarted=true;
				repaint();

			}
		});
		end.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				play1.stop();
				String info = play1.getStatistics();
				System.out.println(info);
			}
		});
		runserver.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				play1.setIDs(311505481,205516321);
				play1.start();
				GameRun=true;
				repaint();
		}
		});


		this.setJMenuBar(menuBar);
		try {
			setMyImage(ImageIO.read(new File("Ariel1.png")));
			myImage1 = ImageIO.read(new File("packman.png"));
			myImage2 = ImageIO.read(new File("apple.png"));
			myImage3 = ImageIO.read(new File("ghost.png"));
			myImage4 = ImageIO.read(new File("apple.png"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}
	int x = -1;
	int y = -1;
	double idi=0;
	/**
	 *paint the background and create 
	 *convert coordinates to pixel and draws packmen and fruit on the screen
	 */
	public void paint(Graphics g)
	{
		g.drawImage(getMyImage(), 0, 0,getWidth(),getHeight(), this);
		
		Iterator<Ghost> iterGhost =  GuiGame.getGhost().iterator();
		Iterator<Block> iterBlock =  GuiGame.getBlock().iterator();
		while(iterBlock.hasNext()) {
			Block bl = iterBlock.next();
			Point3D start = (Map.gpsToPix(bl.getPoint_BlockStart().y(),bl.getPoint_BlockStart().x(),this.getHeight(),this.getWidth()));
			g.fillRect(start.ix(), start.iy(), (int)bl.getwidth(bl, this), (int) bl.getHeight(bl, this));
		}

		
		Iterator<Fruit> iterFruit =  GuiGame.getFruit().iterator();
		Iterator<Packman> iterPackman = GuiGame.getPack().iterator();

		while(iterFruit.hasNext()) {		//All the time paint the fruit
			Fruit f = iterFruit.next();
			Point3D p = new Point3D(Map.gpsToPix(f.getPointer_fruit().y(),f.getPointer_fruit().x(),this.getHeight(),this.getWidth()));
			g.drawImage(f.getMyImage(), (int)p.x(), (int)p.y(), 20,20,this);
		}
		while(iterPackman.hasNext()) {		//All the time paint the fruit
			Packman pac = iterPackman.next();
			Point3D p = new Point3D(Map.gpsToPix(pac.getFirstPointCor().y(),pac.getFirstPointCor().x(),this.getHeight(),this.getWidth()));
			g.drawImage(pac.getMyImage1(), (int)p.x(), (int)p.y(), 20,20,this);
		}
		while(iterGhost.hasNext()) {
			Ghost ghost = iterGhost.next();
			Point3D p = new Point3D(Map.gpsToPix(ghost.getPoint_Ghost().y(),ghost.getPoint_Ghost().x(),this.getHeight(),this.getWidth()));
			g.drawImage(ghost.getImage(), (int)p.x(), (int)p.y(), 50,50,this);
		}
		
		if(BeforeTheGameStarted==true)
		{
			Point3D p = new Point3D(Map.gpsToPix(GuiGame.getPlayer().getFirstPointCor().y(),GuiGame.getPlayer().getFirstPointCor().x(),this.getHeight(),this.getWidth()));
			g.drawImage(GuiGame.getPlayer().getMyImage(), (int)p.x(), (int)p.y(), 30,30,this);
			
		}
		if(GameRun==true)
		{
			Point3D p = new Point3D(Map.gpsToPix(GuiGame.getPlayer().getFirstPointCor().y(),GuiGame.getPlayer().getFirstPointCor().x(),this.getHeight(),this.getWidth()));
			g.drawImage(GuiGame.getPlayer().getMyImage(), (int)p.x(), (int)p.y(), 30,30,this);
			play1.rotate(angle);
			if(play1.isRuning()) 
			{
				String[] info = play1.getStatistics().split(",");
				System.out.println(info[1]);
				update();

			}
			else {
				String info = play1.getStatistics();
				System.out.println(info);	
			}
		}
	}
	public void update() {
		GuiGame = new Game(play1);
		try {

			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
	}


	/**
	 * get pixel for x,y and convert to coordinates for packmen or fruit.
	 * add to arraylist of the Game.
	 */
	public void mouseClicked(MouseEvent arg) { 
		x = arg.getX();
		y = arg.getY();
		Point3D p = new Point3D(x, y);
		p = Map.pixToGps(x, y,this.getHeight(),this.getWidth());
		if(PlaceMyPackmen==true)
		{
			play1.setInitLocation(p.x(),p.y());
			GuiGame.getPlayer().setFirstPointCor(p);
			PlaceMyPackmen=false;
			repaint();
		}

		if(play1.isRuning())
		{
			MyCoords gal = new MyCoords();
			angle=gal.azimuth_elevation_dist(GuiGame.getPlayer().getFirstPointCor(), p)[0];
			repaint();
		}
	}
	/**
	 * read all the elements from the .csv file .
	 * Inserts to the current list of packmen || fruit.
	 */
	public void loadFile() {
		play1 = readFileDialogString();
		GuiGame = new Game(play1);
		repaint();
	}
	/**
	 * read csv file 
	 * @return gis_layer that include list of elements 
	 * any elements is packmen or fruit 
	 */

	public GisLayer readFileDialog() {
		//		try read from the file
		FileDialog fd = new FileDialog(this, "Open text file", FileDialog.LOAD);
		fd.setFile("*.csv");
		fd.setDirectory("C:\\Users\\Roi Abramovitch\\eclipse-workspace\\OOP_EX02-EX04 - Copy\\data");
		fd.setFilenameFilter(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		});
		fd.setVisible(true);
		String folder = fd.getDirectory();
		String fileName = fd.getFile();
		try {
			FileReader fr = new FileReader(folder + fileName);
			BufferedReader br = new BufferedReader(fr);
			br.close();
			fr.close();
		} catch (IOException ex) {
			System.out.print("Error reading file " + ex);
			System.exit(2);
		}
		GisLayer layer =  MultiCsv.Csv2Layer(folder + fileName);
		System.out.println(layer.get_Meta_data());
		return layer;

	}
	/**
	 * read csv file and return the file in a String
	 * @return All - the String diraction for the big file
	 */
	public Play readFileDialogString() {
		Play play;
		//		try read from the file
		FileDialog fd = new FileDialog(this, "Open text file", FileDialog.LOAD);
		fd.setFile("*.csv");
		fd.setDirectory("C:\\Users\\Roi Abramovitch\\eclipse-workspace\\OOP_EX02-EX04 - Copy\\data");
		fd.setFilenameFilter(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		});
		fd.setVisible(true);
		String folder = fd.getDirectory();
		String fileName = fd.getFile();
		try {
			FileReader fr = new FileReader(folder + fileName);
			BufferedReader br = new BufferedReader(fr);
			br.close();
			fr.close();
		} catch (IOException ex) {
			System.out.print("Error reading file " + ex);
			System.exit(2);
		}
		String All = folder + fileName;
		play = new Play(All);
		return play;

	}
	/**
	 * convert the list of packmen && fruit to list elements 
	 * and return layer==csv that include elements
	 */
	public void writeFileDialog() {
		//		 try write to the file
		FileDialog fd = new FileDialog(this, "Save the text file", FileDialog.SAVE);
		fd.setFile("*.csv");
		fd.setFilenameFilter(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".csv");
			}
		});
		fd.setVisible(true);
		String folder = fd.getDirectory();
		String fileName = fd.getFile();
		try {
			FileWriter fw = new FileWriter(folder + fileName);
			PrintWriter outs = new PrintWriter(MultiCsv.Game2csv(GuiGame,folder + fileName));

			outs.close();
			fw.close();
		} catch (IOException ex) {
			System.out.print("Error writing file  " + ex);
		}


	}


	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub	
	}

	public BufferedImage getMyImage() {
		return myImage;
	}

	public void setMyImage(BufferedImage myImage) {
		this.myImage = myImage;
	}


}
