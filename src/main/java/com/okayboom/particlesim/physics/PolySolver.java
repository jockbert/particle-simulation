package com.okayboom.particlesim.physics;

import java.util.Arrays;
import java.util.stream.Stream;

import com.codepoetics.protonpack.StreamUtils;

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
	 * @param poly The polynomial to solve
	 * @return
	 */
	Stream<Double> findRealRoots(SecondDegPolynomial poly) {
		return StreamUtils.ofSingleNullable(poly).filter(this::isFreeOfDivByZeroProblem)
				.filter(this::isPositiveSqrt).flatMap(this::rootsOf);
	}

	private boolean isFreeOfDivByZeroProblem(SecondDegPolynomial poly) {
		return poly.a != 0;
	}

	private boolean isPositiveSqrt(SecondDegPolynomial poly) {
		return partInSqrt(poly) >= 0;
	}

	private Stream<Double> rootsOf(SecondDegPolynomial poly) {
		double term1 = -poly.b / poly.a;
		double term2 = Math.sqrt(partInSqrt(poly));
		return Arrays.asList(term1 - term2, term1 + term2).stream();
	}

	private double partInSqrt(SecondDegPolynomial poly) {
		double term1 = poly.b / poly.a;
		return term1 * term1 - poly.c / poly.a;
	}
}