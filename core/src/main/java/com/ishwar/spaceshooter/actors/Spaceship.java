package com.ishwar.spaceshooter.actors;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Timer;
import java.util.List;
import java.util.ArrayList;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.ishwar.spaceshooter.destruction.DestructionManager;

public class Spaceship extends Sprite implements Disposable
{
	private float spaceshipSpeed = 5.2f;
	private float shootDelayInSeconds = 0.2f;
	
	private Texture bulletTexture;
	private float bulletSpeed = 25;
	private float bulletSize;
	
	private boolean isAlive;
	
	private Timer.Task shootingTask = new Timer.Task(){
		@Override
		public void run(){
			shoot();
		}
	};
	
	private final List<Sprite> bullets = new ArrayList<>();
	private final List<Sprite> inactiveBullets = new ArrayList<>();
	
	public Spaceship(float size){
		super(createTexture("spaceship.png"));
		
		Pixmap map = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
		map.setColor(Color.WHITE);
		map.fillCircle(25, 25, 25);
		this.bulletTexture = new Texture(map, true);
		this.bulletTexture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
		map.dispose();
		this.bulletSize = size / 4;
		
		setSize(size, size);
		setPosition(-getWidth() / 2, 0);
	}
	
	private static Texture createTexture(String path){
		Texture texture = new Texture(Gdx.files.internal(path), true);
		texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
		return texture;
	}
	
	public boolean isAlive(){
		return isAlive;
	}
	
	public void kill(){
		DestructionManager.destroy(getWidth(), getHeight(), getX(), getY(), getWidth() * 0.4f, Color.RED);
		DestructionManager.destroy(getWidth(), getHeight(), getX(), getY(), getWidth() * 0.3f, Color.WHITE);
		DestructionManager.destroy(getWidth(), getHeight(), getX(), getY(), getWidth() * 0.3f, Color.RED);
		isAlive = false;
		shootingTask.cancel();
	}
	
	public void reset(){
		isAlive = true;
		Timer.schedule(shootingTask, 0, shootDelayInSeconds);

		setPosition(-getWidth() / 2, 0);
	}
	
	public List<Sprite> getBullets(){
		return bullets;
	}
	
	public void removeBullet(Sprite bullet){
		inactiveBullets.add(bullet);
		bullets.remove(bullet);
	}
	
	public void shoot(){
		Sprite bullet = null;
		if(inactiveBullets.size() > 0){
			bullet = inactiveBullets.get(0);
			inactiveBullets.remove(0);
		}else{
			bullet = new Sprite(bulletTexture);
			bullet.setSize(bulletSize, bulletSize);
		}
		bullet.setPosition(getX() + getWidth() / 2 - bulletSize / 2, getY() + getHeight());
		bullets.add(bullet); 
	}
	
	public void draw(Batch batch, Camera camera){
		if(!isAlive){
			return;
		}

		super.draw(batch);

		
		translateY(spaceshipSpeed);
		
		int index = 0;
		while(index > -1 && bullets.size() > index){
			Sprite bullet = bullets.get(index);
			bullet.translateY(spaceshipSpeed + bulletSpeed);
			
			if(!camera.frustum.boundsInFrustum(bullet.getX(), bullet.getY(), 0, bullet.getWidth() / 2, bullet.getHeight() / 2, 0)){
				removeBullet(bullet);
				index--;
			}else{
				bullet.draw(batch);
				index++;
			}
		}
	}

	@Override
	public void dispose(){
		this.getTexture().dispose();
		this.bulletTexture.dispose();
		this.bullets.clear();
		this.inactiveBullets.clear();
	}
}
