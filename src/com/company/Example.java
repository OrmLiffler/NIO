package com.company;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Example {

    public void showExample() {
        Path path = Paths.get("./resources/file_for_read.json");

        showPathMethods(path);
        showFiles(path);
        showBuff();
        showAsync();
    }

    private void showFiles(Path path) {
        try {
            if (!Files.exists(Paths.get("./resources/copy"))) {
                Files.createDirectory(Paths.get("./resources/copy"));
            }

            Files.copy(path, Paths.get("./resources/copy/copied.json"), StandardCopyOption.REPLACE_EXISTING);
            List<String> read = Files.readAllLines(Paths.get("./resources/copy/copied.json"));
            System.out.println(read);
            System.out.println("-------");
            Files.write(Paths.get("./resources/copy/copied.json"), read);
            Files.delete(Paths.get("./resources/copy/copied.json"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showAsync() {
        try {
            AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(Paths.get("./resources/file_for_read.json"), StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate(100);
            Future result = fileChannel.read(buffer, 0);

            while (result.isDone()) {
                System.out.println("ololo");
            }
            System.out.println("Bytes read from file: " + result.get());
            buffer.flip();

            while (buffer.hasRemaining()) {
                System.out.print(buffer.getChar());
            }
            buffer.clear();
            fileChannel.close();

            //writing

            AsynchronousFileChannel writefFileChannel = AsynchronousFileChannel.open(Paths.get("./resources/async_write.json"), StandardOpenOption.WRITE);
            ByteBuffer byteBuffer = ByteBuffer.wrap(Files.readAllBytes(Paths.get("./resources/file_for_read.json")));

            CompletionHandler completionHandler = new CompletionHandler() {
                @Override
                public void completed(Object result, Object attachment) {
                    System.out.println("ololo writed");
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("ololo failed");
                }
            };

            writefFileChannel.write(byteBuffer, Files.size(Paths.get("./resources/async_write.json")));
            writefFileChannel.close();

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void showBuff() {
        try {
            RandomAccessFile aFile = new RandomAccessFile("./resources/file_for_read.json", "rw");
            FileChannel inChannel = aFile.getChannel();

            ByteBuffer buf = ByteBuffer.allocate(48);

            int bytesRead = inChannel.read(buf);
            while (bytesRead != -1) {

                buf.flip();

                while (buf.hasRemaining()) {
                    System.out.print((char) buf.get());
                }

                buf.clear();
                bytesRead = inChannel.read(buf);
            }

            aFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPathMethods(Path path) {

        System.out.format("toString: %s%n", path.toString());
        System.out.format("getFileName: %s%n", path.getFileName());
        System.out.format("subpath(0,2): %s%n", path.subpath(0, 2));
        System.out.format("getParent: %s%n", path.getParent());
        System.out.format("getFileSystem: %s%n", path.getFileSystem());
    }
}
