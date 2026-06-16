package org.example;

import org.apache.spark.sql.Dataset;

import org.apache.spark.sql.Row;

import org.apache.spark.sql.SparkSession;

import static org.apache.spark.sql.functions.col;

public class SparkKafkaRedisApp {

    public static void main(String[] args) throws Exception {

        SparkSession spark =
                SparkSession.builder().master("local[*]").appName("KafkaRedis")
                        .getOrCreate();

        Dataset<Row> kafkaDF = spark.readStream().format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", "numbers-topic").load();

        // Convert Kafka value to Integer

        Dataset<Row> numbersDF =
                kafkaDF.selectExpr("CAST(value AS STRING) as number")
                        .select(col("number").cast("int").alias("number"));

        // Count frequencies
        Dataset<Row> frequencyDF = numbersDF.groupBy("number").count();

        // Write to Redis

        frequencyDF.writeStream().outputMode("complete")
                .foreachBatch((batchDF, batchId) -> {
                    System.out.println(
                            "\n========== Batch : " + batchId + " ==========");
                    batchDF.show(false);
                    batchDF.collectAsList().forEach(row -> {
                        Integer number = row.getAs("number");
                        Long count = row.getAs("count");
                        RedisWriter.save(number, count);
                    });
                }).start().awaitTermination();

    }

}

