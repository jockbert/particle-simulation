package com.okayboom.particlesim.simulator;

import com.okayboom.particlesim.SimResult;
import com.okayboom.particlesim.SimSettings;
import com.okayboom.particlesim.Simulator;

public class TimeMeasureSimulator implements Simulator {

	private final Simulator delegate;

	public TimeMeasureSimulator(Simulator delegate) {
		this.delegate = delegate;
	}

	@Override
	public SimResult simulate(SimSettings settings) {
		printSimulationStart(settings);
		long start = System.currentTimeMillis();
		SimResult result = delegate.simulate(settings);
		long duration = System.currentTimeMillis() - start;
		printResult(duration);
		return result;
	}

	private void printSimulationStart(SimSettings settings) {
		System.out.println();
		System.out.println("Running simulation with " + settings);
	}

	private void printResult(long duration) {
		System.out.println("Simulation took " + duration + " ms");
	}
}
