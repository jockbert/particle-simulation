package com.okayboom.particlesim.physics

import org.scalacheck.Gen
import scala.util.Random

import com.okayboom.particlesim.physics.Particle._

object PhysicsGen {

  val limit = 10.0;

  def boxedDoubleGen = Gen.chooseNum(-limit, limit)

  def timeGen = Gen.chooseNum(0.0, 1.0)

  def boxedVectorGen = for {
    x <- boxedDoubleGen
    y <- boxedDoubleGen
  } yield Vector.v(x, y)

  def probableCollisionParticleGen = for {
    position <- boxedVectorGen
    velocity <- boxedVectorGen
  } yield Particle.p(position, velocity mult 2);
}
