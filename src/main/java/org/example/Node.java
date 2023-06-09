package org.example;

import org.apache.commons.lang3.RandomStringUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Node {
    private final Map<Integer, String> nodes;
    private List<Block> blockchain = new ArrayList<>();
    private final int port;
    private ServerSocket serverSocket;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4);

    public Node(int port) {
        nodes = Map.of(8001, "node1", 8002, "node2", 8003, "node3");
        this.port = port;
        Block first = new Block(0, "root", "root");
        first.setHash("22e400d5a4b0041f9164d9381ecb287de85cf369d1b88321e6414cf8d3cd0000");
        blockchain.add(first);
    }

    public void createBlock() {
        if (blockchain.isEmpty()) throw new NullPointerException();
        Block prev = getPrevBlock();
        if (prev == null) throw new NullPointerException();
        long index = prev.getIndex() + 1;
        String data = RandomStringUtils.random(256, true, true);
        Block block = new Block(index, prev.getHash(), data);
        block.setHash(block.calculationHash(data));
        nodes.forEach((key, value) -> {
            try {
                sendMessage(STATUS.NEW, key, value, block);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Block getPrevBlock() {
        if (blockchain.isEmpty()) return null;
        return blockchain.get(blockchain.size() - 1);
    }

    public boolean addBlock(Block block) {
        Block prev = getPrevBlock();
        if (prev != null && Objects.equals(block.getPrev_hash(), prev.getHash())
                && block.getIndex() == prev.getIndex() + 1) {
            blockchain.add(block);
            return true;
        }
        return false;
    }

    public void start() {
        scheduledThreadPoolExecutor.execute(() -> {
            try {
                serverSocket = new ServerSocket(port);
                while (true) {
                    new Server(Node.this, serverSocket.accept()).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        nodes.forEach((key, value) -> {
            try {
                sendMessage(STATUS.REQ, key, value, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private boolean hostInMap(String hostname) {
        for (var entry : nodes.entrySet()) {
            if (Objects.equals(hostname, entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    public void sendMessage(STATUS STATUS, int port, String host, Block blocks) throws IOException {
        String hostname = InetAddress.getLocalHost().getHostName();
        if (hostInMap(hostname)) {
            hostname = host;
        }
        try (Socket socket = new Socket(hostname, port); ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            Message message = (Message) objectInputStream.readObject();
            while (message != null) {
                if (message.getStatus() == STATUS.READY) {
                    objectOutputStream.writeObject(new Message(Collections.singletonList(blocks), this.port, port, STATUS));
                } else if (message.getStatus() == STATUS.RSP) {
                    if (!message.getBlocks().isEmpty() && this.blockchain.size() == 1) {
                        blockchain = new ArrayList<>(message.getBlocks());
                    }
                    break;
                }
                message = (Message) objectInputStream.readObject();
            }
        } catch (UnknownHostException | ClassNotFoundException unknownHostException) {
            unknownHostException.printStackTrace();
        } catch (IOException ioException) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public int getPort() {
        return port;
    }

}