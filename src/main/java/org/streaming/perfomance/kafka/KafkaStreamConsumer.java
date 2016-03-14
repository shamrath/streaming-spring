package org.streaming.perfomance.kafka;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.perfomance.Consumer;

import java.util.Calendar;

/**
 * Created by syodage on 1/26/16.
 */
public class KafkaStreamConsumer implements Consumer, Runnable{

    private static final Logger log = LoggerFactory.getLogger(KafkaStreamConsumer.class);
    private KafkaStream<String, String> kafkaStream;
    private int consumerNumber;

    public KafkaStreamConsumer(KafkaStream<String, String> kafkaStream, int consumerNumber) {
        this.kafkaStream = kafkaStream;
        this.consumerNumber = consumerNumber;
    }

    @Override
    public void consume() {
        ConsumerIterator<String, String> it = kafkaStream.iterator();
        while (it.hasNext()) {
            MessageAndMetadata<String, String> next = it.next();
            log.info("Consumer:{} ,Partition:{} ,Offset:{} :- {}", String.valueOf(consumerNumber),
                    next.partition(), next.offset(), next.message() + " - " + String.valueOf(Calendar.getInstance().getTime().getTime()));
        }
        log.info("Consumer {} shutdown", String.valueOf(consumerNumber));
    }

    @Override
    public void close() {
        log.info("*********  consumer close invoked");
        // nothing
    }

    @Override
    public void run() {
        consume();
    }
}