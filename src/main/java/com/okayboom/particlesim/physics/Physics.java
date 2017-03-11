package com.okayboom.particlesim.physics;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Predicate;

public class Physics {

	private static final double NO_COLLISION = 0.0;

	static class WallHit {

		final OptionalDouble pressure;
		final Particle particle;

		public WallHit(Particle p, OptionalDouble pressure) {
			particle = p;
			this.pressure = pressure;
		}

		public static WallHit noHit(Particle p) {
			return new WallHit(p, OptionalDouble.empty());
		}

		public static WallHit hit(Particle p, double pressure) {
			return new WallHit(p, OptionalDouble.of(pressure));
		}
	}

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
	public WallHit wall_collide(Particle p, Box box) {

		Particle q = p.copy();
		double gPreassure = NO_COLLISION;

		if (q.position.x < box.min.x) {
			q.velocity.x = -q.velocity.x;
			q.position.x = box.min.x + (box.min.x - q.position.x);
			gPreassure += abs(q.velocity.x);
		}
		if (q.position.x > box.max.x) {
			q.velocity.x = -q.velocity.x;
			q.position.x = box.max.x - (q.position.x - box.max.x);
			gPreassure += abs(q.velocity.x);
		}
		if (q.position.y < box.min.y) {
			q.velocity.y = -q.velocity.y;
			q.position.y = box.min.y + (box.min.y - q.position.y);
			gPreassure += abs(q.velocity.y);
		}
		if (q.position.y > box.max.y) {
			q.velocity.y = -q.velocity.y;
			q.position.y = box.max.y - (q.position.y - box.max.y);
			gPreassure += abs(q.velocity.y);
		}

		return NO_COLLISION == gPreassure ? WallHit.noHit(q) : WallHit.hit(q, 2.0 * gPreassure);
	}

	/**
	 * The routine collide returns no value if there will be no collision this
	 * time step, otherwise it will return when in time the collision occurs.
	 */
	public Optional<Double> collide(Particle p1, Particle p2) {

		Vector deltaVelocity = p1.velocity.sub(p2.velocity);
		Vector deltaPosition = p1.position.sub(p2.position);
		Particle q = Particle.p(deltaPosition, deltaVelocity);

		double a = sqr(q.velocity.x) + sqr(q.velocity.y);
		double b = q.position.x * q.velocity.x + q.position.y * q.velocity.y;
		double c = sqr(q.position.x) + sqr(q.position.y) - 4;

		boolean hasExactSameVelocity = a == 0.0;
		if (hasExactSameVelocity) {
			// distance is stationary over time and will never collide.
			return Optional.empty();
		}

		double temp = sqr(b / a) - c / a;
		boolean hasSqrtSolution = temp >= 0;
		if (!hasSqrtSolution) { // polynomial has no real solution
			Optional.empty();
		}

		temp = Math.sqrt(temp);
		double t1 = -b / a + temp;
		double t2 = -b / a - temp;

		double tMin = Math.min(t1, t2);
		double tMax = Math.max(t1, t2); // StreamUtils.ofNullable(t1, t2);
		if ((tMin >= 0) & (tMin <= 1))
			return Optional.of(tMin);
		else if ((tMax >= 0) & (tMax <= 1))
			return Optional.of(tMax);
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
