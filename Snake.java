package tp7_SnakeGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Snake extends JFrame {
	
	int nbCaseX=50;
	int nbCaseY=25;
	
	int largeur=20 ;
	int vitesse=100;//en ms the bigger it is the slower ,cuz in timer it plays teh role of delay
	int direction=1;//1:right 2:down 3:left 4:up
	int marge=50;//if we wanna change it
	Color bg=Color.black;
	Color couleurtete=Color.red;
	Color couleurcorps=Color.yellow;
	Color CouleurRepas=Color.green;
	Color CouleurObstacle=Color.pink;
	ArrayList<Cellule> serpent;
	ArrayList<Cellule> obstacles;
	Cellule tete= new Cellule(5,5,couleurtete);
	Cellule repas;
	JPanel notrePanneau;
	Timer horloge;
	
	void initialiserSerpent() {
		serpent= new ArrayList<Cellule>();
		int centreX=nbCaseX/2;
		int centreY=nbCaseY/2;
		serpent.add(new Cellule(centreX,centreY,couleurtete));
		serpent.add(new Cellule(centreX-1,centreY,couleurcorps));
		serpent.add(new Cellule(centreX-2,centreY,couleurcorps));
		serpent.add(new Cellule(centreX-3,centreY,couleurcorps));
		serpent.add(new Cellule(centreX-4,centreY,couleurcorps));
		serpent.add(new Cellule(centreX-5,centreY,couleurcorps));
		
	}
	void genererObstacle() {
		obstacles=new ArrayList<Cellule>();
		int x1=nbCaseX/4;
		int y1=nbCaseY/3;
		int x2=3*nbCaseX/4;
		int y2=2*nbCaseY/3;
		for (int x=x1;x<x2;x++) {
			obstacles.add(new Cellule(x,y1,CouleurObstacle));	
		}
		for (int x=x1;x<x2;x++) {
			obstacles.add(new Cellule(x,y2,CouleurObstacle));
		}
	}
	void dessinerObstacle(Graphics g) {
		for(Cellule c:obstacles)
			dessinerCellule(c,g);
	}
	void genererRepas() {
		boolean b=false;
		do {
		b=false;
		Random r =new Random();
		int x=r.nextInt(nbCaseX);//la premiere case ds notre natrice est 0,0
		int y=r.nextInt(nbCaseY);
		repas=new Cellule(x,y,CouleurRepas);
		for(Cellule c:serpent) {
			if (c.x==repas.x && c.y==repas.y) {
				b=true;
				break;
			}
		}
		for (Cellule h:obstacles) {
			if(h.x==repas.x && h.y==repas.y) {
				b=true;
			}
		}
		
		}while(b==true);
	}
	//constructor:
	public Snake() {
		this.setTitle("Le jeu snake !!");//donne le nom de la fennetre
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(nbCaseX*largeur+2*marge+2*7,nbCaseY*largeur+2*marge+2*14); //the value 7 is for margins in left and right sides
		this.setLocationRelativeTo(null);//to center our Frame in the screen,better add it after setting the size of 
		initialiserSerpent();
		genererObstacle();//must be before "generer repas",cuz we must have the array of obstacles initialized first for the repas to be conditionned
		genererRepas();//we shouldn't call in paint because (paint) gets updated by each incrementation of horloge
		
		notrePanneau=new JPanel(){
			@Override
			public void paint(Graphics g) {
				// TODO Auto-generated method stub
				super.paint(g);//on a l'objet g
				dessinerGrille(g);
				dessinerSnake(g);
				dessinerCellule(repas, g);
				dessinerObstacle(g);
				
			}
		};
		//instantiation de l'horloge:
		horloge =new Timer(vitesse,new ActionListener() {//1er:delay et 2nd: objet de type actionlistener
			
			@Override
			public void actionPerformed(ActionEvent e) {
	// ds le deplacement the body moves first then the head 
				for (int i=serpent.size()-1;i>=1;i--) {
					serpent.get(i).x=serpent.get(i-1).x;
					serpent.get(i).y=serpent.get(i-1).y;
				}
				//deplacer la tete:
				if (direction==1) serpent.get(0).x++;
				if (direction==2) serpent.get(0).y++;
				if (direction==3) serpent.get(0).x--;
				if (direction==4) serpent.get(0).y--;
				//code pour ne pas quitter les limites du pannel 
				if (serpent.get(0).x==nbCaseX) serpent.get(0).x=0;
				if(serpent.get(0).x==-1) serpent.get(0).x=nbCaseX-1;
				if (serpent.get(0).y==nbCaseY) serpent.get(0).y=0;
				if(serpent.get(0).y==-1) serpent.get(0).y=nbCaseY-1;
//genererRepas(); we can't add it here in horloge area cuz it will keep being updated per each sec
				//tester si le repas a ete mange ou ps par la tete du serpent:
				if (serpent.get(0).x==repas.x && serpent.get(0).y==repas.y ) {
					genererRepas();
					serpent.add(new Cellule(serpent.get(serpent.size()-1).x,serpent.get(serpent.size()-1).y, couleurcorps));
				}
				//arret du jeu : si la tete touche l'une des cellules de l'obstacle ou du corps
				for (int i=1;i<serpent.size();i++) {
					if (serpent.get(0).x==serpent.get(i).x && serpent.get(0).y==serpent.get(i).y) {
						horloge.stop();
						finalFrame();
					}
				}
				for (int i=0;i<obstacles.size();i++) {
					if (serpent.get(0).x==obstacles.get(i).x && serpent.get(0).y==obstacles.get(i).y) {
						horloge.stop();
						finalFrame();
					}
				}
				repaint();
				
			}
		});
		horloge.start();
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			
		public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				super.keyPressed(e);
				if(e.getKeyCode()==KeyEvent.VK_DOWN && direction!=4) 
					direction=2;
				if(e.getKeyCode()==KeyEvent.VK_UP && direction!=2) 
					direction=4;
				if(e.getKeyCode()==KeyEvent.VK_RIGHT && direction!=3)//pour conditionner le mvt  
					direction=1;
				if(e.getKeyCode()==KeyEvent.VK_LEFT && direction!=1) 
					direction=3;
			
			}
		}
		
		);
		this.setContentPane(notrePanneau);
		this.setVisible(true);
	}
