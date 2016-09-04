package rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by tomag on 02.09.2016.
 */
public abstract class Member {

    protected Connection connection;
    protected Channel channel;
    protected final String exchange;

    public Member(String exchange, String host) throws IOException, TimeoutException {
        this.exchange = exchange;
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
        if (exchange!=null) {
            channel.exchangeDeclare(exchange,"fanout");
        }
    }

    public void close() {
        try {
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
