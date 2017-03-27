package com.okayboom.particlesim.physics;

public final class Particle {
	private static final Vector NEG_RADIUS = Vector.v(-1, -1);
	private static final Vector POS_RADIUS = Vector.v(1, 1);

	public Vector position;
	public Vector velocity;

	public Particle(Vector position, Vector velocity) {
		this.position = position;
		this.velocity = velocity;
	}

	public static final Particle p(Vector position, Vector velocity) {
		return new Particle(position, velocity);
	}

	public Box boundingBox() {
		Vector positionPlus = position.add(POS_RADIUS);
		Vector positionMinus = position.add(NEG_RADIUS);

		Vector nextPosition = position.add(velocity);

		Vector nextPositionPlus = nextPosition.add(POS_RADIUS);
		Vector nextPositionMinus = nextPosition.add(NEG_RADIUS);

		Vector boxMin = positionMinus.min(nextPositionMinus);
		Vector boxMax = positionPlus.max(nextPositionPlus);

		return Box.box(boxMin, boxMax);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result;
		result = ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((velocity == null) ? 0 : velocity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Particle other = (Particle) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (velocity == null) {
			if (other.velocity != null)
				return false;
		} else if (!velocity.equals(other.velocity))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Particle [pos=" + position + ", vel=" + velocity + "]";
	}

	public Particle move(double time_step) {
		Vector newPosition = position.add(velocity.mult(time_step));
		return new Particle(newPosition, velocity.copy());
	}

	public Particle moveUnit() {
		return new Particle(position.add(velocity), velocity);
	}

	@Deprecated
	public Particle copy() {
		return p(position.copy(), velocity.copy());
	}

	public Particle subtract(Particle other) {
		Vector diffVelocity = velocity.sub(other.velocity);
		Vector diffPosition = position.sub(other.position);
		return Particle.p(diffPosition, diffVelocity);
	}

	@Deprecated
	public void mutableMove(double time_step) {
		position = position.add(velocity.mult(time_step));
	}
}
