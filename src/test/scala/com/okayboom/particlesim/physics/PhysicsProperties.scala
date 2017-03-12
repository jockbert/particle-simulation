package com.okayboom.particlesim.physics

import org.junit.Test
import org.scalacheck.Prop._

import com.kastrull.scalachecktojunit.PropertiesToJUnit
import com.okayboom.particlesim.physics.PhysicsGen._

import scala.language.implicitConversions

class PhysicsProperties extends PropertiesToJUnit("Physics") {

  type JavaOpt = java.util.Optional[java.lang.Double]
  type ScalaOpt = Option[Double]

  implicit def javaToScalaOpt(opt: JavaOpt): ScalaOpt =
    if (opt.isPresent) Some(opt.get())
    else None

  def approxEq(expected: ScalaOpt, actual: ScalaOpt, delta: Double) = {
    val doEqual = (expected, actual) match {
      case (Some(e), Some(a)) => (e - a).abs <= delta
      case (None, None)       => true
      case _                  => false
    }
    if (!doEqual) println(s"Expected $expected but got $actual")
    doEqual
  }

  val particles = probableCollisionParticleGen
  val legacy = new LegacyPhysics();
  val novus = new Physics();

  val smallBox = Box.box(-limit / 2, -limit / 2, limit / 2, limit / 2)

  property("wall_collide works just as legacy code") = forAll(particles) {
    (a: Particle) =>
      val legacyA = a.copy();
      val novusA = a.copy();

      val legacyPressure: Double = legacy.wall_collide(legacyA, smallBox)
      val wallHit = novus.wall_collide(novusA, smallBox);

      val novusPressure = wallHit.pressure.orElse(0.0);
      val novusA2 = wallHit.particle

      (novusA2 ?= legacyA) && (novusPressure ?= legacyPressure)
  }

  property("collide works just as in legacy code") = forAll(particles, particles) {
    (a: Particle, b: Particle) =>

      val legacyTime = legacy.collide(a, b)
      val novusTime = novus.collide(a, b)

      approxEq(legacyTime, novusTime, 10e-15)
  }

  property("interact works just as legacy code") = forAll(particles, particles, timeGen) {
    (a: Particle, b: Particle, time: Double) =>

      val aLegacy = a.copy();
      val bLegacy = b.copy();

      val aNovus = a.copy();
      val bNovus = b.copy();

      legacy.interact(aLegacy, bLegacy, time)
      novus.interact(aNovus, bNovus, time)

      (aNovus ?= aLegacy) && (bNovus ?= bLegacy)
  }

  @Test
  def test() = testScalaCheckProperies()
}
