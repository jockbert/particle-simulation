package com.okayboom.particlesim.collision;

import static com.okayboom.particlesim.physics.Box.box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.okayboom.particlesim.physics.Box;

public class QuadTree<V> implements SpatialMap<V> {

	private final static int DEFAULT_LEAF_LIMIT = 8;

	private final int leafLimit;
	private List<Pair> values = new ArrayList<>();
	private QuadTree<V>[] childTrees = null;
	private Box boundary;

	public QuadTree(Box boundary, int leafLimit) {
		this.boundary = boundary;
		this.leafLimit = leafLimit;
	}

	private Pair pair(V value, Box box) {
		return new Pair(value, box);
	}

	class Pair {
		final V value;
		final Box box;

		Pair(V value, Box box) {
			this.value = value;
			this.box = box;
		}

		@Override
		public String toString() {
			return "(v=" + value + ", " + box + ")";
		}

	}

	public static final <X> QuadTree<X> empty(Box boundary) {
		return new QuadTree<X>(boundary, DEFAULT_LEAF_LIMIT);
	}

	public static final <X> QuadTree<X> empty(Box boundary, int leafLimit) {

		return new QuadTree<X>(boundary, leafLimit);
	}

	public boolean isLeaf() {
		return childTrees == null;
	}

	@Override
	public void add(Box box, V value) {
		Pair p = pair(value, box);

		if (isLeaf()) {
			values.add(p);

			boolean hasReachedLimit = values.size() > leafLimit;
			if (hasReachedLimit) {
				transformFromLeafToInnerNode();
			}
		} else {
			distributeToChildren(p);
		}
	}

	private void distributeToChildren(Pair p) {
		for (QuadTree<V> child : childTrees) {
			if (child.boundary.doSorround(p.box)) {
				child.add(p.box, p.value);
				return;
			}
		}
		values.add(p);
	}

	@SuppressWarnings("unchecked")
	private void transformFromLeafToInnerNode() {
		Box minMinBox = box(boundary.mid(), boundary.min);
		Box maxMaxBox = box(boundary.mid(), boundary.max);
		Box minMaxBox = box(boundary.mid(), boundary.minMax());
		Box maxMinBox = box(boundary.mid(), boundary.maxMin());

		childTrees = (QuadTree<V>[]) new QuadTree<?>[4];

		childTrees[0] = empty(minMinBox, leafLimit);
		childTrees[1] = empty(maxMaxBox, leafLimit);
		childTrees[2] = empty(minMaxBox, leafLimit);
		childTrees[3] = empty(maxMinBox, leafLimit);

		List<QuadTree<V>.Pair> oldValues = values;
		values = new ArrayList<>();

		for (Pair oldValue : oldValues)
			distributeToChildren(oldValue);
	}

	@Override
	public List<V> get(Box box) {
		List<V> intersectingValue = new ArrayList<>();
		get(intersectingValue, box);
		return intersectingValue;
	}

	private void get(List<V> result, Box box) {

		for (Pair value : values) {
			if (value.box.doIntersect(box))
				result.add(value.value);
		}

		if (!isLeaf()) {
			for (QuadTree<V> childTree : childTrees) {
				if (childTree.boundary().doIntersect(box))
					childTree.get(result, box);
			}
		}
	}

	public Box boundary() {
		return boundary;
	}

	public List<QuadTree<V>> childTrees() {
		return isLeaf() ? null : Arrays.asList(childTrees);
	}

	public List<Pair> values() {
		return values;
	}

	public int size() {
		return values.size()
				+ (isLeaf() ? 0 : childSize(0) + childSize(1) + childSize(2)
						+ childSize(3));
	}

	@Override
	public String toString() {
		String treeRepresentation = isLeaf() ? "is leaf" : childSize(0) + " "
				+ childSize(1) + " " + childSize(2) + " " + childSize(3);
		return "QuadTree(" + leafLimit + ")[" + treeRepresentation
				+ ", valCount=" + values.size() + ", bound=" + boundary + "]";
	}

	private int childSize(int index) {
		return childTrees[index].size();
	}
}
