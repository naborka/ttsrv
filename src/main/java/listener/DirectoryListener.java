package listener;

import listener.file.EventedFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

/**
 * Created by tomag on 02.09.2016.
 */
public class DirectoryListener {

    Logger logger = Logger.getLogger("DirectoryListener");

    private Path path;
    private FileSystem fs;
    private Set<EventedFile> dirsAndFilesList = new TreeSet<>();
    private WatchService watchService;
    private BlockingQueue<EventedFile> changesQueue;

    public DirectoryListener(String path, BlockingQueue<EventedFile> respondQueue) {
        this.path = Paths.get(path);
        this.changesQueue = respondQueue;
        try {
            updateFileList(); // Initial directory scan
            fs = this.path.getFileSystem();
            watch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void watch() throws IOException {
        watchService = fs.newWatchService();
        path.register(watchService,
                ENTRY_CREATE,
                ENTRY_DELETE
        );
        new Thread(new Runnable() {
            @Override
            public void run() {
                WatchKey key;
                while (true) {
                    try {
                        key = watchService.take();
                        WatchEvent.Kind<?> kind = null;
                        List<WatchEvent<?>> eventList = key.pollEvents();
                        if (!eventList.isEmpty()) {
                            for (WatchEvent<?> event : eventList
                                    ) {
                                kind = event.kind();
                                Path context = path.resolve((Path) event.context());
                                EventedFile eventedFile;
                                if (kind == ENTRY_CREATE) {
                                    eventedFile = new EventedFile(context,"created");
                                    dirsAndFilesList.add(eventedFile);
                                    changesQueue.put(eventedFile);
                                    logger.info("new listener.file created: "+eventedFile.getFile());
                                }
                                if (kind == ENTRY_DELETE) {
                                    eventedFile = new EventedFile(context,"deleted");
                                    dirsAndFilesList.remove(eventedFile);
                                    changesQueue.put(eventedFile);
                                    logger.info("listener.file removed: "+eventedFile.getFile());
                                }
                            }
                        }
                        key.reset();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void updateFileList() throws IOException {
        dirsAndFilesList.clear();
        updateFileList(path);
    }

    // Recursively scan subdirectories
    private void updateFileList(Path p) throws IOException {
        dirsAndFilesList.add(new EventedFile(p,"created"));
        if (Files.isDirectory(p)) {
            DirectoryStream<Path> localDirStream = Files.newDirectoryStream(p);
            for (Path path : localDirStream
                    ) {
                updateFileList(path);
            }
            localDirStream.close();
        }
    }


    public Set<EventedFile> getDirsAndFilesList() throws IOException {
        updateFileList();
        return dirsAndFilesList;
    }
}