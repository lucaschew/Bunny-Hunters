import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.applet.*;
import javax.swing.*;
import java.util.*;
import java.awt.font.*;

//Run in 1051x720 Applet

public class Bunny_Hunter extends Applet implements Runnable, KeyListener, MouseListener {
	
	Integer bullet[][] = new Integer[100][5];
	//0 - ON(0) or OFF(1)
	//1 - Direction
		//0 = Up
		//1 = Right
		//2 = Down
		//3 = Left
		//4 = NE
		//5 = SE
		//6 = SW
		//7 = NW
	//2 - X
	//3 - Y
	
	Integer enemy[][] = new Integer[300][3];
	//0 - ON(0) or OFF(1)
	//1 - X
	//2 - Y
	
	Boolean Pmove[] = new Boolean[4];
	Boolean shoot[] = new Boolean[4];
	//0 - Up
	//1 - Right
	//2 - Down
	//3 - Left
	
	Thread T = null;
	int speedE, meS, meX, meY, lives, bulletS, previous, etimer, ecounter, bt, powerupT, powerX, powerY, limit;
	int begin, end;
	static int points, hs, hold, pupC;
	Graphics OffG;
	Image Screen, background, player[], ball[], zoom, eLife, board, evil, zoomy, OP;
	Icon EL, ER;
	Font f1,f2;
	
	AudioClip opM;
	
	//powerupT = 0 (nothing) 1 (heart) 2 (zoom)
	
	//-------------------------------------------------------------------------
	
	public void init()	{
		
		try {
			Scanner sc = new Scanner(new FileReader("hs.rbss"));
			hs = sc.nextInt();
			sc.close();
		} catch (FileNotFoundException e) {}
		
		addKeyListener(this);
		addMouseListener(this);
		
		for (int i = 0; i < 100; i++)	{
			bullet[i][0] = 1;
			enemy[i][0] = 1;
		}
		for (int i = 0; i < 4; i++) {
			Arrays.fill(Pmove, false);
			Arrays.fill(shoot, false);
		}
		
		Screen = createImage(1320, 720);
		OffG = Screen.getGraphics();
		board = (getImage(getCodeBase(), "Score.png"));
		evil = getImage(getCodeBase(), "ER.png");
		EL = new ImageIcon("EL.gif");
		ER = new ImageIcon("ER.gif");
		player = new Image[] {getImage(getCodeBase(), "Boku_R.png"), getImage(getCodeBase(), "Boku_L.png")};
		ball = new Image[] {getImage(getCodeBase(), "Pew_U.png"), getImage(getCodeBase(), "Pew_R.png"), getImage(getCodeBase(), "Pew_D.png"),getImage(getCodeBase(), "Pew_L.png")};
		background = getImage(getCodeBase(), "background.png");
		zoom = getImage(getCodeBase(), "Zoomyy.png");
		zoomy = getImage(getCodeBase(), "Zoom.png");
		eLife = getImage(getCodeBase(), "heart.png");
		OP = getImage(getCodeBase(), "OP.png");
		
		opM = getAudioClip(getCodeBase(), "OP.wav");
		
		previous = 0;
		ecounter = -40;
		etimer = 100; //1000 ms / 16 ms delay / etimer = spawns per a second (Base 3)
		bulletS = 8;
		points = 0;
		lives = 3;
		meS = 4; 
		speedE = 1;
		meX = 360;
		meY = 360;
		bt = 0;
		points = 0;
		pupC = 0;
		limit = 5;
		hold = 0;
		begin = 0;

		
		f1 = new Font("Times New Roman", Font.PLAIN, 30);
		f2 = new Font("Times New Roman", Font.PLAIN, 50);
		
		
		
		//del(400);
		
		System.out.println("init");
		
		opM.loop();
		
		start();
		
	}
	
	//-------------------------------------------------------------------------
	
