package org.example;

import java.io.Serializable;
import java.util.List;


public class Message implements Serializable {

    private List<Block> blocks;
    private int sender;
    private int receiver;
    private STATUS status;

    public Message(List<Block> blocks, int sender, int receiver, STATUS status) {
        this.blocks = blocks;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
    }

    public Message() {
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("\ntimestamp = %s, \nsender = %d, \nreceiver = %d, \nblocks = %s", System.currentTimeMillis(), sender, receiver, blocks);
    }
}