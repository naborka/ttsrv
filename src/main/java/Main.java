import server.CMD;
import server.DirectoryObserverServer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by tomag on 04.09.2016.
 */
public class Main {

    private static final String PROPERTY_FILE = "properties.cfg";
    private static final String AMQP_ADDRESS = "localhost";
    private static Properties properties;

    public static void main(String[] args) {
        if (args.length > 1) {
            saveProps(args[0], args[1]);
        }  if (Files.exists(Paths.get(PROPERTY_FILE))) {
            loadProps();
        } else try {
            Files.createFile(Paths.get(PROPERTY_FILE));
            saveProps("C:\\Intel","files");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String path = properties.getProperty("path");
        String exchange = properties.getProperty("exchange");
        String host = properties.getProperty("host");
        if (null != path & null != exchange) {
            if (null == host) {
                System.out.println("AMQP server address not specified (properties.cfg), connecting to localhost.");
                host = AMQP_ADDRESS;
            }
            DirectoryObserverServer dos = new DirectoryObserverServer(path, exchange, host);
            dos.start();
            System.out.println("Observing now: "+path+"\nAMQP Exchange: "+exchange);
            CMD cmd = new CMD(dos);
            cmd.setDaemon(true);
            cmd.start();
        } else {
            System.out.println("Please specify directory path and amqp exchange name via cmdline, or property file.\n.jar [directory] [exchange_name]");
        }
    }

    private static void saveProps(String path, String exchange) {
        try (BufferedOutputStream propertybos = new BufferedOutputStream(new FileOutputStream(PROPERTY_FILE))) {

            properties = new Properties();
            properties.setProperty("path",path);
            properties.setProperty("exchange",exchange);
            properties.store(propertybos,"mainconfig");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadProps() {
        try (BufferedInputStream propertybis = new BufferedInputStream(new FileInputStream(PROPERTY_FILE))) {

            properties = new Properties();
            properties.load(propertybis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