	public void start()	{
		
		if (T==null) {
			T = new Thread(this);
			T.start();
		}
		
	}
	
	//-------------------------------------------------------------------------
	
	public void stop()	{
		
		if (T != null)
			T = null;
		
	}
	
	//-------------------------------------------------------------------------
	
	public void paint(Graphics g)	{
		g.drawImage(Screen, 0, 0, this);
	}
	
	//-------------------------------------------------------------------------
	
	public void update(Graphics g)	{
		
		paint(g);
		
	}
	
	//-------------------------------------------------------------------------
	
	public boolean coll (Image pic1, int x1, int y1, Image pic2, int x2, int y2) {
		
		Shape s1;
		s1 = new Ellipse2D.Double(x1, y1, pic1.getWidth(this), pic1.getHeight(this));
		return s1.intersects(x2, y2, pic2.getWidth(this), pic2.getHeight(this));
		
	}
	
	//-------------------------------------------------------------------------
	
	public void mouseClicked(MouseEvent domo) {
		// TODO Auto-generated method stub
		
		int mX = domo.getX();
		int mY = domo.getY();
		System.out.println(begin);
		
		if (mX >= 440 && mX <= 615 && mY >= 445 && mY <= 500 && begin == 0)
			begin = 1;
		
	}
	
	//-------------------------------------------------------------------------
	
	public void keyPressed(KeyEvent e)	{

		
		if (e.getKeyCode() == KeyEvent.VK_W)
			Pmove[0] = true;
		if (e.getKeyCode() == KeyEvent.VK_D)
			Pmove[1] = true;
		if (e.getKeyCode() == KeyEvent.VK_S)
			Pmove[2] = true;
		if (e.getKeyCode() == KeyEvent.VK_A)
			Pmove[3] = true;
		
		if (e.getKeyCode() == KeyEvent.VK_UP)
			shoot[0] = true;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			shoot[1] = true;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			shoot[2] = true;
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			shoot[3] = true;
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE && hold == 1) {
			
			hold = 0;
			pupC = 800;
			powerupT = 0;
		}
		
