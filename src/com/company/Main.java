package com.company;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class Main {

    public static void main(String[] args) throws Exception {
        Example example = new Example();

        int i = 1;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WatchService watcher = FileSystems.getDefault().newWatchService();
                    Path dir = Paths.get("./resources");
                    dir.register(watcher,
                            ENTRY_CREATE,
                            ENTRY_DELETE,
                            ENTRY_MODIFY);
                    WatchKey key;
                    while (true) {
                        while ((key = watcher.take()) != null) {
                            for (WatchEvent<?> event : key.pollEvents()) {
                                System.out.println("Event kind:" + event.kind() +". File affected: " + event.context() + ".");
                            }
                            key.reset();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();

        example.showExample();

        while (i == 1) {
            System.out.println("Any key to exit");
            i = System.in.read();
        }
    }
}
