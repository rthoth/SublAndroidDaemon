package com.app;

public class Boot implements Runnable {
	
	@Override
	public void run() {
		Application app = new Application();

		app.aMethod("", 1, false);

		app.hello();

		app.aMethod("", true);
	}

	public String start() {
		Application app2 = new Application();
		return app2.toString();
	}

	public int stop() {
		Application app3 = new Application(true);

		app3.aMethod(false, true);

		return 0;
	}

	public int resume() {
		Application app4 = new Application();
		app4.aMethod("", 0);
	}
}