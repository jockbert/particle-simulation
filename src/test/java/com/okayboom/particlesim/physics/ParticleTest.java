package com.okayboom.particlesim.physics;

import static org.junit.Assert.*;

import org.junit.Test;

public class ParticleTest {

	@Test
	public void testBoundingBox() throws Exception {

		Particle p = new Particle(Vector.v(1, 2), Vector.v(10, 20));
		Box bb = p.boundingBox();

		assertEquals(Box.box(0, 1, 12, 23), bb);
	}
}
