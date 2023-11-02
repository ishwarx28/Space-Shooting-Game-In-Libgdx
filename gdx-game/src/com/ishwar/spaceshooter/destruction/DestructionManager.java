package com.ishwar.spaceshooter.destruction;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.List;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Color;

public class DestructionManager
{
	public final static List<DestroyEffect> runningEffects = new ArrayList<>();
	public final static List<DestroyEffect> stoppedEffects = new ArrayList<>();
	
	public static void destroy(float width, float height, float x, float y, float particleSize, Color color){
		DestroyEffect effect = null;
		
		for(int i = 0; i < stoppedEffects.size(); ++ i){
			DestroyEffect newEffect = stoppedEffects.get(i);
			if(newEffect.getColor().equals(color)){
				effect = newEffect;
				stoppedEffects.remove(i);
				break;
			}
		}
		
		if(effect == null) {
			effect = new DestroyEffect(width, height, particleSize, color);
		}
		
		runningEffects.add(effect);
		effect.start(x, y);
	}
	
	public static void draw(Batch batch){
		int index = 0;
		while(index > -1 && runningEffects.size() > index){
			DestroyEffect effect = runningEffects.get(index);
			
			if(!effect.isRunning()){
				stoppedEffects.add(effect);
				runningEffects.remove(effect);
				index--;
				continue;
			}

			effect.draw(batch, 1);

			index++;
		}
	}
	
	public static void dispose(){
		synchronized(runningEffects){
			for(DestroyEffect effect : runningEffects){
				effect.dispose();
			}
			runningEffects.clear();
		}
		synchronized(stoppedEffects){
			for(DestroyEffect effect : stoppedEffects){
				effect.dispose();
			}
			stoppedEffects.clear();
		}
	}
}
