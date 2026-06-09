package org.xpen.hello.kafka;

import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.common.config.SaslConfigs.SASL_MECHANISM;
import static org.apache.kafka.common.config.SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka Producer/Consumer示例
 */
@TestMethodOrder(OrderAnnotation.class)
public class KafkaBasicTest {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaBasicTest.class);
    private static final String TOPIC = "demo_java";
    private static Properties props = new Properties();

    @BeforeAll
    public static void setUp() throws Exception {
    	//需要自行修改以下配置,演示采用SSL-NoSASL
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        props.put(SSL_TRUSTSTORE_LOCATION_CONFIG, "d:/xx.truststore.jks");
        props.put(SSL_TRUSTSTORE_PASSWORD_CONFIG, "pass");
        
        props.put(SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(SASL_MECHANISM, "");
        props.put(SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ACKS_CONFIG, "all");
        props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(GROUP_ID_CONFIG, "java-app1");
	}
	
    /**
     * 生产者
     */
    @Test
    @Order(1)
    public void testProducer() throws Exception {
    	Random random = new Random();
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(TOPIC, "hello world" + random.nextInt(100));
        
        producer.send(producerRecord);
        producer.flush();
        producer.close();
    }
    
    /**
     * 消费者
     */
    @Test
    @Order(2)
    public void testConsumer() throws Exception {
    	KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
    	
    	CountDownLatch mainDone = new CountDownLatch(1);
        
        final Thread shutdownHook = new Thread() {
            public void run() {
            	LOGGER.info("Detected shutdown, calling consumer.wakeup...");
                consumer.wakeup();

                try {
                	mainDone.await(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    	
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        
        try {
            consumer.subscribe(Arrays.asList(TOPIC));
            
            int emptyPollCount = 10;
            
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                if (records.isEmpty()) {
                	//启动 hook 线程，等同于JVM触发 hook 时由JVM调用线程的 run()
                	//此处仅用作junit测试用,真实生产环境请删除
                	LOGGER.info("records is empty");
                	emptyPollCount--;
                	if (emptyPollCount <= 0) {
                    	LOGGER.info("exceed max empty poll count");
                    	shutdownHook.start();
                    	return;
                	}
                }

                for (ConsumerRecord<String, String> record : records) {
                	LOGGER.info("Key: " + record.key() + ", Value: " + record.value());
                	LOGGER.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                }
            }

        } catch (WakeupException e) {
        	LOGGER.info("Consumer is starting to shut down");
        } catch (Exception e) {
        	LOGGER.error("Unexpected exception in the consumer", e);
        } finally {
            consumer.close(); // close the consumer, this will also commit offsets
            LOGGER.info("The consumer is now gracefully shut down");
        }
        mainDone.countDown();
    }

}
