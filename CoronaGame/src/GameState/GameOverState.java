package GameState;

import java.awt.*;
import java.awt.event.KeyEvent;

import TileMap.Background;
public class GameOverState extends GameState {
	private Background bg;
	private int currentChoice = 0;
	private String[] options = {
			"Tentar Novamente",
			"Sair"
	};
	private Color titleColor;
	private Font titleFont;
	private Font font;	
	 public GameOverState(GameStateManager gsm) {
		 this.gsm =gsm;
		 try {
			 bg= new Background("/Backgrounds/gameover.gif",1);
			 titleColor = new Color(128,0,0);
			 titleFont = new Font("Century Gothic",Font.PLAIN,28);
			 font = new Font ("Arial", Font.PLAIN,12);
		 }
		 catch(Exception e) {
			 e.printStackTrace();
		 }
	 }
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D g) {
		bg.draw(g);
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("Game Over",10,70);
		
		g.setFont(font);
		for(int i=0;i<options.length;i++) {
			if(i==currentChoice) {
				g.setColor(Color.BLACK);
			}else {
				g.setColor(Color.RED);
			}
			g.drawString(options[i], 145, 140 + i*15);
		}
	}
	private void select() {
		if(currentChoice ==0) {
			gsm.setState(GameStateManager.LEVEL1STATE);
		}
		if(currentChoice ==1) {
			System.exit(0);
		}
	}
	@Override
	public void keyPressed(int k) {
		if(k==KeyEvent.VK_ENTER) {
			select();
		}
		if(k==KeyEvent.VK_UP) {
			currentChoice--;
			if(currentChoice==-1) {
				currentChoice = options.length -1;
			}
		}
		if(k==KeyEvent.VK_DOWN) {
			currentChoice++;
			if(currentChoice == options.length) {
				currentChoice =0;
			}
		}
	}

	@Override
	public void keyReleased(int k) {
		// TODO Auto-generated method stub
		
	}

}
