package org.example;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Server extends Thread {
    private final Node node;
    private final Socket client;

    Server(Node node, Socket client) {
        this.node = node;
        this.client = client;
    }

    @Override
    public void start() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(client.getOutputStream()); ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream())) {
            Message current = new Message();
            current.setSender(node.getPort());
            current.setStatus(STATUS.READY);
            objectOutputStream.writeObject(current);
            Message message = (Message) objectInputStream.readObject();
            while ((message != null)) {
                if (STATUS.NEW == message.getStatus()) {
                    synchronized (node) {
                        if (node.addBlock(message.getBlocks().get(0))) {
                            System.out.printf("NEW %d received: %s%n", node.getPort(), message);
                        }
                    }
                    break;
                } else if (STATUS.REQ == message.getStatus()) {
                    System.out.println("\nREQ\n");
                    Message message1 = new Message();
                    message1.setBlocks(node.getBlockchain());
                    message1.setStatus(STATUS.RSP);
                    message1.setSender(node.getPort());
                    objectOutputStream.writeObject(message1);
                    break;
                }
                message = (Message) objectInputStream.readObject();
            }
            client.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
