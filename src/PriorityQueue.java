import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.ceil;
import static java.lang.Math.log;
import static java.util.Collections.swap;


/**
 * Implements Priority Queue based on Fibonacci Heap implementation.
 *
 * @param <T> the type of elements stored in the Priority Queue
 * @author Vladimir Ryabenko
 * @version 1.0; 05.04.2022
 */
public class PriorityQueue<T extends Comparable<? super T>> implements IPriorityQueue<T> {
    private Node min;
    private int size;
    private HashMap<T, Node> links; // Links items and corresponding nodes

    /**
     * Creates the new Priority Queue.
     */
    public PriorityQueue() {
        size = 0;
        min = null;
        links = new HashMap<>();
    }

    /**
     * Inserts the given item into the queue.
     *
     * @param item the element to be inserted
     */
    @Override
    public void insert(T item) {
        Node newNode = new Node(item);
        insertToRoot(newNode);
        links.put(item, newNode);
        if (newNode.item.compareTo(min.item) < 0)
            min = newNode;
        size++;
    }

    /**
     * Returns the minimum element stored in the queue.
     *
     * @return the minimum element stored in the queue
     */
    @Override
    public T findMin() {
        if (size == 0)
            return null;
        return min.item;
    }

    /**
     * Extracts and returns the minimum element stored in the queue.
     *
     * @return the minimum element stored in the queue
     */
    @Override
    public T extractMin() {
        if (min == null)
            return null;
        Node curMin = min;
        Node child = curMin.child;
        while (child != null) {
            extractFromChildren(curMin.child, curMin);
            insertToRoot(child);
            child = curMin.child;
        }

        if (curMin == curMin.right)
            min = null;
        else {
            min = curMin.right;
            extractFromList(curMin);
            consolidate();
        }
        links.remove(curMin.item);
        size--;
        return curMin.item;
    }

    /**
     * Decreases the key of a given element.
     *
     * @param item   the element, which key should be decreased
     * @param newKey the new key
     */
    @Override
    public void decreaseKey(T item, T newKey) {
        Node decreasingNode = links.get(item);
        if (newKey != null)
            links.put(newKey, decreasingNode);
        links.remove(decreasingNode.item);
        if (newKey != null && newKey.compareTo(decreasingNode.item) > 0)
            return;
        decreasingNode.item = newKey;
        Node parent = decreasingNode.parent;
        if (parent != null && (decreasingNode.item == null || decreasingNode.item.compareTo(parent.item) < 0)) {
            cut(decreasingNode, parent);
            cascadingCut(parent);
        }
        if (decreasingNode.item == null || decreasingNode.item.compareTo(min.item) < 0)
            min = decreasingNode;
    }

    /**
     * Deletes the given element from the queue.
     *
     * @param item the element to be deleted
     */
    @Override
    public void delete(T item) {
        if (links.containsKey(item)) {
            decreaseKey(item, null);
            extractMin();
        }
    }

    /**
     * Unites two queues. The current queue represents the result.
     *
     * @param anotherQueue the queue, which should be united with the current one
     */
    @Override
    public void union(PriorityQueue<T> anotherQueue) {
        if (anotherQueue.isEmpty())
            return;
        if (isEmpty()) {
            min = anotherQueue.min;
            size = anotherQueue.size;
        } else {
            unionLists(min, anotherQueue.min);
            size += anotherQueue.size;
            this.links.putAll(anotherQueue.links);
            if (min == null || (anotherQueue.min != null && anotherQueue.min.item.compareTo(min.item) < 0))
                min = anotherQueue.min;
        }
    }

    /**
     * Returns the current size of the queue.
     *
     * @return the current size of the queue
     */
    public int size() {
        return size;
    }

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty and false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Unites two lists.
     *
     * @param first  first list to be merged
     * @param second second list to be merged
     */
    private void unionLists(Node first, Node second) {
        Node L = first.left;
        Node R = second.right;
        second.right = first;
        first.left = second;
        L.right = R;
        R.left = L;
    }

    /**
     * Inserts to the root list.
     *
     * @param newNode the node to be inserted
     */
    private void insertToRoot(Node newNode) {
        if (min != null) {
            min.left.right = newNode;
            newNode.left = min.left;
            newNode.right = min;
            min.left = newNode;
        } else {
            min = newNode;
            newNode.left = newNode.right = newNode;
        }
    }

    /**
     * Inserts to the children list.
     *
     * @param child    the reference to the children list
     * @param newChild the node to be inserted
     */
    private void insertToChildren(Node child, Node newChild) {
        child.left.right = newChild;
        newChild.left = child.left;
        newChild.right = child;
        child.left = newChild;
    }

    /**
     * Extracts the node from the list.
     *
     * @param node the node be extracted
     */
    private void extractFromList(Node node) {
        Node right = node.right;
        Node left = node.left;
        right.left = left;
        left.right = right;
    }

