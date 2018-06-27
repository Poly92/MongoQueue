package beans;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;

/**
 * @author surabhi.prasad
 * @since 27/06/18
 */
public class MongoPriorityQueue extends MongoQueue implements PriorityQueue {

    public MongoPriorityQueue(MongoTemplate mongoTemplate, MongoQueueProperties properties, MongoQueue failedQueue, MongoQueue processedQueue) {
        super(mongoTemplate, properties, failedQueue, processedQueue);
    }

    @Override
    public QueueData push(String payload, int priority) {
        return save(payload, priority);
    }

    @Override
    protected Sort getSort() {
        ArrayList<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "priority"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "_id"));
        return new Sort(orders);
    }
}
