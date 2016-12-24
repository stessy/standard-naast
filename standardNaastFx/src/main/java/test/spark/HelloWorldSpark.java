package test.spark;

import spark.Spark;

public class HelloWorldSpark {

	public static void main(final String[] args) {
		// TODO Auto-generated method stub

		Spark.get("/hello", (req, res) -> "Hello World");

	}

}
