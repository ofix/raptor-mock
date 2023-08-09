package com.greatwall.mock.client;

import java.io.IOException;

public class Application {

    public static void main(String[] args) {
        try {
            MockRaptorClient.run(args);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
