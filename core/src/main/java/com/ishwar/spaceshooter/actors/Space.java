package com.ishwar.spaceshooter.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Texture;
import java.util.Random;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import java.util.Objects;
import com.badlogic.gdx.graphics.Camera;

public class Space extends Actor implements Disposable {
	
	private final static int starMinSize = 10;
	private final static int starMaxSize = 60;

	private Texture starTexture;
	private float tileSize;
	private Random random;

	public Space(float tileSize) {
        this.starTexture = new Texture(Gdx.files.internal("star.png"));
		this.tileSize = tileSize;
		this.random = new Random();
    }
	
	public void draw(Batch batch, Camera camera){
		float screenLeft = camera.position.x - camera.viewportWidth / 2;
		float screenRight = screenLeft + camera.viewportWidth;
		float screenTop = camera.position.y - camera.viewportHeight / 2;
		float screenBottom = screenTop + camera.viewportHeight;

		float left = screenLeft - (screenLeft % tileSize);
		float top = screenTop - (screenTop % tileSize);
		float right = screenRight - (screenRight % tileSize);
		float bottom = screenBottom - (screenBottom % tileSize);

		if(left > screenLeft){
			left -= tileSize;
		}

		if(top > screenTop){
			top -= tileSize;
		}

		if(right < screenRight){
			right += tileSize;
		}

		if(bottom < screenBottom){
            bottom += tileSize;
        }

		int startYIndex = (int) (top / tileSize);
		int endYIndex = (int) (bottom / tileSize);
		int startXIndex = (int) (left / tileSize);
		int endXIndex = (int) (right / tileSize);

		for(int h = startXIndex; h < endXIndex; ++h){
			float x = h * tileSize;

			for(int v = startYIndex; v < endYIndex; ++v){

				int uniquePositionSeed = Objects.hash(h, v);
				random.setSeed(uniquePositionSeed);

				float rx = x - tileSize * random.nextFloat() * random.nextFloat();
				float y = v * tileSize + tileSize * random.nextFloat();
				float starSize = (starMinSize + random.nextFloat() * starMaxSize) / 2;

				boolean flipX = random.nextInt(2) == 0;
				boolean flipY = random.nextInt(2) == 1;
				batch.draw(starTexture, rx, y, starSize, starSize, 0, 0, starTexture.getWidth(), starTexture.getHeight(), flipX, flipY);
			}
		}
    }
	
	@Override
	public void dispose(){
		starTexture.dispose();
	}

}
