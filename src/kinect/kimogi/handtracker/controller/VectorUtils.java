package kinect.kimogi.handtracker.controller;

public class VectorUtils {
	public static double norm(Integer[] vector) {
		long sum = 0;
		for (int i = 0; i < vector.length; i++) {
			sum += vector[i] * vector[i];
		}
		return Math.sqrt(sum);
	}

	public static long dotProduct(Integer[] vector1, Integer[] vector2) {
		long dotProduct = 0;
		if (vector1.length != vector2.length) {
			System.out.println("Not equal vectors lengths : " + vector1.length + " " + vector2.length);
		} else {
			for (int i = 0; i < vector1.length; i++) {
				dotProduct += vector1[i] * vector2[i];
			}
		}
		return dotProduct;
	}
	
	public static double resemblance(Integer[] curve1, Integer[] curve2) {
		return Math.abs(dotProduct(curve1, curve2) / (norm(curve1) * norm(curve2)));
	}
}
