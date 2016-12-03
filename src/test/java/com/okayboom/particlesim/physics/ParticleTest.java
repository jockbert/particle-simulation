package com.okayboom.particlesim.physics;

import static org.junit.Assert.*;
import static com.okayboom.particlesim.physics.Vector.*;

import org.junit.Test;

public class ParticleTest {

	Particle p = new Particle(v(1, 2), v(10, 20));

	@Test
	public void testBoundingBox() throws Exception {
		Box bb = p.boundingBox();
		assertEquals(Box.box(0, 1, 12, 23), bb);
	}

	@Test
	public void testMove() throws Exception {
		Particle p2 = p.move(0.5);
		Particle expected = new Particle(v(6, 12), v(10, 20));

		assertEquals(expected, p2);
	}

	@Test
	public void testMoveUnit() throws Exception {
		Particle p2 = p.moveUnit();
		Particle expected = new Particle(v(11, 22), v(10, 20));
		assertEquals(expected, p2);
	}
}
