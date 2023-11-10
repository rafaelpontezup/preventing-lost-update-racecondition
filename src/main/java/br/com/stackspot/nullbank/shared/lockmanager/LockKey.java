package br.com.stackspot.nullbank.shared.lockmanager;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents the key used to acquire an Advisory Lock
 */
public final class LockKey {

    private final String[] keys;

    private LockKey(String[] keys) {
        this.keys = keys;
    }

    /**
     * Returns the actual lock key
     */
    public String getKey() {
        return String
                .join(":", keys);
    }

    @Override
    public String toString() {
        return "LockKey{" +
                "actualKey=" + getKey() +
                "keys=" + Arrays.toString(keys) +
                '}';
    }

    /**
     * Creates a key that will be used to acquire a lock from database
     * @param keys one or more keys that will be concatenated to generate the actual lock key
     * @return an instance of {@code LockKey}
     */
    public static LockKey of(Serializable...keys) {
        if (keys == null)
            throw new IllegalArgumentException("Key(s) can not be null");

        String[] textKeys = Arrays.stream(keys).map(k -> {
            if (k == null) return "null";
            return k.toString();
        }).toArray(String[]::new);

        return new LockKey(textKeys);
    }

}
