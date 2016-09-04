package listener;

import listener.file.EventedFile;
import org.apache.commons.lang3.SerializationUtils;
import rabbit.Member;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * Created by tomag on 02.09.2016.
 */
public class Producer extends Member {
    Logger logger = Logger.getLogger("Member");

    /**
     * Creates filelist producer
     * @param exchange Name of AMQP Exchange
     * @param host AMQP host address
     */

    public Producer(String exchange,String host) throws IOException, TimeoutException {
        super(exchange,host);
    }
    public void send(EventedFile obj) throws IOException {
        channel.basicPublish(exchange,"",null, SerializationUtils.serialize(obj));
        logger.info("object published");
    }
}
