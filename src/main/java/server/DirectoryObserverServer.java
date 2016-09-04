package server;

import listener.DirectoryListener;
import listener.Producer;
import listener.file.EventedFile;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * Created by tomag on 02.09.2016.
 */
public class DirectoryObserverServer extends Thread {

    private String path;
    private String exchange;
    private String host;
    private DirectoryListener directoryListener;
    Producer producer;

    /**
     * Creates new directory observer server.
     * @param path - path to observe
     * @param exchange - name of amqp exchange
     * @param host - AMQP host address
     */

    public DirectoryObserverServer(String path, String exchange, String host) {
        this.path = path;
        this.exchange = exchange;
        this.host = host;
    }

    private void go() throws IOException, TimeoutException {
        BlockingQueue<EventedFile> queue = new ArrayBlockingQueue<>(5);
        directoryListener = new DirectoryListener(path,queue);
        producer = new Producer(exchange,host);

        while (true) {
            try {
                producer.send(queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendDirAndFileList() throws IOException {
        for (EventedFile file : directoryListener.getDirsAndFilesList()
                ) {
            producer.send(file);
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            go();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