void finalFrame() {
	JFrame finalFrame=new JFrame();
	JButton button1=new JButton("Restart");
	JButton button2=new JButton("EXIT");
	ImageIcon icon = new ImageIcon("C:\\Users\\hp\\Documents\\info 1\\S2\\java\\Over.jpg");
	JLabel label=new JLabel(icon);
	button1.setBounds(50,30,30,10);
	button2.setBounds(50,30,30,10);
	
	//finalFrame.setLayout(null);
	finalFrame.setVisible(true);
	finalFrame.setTitle("Game over");
	finalFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
	finalFrame.setSize(550,350); //le 7 pr les marges des cotes
	finalFrame.setLocationRelativeTo(null);
	
	finalFrame.add(label);
	//finalFrame.add(button1);
	//finalFrame.add(button2);
}
void dessinerSnake (Graphics g) {
	for(Cellule c:serpent) {
		dessinerCellule(c,g);
	}
}
void dessinerCellule(Cellule cel,Graphics g) {
	g.setColor(cel.couleur);
	g.fillRect(marge+cel.x*largeur,marge+cel.y*largeur,largeur,largeur);
}
void dessinerGrille(Graphics g) {
	g.setColor(bg);
	g.fillRect(marge, marge, nbCaseX*largeur, nbCaseY*largeur);
	//pr dessin des lignes verticaux:
	g.setColor(Color.white);//avant de dessiner n'importe  il faut d'abord preciser sa couleur
	for (int i=0;i<nbCaseX+1;i++) {
		g.drawLine(marge+i*largeur,marge, marge+i*largeur, marge+nbCaseY*largeur);
	}
	
	//pr dessin des lignes horizontaux:
	g.setColor(Color.white);
	for (int i=0;i<nbCaseY+1;i++) {
		g.drawLine(marge,marge+i*largeur, marge+nbCaseX*largeur, marge+i*largeur);
	}
}


public static void main(String[] args) {
		new Snake();
	}
	

}

class Cellule{
	int x,y;
	Color couleur;
	public Cellule(int x, int y, Color couleur) {
		super();
		this.x = x;
		this.y = y;
		this.couleur = couleur;
	}
}
