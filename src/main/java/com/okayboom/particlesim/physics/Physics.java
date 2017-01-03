package com.okayboom.particlesim.physics;

import java.util.Optional;

public class Physics {

	private double abs(double n) {
		return n < 0 ? -n : n;
	}

	private double sqr(double n) {
		return n * n;
	}

	/**
	 * wall_collide checks if a particle has exceeded the boundary and returns a
	 * momentum. Use this momentum to calculate the pressure.
	 */
	public double wall_collide(Particle p, Box box) {
		double gPreassure = 0.0;

		if (p.position.x < box.min.x) {
			p.velocity.x = -p.velocity.x;
			p.position.x = box.min.x + (box.min.x - p.position.x);
			gPreassure += abs(p.velocity.x);
		}
		if (p.position.x > box.max.x) {
			p.velocity.x = -p.velocity.x;
			p.position.x = box.max.x - (p.position.x - box.max.x);
			gPreassure += abs(p.velocity.x);
		}
		if (p.position.y < box.min.y) {
			p.velocity.y = -p.velocity.y;
			p.position.y = box.min.y + (box.min.y - p.position.y);
			gPreassure += abs(p.velocity.y);
		}
		if (p.position.y > box.max.y) {
			p.velocity.y = -p.velocity.y;
			p.position.y = box.max.y - (p.position.y - box.max.y);
			gPreassure += abs(p.velocity.y);
		}

		return 2.0 * gPreassure;
	}

	/**
	 * The routine collide returns -1 if there will be no collision this time
	 * step, otherwise it will return when the collision occurs.
	 */
	public Optional<Double> collide(Particle p1, Particle p2) {
		double a, b, c;
		double temp, t1, t2;

		a = sqr(p1.velocity.x - p2.velocity.x)
				+ sqr(p1.velocity.y - p2.velocity.y);
		b = 2 * ((p1.position.x - p2.position.x)
				* (p1.velocity.x - p2.velocity.x) + (p1.position.y - p2.position.y)
				* (p1.velocity.y - p2.velocity.y));
		c = sqr(p1.position.x - p2.position.x)
				+ sqr(p1.position.y - p2.position.y) - 4 * 1 * 1;

		if (a != 0.0) {
			temp = sqr(b) - 4 * a * c;
			if (temp >= 0) {
				temp = Math.sqrt(temp);
				t1 = (-b + temp) / (2 * a);
				t2 = (-b - temp) / (2 * a);

				if (t1 > t2) {
					temp = t1;
					t1 = t2;
					t2 = temp;
				}
				if ((t1 >= 0) & (t1 <= 1))
					return Optional.of(t1);
				else if ((t2 >= 0) & (t2 <= 1))
					return Optional.of(t2);
			}
		}
		return Optional.empty();
	}

	/** The routine interact moves two particles involved in a collision. */
	public void interact(Particle p1, Particle p2, double t) {
		double c, s, a, b, tao;
		Particle p1temp = new Particle(new Vector(0, 0), new Vector(0, 0));
		Particle p2temp = new Particle(new Vector(0, 0), new Vector(0, 0));

		if (t >= 0) {

			/* Move to impact point */
			p1 = p1.move(t);
			p2 = p2.move(t);

			/* Rotate the coordinate system around p1 */
			p2temp.position.x = p2.position.x - p1.position.x;
			p2temp.position.y = p2.position.y - p1.position.y;

			/* Givens plane rotation, Golub, van Loan p. 216 */
			a = p2temp.position.x;
			b = p2temp.position.y;
			if (p2.position.y == 0) {
				c = 1;
				s = 0;
			} else {
				if (abs(b) > abs(a)) {
					tao = -a / b;
					s = 1 / (Math.sqrt(1 + sqr(tao)));
					c = s * tao;
				} else {
					tao = -b / a;
					c = 1 / (Math.sqrt(1 + sqr(tao)));
					s = c * tao;
				}
			}

			/* This should be equal to 2r */
			p2temp.position.x = c * p2temp.position.x + s * p2temp.position.y;
			p2temp.position.y = 0.0;

			p2temp.velocity.x = c * p2.velocity.x + s * p2.velocity.y;
			p2temp.velocity.y = -s * p2.velocity.x + c * p2.velocity.y;
			p1temp.velocity.x = c * p1.velocity.x + s * p1.velocity.y;
			p1temp.velocity.y = -s * p1.velocity.x + c * p1.velocity.y;

			/* Assume the balls has the same mass... */
			p1temp.velocity.x = -p1temp.velocity.x;
			p2temp.velocity.x = -p2temp.velocity.x;

			p1.velocity.x = c * p1temp.velocity.x - s * p1temp.velocity.y;
			p1.velocity.y = s * p1temp.velocity.x + c * p1temp.velocity.y;
			p2.velocity.x = c * p2temp.velocity.x - s * p2temp.velocity.y;
			p2.velocity.y = s * p2temp.velocity.x + c * p2temp.velocity.y;

			/* Move the balls the remaining time. */
			c = 1.0 - t;
			p1 = p1.move(c);
			p2 = p2.move(c);
		}
	}
}
