package controller;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import view.Display;

public class Game implements Runnable {

	private final String title = "Maze Runner";
	private Thread thread;
	private boolean running;
	private final int width = 500;
	private final int height = 500;
	private Display display;

	// private final double width = ;
	public Game() {
		running = false;
		
	}

	public synchronized void start() { //synchronized is used whenever we directly work with threads 
		if (running) // to avoid problems if this method is called by accident when the game is already running 
			return;
		running = true; // the game will be running now that we're making a new thread
		thread = new Thread(this); // make a new thread which makes this part of the program (this class) runs separately from the rest of the program 
		thread.start(); // start starts the thread and calls the run method bellow
	}

	public synchronized void stop() { //synchronized is used whenever we directly work with threads 
		if (!running) // to avoid problems if this method is called by accident when the game has already stopped running 
			return;
		running = false; // the game won't be running now that we're stopping the current thread
		try {
			thread.join(); // stops the current thread safely 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}
	public void init() {
		display = new Display(title, width, height);
	}
	private void tick() { // updates everything for the game (logic)

	}

	private void render() { // render the graphics for the game, redraw the game screen  
		BufferStrategy bufferStrategy;
		Graphics g;
		bufferStrategy = display.getCanvas().getBufferStrategy(); // buffer strategy tells the computer how to draw to the screen and it uses buffers 
		if (bufferStrategy == null) {
			display.getCanvas().createBufferStrategy(3);
			return;
		}
		g = bufferStrategy.getDrawGraphics();
		// clear screen
		g.clearRect(0, 0, width, height);
		
		// test code
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		
		bufferStrategy.show();
		g.dispose();
	}

	// limit how many times tick and render are called every single second so that it runs with the same speed on every computer 
	@Override
	public void run() {
		init();
		int fps = 60; // frames per second a.k.a ticks per second a.k.a the number of times we want to call render() and tick() per second
		double nsTimePerTick = 1000000000.0 / fps; // dividing 1000000000.0 nano second (1 second) by fps to get the time in nano second for one tick 
		long lastTime = System.nanoTime();
		double unprocessed = 0.0;
		
		// test code to check we're running 60 ticks per second 
		long timer = 0;
		int tps = 0; // ticks per second

		while (running) {
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / nsTimePerTick; // the amount of time we have until we have to call render() and tick() methods again // each time we add the difference between now and lastTime we entered this method
			
			timer += now - lastTime;
			
			lastTime = now;

			if (unprocessed >= 1.0) { // means that the difference between now and last time we rendered and ticked is equal or greater than nsTimePerTick				
				tick();
				render();
				unprocessed--;
				tps++;	
			} 
			
			
			/*try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			*/
			if (timer >= 1000000000) { // timer has passes another second 
				System.out.println("ticks per second: " + tps);
				tps = 0;
				timer = 0;
			}
		}
		stop();
	}

	

}
