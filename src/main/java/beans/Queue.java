package beans;

/**
 * @author surabhi.prasad
 * @since 27/06/18
 */
public interface Queue {

    /**
     * Pushes the data to the queue.
     *
     * @param payload The payload for the data.
     * @return The pushed data initialized with some id. This id can be used to remove the data from the queue after
     * processing using {@link Queue#remove(String)} method
     */
    QueueData push(String payload);

    /**
     * Removes and returns the relevant data from the queue. The returned data is the data with highest priority if its
     * a {@link PriorityQueue} else the Oldest data
     *
     * @return The oldest or the maximum priority(in case of {@link PriorityQueue}) data available.
     */
    QueueData pop();

    /**
     * Returns the oldest or max priority data available.
     *
     * @return The oldest or the maximum priority(in case of {@link PriorityQueue}) data available.
     */
    QueueData get();

    /**
     * If the queue is initialized with a processed queue, moves the data for the given id to the processed queue, else
     * marks the data as processed. The processed data is then filtered in the subsequent calls to {@link Queue#pop()}
     * and {@link Queue#get()} methods
     *
     * @param id id of the data to be removed.
     * @return The removed {@link QueueData} object.
     */
    QueueData remove(String id);

    /**
     * @return The number of unprocessed entries in the queue.
     */
    long count();
}
