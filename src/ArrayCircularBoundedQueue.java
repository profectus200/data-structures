/**
 * Implements Array Circular Bounded Queue using primitive arrays.
 * <p>
 * The entire analysis of time complexity is done in the worst case scenario.
 *
 * @param <T> the type of elements stored in this queue
 * @author Vladimir Ryabenko
 * @version 1.0; 10.02.2022
 * @see ICircularBoundedQueue
 */
public class ArrayCircularBoundedQueue<T> implements ICircularBoundedQueue<T> {
    private T[] slots;
    private final int CAPACITY;
    private int size, first, last;

    /**
     * Creates a new queue with given capacity.
     *
     * @param capacity the maximum possible number of elements
     * @throws IllegalArgumentException when capacity is not a natural number
     */
    @SuppressWarnings("unchecked")
    public ArrayCircularBoundedQueue(int capacity) throws IllegalArgumentException {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be a natural number");
        }
        this.CAPACITY = capacity;
        this.size = 0;
        slots = (T[]) new Object[CAPACITY];
        first = -1;
        last = -1;
    }

    /**
     * Inserts an element to the rear of the queue.
     * <p>
     * If the queue is completely full, overwrites the oldest element.
     * <p>
     * Time Complexity: O(1).
     *
     * @param value the element to be inserted
     */
    @Override
    public void offer(T value) {
        if (isEmpty()) {
            first = 0;
            last = 0;
            slots[last] = value;
            size = 1;
        } else if (isFull()) {
            first = (first + 1) % CAPACITY;
            last = (last + 1) % CAPACITY;
            slots[last] = value;
        } else {
            last = (last + 1) % CAPACITY;
            slots[last] = value;
            size++;
        }
    }

    /**
     * Removes an element from the front of the queue.
     * <p>
     * Time Complexity: O(1).
     *
     * @return removed element of type T
     */
    @Override
    public T poll() {
        if (isEmpty())
            return null;
        T element = slots[first];
        first = (first + 1) % CAPACITY;
        size -= 1;
        return element;
    }

    /**
     * Returns the element at the front of the queue (without removing it).
     * <p>
     * Time Complexity: O(1).
     *
     * @return the element at the front of the queue
     */
    @Override
    public T peek() {
        if (isEmpty())
            return null;
        return slots[first];
    }

    /**
     * Removes all elements from the queue.
     * <p>
     * Time Complexity: O(1).
     */
    @Override
    public void flush() {
        first = -1;
        last = -1;
        size = 0;
    }

    /**
     * Checks if the queue is empty.
     * <p>
     * Time Complexity: O(1).
     *
     * @return true if the queue is empty and false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Checks if the queue is full.
     * <p>
     * Time Complexity: O(1).
     *
     * @return true if the queue is full and false otherwise
     */
    @Override
    public boolean isFull() {
        return size == CAPACITY;
    }

    /**
     * Returns the current size of the queue.
     * <p>
     * Time Complexity: O(1).
     *
     * @return the current size of the queue
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns the maximum capacity of the queue.
     * <p>
     * Time Complexity: O(1).
     *
     * @return the maximum capacity of the queue
     */
    @Override
    public int capacity() {
        return CAPACITY;
    }
}

/**
 * Interface for the Circular Bounded Queue.
 *
 * @param <T> the type of the stored values
 */
interface ICircularBoundedQueue<T> {
    void offer(T value); // insert an element to the rear of the queue overwrite the oldest elements

    // when the queue is full
    T poll(); // remove an element from the front of the queue

    T peek(); // look at the element at the front of the queue (without removing it)

    void flush(); // remove all elements from the queue

    boolean isEmpty(); // is the queue empty?

    boolean isFull(); // is the queue full?

    int size(); // number of elements

    int capacity(); // maximum capacity
}
