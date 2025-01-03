package com.ishwar.spaceshooter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.ishwar.spaceshooter.actors.Space;
import com.ishwar.spaceshooter.actors.Spaceship;
import com.ishwar.spaceshooter.destruction.DestructionManager;
import com.ishwar.spaceshooter.obstacles.Obstacle;
import com.ishwar.spaceshooter.obstacles.ObstacleManager;
import java.text.Format;
import java.util.List;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
public class SpaceGame implements ApplicationListener
{
	private boolean isRunning;
	private boolean isInitialized;

	private float iconWidth;
	private float iconHeight;
	private Texture iconPlay;
	private Texture iconRestart;
	private Texture dimTexuture;

	private BitmapFont scoreFont;

	private SpriteBatch batch;
	private OrthographicCamera camera;

	private Vector3 touchPoints;
	private float touchAccelerator = 1.2f;

	private Space space;
	private Spaceship spaceship;

	private Sound obstacleHarmSound;
	private Sound obstacleKillSound;
	private Sound gameOverSound;

	private int harms;
	private int kills;

	private final int rewardPerHarm = 1;
	private final int rewardPerKill = 5;

	@Override
	public void create(){
		this.iconPlay = new Texture(Gdx.files.internal("icons/play-icon.png"), true);
		iconPlay.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
		this.iconRestart = new Texture(Gdx.files.internal("icons/restart-icon.png"), true);
		iconRestart.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
		this.iconWidth = Gdx.graphics.getWidth() * 0.22f;
		this.iconHeight = iconWidth * 0.53f;
		Pixmap map = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
		map.setColor(0, 0, 0, 0.6f);
		map.fill();
		this.dimTexuture = new Texture(map);
		map.dispose();

		this.scoreFont = new BitmapFont();
		scoreFont.setColor(Color.TEAL);
//		scoreFont.setScale(5);

		this.batch = new SpriteBatch();
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		this.space = new Space(Gdx.graphics.getWidth() * 0.3f);

		ObstacleManager.initialize(Gdx.graphics.getWidth() * 0.25f);
		this.obstacleHarmSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pop.ogg"));
		this.obstacleKillSound = Gdx.audio.newSound(Gdx.files.internal("sounds/proof.ogg"));
		this.gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/wrong_buzzer.mp3"));

		this.spaceship = new Spaceship(Gdx.graphics.getWidth() * 0.15f);

		this.touchPoints = new Vector3();
	}

	@Override
	public void resize(int p1, int p2){

	}

	@Override
	public void render(){
		Gdx.gl.glClearColor(0.0f, 0.0627f, 0.133f, 1.0f);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.position.set(0, spaceship.getY() + Gdx.graphics.getWidth() * 0.7f, 0);
		camera.update();


		if(Gdx.input.isTouched() && isRunning){
			handleTouchInputs();
		}else if(Gdx.input.justTouched() && !isRunning){
			touchPoints.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPoints);

			if(Rectangle.tmp.set(camera.position.x - iconWidth / 2, camera.position.y - camera.viewportHeight * 0.45f, iconWidth, iconHeight).contains(touchPoints.x, touchPoints.y)){
				isRunning = true;
				isInitialized = true;
				spaceship.reset();
				ObstacleManager.reset();
				harms = 0;
				kills = 0;
			}
		}


		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		space.draw(batch, camera);
		ObstacleManager.draw(batch, camera);
		spaceship.draw(batch, camera);
		DestructionManager.draw(batch);

		if(spaceship.isAlive()){
			detectCollisions();
		}

		if(!isRunning){
			float x = camera.position.x;
			float y = camera.position.y;
			batch.draw(dimTexuture, x - camera.viewportWidth / 2, y - camera.viewportHeight / 2);
			batch.draw(isInitialized ? iconRestart : iconPlay, x - iconWidth / 2, y - camera.viewportHeight * 0.45f, iconWidth, iconHeight);

			int score = rewardPerHarm * harms + rewardPerKill * kills;
			int bestScore = 0; // scoreStore.getInt("bestScore", 0);

			if(score > bestScore){
				bestScore = score;
//				scoreStore.edit().putInt("bestScore", bestScore).apply();
			}

			String text = "BEST SCORE";
//			scoreFont.setScale(2.8f);
//			BitmapFont.TextBounds bounds = scoreFont.getBounds(text);
			scoreFont.setColor(Color.TEAL);
//			scoreFont.drawMultiLine(batch, text, x - bounds.width / 2, y);

			text = Integer.toString(bestScore);
//			bounds = scoreFont.getBounds(text);
			scoreFont.setColor(Color.WHITE);
//			scoreFont.drawMultiLine(batch, text, x - bounds.width / 2, y - bounds.height - 30);

			if(isInitialized){
				text = Integer.toString(score);
//				scoreFont.setScale(8.0f);
//				bounds = scoreFont.getBounds(text);
				scoreFont.setColor(Color.WHITE);
//				scoreFont.drawMultiLine(batch, text, x - bounds.width / 2, y + bounds.height + 40);

			}
		}

		batch.end();
	}

	private void handleTouchInputs(){
		float minX = -Gdx.graphics.getWidth()/ 2; // Left boundary
		float maxX = Gdx.graphics.getWidth() / 2 - spaceship.getWidth(); // Right boundary
		spaceship.setX(MathUtils.clamp(spaceship.getX() + Gdx.input.getDeltaX() * touchAccelerator, minX, maxX));
	}

	private void detectCollisions(){
		Rectangle rectangle = spaceship.getBoundingRectangle();
		List<Sprite> bullets = spaceship.getBullets();
		for(Obstacle obstacle : ObstacleManager.getObstacles()){
			Rectangle obstacleBounds = rectangle.tmp.set(obstacle.x, obstacle.y, ObstacleManager.getObstacleWidthWithoutPadding(), ObstacleManager.getObstacleHeight());
			for(int i = 0; i < bullets.size(); ++i){
				Sprite bullet = bullets.get(i);
				if(bullet.getBoundingRectangle().overlaps(obstacleBounds)){
					spaceship.removeBullet(bullet);
					obstacle.harm();
					if(obstacle.isDead()){
						obstacleKillSound.play();
						Gdx.input.vibrate(40);
						kills++;
					}else{
						obstacleHarmSound.play();
						harms++;
					}
					i--;
				}
			}
			if(!obstacle.isDead() && rectangle.overlaps(obstacleBounds)){
				spaceship.kill();
				gameOverSound.play();
				isRunning = false;
				return;
			}
		}
	}

	@Override
	public void pause(){

	}

	@Override
	public void resume(){

	}

	@Override
	public void dispose(){
		scoreFont.dispose();
		dimTexuture.dispose();
		iconPlay.dispose();
		iconRestart.dispose();
		space.dispose();
		spaceship.dispose();
		ObstacleManager.dispose();
		DestructionManager.dispose();
		obstacleHarmSound.dispose();
		obstacleKillSound.dispose();
		gameOverSound.dispose();
	}

}
