import beans.MongoPriorityQueue;
import beans.MongoQueue;
import beans.QueueData;
import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author surabhi.prasad
 * @since 27/06/18
 */
public class MongoQueueTest {

    @Test
    public void testMongoQ() {
        MongoTemplate mongoTemplate = getMongoTemplate();
        MongoQueue failedQueue = new MongoQueue(mongoTemplate, new MongoQueue.MongoQueueProperties("queue1_failed", 1), null, null);
        MongoQueue processedQueue = new MongoQueue(mongoTemplate, new MongoQueue.MongoQueueProperties("queue1_processed", 1), null, null);
        MongoQueue mongoQueue = new MongoQueue(mongoTemplate, new MongoQueue.MongoQueueProperties("queue1", 5), failedQueue, processedQueue);
        String payload = "test payload 1";
        mongoQueue.push(payload);
        Assert.assertTrue("Queue must contain only one element!", mongoQueue.count() == 1);
        QueueData pop = mongoQueue.pop();
        Assert.assertTrue("queue data must not be null", pop != null);
        Assert.assertTrue("Payload mismatch", pop.getPayload().equals(payload));
        Assert.assertTrue("Queue must be empty!", mongoQueue.count() == 0);
        pop = mongoQueue.pop();
        Assert.assertTrue("queue data must be null", pop == null);
    }

    @Test
    public void testPriorityQueue() throws Exception {
        MongoTemplate mongoTemplate = getMongoTemplate();
        MongoQueue failedQueue = new MongoQueue(mongoTemplate, new MongoQueue.MongoQueueProperties("queue1_failed", 1), null, null);
        MongoQueue processedQueue = new MongoQueue(mongoTemplate, new MongoQueue.MongoQueueProperties("queue1_processed", 1), null, null);
        MongoPriorityQueue mongoQueue = new MongoPriorityQueue(mongoTemplate, new MongoQueue.MongoQueueProperties("queue1", 5), failedQueue, processedQueue);
        mongoQueue.push("payload200", 200);
        mongoQueue.push("payload150", 150);
        mongoQueue.push("payload500", 500);
        mongoQueue.push("payload400", 400);
        Assert.assertTrue("Queue must contain only four element!", mongoQueue.count() == 4);
        QueueData pop = mongoQueue.pop();
        Assert.assertTrue("queue data must not be null", pop != null);
        Assert.assertTrue("Payload mismatch. should be payload500, found " + pop.getPayload(), pop.getPayload().equals("payload500"));
        pop = mongoQueue.pop();
        Assert.assertTrue("Payload mismatch. should be payload400, found " + pop.getPayload(), pop.getPayload().equals("payload400"));
        pop = mongoQueue.pop();
        Assert.assertTrue("Payload mismatch. should be payload200, found " + pop.getPayload(), pop.getPayload().equals("payload200"));
        pop = mongoQueue.pop();
        Assert.assertTrue("Payload mismatch. should be payload150, found " + pop.getPayload(), pop.getPayload().equals("payload150"));
        pop = mongoQueue.pop();
        Assert.assertTrue("queue data must be null", pop == null);

    }

    private MongoTemplate getMongoTemplate() {
        MongoClient mongoClient = new Fongo("test").getMongo();
        return new MongoTemplate(mongoClient, "test");
    }
}
