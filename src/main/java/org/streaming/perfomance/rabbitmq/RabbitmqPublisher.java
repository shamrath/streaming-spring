package org.streaming.perfomance.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.perfomance.ConfigReader;
import org.streaming.perfomance.Publisher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by sham on 3/29/16.
 */
public class RabbitmqPublisher implements Publisher{

    private static final Logger log = LoggerFactory.getLogger(RabbitmqPublisher.class);

    private final String brokerURI;
    private final String exchangeName;
    private final String exchangeType;
    private final String publisherId;
    private final List<String> routing_keys;
    private final int numOfQueues;
    private int bind = 0;
    private Channel channel;
    private Connection connection;
    private final boolean isDebug;

    public RabbitmqPublisher(String publisherId, String brokerURI, List<String> routing_keys) throws Exception {
        this.brokerURI = brokerURI;
        this.publisherId = publisherId;
        this.routing_keys = routing_keys;
        this.numOfQueues = routing_keys.size();
        this.exchangeName = ConfigReader.getProperty(RABBITMQ_EXCHANGE_NAME);
        this.exchangeType = ConfigReader.getProperty(RABBITMQ_EXCHANGE_TYPE);
        this.isDebug = ConfigReader.getBoolProperty(ENBALE_DEBUG, false);
        init();
    }

    public void init() throws java.io.IOException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUri(this.brokerURI);
        connection = connectionFactory.newConnection();
        connection.addShutdownListener(new ShutdownListener() {
            @Override
            public void shutdownCompleted(ShutdownSignalException e) {
                log.error("**************************Connection closed******************** ");
            }
        });
        channel = connection.createChannel();
        channel.exchangeDeclare(exchangeName, exchangeType);

    }

    @Override
    public void publish(String msg) {
        bind = bind % numOfQueues;
        try {
            channel.basicPublish(exchangeName, routing_keys.get(bind), null, msg.getBytes());
        } catch (IOException e) {
            log.error("Error while publishing message (size : " + msg.length() + ") to exchanger : " +
                    exchangeName + ", routing_key : " + routing_keys.get(bind), e);
        }
        bind++;
    }

    @Override
    public void close() {
        try {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            log.error("Error while closing resources", e);
        }
    }

}
