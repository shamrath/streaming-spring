package org.streaming.perfomance.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.perfomance.ConfigReader;
import org.streaming.perfomance.Consumer;

import java.io.IOException;

/**
 * Created by sham on 3/29/16.
 */
public class RabbitmqConsumer implements Consumer{

    private static final Logger log = LoggerFactory.getLogger(RabbitmqConsumer.class);

    private final String brokerURI;
    private final String exchangeName;
    private final String exchangeType;
    private final String consumerId;
    private final String binding_key;
    private Channel channel;
    private Connection connection;
    private final boolean isDebug;
    private DefaultConsumer consumer;
    private String queueName;

    public RabbitmqConsumer(String brokerURI, String consumerId, String binding_key) throws Exception {
        this.brokerURI = brokerURI;
        this.consumerId = consumerId;
        this.binding_key = binding_key;
        exchangeName = ConfigReader.getProperty(RABBITMQ_EXCHANGE_NAME);
        exchangeType = ConfigReader.getProperty(RABBITMQ_EXCHANGE_TYPE);
        isDebug = ConfigReader.getBoolProperty(ENBALE_DEBUG, false);
        init();
    }

    public void init() throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUri(brokerURI);
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(exchangeName, exchangeType);
        // queue, durable, exclusive, autoDelete, properties
        queueName = channel.queueDeclare(consumerId, false, false, false, null).getQueue();
//        queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, binding_key);
        consumer = new DefaultConsumer(channel) {
            private int count = 0;
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
//                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
                long consumedTime = System.nanoTime();
                Long produceTime = getProduceTime(message);
                long diff = consumedTime - produceTime;
                log.info("Consumer:{} , Bind :{} ,Offset:{} :- {} = {}",
                        String.valueOf(consumerId),
                        binding_key , String.valueOf(count++),
                        String.valueOf(consumedTime) + " - " + produceTime,
                        String.valueOf(diff) + " ns");

            }
        };
    }

    private Long getProduceTime(String message) {
        int l = message.length();
        if (isDebug) {
            log.info("Consumed message length : {}", l);
        }
        return Long.valueOf(message.substring(l - 16, l));
    }

    @Override
    public void consume() {
        try {
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            log.error("Error while consuming messages", e);
        }
    }

    @Override
    public void close() {
        try {
            channel.close();
            connection.close();
        } catch (IOException e) {
            log.error("Error closing resources", e);
        }
    }
}
