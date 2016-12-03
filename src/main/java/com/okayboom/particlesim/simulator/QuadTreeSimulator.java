package com.okayboom.particlesim.simulator;

import java.util.List;
import java.util.Optional;

import com.okayboom.particlesim.SimResult;
import com.okayboom.particlesim.SimSettings;
import com.okayboom.particlesim.Simulator;
import com.okayboom.particlesim.collision.QuadTree;
import com.okayboom.particlesim.collision.SpatialMap;
import com.okayboom.particlesim.physics.Box;
import com.okayboom.particlesim.physics.Particle;
import com.okayboom.particlesim.physics.Physics;

public class QuadTreeSimulator implements Simulator {

	private static final Physics PHY = new Physics();

	@Override
	public SimResult simulate(final SimSettings settings) {

		List<Particle> particles = SimUtil.particles(settings);
		Box walls = SimUtil.walls(settings);

		double boundaryMargin = settings.maxInitialVelocity;

		double totalMomentum = 0;
		for (int timeStep = 0; timeStep < settings.steps; ++timeStep) {
			totalMomentum += simulateOneStep(particles, walls, boundaryMargin);
		}

		return SimResult.EMPTY.givenSettings(settings).totalBoxMomentum(
				totalMomentum);
	}

	private double simulateOneStep(List<Particle> particles, Box walls,
			double boundaryMargin) {

		boolean[] hasMoved = new boolean[particles.size()];

		SpatialMap<Integer> map = QuadTree.empty(walls, 10, boundaryMargin);

		for (int i = 0; i < particles.size(); ++i) {
			Particle particle = particles.get(i);
			map.add(particle.boundingBox(), i);
		}

		int totalMomentum = 0;
		for (int i = 0; i < particles.size(); ++i) {

			Particle p1 = particles.get(i);

			Optional<Collision> collisionOpt = findCollision(map, p1,
					particles, hasMoved);

			if (collisionOpt.isPresent()) {
				Collision collision = collisionOpt.get();

				hasMoved[collision.otherParticleIndex] = true;

				Particle p2 = particles.get(collision.otherParticleIndex);
				double collisionTime = collision.collisionTime;
				PHY.interact(p1, p2, collisionTime);
			} else {
				p1 = p1.moveUnit();
				particles.set(i, p1);
			}

			hasMoved[i] = true;
			totalMomentum += PHY.wall_collide(p1, walls);
		}

		return totalMomentum;
	}

	private Optional<Collision> findCollision(SpatialMap<Integer> map,
			Particle p, List<Particle> particles, boolean[] hasMoved) {

		List<Integer> candidates = map.get(p.boundingBox());

		if (candidates.size() > 100)
			System.err.println("To many candidates: " + candidates.size());

		for (int candidate : candidates) {

			if (!hasMoved[candidate]) {

				Particle p2 = particles.get(candidate);
				double collisionTime = PHY.collide(p, p2);

				if (collisionTime != Physics.NO_COLLISION)
					return Optional.of(new Collision(candidate, collisionTime));

			}
		}
		return Optional.empty();
	}
}
