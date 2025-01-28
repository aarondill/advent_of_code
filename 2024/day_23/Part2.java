import java.io.File;
import java.io.FileNotFoundException;
import static java.util.stream.Collectors.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  // Part 1
  // Find all cycles of length 3 begining with nodes starting with the letter 't'
  // 1. parse input
  // 2. find all nodes starting with 't'
  // 3. find cycles of length 3 starting with those nodes
  // 4. print number of cycles
  public static void main(String[] args) throws FileNotFoundException {
    Map<String, Node> nodes = new HashMap<>();
    try (Scanner scan = readInput(args)) {
      while (scan.hasNextLine()) {
        String[] names = scan.nextLine().split("-"); // kh-tc
        Node a = nodes.computeIfAbsent(names[0], Node::new);
        Node b = nodes.computeIfAbsent(names[1], Node::new);
        a.connect(b);
      }
    }
    List<Node> t = nodes.values().stream().filter(n -> n.name().startsWith("t")).toList();
    Set<Node> largest = t.stream().parallel().map(Part2::findInterconnected).flatMap(Set::stream).distinct()
        .reduce((a, b) -> a.size() > b.size() ? a : b).get();
    System.out.println(largest.stream().map(Node::name).sorted().collect(joining(",")));
  }

  // Returns a list of nodes, in which all nodes are connected to every other node.
  public static Set<Set<Node>> findInterconnected(Node start) {
    HashMap<Set<Node>, Set<Set<Node>>> cache = new HashMap<>();
    return findInterconnected(start, start, new HashSet<>(), cache);
  }

  public static Set<Set<Node>> findInterconnected(Node n, Node target, Set<Node> path,
      HashMap<Set<Node>, Set<Set<Node>>> cache) {
    if (path.contains(target)) return Set.of(path);
    if (cache.containsKey(path)) return cache.get(path);
    Set<Set<Node>> cycles = new HashSet<>();
    for (Node node : n.neighbors()) {
      if (path.contains(node)) continue; // skip nodes we've already visited (we can't repeat nodes)
      if (!node.neighbors().containsAll(path)) continue; // skip nodes that aren't connected to every other node in the path
      Set<Node> newPath = new HashSet<>(path);
      newPath.add(node);
      cycles.addAll(findInterconnected(node, target, newPath, cache));
    }
    cache.put(path, cycles);
    return cycles;
  }
}

record Node(String name, List<Node> neighbors) {
  public List<Node> neighbors() {
    return Collections.unmodifiableList(neighbors);
  }

  public Node connect(Node other) {
    if (other != null) {
      neighbors.add(other);
      other.neighbors.add(this);
    }
    return this;
  }

  public Node(String name) {
    this(name, new ArrayList<>());
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Node node = (Node) o;
    return name.equals(node.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
