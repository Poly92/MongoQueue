package beans;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.concurrent.TimeUnit;

/**
 * An implementation of the Queue backed by a mongo database.
 *
 * @author surabhi.prasad
 * @since 27/06/18
 */
public class MongoQueue implements Queue {

    private final String queueId;
    private final MongoTemplate mongoTemplate;
    private final MongoQueueProperties properties;
    private final MongoQueue failedQueue;
    private final MongoQueue processedQueue;

    public MongoQueue(MongoTemplate mongoTemplate, MongoQueueProperties properties, MongoQueue failedQueue, MongoQueue processedQueue) {
        this.queueId = new ObjectId().toString();
        this.mongoTemplate = mongoTemplate;
        this.properties = properties;
        this.failedQueue = failedQueue;
        this.processedQueue = processedQueue;
    }

    @Override
    public QueueData push(String payload) {
        return save(payload, -1);
    }

    @Override
    public QueueData pop() {
        Query query = getDataCriteria();
        FindAndModifyOptions options = FindAndModifyOptions.options().remove(true);
        return mongoTemplate.findAndModify(query, new Update(), options, QueueData.class, properties.getCollectionName());
    }

    /**
     * Returns the oldest or max priority data available. A data returned by this method will become available again
     * after {@link MongoQueueProperties#delay} millis if not removed using {@link Queue#remove(String)} or it has been already
     * fetched/retried {@link MongoQueueProperties#maxRetries} times.
     *
     * @return The oldest or the maximum priority(in case of {@link PriorityQueue}) data available.
     */
    @Override
    public QueueData get() {
        Query query = getDataCriteria();
        Update update = new Update().inc("retryCount", 1).set("availableAfter", System.currentTimeMillis() + properties.getDelay());
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        QueueData data = mongoTemplate.findAndModify(query, update, options, QueueData.class, properties.getCollectionName());
        if (data != null && data.getRetryCount() > properties.getMaxRetries()) {
            mongoTemplate.remove(Query.query(Criteria.where("_id").is(data.getId())));
            failedQueue.push(data.getPayload());
            return get();
        }
        return data;
    }

    @Override
    public QueueData remove(String id) {
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        Update update = new Update().set("processed", true);
        if (processedQueue != null) {
            options = FindAndModifyOptions.options().remove(true);
        }
        QueueData queueData = mongoTemplate.findAndModify(Query.query(Criteria.where("_id").is(id)), update, options, QueueData.class, properties.getCollectionName());
        if (processedQueue != null) {
            processedQueue.push(queueData.getPayload());
        }
        return queueData;
    }

    @Override
    public long count() {
        return mongoTemplate.count(Query.query(Criteria.where("queueId").is(this.queueId)), properties.getCollectionName());
    }

    public QueueData save(String payload, int priority) {
        QueueData data = new QueueData(payload, priority, queueId);
        mongoTemplate.save(data, properties.getCollectionName());
        return data;
    }

    private Query getDataCriteria() {
        return Query.query(Criteria.where("queueId").is(queueId).and("availableAfter").lte(System.currentTimeMillis()))
                .with(getSort());
    }

    protected Sort getSort() {
        return new Sort(Sort.Direction.ASC, "_id");
    }

    public static class MongoQueueProperties {

        private final String collectionName;
        private final int maxRetries;
        private long delay = TimeUnit.MINUTES.toMillis(5);

        public MongoQueueProperties(String collectionName, int maxRetries) {
            this.collectionName = collectionName;
            this.maxRetries = maxRetries;
        }

        public MongoQueueProperties delay(final long delay) {
            this.delay = delay;
            return this;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public long getDelay() {
            return delay;
        }

        public void setDelay(long delay) {
            this.delay = delay;
        }

    }
}
