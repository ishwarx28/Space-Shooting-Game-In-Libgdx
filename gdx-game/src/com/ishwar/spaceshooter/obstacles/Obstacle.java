package com.ishwar.spaceshooter.obstacles;

public class Obstacle
{
	public int lifeline;
	public float x;
	public float y;
	private float mx;
	
	public void set(int lifeline, float x, float y, float mx){
		this.lifeline = lifeline;
		this.x = x;
		this.y = y;
		this.mx = mx;
	}
	
	public boolean isDead(){
		return lifeline <= 0;
	}
	
	public void harm(){
		lifeline--;
	}
	
	public void tick(){
		x += mx;
	}
}
