package com.okayboom.particlesim.simulator;

import java.util.List;
import java.util.Optional;

import com.okayboom.particlesim.SimResult;
import com.okayboom.particlesim.SimSettings;
import com.okayboom.particlesim.Simulator;
import com.okayboom.particlesim.physics.Box;
import com.okayboom.particlesim.physics.Particle;
import com.okayboom.particlesim.physics.Physics;

public class BasicSimulator implements Simulator {

	private static final Physics PHY = new Physics();

	@Override
	public SimResult simulate(final SimSettings settings) {

		List<Particle> particles = SimUtil.particles(settings);

		Box walls = SimUtil.walls(settings);

		double totalMomentum = 0;
		for (int timeStep = 0; timeStep < settings.steps; ++timeStep) {
			totalMomentum += simulateOneStep(particles, walls);
		}

		return SimResult.EMPTY.givenSettings(settings).totalBoxMomentum(
				totalMomentum);
	}

	private double simulateOneStep(List<Particle> particles, Box walls) {
		int particleCount = particles.size();

		boolean[] hasMoved = new boolean[particleCount];

		int totalMomentum = 0;
		for (int i = 0; i < particleCount; ++i) {
			Particle p1 = particles.get(i);

			Optional<Collision> collisionOpt = findCollision(particles, i,
					hasMoved);

			if (collisionOpt.isPresent()) {
				Collision collision = collisionOpt.get();

				hasMoved[collision.otherParticleIndex] = true;

				Particle p2 = particles.get(collision.otherParticleIndex);
				double collisionTime = collision.collisionTime;
				PHY.interact(p1, p2, collisionTime);
			} else {
				PHY.euler(p1, 1);
			}

			hasMoved[i] = true;

			totalMomentum += PHY.wall_collide(p1, walls);
		}

		return totalMomentum;
	}

	private Optional<Collision> findCollision(List<Particle> particles,
			int firstParticleIndex, boolean[] hasMoved) {

		Particle p1 = particles.get(firstParticleIndex);
		for (int j = firstParticleIndex + 1; j < particles.size(); ++j) {

			if (!hasMoved[j]) {

				Particle p2 = particles.get(j);
				double collisionTime = PHY.collide(p1, p2);

				if (collisionTime != Physics.NO_COLLISION)
					return Optional.of(new Collision(j, collisionTime));

			}
		}
		return Optional.empty();
	}
}
