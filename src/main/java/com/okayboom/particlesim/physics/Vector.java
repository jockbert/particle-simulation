package com.okayboom.particlesim.physics;

public final class Vector {
	public double x;
	public double y;

	Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static Vector v(double x, double y) {
		return new Vector(x, y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		long temp;
		temp = Double.doubleToLongBits(x);
		int result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		return Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public Vector mult(double factor) {
		return new Vector(x * factor, y * factor);
	}

	public Vector add(Vector other) {
		return new Vector(x + other.x, y + other.y);
	}

	public double abs() {
		return Math.sqrt(x * x + y * y);
	}

	public Vector min(Vector v2) {
		return v(Math.min(x, v2.x), Math.min(y, v2.y));
	}

	public Vector max(Vector v2) {
		return v(Math.max(x, v2.x), Math.max(y, v2.y));
	}

	public Vector sub(Vector other) {
		return v(x - (other.x), y - (other.y));
	}

	public Vector copy() {
		return v(x, y);
	}
}
