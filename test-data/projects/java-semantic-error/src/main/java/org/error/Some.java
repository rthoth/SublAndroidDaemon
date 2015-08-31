package org.error;

public class Some {
	public String doSomething();

	public void main(Main main) {
		main.onCreate(null);
		main.onCreatee(null);
	}

	// Ambigous
	public void call() {
		call(5);
	}

	public void callMe(Integer num) {

	}

	public void callMe(Number num) {

	}
}