    /**
     * Extracts from the children list.
     *
     * @param child  the reference to the children list
     * @param parent the parent of the children
     */
    private void extractFromChildren(Node child, Node parent) {
        if (child.right == child) {
            parent.child = null;
        } else {
            parent.child = child.right;
            extractFromList(child);
        }
        child.parent = null;
    }

    /**
     * Consolidates the root list of the queue.
     */
    private void consolidate() {
        int D = (int) ceil(log(size) / log(2)) + 1;
        ArrayList<Node> A = new ArrayList<>();
        for (int i = 0; i < D; i++) {
            A.add(null);
        }
        Node curNode = min;
        do {
            int degree = curNode.degree;
            while (A.get(degree) != null && A.get(degree) != curNode) {
                Node anotherNode = A.get(degree);
                if (curNode.item.compareTo(anotherNode.item) > 0) {
                    ArrayList<Node> toSwap = new ArrayList<>();
                    toSwap.add(curNode);
                    toSwap.add(anotherNode);
                    swap(toSwap, 0, 1);
                    curNode = toSwap.get(0);
                    anotherNode = toSwap.get(1);
                }
                if (anotherNode == min) {
                    min = curNode; // fixme
                }
                fibHeapLink(anotherNode, curNode);
                A.set(degree, null);
                degree++;
            }
            A.set(degree, curNode);
            curNode = curNode.right;
        } while (curNode != min);
        min = null;
        for (int i = 0; i < D; i++) {
            if (A.get(i) != null) {
                Node oldMin = min;
                insertToRoot(A.get(i));
                if (oldMin != null && A.get(i).item.compareTo(min.item) < 0)
                    min = A.get(i);
            }
        }
    }

    /**
     * Links two heaps from the root list.
     *
     * @param newChild the new child of the "parent"
     * @param parent   the new parent of the "newChild"
     */
    private void fibHeapLink(Node newChild, Node parent) {
        extractFromList(newChild);

        newChild.parent = parent;
        if (parent.child == null) {
            parent.child = newChild;
            newChild.left = newChild.right = newChild;
        } else {
            insertToChildren(parent.child, newChild);
        }
        parent.degree++;
        newChild.mark = false;
    }

    /**
     * Cuts the link between child and its parent, making child a root.
     *
     * @param child  the child of the "parent"
     * @param parent the parent of the "child"
     */
    private void cut(Node child, Node parent) {
        extractFromChildren(child, parent);

        parent.degree--;
        child.mark = false;

        insertToRoot(child);
    }

    /**
     * Repeats its way up the tree until it finds either a root or an unmarked node.
     *
     * @param node current node
     */
    private void cascadingCut(Node node) {
        Node parent = node.parent;
        if (parent != null) {
            if (!node.mark)
                node.mark = true;
            else {
                cut(node, parent);
                cascadingCut(parent);
            }
        }
    }

    /**
     * Represents the node of the Priority Queue.
     */
    private class Node {
        Node left, right;
        Node parent, child;
        int degree;
        T item;
        boolean mark;

        /**
         * Creates the new node.
         *
         * @param item the element to be stored in the queue.
         */
        public Node(T item) {
            parent = child = null;
            left = right = this;
            degree = 0;
            this.item = item;
            mark = false;
        }
    }
}

/**
 * Represents the pair of key and corresponding to it value.
 *
 * @param <K> the type of key element
 * @param <V> the type of value element
 */
class Pair<K extends Comparable<? super K>, V extends Comparable<? super V>> implements Comparable<Pair<K, V>> {
    private final K key;
    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Compares current element with given one of the same type.
     *
     * @param pair item that is compared with current element
     * @return value, which is greater than 0 if current element is greater than another element,
     * lesser than 0 if current element is lesser than another element and 0 if they are equal
     */
    @Override
    public int compareTo(Pair<K, V> pair) {
        if (this.key.compareTo(pair.key) == 0) {
            return this.value.compareTo(pair.value);
        }
        return this.key.compareTo(pair.key);
    }

    /**
     * Returns the value of the 'key'
     *
     * @return value of the 'key'
     */
    public K getKey() {
        return key;
    }

    /**
     * Returns the value of the 'value'
     *
     * @return value of the 'key'
     */
    public V getValue() {
        return value;
    }
}

/**
 * Interface for the Priority Queue.
 *
 * @param <T> the type of elements stored in the Priority Queue
 */
interface IPriorityQueue<T extends Comparable<? super T>> {
    void insert(T item); // Inserts item to the queue

    T findMin(); // Returns the minimum value stored in the queue

    T extractMin(); // Extracts and returns the minimum value stored in the queue

    void decreaseKey(T item, T newKey); // Decreases the key of a given element

    void delete(T item); // Deletes the given element from the queue

    void union(PriorityQueue<T> anotherQueue); //Unites two queues
}
