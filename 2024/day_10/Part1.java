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

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  private static Map<Node, Node> dijkstra(Node start) {
    Map<Node, Node> prev = new HashMap<>();
    Map<Node, Integer> dist = new HashMap<>();
    PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(dist::get));
    dist.put(start, 0);
    queue.add(start);
    while (!queue.isEmpty()) {
      Node node = queue.poll();
      for (Node n : node.connections()) {
        int newDist = dist.get(node) + 1;
        if (newDist < dist.getOrDefault(n, Integer.MAX_VALUE)) {
          dist.put(n, newDist);
          prev.put(n, node);
          queue.add(n);
        }
      }
    }
    return prev;
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
    long result =
        starts.stream().map(Part1::dijkstra).mapToLong(res -> ends.stream().filter(res::containsKey).count()).sum();
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
