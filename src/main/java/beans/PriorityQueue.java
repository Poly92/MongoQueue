package beans;

/**
 * @author surabhi.prasad
 * @since 27/06/18
 */
public interface PriorityQueue extends Queue {

    /**
     * Pushes the data to the queue along with some priority
     *
     * @param payload  The payload for the data.
     * @param priority Associated priority of the data. The priority is used while fetching data using
     *                 {@link Queue#pop()} and {@link Queue#get()} methods
     * @return The pushed data initialized with some id. This id can be used to remove the data from the queue after
     * processing using {@link Queue#remove(String)} method
     */
    QueueData push(String payload, int priority);
}
