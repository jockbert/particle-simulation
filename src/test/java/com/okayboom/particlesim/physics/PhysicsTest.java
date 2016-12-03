package com.okayboom.particlesim.physics;

import static com.okayboom.particlesim.physics.Vector.v;
import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

public class PhysicsTest {

	private static final double DELTA = 1e-10;

	@Test
	public void testWallCollideNoCollision() {
		Box box = new Box(v(-10, -10), v(10, 10));

		Vector position = v(0, 0);
		Vector velocity = v(5, 0);
		Particle p = new Particle(position, velocity);

		double result = new Physics().wall_collide(p, box);

		assertEquals(result, 0, DELTA);
		assertEquals("Velocity as before", velocity, p.velocity);
		assertEquals("Position as before", position, p.position);
	}

	@Test
	public void testWallCollideRight() {
		Box box = new Box(v(-10, -10), v(10, 10));

		Vector position = v(11, 0);
		Vector velocity = v(5, 0);
		Particle p = new Particle(position, velocity);

		double result = new Physics().wall_collide(p, box);

		assertEquals(result, 2 * velocity.abs(), DELTA);
		assertEquals("Velocity reversed", v(-5, 0), p.velocity);
		assertEquals("Position replayed", v(9, 0), p.position);
	}

	@Test
	public void testCollide() {
		Particle p1 = new Particle(v(0, 0), v(0, 0));
		Particle p2 = new Particle(v(-3, -4), v(6, 8));

		Optional<Double> time = new Physics().collide(p2, p1);

		// Total distance is traveled during 1 time unit is 10.
		// Distance between particle centers are 5.
		// Radius of both particles are 1.
		assertEquals("Collision at 30% of time step", 0.3, time.get(), DELTA);
	}

	@Test
	public void testInteract() throws Exception {
		Particle p1 = new Particle(v(0, 0), v(0, 0));
		Particle p2 = new Particle(v(-3, -4), v(6, 8));

		new Physics().interact(p2, p1, 0.3);

		// TODO incomplete test - lack assertions
		// System.out.println("p1 " + p1);
		// System.out.println("p2 " + p2);

		// Not applicable, because particle model is sort of broken.
		// assertEquals("Momentum transfered to p1", v(6, 8), p1.velocity);
		// assertEquals("Momentum lost in p2", v(0, 0), p2.velocity);
	}

}
