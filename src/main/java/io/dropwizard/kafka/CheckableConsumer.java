package io.dropwizard.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CheckableConsumer<K, V> implements Consumer<K, V> {
    private final Consumer<K, V> delegatee;

    private boolean isActive = false;

    public CheckableConsumer(final Consumer<K, V> delegatee) {
        this.delegatee = delegatee;
    }

    @Override
    public Set<TopicPartition> assignment() {
        return delegatee.assignment();
    }

    @Override
    public Set<String> subscription() {
        return delegatee.subscription();
    }

    @Override
    public void subscribe(final Collection<String> collection) {
        delegatee.subscribe(collection);
        isActive = true;
    }

    @Override
    public void subscribe(final Collection<String> topics, final ConsumerRebalanceListener listener) {
        delegatee.subscribe(topics, listener);
        isActive = true;
    }

    @Override
    public void assign(final Collection<TopicPartition> collection) {
        delegatee.assign(collection);
    }

    @Override
    public void subscribe(final Pattern pattern, final ConsumerRebalanceListener consumerRebalanceListener) {
        delegatee.subscribe(pattern, consumerRebalanceListener);
        isActive = true;
    }

    @Override
    public void subscribe(final Pattern pattern) {
        delegatee.subscribe(pattern);
        isActive = true;
    }

    @Override
    public void unsubscribe() {
        delegatee.unsubscribe();
        isActive = false;
    }

    @Override
    public ConsumerRecords<K, V> poll(final long l) {
        return delegatee.poll(l);
    }

    @Override
    public ConsumerRecords<K, V> poll(final Duration duration) {
        return delegatee.poll(duration);
    }

    @Override
    public void commitSync() {
        delegatee.commitSync();
    }

    @Override
    public void commitSync(final Duration duration) {
        delegatee.commitSync(duration);
    }

    @Override
    public void commitSync(final Map<TopicPartition, OffsetAndMetadata> map) {
        delegatee.commitSync(map);
    }

    @Override
    public void commitSync(final Map<TopicPartition, OffsetAndMetadata> map, final Duration duration) {
        delegatee.commitSync(map, duration);
    }

    @Override
    public void commitAsync() {
        delegatee.commitAsync();
    }

    @Override
    public void commitAsync(final OffsetCommitCallback offsetCommitCallback) {
        delegatee.commitAsync(offsetCommitCallback);
    }

    @Override
    public void commitAsync(final Map<TopicPartition, OffsetAndMetadata> map,
                            final OffsetCommitCallback offsetCommitCallback) {
        delegatee.commitAsync(map, offsetCommitCallback);
    }

    @Override
    public void seek(final TopicPartition topicPartition, final long l) {
        delegatee.seek(topicPartition, l);
    }

    @Override
    public void seekToBeginning(final Collection<TopicPartition> collection) {
        delegatee.seekToBeginning(collection);
    }

    @Override
    public void seekToEnd(final Collection<TopicPartition> collection) {
        delegatee.seekToEnd(collection);
    }

    @Override
    public long position(final TopicPartition topicPartition) {
        return delegatee.position(topicPartition);
    }

    @Override
    public long position(final TopicPartition topicPartition, final Duration duration) {
        return delegatee.position(topicPartition, duration);
    }

    @Override
    public OffsetAndMetadata committed(final TopicPartition topicPartition) {
        return delegatee.committed(topicPartition);
    }

    @Override
    public OffsetAndMetadata committed(final TopicPartition topicPartition, final Duration duration) {
        return delegatee.committed(topicPartition, duration);
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
        return delegatee.metrics();
    }

    @Override
    public List<PartitionInfo> partitionsFor(final String s) {
        return delegatee.partitionsFor(s);
    }

    @Override
    public List<PartitionInfo> partitionsFor(final String s, final Duration duration) {
        return delegatee.partitionsFor(s, duration);
    }

    @Override
    public Map<String, List<PartitionInfo>> listTopics() {
        return delegatee.listTopics();
    }

    @Override
    public Map<String, List<PartitionInfo>> listTopics(final Duration duration) {
        return delegatee.listTopics(duration);
    }

    @Override
    public Set<TopicPartition> paused() {
        return delegatee.paused();
    }

    @Override
    public void pause(final Collection<TopicPartition> collection) {
        delegatee.pause(collection);
    }

    @Override
    public void resume(final Collection<TopicPartition> collection) {
        delegatee.resume(collection);
    }

    @Override
    public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(final Map<TopicPartition, Long> map) {
        return delegatee.offsetsForTimes(map);
    }

    @Override
    public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(final Map<TopicPartition, Long> map, final Duration duration) {
        return delegatee.offsetsForTimes(map, duration);
    }

    @Override
    public Map<TopicPartition, Long> beginningOffsets(final Collection<TopicPartition> collection) {
        return delegatee.beginningOffsets(collection);
    }

    @Override
    public Map<TopicPartition, Long> beginningOffsets(final Collection<TopicPartition> collection, final Duration duration) {
        return delegatee.beginningOffsets(collection, duration);
    }

    @Override
    public Map<TopicPartition, Long> endOffsets(final Collection<TopicPartition> collection) {
        return delegatee.endOffsets(collection);
    }

    @Override
    public Map<TopicPartition, Long> endOffsets(final Collection<TopicPartition> collection, final Duration duration) {
        return delegatee.endOffsets(collection, duration);
    }

    @Override
    public void close() {
        delegatee.close();
        isActive = false;
    }

    @Override
    public void close(final long l, final TimeUnit timeUnit) {
        delegatee.close(l, timeUnit);
        isActive = false;
    }

    @Override
    public void close(final Duration duration) {
        delegatee.close(duration);
        isActive = false;
    }

    @Override
    public void wakeup() {
        delegatee.wakeup();
    }

    public boolean isActive() {
        return isActive;
    }
}
