import java.util.ArrayList;
import java.util.List;


/**
 * Implements Range Map based on B-tree implementation.
 *
 * @param <K> the type of the key values
 * @param <V> the type of the stored values
 * @author Vladimir Ryabenko
 * @version 1.0; 05.04.2022
 */
public class RangeMap<K extends Comparable<? super K>, V> implements IRangeMap<K, V> {
    private Node root;
    private final int minDeg;
    private int size;

    /**
     * Creates the new Range Map with given minimum degree.
     *
     * @param minDeg the minimum degree of the node
     */
    public RangeMap(int minDeg) {
        this.root = new Node(minDeg, true);
        this.minDeg = minDeg;
        size = 0;
    }

    /**
     * Creates the new Range Map with minimum degree by default.
     */
    public RangeMap() {
        this.root = new Node(17, true);
        this.minDeg = 17;
        size = 0;
    }

    /**
     * Returns the current size of the Range Map.
     *
     * @return the current size of the Range Map
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns true if the map is empty and false otherwise.
     *
     * @return true if the map is empty and false otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Adds new element to the map.
     *
     * @param key   the key of the element
     * @param value the value of the element
     */
    @Override
    public void add(K key, V value) {
        size++;
        Node rootNode = root;
        if (rootNode.size == 2 * minDeg - 1) {
            Node newRoot = new Node(minDeg, false);
            root = newRoot;
            newRoot.children.set(0, rootNode);
            splitChild(newRoot, 0);
            insertNotFull(newRoot, key, value);
        } else
            insertNotFull(rootNode, key, value);
    }

    /**
     * Checks if the map contains element with the given key.
     *
     * @param key the key to be checked
     * @return true if the map contains element with the given key and false otherwise
     */
    @Override
    public boolean contains(K key) {
        return root != null && search(root, key) != null;
    }

    /**
     * Returns the value of the element with the given key.
     *
     * @param key the key to checked
     * @return the value of the element with the given key
     */
    @Override
    public V lookup(K key) {
        return search(root, key);
    }

    /**
     * Returns the list with all values of the elements, which keys are between 'from' and 'to'.
     *
     * @param from the lower boundary of the range
     * @param to   the upper boundary of the range
     * @return the list with all values of the elements, which keys are between 'from' and 'to'
     */
    @Override
    public ArrayList<V> lookupRange(K from, K to) {
        if (root == null)
            return null;
        return searchRange(root, from, to);
    }

    /**
     * Returns the list with all values of the elements, which keys are between 'from' and 'to' in the particular node.
     *
     * @param node the given node
     * @param from the lower boundary of the range
     * @param to   the upper boundary of the range
     * @return the list with all values of the elements, which keys are between 'from' and 'to' in the given node
     */
    private ArrayList<V> searchRange(Node node, K from, K to) {
        ArrayList<V> range = new ArrayList<>();
        if (node != null) {
            int index = 0;
            while (index < node.size && from.compareTo(node.items.get(index).key) > 0)
                index++;

            while (index < node.size && to.compareTo(node.items.get(index).key) >= 0) {
                if (!node.isLeaf)
                    range.addAll(searchRange(node.children.get(index), from, to));
                range.add(node.items.get(index).value);
                index++;
            }
            range.addAll(searchRange(node.children.get(index), from, to));
        }

        return range;
    }

    /**
     * Splits the child by index of the 'node'.
     *
     * @param node  the node, which child should be split
     * @param index the index of the child, which should be split
     */
    private void splitChild(Node node, int index) {
        Node newNode = new Node(minDeg, node.isLeaf);
        Node child = node.children.get(index);
        newNode.size = minDeg - 1;
        newNode.isLeaf = child.isLeaf;

        for (int i = 0; i < minDeg - 1; i++)
            newNode.items.set(i, child.items.get(i + minDeg));

        if (!child.isLeaf) {
            for (int j = 0; j < minDeg; j++)
                newNode.children.set(j, child.children.get(j + minDeg));
        }
        child.size = minDeg - 1;

        for (int j = node.size; j >= index + 1; j--)
            node.children.set(j + 1, node.children.get(j));
        node.children.set(index + 1, newNode);

        for (int j = node.size - 1; j >= index; j--)
            node.items.set(j + 1, node.items.get(j));
        node.items.set(index, child.items.get(minDeg - 1));

        node.size++;
    }

    /**
     * Inserts new element to the not full node.
     *
     * @param node  the node, to which we are going to insert
     * @param key   the key of the element to be inserted
     * @param value the value of the element to be inserted
     */
    private void insertNotFull(Node node, K key, V value) {
        int index = node.size - 1;

        if (node.isLeaf) {
            while (index >= 0 && node.items.get(index).key.compareTo(key) > 0) {
                node.items.set(index + 1, node.items.get(index));
                index--;
            }
            node.items.set(index + 1, new Pair(key, value));
            node.size++;
        } else {
            while (index >= 0 && node.items.get(index).key.compareTo(key) > 0)
                index--;
            index++;
            if (node.children.get(index).size == 2 * minDeg - 1) {
                splitChild(node, index);
                if (node.items.get(index).key.compareTo(key) < 0)
                    index++;
            }
            insertNotFull(node.children.get(index), key, value);
        }
    }

    /**
     * Searches the value of the element with the given key in the given node.
     *
     * @param node the given node for searching
     * @param key  the key to be searched
     * @return the value of the element with the given key in the given node
     */
    private V search(Node node, K key) {
        V result = null;
        if (node != null) {
            int index = 0;
            while (index < node.size && key.compareTo(node.items.get(index).key) > 0)
                index++;

            if (index < node.size && key.compareTo(node.items.get(index).key) == 0)
                result = node.items.get(index).value;
            else if (!node.isLeaf)
                result = search(node.children.get(index), key);
        }
        return result;
    }

    /**
     * Represents the node of the B-tree.
     */
    private class Node {
        ArrayList<Pair> items;
        int minDeg;
        ArrayList<Node> children;
        int size;
        boolean isLeaf;

        /**
         * Creates the new Node with given minimum degree and isLeaf value.
         *
         * @param minDeg the minimum degree
         * @param isLeaf the value that shows if the node is leaf or not
         */
        public Node(int minDeg, boolean isLeaf) {
            this.minDeg = minDeg;
            this.isLeaf = isLeaf;
            this.size = 0;

            this.items = new ArrayList<>();
            for (int i = 0; i < 2 * this.minDeg - 1; i++) {
                this.items.add(null);
            }

            this.children = new ArrayList<>();
            for (int i = 0; i < 2 * this.minDeg; i++) {
                this.children.add(null);
            }
        }
    }

    /**
     * Represents the elements stored in the map.
     */
    private class Pair {
        K key;
        V value;

        /**
         * Creates the new pair of key-value
         *
         * @param key   the key of the element
         * @param value the value of the element
         */
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}

/**
 * Interface for the Range Map.
 *
 * @param <K> the type of the key values
 * @param <V> the type of the stored values
 */
interface IRangeMap<K, V> {
    int size(); // Returns the current size of the map

    boolean isEmpty(); // Checks if the map is empty

    void add(K key, V value); // Inserts new item into the map

    boolean contains(K key); // Checks if a key is present

    V lookup(K key); // Lookups a value by the key

    List<V> lookupRange(K from, K to); // Lookups values for a range of keys
}
