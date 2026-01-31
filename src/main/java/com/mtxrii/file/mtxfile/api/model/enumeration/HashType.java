package com.mtxrii.file.mtxfile.api.model.enumeration;

public enum HashType {
    SHA_224("SHA-224"),
    SHA_256("SHA-256"),
    SHA_384("SHA-384"),
    SHA_512("SHA-512"),
    SHA_512_224("SHA-512/224"),
    SHA_512_256("SHA-512/256"),
    MD2("MD2"),
    MD5("MD5");

    private final String key;

    HashType(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public static HashType fromKey(String key) {
        for (HashType hashType : HashType.values()) {
            if (hashType.getKey().equals(key)) {
                return hashType;
            }
        }
        return null;
    }
}
