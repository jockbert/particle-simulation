package com.okayboom.particlesim.physics

import org.junit.Test
import org.scalacheck.Prop._

import com.kastrull.scalachecktojunit.PropertiesToJUnit
import com.okayboom.particlesim.physics.PhysicsGen._

class PhysicsProperties extends PropertiesToJUnit("Physics") {

  val particleGen = probableCollisionParticleGen
  val legacy = new LegacyPhysics();
  val novus = new Physics();

  val smallBox = Box.box(-limit / 2, -limit / 2, limit / 2, limit / 2)

  property("wall_collide works just as legacy code") = forAll(particleGen) {
    (a: Particle) =>
      val legacyPressure: Double = legacy.wall_collide(a.copy(), smallBox)
      val novusPressure: Double = novus.wall_collide(a.copy(), smallBox);

      legacyPressure.equals(novusPressure)
  }

  property("collide works just as legacy code") = forAll(particleGen, particleGen) {
    (a: Particle, b: Particle) =>

      val legacyTime = legacy.collide(a, b)
      val novusTime = novus.collide(a, b)

      legacyTime.equals(novusTime)
  }

  property("interact works just as legacy code") = forAll(particleGen, particleGen, timeGen) {
    (a: Particle, b: Particle, time: Double) =>

      val aLegacy = a.copy();
      val bLegacy = b.copy();

      val aNovus = a.copy();
      val bNovus = b.copy();

      legacy.interact(aLegacy, bLegacy, time)
      novus.interact(aNovus, bNovus, time)

      aLegacy.equals(aNovus) && bLegacy.equals(bNovus)
  }

  @Test
  def test() = testScalaCheckProperies()
}
