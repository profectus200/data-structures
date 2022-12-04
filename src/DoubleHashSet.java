import javax.naming.SizeLimitExceededException;

/**
 * Implements Double Hash Set.
 *
 * @param <T> the type of elements stored in this set
 * @author Vladimir Ryabenko
 * @version 1.0; 10.02.2022
 * @see ISet
 */
public class DoubleHashSet<T> implements ISet<T> {
    private boolean[] deleted;
    private T[] slots;
    private final int CAPACITY;
    private int size;

    /**
     * Creates a new set with given capacity.
     *
     * @param capacity the maximum possible number of elements
     * @throws IllegalArgumentException when capacity is not a natural number
     */
    @SuppressWarnings("unchecked")
    public DoubleHashSet(int capacity) throws IllegalArgumentException {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be a natural number");
        }
        this.CAPACITY = capacity;
        this.size = 0;
        slots = (T[]) new Object[CAPACITY];
        deleted = new boolean[CAPACITY];
    }

    /**
     * Creates a new set with capacity equal to 1000003.
     */
    @SuppressWarnings("unchecked")
    public DoubleHashSet() {
        this.CAPACITY = 1000003;
        this.size = 0;
        slots = (T[]) new Object[CAPACITY];
        deleted = new boolean[CAPACITY];
    }

    /**
     * Adds item in the set.
     * <p>
     * Amortized Time Complexity: O(1).
     * The Worst Case Time Complexity: O(n), where n - current number of items in the set.
     *
     * @param item the element to be added of type T
     * @throws SizeLimitExceededException when capacity limit exceeded
     */
    @Override
    public void add(T item) throws SizeLimitExceededException {
        boolean hasInserted = false;
        int firstHash = compOne(item);
        int secondHash = compTwo(item);
        int index = firstHash;
        for (int i = 0; i < CAPACITY; i++) {
            if (slots[index] != null && slots[index].equals(item)) {
                hasInserted = true;
                break;
            } else if (slots[index] == null || deleted[index]) {
                slots[index] = item;
                size += 1;
                deleted[index] = false;
                hasInserted = true;
                break;
            }
            index = (index + secondHash) % CAPACITY;
        }
        if (!hasInserted) {
            throw new SizeLimitExceededException("Capacity Limit Exceeded");
        }
    }

    /**
     * Removes the item from the set.
     * <p>
     * Amortized Time Complexity: O(1).
     * The Worst Case Time Complexity: O(n), where n - current number of items in the set.
     *
     * @param item the element to be removed from the set
     */
    @Override
    public void remove(T item) {
        int firstHash = compOne(item);
        int secondHash = compTwo(item);
        int index = firstHash;
        for (int i = 0; i < CAPACITY; i++) {
            if (slots[index] != null && slots[index].equals(item)) {
                deleted[index] = true;
                slots[index] = null;
                size -= 1;
                break;
            }
            index = (index + secondHash) % CAPACITY;
        }
    }

    /**
     * Checks if there is an item in the set.
     * <p>
     * Amortized Time Complexity: O(1).
     * The Worst Case Time Complexity: O(n), where n - current number of items in the set.
     *
     * @param item the element to be checked
     * @return true if there is an item in the set, false otherwise.
     */
    @Override
    public boolean contains(T item) {
        int firstHash = compOne(item);
        int secondHash = compTwo(item);
        int index = firstHash;
        for (int i = 0; i < CAPACITY; i++) {
            if (slots[index] == null && !deleted[index])
                return false;
            else if (slots[index] != null && slots[index].equals(item))
                return true;
            index = (index + secondHash) % CAPACITY;
        }
        return false;
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
        return size;
    }

    /**
     * Checks if the queue is empty.
     * <p>
     * Time Complexity: O(1).
     *
     * @return true if the stack is empty, and false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns all values stored in the set.
     * <p>
     * Time Complexity: O(n), where n - capacity of the set.
     *
     * @return all values stored in the set
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (T elem : slots) {
            if (elem != null)
                s.append(elem).append(" ");
        }
        return s.toString();
    }

    /**
     * Translates the first hashcode into array index
     * <p>
     * Time Complexity: O(1).
     *
     * @param item the element to be translated
     * @return the index obtained by translating the first hashcode to the array index
     */
    private int compOne(T item) {
        int hash = item.hashCode();
        return Math.abs(hash) % CAPACITY;
    }

    /**
     * Translates the second hashcode into array index.
     * <p>
     * Time Complexity: O(1).
     *
     * @param item the element to be translated
     * @return The index obtained by translating the second hashcode to the array index
     */
    private int compTwo(T item) {
        int hash = hashCode2(item);
        return Math.abs(hash) % CAPACITY;
    }

    /**
     * Returns the second hashcode of the item
     * <p>
     * Time Complexity: O(k), where k is the length of item.toString().
     *
     * @param item the element to be hashed
     * @return second hashcode of the item
     */
    private int hashCode2(T item) {
        String s = item.toString();
        int hash = 0;
        for (int i = 0; i < s.length(); i++) {
            hash = (hash + (i * s.charAt(i)) % CAPACITY) % CAPACITY;
        }
        return hash;

    }
}

/**
 * Interface for the Double Hash Set.
 *
 * @param <T> the type of the stored values
 */
interface ISet<T> {
    void add(T item) throws SizeLimitExceededException; // add item in the set

    void remove(T item); // remove an item from a set

    boolean contains(T item); // check if a item belongs to a set

    int size(); // number of elements in a set

    boolean isEmpty(); // check if the set is empty
}
