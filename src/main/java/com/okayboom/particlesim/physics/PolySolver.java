package com.okayboom.particlesim.physics;

import java.util.Arrays;
import java.util.stream.Stream;

public class PolySolver {

	/**
	 * Polynomial on the form p(t) = a * t^2 + b * t + c
	 */
	static final class SecondDegPolynomial {
		private final double a;
		private final double b;
		private final double c;

		SecondDegPolynomial(double a, double b, double c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		static SecondDegPolynomial create(double a, double b, double c) {
			return new SecondDegPolynomial(a, b, c);
		}
	}

	/** Polynomial to solve: <code>0 = a * t^2 + b * t + c </code> */
	static Stream<Double> findRealRoots(double a, double b, double c) {
		SecondDegPolynomial poly = SecondDegPolynomial.create(a, b, c);
		return PolySolver.create().findRealRoots(poly);
	}

	static PolySolver create() {
		return new PolySolver();
	}

	/**
	 * Polynomial to solve: <code>0 = a * t^2 + b * t + c </code> <br>
	 * Found roots are Real numbers on the form:
	 * <code>t = -b/a +- sqrt((b/a)^2-c/a)</code>
	 *
	 * @param poly
	 *            The polynomial to solve
	 * @return
	 */
	Stream<Double> findRealRoots(SecondDegPolynomial poly) {
		if (poly.a == 0)
			// has problem with division by zero
			return streamOf();

		double partInSqrt = (poly.b * poly.b / poly.a - poly.c) / poly.a;
		double term1 = -poly.b / poly.a;

		if (partInSqrt < 0)
			// no real solution exists
			return streamOf();
		if (partInSqrt == 0)
			// only one solution exists
			return streamOf(term1);
		if (poly.c == 0)
			// avoid numerical problem x-Math.sqrt(x*x) != 0
			return streamOf(0.0, term1 * 2);

		double term2 = Math.sqrt(partInSqrt);
		return streamOf(term1 - term2, term1 + term2);
	}

	private Stream<Double> streamOf(Double... roots) {
		return Arrays.asList(roots).stream();
	}
}