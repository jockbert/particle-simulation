package com.okayboom.particlesim.idealgaslaw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.okayboom.particlesim.SimSettings;
import com.okayboom.particlesim.Simulator;
import com.okayboom.particlesim.simulator.QuadTreeSimulator;
import com.okayboom.particlesim.simulator.TimeMeasureSimulator;

public class VerifyGasLawProgram {

	private static Simulator simulator = simulator();
	private static SimToGasLawConverter gasLawConverter = new BasicSimToGasLawConverter();
	private static TextDisplay textDisplay = new TextDisplay();

	public static void main(String[] args) {
		List<SimSettings> settings = settingSeries(baseLineSettings());

		System.out.println(" Running " + settings.size() + " simulations");
		System.out.println(" ===========================================");

		List<GasLawValue> gasLawValues = simulate(settings);
		String textResults = textDisplay.summarizeResults(gasLawValues);

		System.out.println(textResults);
	}

	private static List<GasLawValue> simulate(Collection<SimSettings> settings) {
		return settings.stream().map(simulator::simulate)
				.map(gasLawConverter::convert).collect(Collectors.toList());
	}

	private static Simulator simulator() {
		// return new TimeMeasureSimulator(new BasicSimulator());
		return new TimeMeasureSimulator(new QuadTreeSimulator());
	}

	private static List<SimSettings> settingSeries(SimSettings baseline) {
		ArrayList<SimSettings> settings = new ArrayList<>();

		SimSettings bl = baseline; // just alias rename

		settings.add(bl);

		settings.add(bl.settingsName("Half Width").boxWidth(bl.boxWidth / 2));
		settings.add(bl.settingsName("Double Width").boxWidth(bl.boxWidth * 2));

		settings.add(bl.settingsName("Half Particle Count").particleCount(
				bl.particleCount / 2));
		settings.add(bl.settingsName("Double Particle Count").particleCount(
				bl.particleCount * 2));
		settings.add(bl.settingsName("Quad Particle Count").particleCount(
				bl.particleCount * 4));
		return settings;
	}

	private static SimSettings baseLineSettings() {
		return SimSettings.EMPTY.settingsName("Baseline")
				.maxInitialVelocity(24).particleCount(2000).boxWidth(1000)
				.boxHeight(1000).steps(500);
	}
}
