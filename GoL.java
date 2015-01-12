import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GoL implements ActionListener, Runnable{
	private static Random rand = new Random();
	private int maxRows;
	private int maxCols;
	private int board[][];
	private JPanel squares[][];
	private JPanel golBoard;
	private JPanel mainPanel;
	private JFrame frame;
	private JLayeredPane layeredPane;
	volatile JLabel genLabel;
	private boolean running = false;
	private boolean alreadyCreated = false;
	private static Thread t;
	private static GoL gol;
	private static int generation;
	private static boolean resuming = false;
	private static int ssCount= 0;
	private static int alreadySS = 0;
	private static String d = "";
	private static int stopped = 1;
	
	public GoL(int x, int y) {
		this.maxRows = x;
		this.maxCols = y;
		this.board = new int[x][y];
		this.squares = new JPanel[x][y];
		genLabel  = new JLabel("Generation: 0", JLabel.CENTER);
	}
	
	public void createFrame(int x, int y) {
		frame = new JFrame("Game of Life");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		Dimension boardSize = new Dimension(900, 900);
		layeredPane = new JLayeredPane();
		frame.getContentPane().add(mainPanel);
		layeredPane.setPreferredSize(boardSize);
		mainPanel.add(layeredPane, BorderLayout.CENTER);
		
		golBoard = new JPanel();
		layeredPane.add(golBoard, JLayeredPane.DEFAULT_LAYER);
		golBoard.setLayout(new GridLayout(x, y));
		golBoard.setPreferredSize(boardSize);
		golBoard.setBounds(0, 0, boardSize.width, boardSize.height);
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		Dimension textSize = new Dimension(200, 900);
		textPanel.setPreferredSize(textSize);
		textPanel.add(genLabel, BorderLayout.NORTH);
		
		JButton startButton = new JButton("Start");
		startButton.addActionListener(this);
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(this);
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		JButton ssButton = new JButton("Screenshot");
		ssButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(Box.createVerticalStrut(10));
		buttonPanel.add(startButton);
		buttonPanel.add(Box.createVerticalStrut(10));
		buttonPanel.add(stopButton);
		buttonPanel.add(Box.createVerticalStrut(10));
		buttonPanel.add(resetButton);
		buttonPanel.add(Box.createVerticalStrut(10));
		buttonPanel.add(ssButton);
		
		//buttonPanel.add(Box.createVerticalGlue());
		textPanel.add(buttonPanel, BorderLayout.CENTER);
		mainPanel.add(textPanel, BorderLayout.EAST);
		
		initializeBoard(frame);
	}
	
	public void initializeBoard(JFrame frame) {
		for(int i = 0; i < maxRows; i++) {
			for(int ii = 0; ii < maxCols; ii++) {
				squares[i][ii] = new JPanel(new BorderLayout());
				squares[i][ii].setBackground(Color.black);
				squares[i][ii].addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent me) {
						
					}

					public void mouseEntered(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					public void mouseExited(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					public void mousePressed(MouseEvent arg0) {
						if(!alreadyCreated){
							//arg0.getComponent().setBackground(Color.cyan);
							int rando = rand.nextInt(5);
							switch(rando) {
								case 0: 
									arg0.getComponent().setBackground(Color.blue);
									break;
								case 1: 
									arg0.getComponent().setBackground(Color.magenta);
									break;
								case 2: 
									arg0.getComponent().setBackground(Color.green);
									break;
								case 3: 
									arg0.getComponent().setBackground(Color.yellow);
									break;
								case 4: 
									arg0.getComponent().setBackground(Color.cyan);
									break;	
							}
						}
						
					}

					public void mouseReleased(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}
				});
				golBoard.add(squares[i][ii]);
				board[i][ii] = 0;
			}
		}//for i
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void showBoard() {
		for (int x = 0; x < maxRows; x++) {
			for (int y = 0; y < maxCols; y++) {
				if(board[x][y] == 1) {
					//squares[x][y].setBackground(Color.magenta);
					int rando = rand.nextInt(5);
					switch(rando) {
						case 0: 
							squares[x][y].setBackground(Color.blue);
							break;
						case 1: 
							squares[x][y].setBackground(Color.magenta);
							break;
						case 2: 
							squares[x][y].setBackground(Color.green);
							break;
						case 3: 
							squares[x][y].setBackground(Color.yellow);
							break;
						case 4: 
							squares[x][y].setBackground(Color.cyan);
							break;	
					}
				}
			}
		}
	}
	
	public void updateBoard() {
		for(int x = 0; x < maxRows; x++) {
			for(int y = 0; y < maxCols; y++) {
				int neighbors = checkNeighbors(board, x, y, maxRows-1, maxCols-1);
				if(board[x][y] == 1) {
					if(neighbors < 2) { //If less than two neighbors, mark for death
						board[x][y] = 2;
					} else if(neighbors < 4) {
					} else
						board[x][y] = 2; //Over 3 neighbors; mark for death
				}//if alive
				else {
					if(neighbors == 3)
						board[x][y] = 3; //Mark for life
				}//if dead
			}
		}
		
		for (int x = 0; x < maxRows; x++) {
			for (int y = 0; y < maxCols; y++) {
				if(board[x][y] == 2) {
					board[x][y] = 0;
					squares[x][y].setBackground(Color.darkGray);
				}
				else if(board[x][y] == 3) {
					board[x][y] = 1;
					//squares[x][y].setBackground(Color.magenta);
					int rando = rand.nextInt(5);
					switch(rando) {
						case 0: 
							squares[x][y].setBackground(Color.blue);
							break;
						case 1: 
							squares[x][y].setBackground(Color.magenta);
							break;
						case 2: 
							squares[x][y].setBackground(Color.green);
							break;
						case 3: 
							squares[x][y].setBackground(Color.yellow);
							break;
						case 4: 
							squares[x][y].setBackground(Color.cyan);
							break;	
					}
				}
			}
		}
	}
	
	public int checkNeighbors(int board[][], int x, int y, int rows, int cols) {
		int neighbors = 0;
		//Check top left
		if(x != 0 && y != 0) {
			if(board[x-1][y-1] == 1 || board[x-1][y-1] == 2)
				neighbors++;
		}

		//Check top
		if(x != 0) {
			if(board[x-1][y] == 1 || board[x-1][y] == 2)
				neighbors++;
		}

		//check top right
		if(x != 0 && y < cols)
			if(board[x-1][y+1] == 1 || board[x-1][y+1] == 2)
				neighbors++;

		//check right
		if(y < cols)
			if(board[x][y+1] == 1 || board[x][y+1] == 2)
				neighbors++;

		//check bottom right
		if(x < rows && y < cols)
			if(board[x+1][y+1] == 1 || board[x+1][y+1] == 2)
				neighbors++;

		//check bottom
		if(x < rows)
			if(board[x+1][y] == 1 || board[x+1][y] == 2)
				neighbors++;
		
		//check bottom left
		if(x < rows && y != 0)
			if(board[x+1][y-1] == 1 || board[x+1][y-1] == 2)
				neighbors++;

		//check left
		if(y != 0)
			if(board[x][y-1] == 1 || board[x][y-1] == 2)
				neighbors++;

		return neighbors;
	}
	
	public static void checkClicks() {
		for(int i = 0; i < gol.maxRows; i++) {
			for(int ii = 0; ii < gol.maxCols; ii++) {
				if (gol.squares[i][ii].getBackground() != Color.black)
					gol.board[i][ii] = 1;
			}
		}
		
	}
	
	public static void takeSS(String d) {
		ssCount++;
		BufferedImage img = getScreenShot(gol.frame.getContentPane());
        JOptionPane.showMessageDialog(null,new JLabel(new ImageIcon(
        	img.getScaledInstance(
        	img.getWidth(null)/2,
        	img.getHeight(null)/2,
        	Image.SCALE_SMOOTH )
        )));
        try {
        // write the image as a PNG
        ImageIO.write(img,"png",new File(d+"/screenshot"+ssCount+".png"));
        }catch(Exception ee) {
        	ee.printStackTrace();
        }
	}
	
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("Start")) {
			stopped = 0;
			if(!alreadyCreated) {
				generation = 0;
				checkClicks();
				alreadyCreated = true;
				if(!resuming) {
					t.start();
				}else {
					t.resume();
				}
			}
			else
				t.resume();
		}
		else if (action.equals("Stop")){
			t.suspend();
			stopped = 1;
		}
		else if (action.equals("Screenshot")){
			if(alreadySS == 0) {
				DateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd HH:mm:ss");
				Date date = new Date();
				d = (dateFormat.format(date));
				d = d.replaceAll(":", ".");
				d = d.replaceAll("/", ".");
				System.out.println(d);
				new File(d).mkdir();
				alreadySS = 1;
			}
			new File(d).mkdir();
			if(alreadyCreated)
				t.suspend();
			gol.takeSS(d);
			if(alreadyCreated && stopped != 1)
				t.resume();
		}
		else {
			t.suspend();
			stopped = 1;
			alreadySS = 0;
			generation = 1;
			genLabel.setText("Generation: "+generation);
			alreadyCreated = false;
			for(int i = 0; i < gol.maxRows; i++) {
				for(int ii = 0; ii < gol.maxCols; ii++) {
					gol.squares[i][ii].setBackground(Color.black);
				    gol.board[i][ii] = 0;
				}
			}
			resuming = true;
		}
		
	}
	
	public static BufferedImage getScreenShot(Component component) {
		BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
		    // call the Component's paint method, using
		    // the Graphics object of the image.
		    component.paint( image.getGraphics() );
		    return image;
		  }
	
	public void run() {
		while(true) {
			genLabel.setText("Generation: "+generation);
			updateBoard();
			try {  
         		Thread.sleep(100);  
      		}  
      		catch (InterruptedException e) {  
      		}  
			generation++;
			
		}
	}
	
	public static void main(String[] args) {
		int x = 100; int y = 100;
		gol = new GoL(x, y);
		gol.createFrame(x, y);
		gol.showBoard();
		t = new Thread(gol);
	}


}
