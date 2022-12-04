import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;


/**
 * Implements the graph based on an adjacency matrix.
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 * @author Vladimir Ryabenko
 * @version 1.0; 15.04.2022
 */
public class Graph<V extends Comparable<? super V>, E extends Comparable<? super E>> implements IGraph<V, E> {
    private ArrayList<ArrayList<E>> matrix;
    private ArrayList<HashSet<Integer>> neighbors;
    private HashMap<V, Integer> code;
    private HashMap<Integer, V> decode;
    private int counter, size;

    /**
     * Creates the new Graph.
     */
    public Graph() {
        matrix = new ArrayList<>();
        neighbors = new ArrayList<>();
        code = new HashMap<>();
        decode = new HashMap<>();
        size = 0;
        counter = 0;
    }

    /**
     * Inserts the new vertex to the graph.
     *
     * @param vertex the vertex to be inserted
     */
    @Override
    public void insertVertex(V vertex) {
        if (!code.containsKey(vertex)) {
            code.put(vertex, counter);
            decode.put(counter, vertex);
            matrix.add(new ArrayList<>());
            neighbors.add(new HashSet<>());
            counter++;
            size++;

            for (int i = 0; i < counter; i++)
                matrix.get(counter - 1).add(null);
            for (int i = 0; i < counter - 1; i++)
                matrix.get(i).add(null);
        }
    }

    /**
     * Inserts the new edge to the graph.
     *
     * @param from the beginning of the new edge
     * @param to   the ending of the new edge
     * @param edge the edge to be inserted
     * @throws NoSuchElementException if there is no given vertices in the graph
     */
    @Override
    public void insertEdge(V from, V to, E edge) throws NoSuchElementException {
        if (!code.containsKey(from) || !code.containsKey(to))
            throw new NoSuchElementException("There is no given vertices in the graph");

        int fromIndex = code.get(from);
        int toIndex = code.get(to);
        neighbors.get(fromIndex).add(toIndex);
        matrix.get(fromIndex).set(toIndex, edge);
    }

    /**
     * Removes the vertex from the graph.
     *
     * @param vertex the vertex to be removed
     * @throws NoSuchElementException if there is no given vertex in the graph
     */
    @Override
    public void removeVertex(V vertex) throws NoSuchElementException {
        if (!code.containsKey(vertex))
            throw new NoSuchElementException("There is no given vertex in the graph");

        int index = code.get(vertex);
        for (int i = 0; i < counter; i++) {
            matrix.get(i).set(index, null);
            neighbors.get(i).remove(index);
        }
        size--;
        code.remove(vertex);
    }

    /**
     * Removes the edge from the graph.
     *
     * @param from the beginning of the edge to be removed
     * @param to   the ending of the edge to be removed
     * @return the removed edge
     * @throws NoSuchElementException if there is no given vertices in the graph
     */
    @Override
    public E removeEdge(V from, V to) throws NoSuchElementException {
        if (!code.containsKey(from) || !code.containsKey(to))
            throw new NoSuchElementException("There is no given vertices in the graph");

        int fromIndex = code.get(from);
        int toIndex = code.get(to);
        E removedEdge = matrix.get(fromIndex).get(toIndex);
        matrix.get(fromIndex).set(toIndex, null);
        neighbors.get(fromIndex).remove(toIndex);

        return removedEdge;
    }

    /**
     * Checks if two vertices are adjacent.
     *
     * @param v the first vertex to be checked
     * @param u the second vertex to be checked
     * @return true if two given vertices are adjacent and false otherwise
     * @throws NoSuchElementException if there is no given vertices in the graph
     */
    @Override
    public boolean areAdjacent(V v, V u) throws NoSuchElementException {
        if (!code.containsKey(v) || !code.containsKey(u))
            throw new NoSuchElementException("There is no given vertices in the graph");

        int vIndex = code.get(v);
        int uIndex = code.get(u);
        return neighbors.get(vIndex).contains(uIndex) || neighbors.get(uIndex).contains(vIndex);
    }

    /**
     * Returns the degree of the given vertex
     *
     * @param vertex the vertex to be checked
     * @return the degree of the given vertex
     * @throws NoSuchElementException if there is no given vertex in the graph
     */
    @Override
    public int degree(V vertex) throws NoSuchElementException {
        if (!code.containsKey(vertex))
            throw new NoSuchElementException("There is no given vertex in the graph");

        int index = code.get(vertex);
        return neighbors.get(index).size();
    }

    /**
     * Uses the Prim's algorithm and returns the minimum spanning forest.
     *
     * @return the minimum spanning forest
     */
    public String printMinForest() {
        HashSet<Integer> visited = new HashSet<>();
        String result = "";

        for (int index = 0; index < counter; index++) {
            if (!visited.contains(index) && code.containsKey(decode.get(index))) {
                PriorityQueue<Pair<E, Pair<Integer, Integer>>> forest = new PriorityQueue<>();
                visited.add(index);
                for (Integer vertex : neighbors.get(index)) {
                    if (!visited.contains(vertex))
                        forest.insert(new Pair<>(matrix.get(index).get(vertex), new Pair<>(index, vertex)));
                }

                while (!forest.isEmpty()) {
                    while (!forest.isEmpty() && visited.contains(forest.findMin().getValue().getValue()))
                        forest.extractMin();

                    if (!forest.isEmpty()) {
                        Pair<Integer, Integer> edge = forest.extractMin().getValue();
                        result += decode.get(edge.getKey()).toString() + ":" +
                                decode.get(edge.getValue()).toString() + " ";
                        visited.add(edge.getValue());
                        for (Integer vertex : neighbors.get(edge.getValue())) {
                            if (!visited.contains(vertex))
                                forest.insert(new Pair<>(matrix.get(edge.getValue()).get(vertex),
                                        new Pair<>(edge.getValue(), vertex)));
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Returns the size of the graph.
     *
     * @return the current size of the graph
     */
    public int size() {
        return size;
    }

    /**
     * Checks if the graph is empty.
     *
     * @return true if the graph is empty and false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }
}

/**
 * Interface for the Graph.
 *
 * @param <V> the type of the vertices
 * @param <E> the type of the edges
 */
interface IGraph<V, E> {
    void insertVertex(V v); // Inserts the vertex to a graph

    void insertEdge(V from, V to, E w); // Inserts the edge to a graph

    void removeVertex(V v); // Removes the vertex from a graph

    E removeEdge(V from, V to); // Removes the edge from a graph

    boolean areAdjacent(V v, V u); // Checks if two vertices are adjacent

    int degree(V v); // Returns the degree of the particular vertex
}
