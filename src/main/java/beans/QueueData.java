package beans;

/**
 * @author surabhi.prasad
 * @since 27/06/18
 */
public class QueueData {

    private String id;
    private String payload;
    private String queueId;
    private int priority = -1;
    private long availableAfter;
    private int retryCount;

    public QueueData(String payload, int priority, String queueId) {
        this.payload = payload;
        this.queueId = queueId;
        this.priority = priority;
        this.availableAfter = System.currentTimeMillis();
    }

    public QueueData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getAvailableAfter() {
        return availableAfter;
    }

    public void setAvailableAfter(long availableAfter) {
        this.availableAfter = availableAfter;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
