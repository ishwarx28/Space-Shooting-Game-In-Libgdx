package com.ishwar.spaceshooter.destruction;

public class Particle {
	private final float x;
	private final float y;
	
	private final float mx;
	private final float my;
	private final float mr;
	
	public float X;
	public float Y;
	public float Angle;

	public Particle(float x, float y, float mx, float my, float mr){
		this.x = x;
		this.y = y;
		this.mx = mx;
		this.my = my;
		this.mr = mr;
	}
	
	public void reset(){
		this.X = x;
		this.Y = y;
		this.Angle = 0;
	}

	public void tick(){
		this.X += mx;
		this.Y += my;
		this.Angle += mr;
	}

}
