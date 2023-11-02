package com.ishwar.spaceshooter.obstacles;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.ishwar.spaceshooter.destruction.DestructionManager;

public class ObstacleManager{
	
	private static Random random;
	
	private static int minLifeline = 2;
	private static int maxLifeline = 15;
	
	private static float obstaclePadding;

	private static float obstacleWidth;
	private static float obstacleHeight;
	
	private static Texture[] obstacleTexures;
	
	private static float minGap;
	private static float maxGap;

	private static float lastObstaclePositionY;
	private static float gapForNextObstcle;

	private static List<Obstacle> aliveObstacles = new ArrayList<>();
	private static List<Obstacle> deadObstacles = new ArrayList<>();
	
	public static void initialize(float obstacleSize){
		ObstacleManager.random = new Random();
		
		ObstacleManager.obstacleWidth = obstacleSize;
		ObstacleManager.obstacleHeight = obstacleWidth * 0.8f;

		ObstacleManager.obstaclePadding = obstacleWidth * 0.1f;
		
		ObstacleManager.minGap = obstacleHeight * 3f;
		ObstacleManager.maxGap = obstacleHeight * 4.5f;
		
		ObstacleManager.obstacleTexures = new Texture[15];
		for(int i = 1; i < obstacleTexures.length + 1; ++ i){
			obstacleTexures[i-1] = new Texture(Gdx.files.internal(String.format("%s%d.jpg", Obstacle.class.getSimpleName(), i)), true);
		}
		
		
	}
	
	public static List<Obstacle> getObstacles(){
		return aliveObstacles;
	}
	
	public static float getObstacleWidth(){
		return obstacleWidth;
	}
	
	public static float getObstacleWidthWithoutPadding(){
		return obstacleWidth - obstaclePadding;
	}
	
	public static float getObstacleHeight(){
		return obstacleHeight;
	}
	
	public static void reset(){
		for(Obstacle obstacle : aliveObstacles){
			if(!deadObstacles.contains(obstacle)){
				deadObstacles.add(obstacle);
			}
		}
		aliveObstacles.clear();
		gapForNextObstcle = 0;
	}
	
	public static void draw(Batch batch, Camera camera){
		float top = camera.position.y + camera.viewportHeight / 2;
		
		if(Math.abs(lastObstaclePositionY - top) >= gapForNextObstcle){
			lastObstaclePositionY = top;
			gapForNextObstcle = minGap + random.nextFloat() * (maxGap - minGap);
			generate(camera);
		}
		
		for(int index = 0; index < aliveObstacles.size(); ++index){
			Obstacle obstacle = aliveObstacles.get(index);
			obstacle.tick();
			
			if(!obstacle.isDead() && (camera.frustum.pointInFrustum(obstacle.x, obstacle.y, 0) || camera.frustum.pointInFrustum(obstacle.x, obstacle.y + obstacleHeight, 0) || camera.frustum.pointInFrustum(obstacle.x + obstacleWidth - obstaclePadding, obstacle.y, 0) || camera.frustum.pointInFrustum(obstacle.x + obstacleWidth - obstaclePadding, obstacle.y + obstacleHeight, 0))){
				batch.draw(obstacleTexures[obstacle.lifeline - 1], obstacle.x, obstacle.y, obstacleWidth - obstaclePadding, obstacleHeight);
			}else{
				if(obstacle.isDead()){
					DestructionManager.destroy(getObstacleWidthWithoutPadding(), getObstacleHeight(), obstacle.x, obstacle.y, getObstacleWidthWithoutPadding() * 0.3f, Color.GREEN);
				}
				aliveObstacles.remove(obstacle);
				deadObstacles.add(obstacle);
				index--;
			}
		}
	}

	private static void generate(Camera camera){
		float movingFactorX = MathUtils.clamp((float)random.nextGaussian(), -0.5f, 0.5f);
		
		float left = (camera.position.x - camera.viewportWidth / 2) - obstacleWidth / 2;
		float  right = (camera.position.x + camera.viewportWidth / 2) + obstacleWidth / 2;
		
		if(movingFactorX > 0){
			left += obstacleWidth;
		}else{
			right -= obstacleWidth;
		}
		
		for(; left < right; left += obstacleWidth){
			int lifeline = 0;
			if(random.nextInt(2) == 1){
				lifeline = 1 + random.nextInt(3);
			}else{
				lifeline = minLifeline + random.nextInt(maxLifeline - minLifeline + 1);
			}
			Obstacle obstacle = null;
			if(deadObstacles.size() > 0){
				obstacle = deadObstacles.get(0);
				deadObstacles.remove(obstacle);
			}else{
				obstacle = new Obstacle();
			}
			obstacle.set(lifeline, left, lastObstaclePositionY, movingFactorX);
			aliveObstacles.add(obstacle);
		}
	}
	
	public static void dispose(){
		aliveObstacles.clear();
		deadObstacles.clear();
		for(Texture texture : obstacleTexures){
			texture.dispose();
		}
	}
	
}
