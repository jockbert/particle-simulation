package com.okayboom.particlesim.simulator;

public final class Collision {
	final int otherParticleIndex;
	final double collisionTime;

	Collision(int otherParticleIndex, double collisionTime) {
		this.otherParticleIndex = otherParticleIndex;
		this.collisionTime = collisionTime;
	}
}