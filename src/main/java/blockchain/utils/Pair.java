package blockchain.utils;

import java.io.Serializable;

public class Pair<T, E> implements Serializable {
	private static final long serialVersionUID = -413053001478103703L;
	T t;
	E e;

	public Pair(T t, E e) {
		this.t = t;
		this.e = e;
	}

	public String toString() {
		return "(" + t.toString() + "," + e.toString() + ")";
	}

	public boolean equals(Object o) {
		if (!(o instanceof Pair || o == null))
			return false;
		@SuppressWarnings("unchecked")
		Pair<T, E> tuple = (Pair<T, E>) o;
		if (tuple.e == this.e && tuple.t == this.t)
			return true;
		return false;

	}

	public int hashCode() {
		return t.hashCode() + e.hashCode();
	}

	public T getX() {
		return t;
	}

	public E getY() {
		return e;
	}

	public void setX(T t) {
		this.t = t;
	}

	public void setY(E e) {
		this.e = e;
	}

	public void setAll(T t, E e) {
		this.t = t;
		this.e = e;
	}
}
