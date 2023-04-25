package org.example;

public class Main {
    public static void main(String[] args) {
        Node node = new Node(Integer.parseInt(args[0]));
        node.start();
        while (true) {
            node.createBlock();
        }
    }
}