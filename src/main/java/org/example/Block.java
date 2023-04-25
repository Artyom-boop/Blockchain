package org.example;

import com.google.common.hash.Hashing;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class Block implements Serializable {

    private final long index;
    private final String prevHash;
    private final String data;
    private String hash;
    private long nonce;


    public Block(long index, String prev_hash, String data) {
        this.index = index;
        this.prevHash = prev_hash;
        this.data = data;
    }

    public String calculationHash(String data) {
        String str = index + prevHash + data;
        String hash = "";
        while (!hash.endsWith("0000")) {
            String current = nonce + str;
            hash = Hashing.sha256().hashString(current, StandardCharsets.UTF_8).toString();
            nonce++;
        }
        return hash;
    }

    public long getIndex() {
        return index;
    }

    public String getPrev_hash() {
        return prevHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return String.format("[index = %s, data = %s, hash = %s, prevHash = %s, nonce = %s]",
                index, data, hash, prevHash, nonce);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return index == block.index &&
                nonce == block.nonce &&
                hash.equals(block.hash) &&
                prevHash.equals(block.prevHash) &&
                data.equals(block.data);
    }
}
