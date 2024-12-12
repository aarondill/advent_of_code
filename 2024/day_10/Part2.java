import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  // Count the number of paths from the start to each end (summed).
  private static long numberOfPaths(Node start, List<Node> ends) {
    if (ends.contains(start)) return 1;
    long sum = 0;
    for (Node n : start.connections()) {
      sum += numberOfPaths(n, ends);
    }
    return sum;
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
          char c = line.charAt(x);
          Node node = new Node(c - '0', x, y);
          if (graph.size() <= y) graph.add(new ArrayList<>());
          graph.get(y).add(node);
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
    long result = starts.stream().mapToLong(start -> numberOfPaths(start, ends)).sum();
    System.out.println(result);

  }
}

record Node(int value, List<Node> connections, int x, int y) {
  public Node(int value, int x, int y) {
    this(value, new ArrayList<>(), x, y);
  }

  public String toString() {
    String ret = String.format("%s[%d, %d]", value, x, y);
    if (connections.isEmpty()) return ret;
    return String.format("%s(%s)", ret, connections.stream().map(Node::toString).collect(Collectors.joining(", ")));
  }
}

interface CanConnect {
  boolean test(Node n, int x, int y);
}
