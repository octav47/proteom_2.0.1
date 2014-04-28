/**
 * Created by Kir on 28.04.14.
 */
public class PairP<K, V> {
    private K key;
    private V value;

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public PairP(K k, V v) {
        this.key = k;
        this.value = v;
    }

    public java.lang.String toString() {
        return this.key + "~" + this.value;
    }

    //public int hashCode() { /* compiled code */ }

    public boolean equals(PairP<K, V> p) {
        return this.key.equals(p.key) && this.value.equals(p.value);
    }
}
