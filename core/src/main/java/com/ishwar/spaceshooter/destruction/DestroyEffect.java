package com.ishwar.spaceshooter.destruction;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Pixmap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DestroyEffect extends Actor implements Disposable
{
	private static final float DEFAULT_SCALE_FACTOR = 0.97F;
	private static final float MIN_VISIBLE_SCALE = 0.06f;
	
	private boolean isRunning;
	
	private TextureRegion particleTextureRegion;
	private float particleSize;
	private Color color;
	private Particle[] particles;
	private float globalScale;
	
	public DestroyEffect(float width, float height, float particleSize, Color color){
		this.particleSize = particleSize;
		this.color = color;
		Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		map.setColor(color);
		map.fill();
		particleTextureRegion = new TextureRegion(new Texture(map));
		map.dispose();
		generateParticles(width, height);
	}

	private void generateParticles(float width, float height){
		int horizontal = (int) Math.max(1, width / particleSize);
		int vertical = (int) Math.max(1, height / particleSize);

		particles = new Particle[horizontal * vertical];

		Random random = ThreadLocalRandom.current();
		int particleIndex = 0;

		for(int h = 0; h < horizontal; ++ h){
			float left = h * particleSize;
			for(int v = 0; v < vertical; ++ v){
				float top = v * particleSize;

				float movingX = (float) random.nextGaussian();
				float movingY = (float) random.nextGaussian();
				float angle = (float) random.nextGaussian();

				Particle particle = new Particle(getX() + left, getY() + top, movingX, movingY, angle);
				particles[particleIndex++] = particle;
			}
		}
	}
	
	public Color getColor(){
		return color;
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public void start(float x, float y){
		if(isRunning){
			throw new RuntimeException("DestroyEffect is already running");
		}
		globalScale = 1.0f;
		for(Particle particle : particles){
			particle.reset();
		}
		
		setPosition(x, y);
		
		isRunning = true;
	}

	@Override
	public void draw(Batch batch, float parentAlpha){
		super.draw(batch, parentAlpha);
		
		if(!isRunning){
			return;
		}
		
		globalScale *= DestroyEffect.DEFAULT_SCALE_FACTOR;

		if(globalScale <= MIN_VISIBLE_SCALE){
			isRunning = false;
			return;
		}
		
		for(Particle particle : particles){
			particle.tick();
			float x = getX() + particle.X;
			float y = getY() + particle.Y; 
			batch.draw(particleTextureRegion, x, y, particleSize / 2, particleSize / 2, particleSize, particleSize, globalScale, globalScale, particle.Angle);
		}
	}

	@Override
	public void dispose(){
		isRunning = false;
		particleTextureRegion.getTexture().dispose();
		particles = null;
	}
}
