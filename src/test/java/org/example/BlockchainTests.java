package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class BlockchainTests {

    private Node node1;
    private Node node2;
    private Node node3;

    @BeforeEach
    void setUp() {
        node1 = new Node( 8001);
        node2 = new Node(8002);
        node3 = new Node(8003);
        node1.start();
        node2.start();
        node3.start();
    }

    @Test
    public void testBlockCreation() {
        Block block = new Block(1, "previous_hash", "data");
        assertNotNull(block);
        assertEquals(block.getIndex(), 1);
        assertEquals(block.getPrev_hash(), "previous_hash");
    }

    @Test
    public void testCalculateHash() {
        String data = "data";
        Block block = new Block(1, "previous_hash", data);
        block.setHash(block.calculationHash(data));
        assertNotNull(block.getHash());
        assertEquals(block.getHash().length(), 64);
    }

    @Test
    void testFirstBlock() {
        Block firstBlock1 = node1.getBlockchain().get(0);
        Block firstBlock2 = node2.getBlockchain().get(0);
        Block firstBlock3 = node3.getBlockchain().get(0);
        assertEquals(firstBlock1, firstBlock2);
        assertEquals(firstBlock1, firstBlock3);
        assertEquals(firstBlock2, firstBlock3);
    }

    @Test
    void testHash() {
        Block firstBlock = node1.getBlockchain().get(0);
        Block secondBlock = node2.getBlockchain().get(0);
        Block thirdBlock = node3.getBlockchain().get(0);
        assertTrue(firstBlock.getHash().endsWith("0000"));
        assertTrue(secondBlock.getHash().endsWith("0000"));
        assertTrue(thirdBlock.getHash().endsWith("0000"));
    }
}