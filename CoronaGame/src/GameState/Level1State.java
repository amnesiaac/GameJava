package GameState;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Entity.*;
import Entity.Enemies.Covid;
import Main.GamePanel;

import java.awt.*;
import TileMap.*;
public class Level1State extends GameState{
private TileMap tileMap;
private Background bg;
private Player player;
private ArrayList<Enemy> enemies;
private ArrayList<Megumin>explosions;
private HUD hud;
 
public Level1State(GameStateManager gsm) {
	 this.gsm=gsm;
	 init();
 }

@Override
public void init() {
	tileMap = new TileMap(30);  
	tileMap.loadTiles("/Tilesets/grasstileset.gif");
	tileMap.loadMap("/Maps/level1-1.map");
	tileMap.setPosition(0, 0);
	
	bg = new Background("/Backgrounds/grassbg1.gif",0.1);
	
	player = new Player(tileMap);
	player.setPosition(100, 100);
	populateEnemies();
	explosions = new ArrayList<Megumin>();
	hud = new HUD(player);
}
private void populateEnemies() {
	enemies = new ArrayList<Enemy>();
	Covid c;
	Point[] points = new Point[]{
		new Point(200,100),
		new Point(860,200),
		new Point(1525,200),
		new Point(1680,200),
		new Point(1800,200)
	};
	for(int i=0;i<points.length;i++) {
		c = new Covid(tileMap);
		c.setPosition(points[i].getX(), points[i].getY());
		enemies.add(c);
	}
}
@Override
public void update() {
	// TODO Auto-generated method stub
	player.update();
	tileMap.setPosition(GamePanel.WIDTH/2 - player.getx(), GamePanel.HEIGHT/2 -player.gety());
	
	bg.setPosition(tileMap.getx(), tileMap.gety());
	
	player.checkAttack(enemies);
	
	for(int i=0;i<enemies.size();i++) {
		Enemy en = enemies.get(i);
		en.update();
		if(en.isDead()) {
			enemies.remove(i);
			i--;
			explosions.add(new Megumin(en.getx(),en.gety())) ;
		}
	}
	for(int i=0;i<explosions.size();i++) {
		explosions.get(i).update();
		if(explosions.get(i).explosionRemove()) {
			explosions.remove(i);
			i--;
		}
	}
	if (player.isDead()) {
		gsm.setState(GameStateManager.GAMEOVERSTATE);
	}

}


@Override
public void draw(Graphics2D g) {
	bg.draw(g);
	
	tileMap.draw(g);
	
	player.draw(g);
	
	for(int i=0;i<enemies.size();i++){
		enemies.get(i).setMapPosition();
		enemies.get(i).draw(g);
	}
	for(int i=0;i<explosions.size();i++) {
		explosions.get(i).setMapPosition(tileMap.getx(), tileMap.gety());
		explosions.get(i).draw(g);
	}
	hud.draw(g);
}

@Override
public void keyPressed(int k) {
	// TODO Auto-generated method stub
	if(k==KeyEvent.VK_LEFT) {
		player.setLeft(true);
	}
	if(k==KeyEvent.VK_RIGHT) {
		player.setRight(true);
	}
	if(k==KeyEvent.VK_Z) {
		player.setJumping(true);
	}
	if(k==KeyEvent.VK_X) {
		player.setGliding(true);
	}
	if(k==KeyEvent.VK_A) {
		player.setFiring();
	}
	if(k==KeyEvent.VK_S) {
		player.setMelee();
	}
}

@Override
public void keyReleased(int k) {
	// TODO Auto-generated method stub
	if(k==KeyEvent.VK_LEFT) {
		player.setLeft(false);
	}
	if(k==KeyEvent.VK_RIGHT) {
		player.setRight(false);
	}
	if(k==KeyEvent.VK_Z) {
		player.setJumping(false);
	}
	if(k==KeyEvent.VK_X) {
		player.setGliding(false);
	}
	//if(k==KeyEvent.VK_A) {
	//	player.setFiring(false);
//	}
	//if(k==KeyEvent.VK_S) {
	//	player.setMelee(false);
	//}
}
}
