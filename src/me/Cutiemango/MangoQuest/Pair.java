package me.Cutiemango.MangoQuest;

import java.util.Objects;

public class Pair<K, V>
{
	private final K key;
	private final V value;

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	public boolean equals(Object o) {
		if (!(o instanceof Pair))
			return false;
		Pair pairObj = (Pair) o;
		return key.equals(pairObj.getKey()) && value.equals(pairObj.getValue());
	}
}