		//System.out.println("hi");
		
	}

	
	//-------------------------------------------------------------------------
	
	public void keyReleased(KeyEvent e)	{


		if (e.getKeyCode() == KeyEvent.VK_W)
			Pmove[0] = false;
		if (e.getKeyCode() == KeyEvent.VK_D)
			Pmove[1] = false;
		if (e.getKeyCode() == KeyEvent.VK_S)
			Pmove[2] = false;
		if (e.getKeyCode() == KeyEvent.VK_A)
			Pmove[3] = false;
		
		if (e.getKeyCode() == KeyEvent.VK_UP)
			shoot[0] = false;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			shoot[1] = false;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			shoot[2] = false;
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			shoot[3] = false;
		
		//System.out.println("bye");
	}
	
	//-------------------------------------------------------------------------
	
	public void del(int time)	{
		
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}
	
	//-------------------------------------------------------------------------
	
	//0 - ON(0) of OFF (1)
	//1 - Direction
		//0 = Up
		//1 = Right
		//2 = Down
		//3 = Left
		//4 = NE
		//5 = SE
		//6 = SW
		//7 = NW
	//2 - X
	//3 - Y
	
	//0 - ON(0) or OFF(1)
	//1 - X
	//2 - Y
	
	public void run()	{
		
		while (begin == 0)	{
			
			OffG.drawImage(OP, 0,0, this);
			repaint();
			
			del(33);
			
			
		}
		
		OffG.drawImage(background, 0, 0, this);
		OffG.drawImage(board, 720, 0, this);
		OffG.drawImage(player[0], meX, meY, this);
		
		while (lives != 0) {
			
			OffG.drawImage(background, 0,0 ,this);
			
			if (powerupT == 1)
				OffG.drawImage(eLife, powerX, powerY, this);
			else if (powerupT == 2)
				OffG.drawImage(zoom, powerX, powerY, this);
			
			
																		//Player Movement
			
			if (Pmove[0] && Pmove[1])	{			//NE
				meX += meS;
				meY -= meS;
				previous = 0;
			}	else if (Pmove[0] && Pmove[3])	{	//NW
				meX -= meS;
				meY -= meS;
				previous = 1;
			} else if (Pmove[2] && Pmove[1])	{	//SE
				meX += meS;
				meY += meS;
				previous = 0;
			} else if (Pmove[2] && Pmove[3])	{	//SW
				meX -= meS;
				meY += meS;
				previous = 1;
			} else if (Pmove[0])	{				//N
				meY -= meS;
			} else if (Pmove[1])	{				//E
				meX += meS;
				previous = 0;
			} else if (Pmove[2])	{				//S
				meY += meS;
			} else if (Pmove[3])	{				//W
				meX -= meS;
				previous = 1;
			} 
			
			bt++;
			for (int i = 0; i < 100; i++) {								//Bullet Moving
				
				if (bullet[i][0] == 0)	{
					
					if (bullet[i][1] == 0)	{	//Up
						bullet[i][3] -= bulletS;
						OffG.drawImage(ball[0], bullet[i][2], bullet[i][3], this);
						bullet[i][4] = 0;
					}
					else if (bullet[i][1] == 1)	{	//Right
						bullet[i][2] += bulletS;
						OffG.drawImage(ball[1], bullet[i][2], bullet[i][3], this);
						bullet[i][4] = 1;
						previous = 0;
					}
					else if (bullet[i][1] == 2)	{	//Down
						bullet[i][3] += bulletS;
						OffG.drawImage(ball[2], bullet[i][2], bullet[i][3], this);
						bullet[i][4] = 2;
					}
					else if (bullet[i][1] == 3)	{	//Left
						bullet[i][2] -= bulletS;
						OffG.drawImage(ball[3], bullet[i][2], bullet[i][3], this);
						bullet[i][4] = 3;
						previous = 1;
					}	
					else if (bullet[i][1] == 4)	{	//Up + Right
						
						bullet[i][3] -= bulletS;
						bullet[i][2] += bulletS;
						OffG.drawImage(ball[1], bullet[i][2], bullet[i][3], this);
						bullet[i][4] = 1;
						previous = 0;
					}
					
					else if (bullet[i][1] == 5)	{	//Down + Right
						
						bullet[i][3] += bulletS;
						bullet[i][2] += bulletS;
						OffG.drawImage(ball[1], bullet[i][2], bullet[i][3], this);
						bullet[i][4] = 1;
						previous = 0;
					}
					
					else if (bullet[i][1] == 6)	{	//Down + Left
						
						bullet[i][3] += bulletS;
						bullet[i][2] -= bulletS;
						OffG.drawImage(ball[3], bullet[i][2], bullet[i][3], this);
						bullet[i][4] = 3;
						previous = 1;
					}
					
					else if (bullet[i][1] == 7)	{	//Up + Left
						
						bullet[i][3] -= bulletS;
						bullet[i][2] -= bulletS;
						OffG.drawImage(ball[3], bullet[i][2], bullet[i][3], this);
						bullet[i][4] = 3;
						previous = 1;
					}
					
					
					if (bullet[i][2] >= 770 || bullet[i][2] <= -50 || bullet[i][3] >= 770 || bullet[i][3] <= -50)	{
					
						bullet[i][2] = null;
						bullet[i][3] = null;
						bullet[i][1] = null;
						bullet[i][0] = 1;
						bullet[i][4] = null;
						
					}
					
				}
				if (bt > limit)
					if (bullet[i][0] == 1)										//Bullet Initialize
						
						if (shoot[0] && shoot[1])	{			//NE
							bullet[i][0] = 0;
							bullet[i][1] = 4;
							bullet[i][2] = meX;
							bullet[i][3] = meY;
							bullet[i][4] = 1;
							bt = 0;
							break;
						}	else if (shoot[0] && shoot[3])	{	//NW
							bullet[i][0] = 0;
							bullet[i][1] = 7;
							bullet[i][2] = meX;
							bullet[i][3] = meY;
							bullet[i][4] = 3;
							bt = 0;
							break;
						} else if (shoot[2] && shoot[1])	{	//SE
							bullet[i][0] = 0;
							bullet[i][1] = 5;
							bullet[i][2] = meX;
							bullet[i][3] = meY;
							bullet[i][4] = 1;
							bt = 0;
							break;
						} else if (shoot[2] && shoot[3])	{	//SW
							bullet[i][0] = 0;
							bullet[i][1] = 6;
							bullet[i][2] = meX;
							bullet[i][3] = meY;
							bullet[i][4] = 3;
							bt = 0;
							break;
						} else if (shoot[0])	{				//N
							bullet[i][0] = 0;
							bullet[i][1] = 0;
							bullet[i][2] = meX;
							bullet[i][3] = meY;
							bullet[i][4] = 0;
							bt = 0;
							break;
						} else if (shoot[1])	{				//E
							bullet[i][0] = 0;
							bullet[i][1] = 1;
							bullet[i][2] = meX+35;
							bullet[i][3] = meY+10;
							bullet[i][4] = 1;
							bt = 0;
							break;
						} else if (shoot[2])	{				//S
							bullet[i][0] = 0;
							bullet[i][1] = 2;
							bullet[i][2] = meX;
							bullet[i][3] = meY;
							bullet[i][4] = 2;
							bt = 0;
							break;
						} else if (shoot[3])	{				//W
							bullet[i][0] = 0;
							bullet[i][1] = 3;
							bullet[i][2] = meX-7;
							bullet[i][3] = meY+10;
							bullet[i][4] = 3;
							bt = 0;
							break;
						} 
				
				}
			
			if (meX >= 640)									//Player Reposition
				meX = 640;
			if (meX <= 40)
				meX = 40;
			if (meY >= 630)
				meY = 630;
			if (meY <= 50)
				meY = 50;
			
			for (int i = 0; i < 100; i++) {
				
				for (int z = 0; z < 100; z++)	{		///Collisions
				
					if (enemy[i][0] == 0 && bullet[z][0] == 0)
						if (coll(evil, enemy[i][1], enemy[i][2], ball[bullet[z][4]], bullet[z][2], bullet[z][3]))	{
							
							enemy[i][0] = 1; enemy[i][1] = null; enemy[i][2] = null;
							Arrays.fill(bullet[z], null);
							bullet[z][0] = 1;
							points += 5;
						}
					
					if (enemy[i][0] == 0)
						if (coll(evil, enemy[i][1], enemy[i][2], player[0], meX, meY))	{
							
							ecounter = -80;
							lives--;
							
							for (int ded = 0; ded < 300; ded++)	{
								
								if (ded < 100)	{
									Arrays.fill(bullet[ded], null);
									bullet[ded][0] = 1;
								}
								enemy[ded][0] = 1; enemy[ded][1] = null; enemy[ded][2] = null;
								
							}
							
							
						}
					
				}
		
				
				
				if (enemy[i][0] == 0)	{										//Enemy Mover

					int d = 0;
					
					if (enemy[i][1] > meX+20)	{
						enemy[i][1] -= speedE;
						d = 1;
					}
					if (enemy[i][1] < meX+20)	{
						enemy[i][1] += speedE;
						d = 0;
					}
					if (enemy[i][2] > meY+20)	{
						enemy[i][2] -= speedE;
					}
					if (enemy[i][2] < meY+20)	{
						enemy[i][2] += speedE;
					}
					
					if (d == 0)
						ER.paintIcon(this, OffG, enemy[i][1], enemy[i][2]);
					else
						EL.paintIcon(this, OffG, enemy[i][1], enemy[i][2]);
				}
					
				}
																		//ENEMY SPAWNER
			
			ecounter++;
			if (ecounter >= etimer)	{
				
				for (int i = 0; i < 100; i++)	{
					
					if (enemy[i][0] == 1 )	{
						
						enemy[i][0] = 0;
					
						int domo = (int) Math.round(Math.random()*3);
						int r = (int) Math.round(Math.random()*710);
				
						if (domo == 0)	{	//Top Spawn
							//OffG.drawImage(evil[0], r, -20, this);
							ER.paintIcon(this, OffG, r, -20);
							enemy[i][1] = r;
							enemy[i][2] = -20;
							break;
						}
						if (domo == 1)	{	//Right Spawn
							EL.paintIcon(this, OffG, 740, r);
							enemy[i][1] = 740;
							enemy[i][2] = r;
							break;
						}
						if (domo == 2)	{	//Bot Spawn
							//OffG.drawImage(evil[1], r, 740, this);
							EL.paintIcon(this, OffG, r, 740);
							enemy[i][1] = r;
							enemy[i][2] = 740;
							break;
						}
						if (domo == 3)	{	//Left Spawn
							//OffG.drawImage(evil[0], -20, r, this);
							ER.paintIcon(this, OffG, -20, r);
							enemy[i][1] = -20;
							enemy[i][2] = r;
							break;
						}
						
					}

				}
				ecounter = 0;
				
				if (powerupT == 0 && hold != 1)		{														//POWERUPS
					
					int domo = (int) Math.round(Math.random()*100);
				
							//76;
							
					if (domo == 43)	{
						powerupT = 1;
						powerX = (int) (Math.round(Math.random()*650) + 30);
						powerY = (int) (Math.round(Math.random()*650) + 30);
					}
					if (domo == 76)	{
						powerupT = 2;
						powerX = (int) (Math.round(Math.random()*650) + 30);
						powerY = (int) (Math.round(Math.random()*650) + 30);
					}
				}
				
			}
			
			if (pupC != 0)	{
				pupC--;
				meS = 6;
				limit = 3;
				
			}
			
			if (coll(player[0], meX, meY, eLife, powerX, powerY) && powerupT == 1)	{
				
				lives++;
				powerupT = 0;
			}	else if (coll(player[0], meX, meY, zoom, powerX, powerY) && powerupT == 2)	{
				hold = 1;
				powerupT = 0;
			}
				
			
			
			
																				//ing Finals
			

			OffG.setFont(f1);
			OffG.drawImage(player[previous], meX, meY, this);
			OffG.drawImage(board, 720, 0, this);
			
			if (hold == 1)
				OffG.drawImage(zoomy, 817, 40, this);

			OffG.drawString(Integer.toString(points), 770, 450);
			OffG.drawString(Integer.toString(hs), 770, 650);
			OffG.setFont(f2);
			OffG.drawString(Integer.toString(lives), 885, 277);
			repaint();
			del(16);
			etimer = 50-(points/20);
			if (points <= 100)
				speedE = 1;
			if (points > 100)
				speedE =2;
			if (points > 200)
				speedE =3;
			if (points > 300)
				speedE = 4;
			if (points > 400)
				speedE = 4;
			
		}

		restart();

	}
		
		public void restart()	{
			
			stop();
			
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("hs.rbss", false)));
				
				if (points > hs)
					out.write(Integer.toString(points));
				else
					out.write(Integer.toString(hs));
				out.close();
				
				
			}	catch (IOException e)	{}
			
			del(3000);
			
			begin = 0;
			lives = 3;
			try {
				Scanner sc = new Scanner(new FileReader("hs.rbss"));
				hs = sc.nextInt();
				sc.close();
			} catch (FileNotFoundException e) {}
			points = 0;
			start();
			
			
			
		}


	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
