package com.okayboom.particlesim.simulator;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.codepoetics.protonpack.StreamUtils;
import com.okayboom.particlesim.SimSettings;
import com.okayboom.particlesim.physics.Box;
import com.okayboom.particlesim.physics.Particle;
import com.okayboom.particlesim.physics.Vector;

public class SimUtil {

	static List<Particle> particles(SimSettings settings) {
		return particleStream(settings).limit(settings.particleCount).collect(
				Collectors.toList());
	}

	static private Stream<Particle> particleStream(SimSettings settings) {
		Random rnd = new Random();
		Stream<Vector> velos = velocityStream(settings, rnd);
		Stream<Vector> poses = positionStream(settings, rnd);
		return StreamUtils.zip(poses, velos, (s, v) -> new Particle(s, v));
	}

	static private Stream<Vector> positionStream(SimSettings settings,
			Random rnd) {
		Stream<Double> xPos = rnd.doubles(0, settings.boxWidth).boxed();
		Stream<Double> yPos = rnd.doubles(0, settings.boxWidth).boxed();
		return StreamUtils.zip(xPos, yPos, (x, y) -> Vector.v(x, y));
	}

	static private Stream<Vector> velocityStream(SimSettings settings,
			Random rnd) {
		Stream<Double> radians = rnd.doubles(0, Math.PI * 2).boxed();
		Stream<Double> speeds = rnd.doubles(0, settings.maxInitialVelocity)
				.boxed();

		return StreamUtils.zip(radians, speeds, (r, s) -> {
			double x = s * Math.cos(r);
			double y = s * Math.sin(r);
			return Vector.v(x, y);
		});
	}

	public static Box walls(SimSettings settings) {
		return new Box(Vector.v(0, 0), Vector.v(settings.boxWidth,
				settings.boxHeight));
	}

}
