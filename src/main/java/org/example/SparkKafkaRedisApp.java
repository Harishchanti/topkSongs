package org.example;

import org.apache.spark.sql.Dataset;

import org.apache.spark.sql.Row;

import org.apache.spark.sql.SparkSession;

public class SparkKafkaRedisApp {

    public static void main(String[] args) throws Exception {

        SparkSession spark =
                SparkSession.builder().master("local[*]").appName("KafkaRedis")
                        .getOrCreate();

        Dataset<Row> df = spark.readStream().format("kafka")
                .option("kafka.bootstrap.servers", "localhost:9092")
                .option("subscribe", "numbers-topic").load();

        Dataset<Row> values = df.selectExpr("CAST(value AS STRING)");
        values.writeStream().foreachBatch((batch, id) -> {
            batch.collectAsList().forEach(row -> {
                String value = row.getString(0);
                RedisWriter.save(value);
            });
        }).start().awaitTermination();

    }

}

