/**
 * Implements Queued Bounded Stack using two ArrayCircularBoundedQueue.
 * <p>
 * The entire analysis of time complexity is done in the worst case scenario.
 *
 * @param <T> the type of elements stored in this stack
 * @author Vladimir Ryabenko
 * @version 1.0; 10.02.2022
 * @see IBoundedStack
 * @see ArrayCircularBoundedQueue
 */
public class QueuedBoundedStack<T> implements IBoundedStack<T> {
    private ArrayCircularBoundedQueue<T> main, extra;

    /**
     * Creates a new stack with given capacity.
     *
     * @param capacity the maximum possible number of elements
     * @throws IllegalArgumentException when capacity is not a natural number
     */
    public QueuedBoundedStack(int capacity) throws IllegalArgumentException {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be a natural number");
        }
        main = new ArrayCircularBoundedQueue<>(capacity);
        extra = new ArrayCircularBoundedQueue<>(capacity);
    }

    /**
     * Pushes an element into the stack.
     * <p>
     * Removes the oldest element if the stack is full.
     * <p>
     * Time Complexity: O(1).
     *
     * @param value element to be pushed of type T
     */
    @Override
    public void push(T value) {
        main.offer(value);
    }

    /**
     * Removes an element from the top of the stack.
     * <p>
     * Time Complexity: O(n), where n - current number of items in the stack.
     *
     * @return removed element
     */
    @Override
    public T pop() {
        if (isEmpty())
            return null;
        while (main.size() > 1) {
            extra.offer(main.poll());
        }
        swap();
        return extra.poll();
    }

    /**
     * Shows the element at the top of the stack (without removing it).
     * <p>
     * Time Complexity: O(n), where n - current number of items in the stack.
     *
     * @return the element at the top of the stack
     */
    @Override
    public T top() {
        if (isEmpty())
            return null;
        while (main.size() > 1) {
            extra.offer(main.poll());
        }
        T element = main.peek();
        extra.offer(main.poll());
        swap();
        return element;
    }

    /**
     * Remove all elements from the stack.
     * <p>
     * Time Complexity: O(1).
     */
    @Override
    public void flush() {
        main.flush();
    }

    /**
     * Checks if the queue is empty.
     * <p>
     * Time Complexity: O(1).
     *
     * @return true if the stack is empty and false otherwise
     */
    @Override
    public boolean isEmpty() {
        return main.isEmpty();
    }

    /**
     * Checks if the stack is full.
     * <p>
     * Time Complexity: O(1).
     *
     * @return true if the stack is full and false otherwise
     */
    @Override
    public boolean isFull() {
        return main.isFull();
    }

    /**
     * Returns the current size of the stack.
     * <p>
     * Time Complexity: O(1).
     *
     * @return the current size of the stack
     */
    @Override
    public int size() {
        return main.size();
    }

    /**
     * Returns the maximum capacity of the stack.
     * <p>
     * Time Complexity: O(1).
     *
     * @return the maximum capacity of the stack
     */
    @Override
    public int capacity() {
        return main.capacity();
    }

    /**
     * Swaps references 'main' and 'extra'.
     * <p>
     * Time Complexity: O(1).
     */
    private void swap() {
        ArrayCircularBoundedQueue<T> tmp = main;
        main = extra;
        extra = tmp;
    }
}

/**
 * Interface for the Bounded Stack.
 *
 * @param <T> the type of the stored values
 */
interface IBoundedStack<T> {
    void push(T value); // push an element onto the stack remove the oldest element when if stack is full

    T pop(); // remove an element from the top of the stack

    T top(); // look at the element at the top of the stack (without removing it)

    void flush(); // remove all elements from the stack

    boolean isEmpty(); // is the stack empty?

    boolean isFull(); // is the stack full?

    int size(); // number of elements

    int capacity(); // maximum capacity
}
