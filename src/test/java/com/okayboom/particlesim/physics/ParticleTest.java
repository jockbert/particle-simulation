package com.okayboom.particlesim.physics;

import static org.junit.Assert.*;
import static com.okayboom.particlesim.physics.Vector.*;

import org.junit.Test;

public class ParticleTest {

	Vector pos = v(1, 2);
	Vector vel = v(10, 20);
	Particle p = new Particle(pos, vel);

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

	@Test
	public void testCopy() throws Exception {

		Particle v = this.p;
		Particle vAlias = v;

		Particle w = new Particle(pos, vel);
		Particle vCopy = v.copy();

		assertEquals("Two instances be equal", v, w);
		assertEquals("Copy should be equal", v, vCopy);

		assertTrue("Same instance", v == vAlias);
		assertTrue("Different instance", v != w);
		assertTrue("Copy instance", v != vCopy);

		assertEquals("Position should be equal", v.position, vCopy.position);
		assertTrue("Copy instance of position", v.position != vCopy.position);

		assertEquals("Velocity should be equal", v.velocity, vCopy.velocity);
		assertTrue("Copy instance of velocity", v.velocity != vCopy.velocity);
	}
}
