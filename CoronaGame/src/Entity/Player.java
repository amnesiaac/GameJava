package Entity;
import TileMap.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
public class Player extends MapObject{
	private int health;
	private int maxHealth;
	private int ammo;
	private int maxAmmo;
	private boolean dead;
	private boolean flinching;
	private long flinchTime;
	
	private boolean firing;
	private int ammCost;
	private int gunDamage;
	private ArrayList<Ammo> bullets;
	
	private boolean melee;
	private int meleeDamage;
	private int meleeRange;
	
	private boolean gliding;
	
	private ArrayList<BufferedImage[]>sprites;
	private final int[] numFrames = {2,8,1,2,4,2,5};
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int GLIDING = 4;
	private static final int GUNNING = 5;
	private static final int MELEE = 6;
	
	public Player(TileMap tm) {
		super(tm);
		width = 30;
		height = 30;
		cwidth = 20;
		cheight = 20;
		
		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxfallSpeed = 4.0;
		jumpStart = -4.8;
		stopjumpSpeed = 0.3;
		
		facingRight = true;
		health = maxHealth = 5;
		ammo = maxAmmo = 2500;
		ammCost =200;
		gunDamage = 5;
		bullets = new ArrayList<Ammo>();
		dead= false;
		meleeDamage=8;
		meleeRange=40;
		
		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/playersprites.gif"));
			sprites = new ArrayList<BufferedImage[]>();
			for(int i=0;i<7;i++) {
		    	BufferedImage[]bv = new BufferedImage[numFrames[i]];
		    		for(int j=0;j<numFrames[i];j++) {
		    			if(i!=MELEE) {
		    			bv[j] = spritesheet.getSubimage(j*width,i*height,width,height);
		    		} else {
		    			bv[j] = spritesheet.getSubimage(j*width*2,i*height,width*2,height);
		    		}
		    	}
		    	sprites.add(bv);
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
	}
	public int getHealth() {
		return health;
	}
	public int getMaxHealth(){
		return maxHealth;
	}

	public int getAmmo() {
		return ammo;
	}
	public int getMaxAmmo() {
		return maxAmmo;
	}
	
	public void setFiring() {
		firing = true;
	}
	public void setMelee(){
		melee = true;
	}
	public void setGliding(boolean b) {
		gliding=b;
	}
	public void checkAttack(ArrayList<Enemy>enemies) {
		for(int i=0;i<enemies.size();i++) {
			Enemy en = enemies.get(i);
			
			if(melee) {
				if(facingRight) {	
						if(en.getx()>x && en.getx()<x + meleeRange && en.gety()>y - height/2 && en.gety()<y+height/2) {
							en.hit(meleeDamage);
						}
					}
				}else {
						if(en.getx()<x && en.getx()>x - meleeRange && en.gety()<y - height/2 && en.gety()>y+height/2) {
							en.hit(meleeDamage);
						}
					}
			for(int j=0;j<bullets.size();j++) {
				if(bullets.get(j).intersects(en)) {
					en.hit(gunDamage);
					bullets.get(j).setHit();
					break;
				}
			}
			if(intersects(en)) {
				hit(en.getDamage());
			}
				}
		

	}
	public void hit(int damage) {
		if(flinching) {
			return;
		}
		health -= damage;
		if(health<0) {
			health=0;
		}
		if(health==0) {
			dead=true;
		}
		flinching = true;
		flinchTime=System.nanoTime();
	}
	public void playerDeath() {
		if(health <= 0) {
			dead=true;
		}
	}
	public boolean isDead() {
		return dead;
	}
	private void getNextPosition() {
		if(left) {
			dx-=moveSpeed;
			if(dx<-maxSpeed) {
				dx= -maxSpeed;
			}
		}else if(right) {
			dx+=moveSpeed;
			if(dx>maxSpeed) {
				dx = maxSpeed;
			}
		}else {
			if(dx>0) {
				dx-=stopSpeed;
				if(dx<0) {
					dx=0;
				}
			}else if(dx<0) {
				dx+=stopSpeed;
				if(dx>0) {
					dx=0;
				}
			}
		}
		if((currentAction == MELEE||currentAction==GUNNING)&& !(jumping||falling)) {
			dx=0;
		}
		if(jumping && !falling) {
			dy=jumpStart;
			falling = true;
		}
		if(falling) {
			if(dy>0 && gliding) {
				dy+=fallSpeed*0.1;
			}else {
				dy+=fallSpeed;	
			}
			if(dy>0) {
				jumping=false;
			}
			if(dy<0 && !jumping) {
				dy+=stopjumpSpeed;
			}
			if(dy>maxfallSpeed) {
				dy=maxfallSpeed;
			}
		}
	}
	public void update() {
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		if(currentAction==MELEE) {
			if(animation.hasPlayedOnce()) {
				melee=false;
			}
		}
		if(currentAction==GUNNING) {
			if(animation.hasPlayedOnce()) {
				firing=false;
			}
		}
		ammo+=1;
		if(ammo>maxAmmo) {
			ammo=maxAmmo;
		}
		if(firing && currentAction !=GUNNING) {
			if(ammo>ammCost) {
				ammo-=ammCost;
				Ammo bullet = new Ammo(tileMap, facingRight);
				bullet.setPosition(x, y);
				bullets.add(bullet);
			}
		}
		for(int i=0;i<bullets.size();i++) {
			bullets.get(i).update();
			if(bullets.get(i).ammoRemove()) {
				bullets.remove(i);
				i--;
			}
		}
		
		if(flinching) {
			long elapsed = (System.nanoTime()-flinchTime)/1000000;
			if(elapsed>1000) {
				flinching = false;
			}
		}
		
		if(melee) {
			if(currentAction != MELEE) {
				currentAction = MELEE;
				animation.setFrames(sprites.get(MELEE));
				animation.setDelay(50);
				width=60;
			}
		}else if (firing) {
			if(currentAction!=GUNNING) {
				currentAction = GUNNING;
				animation.setFrames(sprites.get(GUNNING));
				animation.setDelay(100);
				width = 30;
			}
		}else if(dy>0) {
			if(gliding) {
				if(currentAction !=GLIDING) {
					currentAction = GLIDING;
					animation.setFrames(sprites.get(GLIDING));
					animation.setDelay(100);
					width = 30;
				}
			}else if(currentAction!=FALLING) {
				currentAction=FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);
				width=30;
			}
		}else if(dy<0) {
			if(currentAction !=JUMPING) {
				currentAction=JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
				width=30;
			}
		}else if(left || right) {
			if(currentAction!=WALKING) {
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(40);
				width=30;
			}
		}else {
			if(currentAction!=IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				width=30; 
			}
		}
		animation.update();
		
		if(currentAction!=MELEE && currentAction !=GUNNING) {
			if(right) facingRight = true;
			if(left)  facingRight = false;
		}
	}
	
	public void draw (Graphics2D g) {
		setMapPosition();
		
		for(int i=0;i<bullets.size();i++) {
			bullets.get(i).draw(g);
		}
		
		if(flinching) {
			long elapsed = (System.nanoTime() - flinchTime)/1000000;
			if(elapsed/100 % 2==0) {
				return;
			}
		}
		super.draw(g);
	}
}
