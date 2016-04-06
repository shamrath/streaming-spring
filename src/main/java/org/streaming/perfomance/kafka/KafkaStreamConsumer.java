package org.streaming.perfomance.kafka;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streaming.perfomance.ConfigReader;
import org.streaming.perfomance.Consumer;

import java.util.Calendar;

/**
 * Created by syodage on 1/26/16.
 */
public class KafkaStreamConsumer implements Consumer, Runnable{

    private static final Logger log = LoggerFactory.getLogger(KafkaStreamConsumer.class);
    private KafkaStream<String, String> kafkaStream;
    private int consumerNumber;
    boolean isDebug = ConfigReader.getBoolProperty(ENBALE_DEBUG, false);

    public KafkaStreamConsumer(KafkaStream<String, String> kafkaStream, int consumerNumber) {
        this.kafkaStream = kafkaStream;
        this.consumerNumber = consumerNumber;
    }

    @Override
    public void consume() {
        ConsumerIterator<String, String> it = kafkaStream.iterator();
        while (it.hasNext()) {
            MessageAndMetadata<String, String> next = it.next();
            long consumedTime = System.nanoTime();
            Long produceTime = getProduceTime(next.message());
            long diff = consumedTime - produceTime;
            log.info("Consumer:{} ,Partition:{} ,Offset:{} :- {} = {}",
                    String.valueOf(consumerNumber),
                    next.partition(), next.offset(),
                    String.valueOf(consumedTime) + " - " + produceTime,
                    String.valueOf(diff) + " ns");

        }
        log.info("Consumer {} shutdown", String.valueOf(consumerNumber));
    }

    private Long getProduceTime(String message) {
        int l = message.length();
        String sTime;
        if (l > 16) {
            sTime = message.substring(l - 16, l);
        } else {
            sTime = message;
        }
        if (isDebug) {
            log.info("Consumed message length : {}, prodTime : {} ", l, sTime);
        }
        return Long.valueOf(sTime);
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
