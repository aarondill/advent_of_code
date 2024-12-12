import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  private static int countReachable(Node start, Collection<Node> ends) {
    return countReachable(start, new HashSet<>(ends));
  }

  // NOTE: modifies ends!
  private static int countReachable(Node start, HashSet<Node> ends) {
    return start.connections().stream().mapToInt(n -> {
      if (ends.contains(n)) {
        ends.remove(n);
        return 1;
      }
      return countReachable(n, ends);
    }).sum();
  }

  public static void main(String[] args) throws FileNotFoundException {
    // indexed [y][x]
    List<List<Node>> graph = new ArrayList<>();
    try (Scanner scan = readInput(args)) {
      int y = 0;
      while (scan.hasNextLine()) {
        String line = scan.nextLine();
        if (line.isEmpty()) continue;
        for (int x = 0; x < line.length(); x++) {
          int v = line.charAt(x) - '0';
          if (graph.size() <= y) graph.add(new ArrayList<>());
          graph.get(y).add(new Node(v));
        }
        y++;
      }
    }

    CanConnect canConnect = (n, x, y) -> {
      try {
        return graph.get(y).get(x).value() - n.value() == 1;
      } catch (IndexOutOfBoundsException e) {
        return false;
      }
    };
    for (int y = 0; y < graph.size(); y++) {
      for (int x = 0; x < graph.get(y).size(); x++) {
        Node node = graph.get(y).get(x);
        if (canConnect.test(node, x, y - 1)) node.connections().add(graph.get(y - 1).get(x)); // North
        if (canConnect.test(node, x - 1, y)) node.connections().add(graph.get(y).get(x - 1)); // West
        if (canConnect.test(node, x + 1, y)) node.connections().add(graph.get(y).get(x + 1)); // East
        if (canConnect.test(node, x, y + 1)) node.connections().add(graph.get(y + 1).get(x)); // South
      }
    }
    List<Node> starts = graph.stream().flatMap(List::stream).filter(n -> n.value() == 0).toList();
    List<Node> ends = graph.stream().flatMap(List::stream).filter(n -> n.value() == 9).toList();
    long result = starts.stream().mapToInt(start -> countReachable(start, ends)).sum();
    System.out.println(result);

  }
}

record Node(int value, List<Node> connections) {
  public Node(int value) {
    this(value, new ArrayList<>());
  }

  // Needed so List.contains() works. Each node is unique, not only by value, but by it's position (reference) in the graph.
  public boolean equals(Object o) {
    return this == o;
  }

  public String toString() {
    String ret = value + "";
    if (connections.isEmpty()) return ret;
    return String.format("%s(%s)", ret, connections.stream().map(Node::toString).collect(Collectors.joining(", ")));
  }
}

interface CanConnect {
  boolean test(Node n, int x, int y);
}
