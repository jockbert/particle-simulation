package com.okayboom.particlesim.physics

import org.junit.Test
import org.scalacheck.Prop._

import com.kastrull.scalachecktojunit.PropertiesToJUnit
import com.okayboom.particlesim.physics.PhysicsGen._

import com.okayboom.particlesim.physics.Particle._
import com.okayboom.particlesim.physics.Vector._

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
    (particle: Particle) =>
      val legacyParticle = particle.copy();
      val novusParticle = particle.copy();

      val legacyPressure: Double = legacy.wall_collide(legacyParticle, smallBox)
      val wallHit = novus.wall_collide(novusParticle, smallBox);

      val novusPressure = wallHit.pressure.orElse(0.0);
      val novusA2 = wallHit.particle

      (novusA2 ?= legacyParticle) && (novusPressure ?= legacyPressure)
  }

  property("collide works just as in legacy code") = forAll(particles, particles)(collideWorksAsLegacy)

  def collideWorksAsLegacy(a: Particle, b: Particle) = {

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
      val particles = novus.interact(aNovus, bNovus, time)

      (particles.a ?= aLegacy) && (particles.b ?= bLegacy)
  }

  @Test
  def test() = testScalaCheckProperies()

  @Test
  def collideWorksAsLegacyBug_singleSolution() {
    val a = p(v(-1.0, 0.0), v(-20.0, 20.0));
    val b = p(v(1.0, 10.0), v(-20.0, -2.0));
    assert(collideWorksAsLegacy(a, b));
  }

  @Test
  def collideWorksAsLegacyBug_collideAtTimeZero() {
    val a = p(v(0.0, 0.0), v(0.0, 2.0));
    val b = p(v(2.0, 0.0), v(-11.943185085924132, -15.765249773787602));
    assert(collideWorksAsLegacy(a, b));
  }

  @Test
  def collideWorksAsLegacyBug_secondPolyParamIsZero() {
    val a = p(v(1.0, -1.0), v(2.0, -2.0));
    val b = p(v(1.0, 1.0), v(-3.630258016724671, -2.0));
    assert(collideWorksAsLegacy(a, b));
  }

  @Test
  def collideWorksAsLegacyBug_3() {
    val a = p(v(-1.0, -1.0), v(-2.0, 7.228226294920908));
    val b = p(v(1.0, 1.0), v(-3.630258016724671, -2.0));
    assert(collideWorksAsLegacy(a, b));
  }

  @Test
  def collideWorksAsLegacyBug_samePosition() {
    val a = p(v(10.0, 10.0), v(0.0, 0.0));
    val b = p(v(10.0, 10.0), v(0.0, 5.0));
    assert(collideWorksAsLegacy(a, b));
  }
}
