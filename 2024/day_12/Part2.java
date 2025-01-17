import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;

public class Part2 {
  public static void main(String[] args) throws FileNotFoundException {
    Graph graph = new Graph();
    try (Scanner scan = readInput(args)) {
      for (int y = 0; scan.hasNextLine(); y++) {
        String line = scan.nextLine();
        for (int x = 0; x < line.length(); x++) {
          char c = line.charAt(x);
          graph.addNode(new Node(c, new Point(x, y)));
        }
      }
    }
    List<Region> regions = new ArrayList<>();
    Set<Node> visited = new HashSet<>();
    for (Node node : graph.nodes()) {
      if (visited.contains(node)) continue;
      Region region = new Region().recursiveAddToRegion(node, graph);
      regions.add(region);
      visited.addAll(region.nodes());
    }

    System.out.println(regions.stream().mapToLong(Region::price).sum());
  }

  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }
}

class Region {
  // The inverse of nodes, maps positions to nodes
  private HashMap<Point, Node> map = new HashMap<>();
  private Set<Node> nodes = new HashSet<>();

  public Region recursiveAddToRegion(Node node, Graph graph) {
    this.add(node);
    List<Node> neighbors = graph.getNeighbors(node).stream().filter(Predicate.not(this::contains)).toList();
    neighbors.forEach(n -> this.recursiveAddToRegion(n, graph));
    return this;
  }

  public Set<Node> nodes() {
    return Collections.unmodifiableSet(nodes);
  }

  public Region add(Node node) {
    nodes.add(node);
    map.computeIfAbsent(node.pos(), k -> node);
    return this;
  }

  public boolean contains(Node node) {
    return nodes.contains(node);
  }

  public int area() {
    return nodes.size();
  }

  // Sides=corners!
  public long sides() {
    Point[] ne = {new Point(0, -1), new Point(1, 0)};
    Point[] se = {new Point(0, 1), new Point(1, 0)};
    Point[] sw = {new Point(-1, 0), new Point(0, -1)};
    Point[] nw = {new Point(-1, 0), new Point(0, 1)};
    List<Point[]> directions = List.of(ne, se, sw, nw);
    long corners = nodes.stream().mapToLong(node -> {
      return directions.stream().filter(direction -> {
        Point vertical = node.pos().add(direction[0]);
        Point horizontal = node.pos().add(direction[1]);
        Point diagonal = node.pos().add(direction[0].add(direction[1]));
        // It's a convex (outside) corner if both points are different from this one
        return (!map.containsKey(vertical) && !map.containsKey(horizontal))
            // It's a concave (inside) corner if both points are the same and the diagonal is different
            || (map.containsKey(vertical) && map.containsKey(horizontal) && !map.containsKey(diagonal));
      }).count();
    }).sum();
    return corners;
  }

  public long price() {
    return sides() * area();
  }

  @Override
  public String toString() {
    return String.format("%s{%s * %s = %s}", nodes.toString(), sides(), area(), price());
  }

}

record Point(int x, int y) {
  public Point add(Point other) {
    return new Point(x + other.x, y + other.y);
  }

  // Returns cardinal directions, North, East, South, West
  public List<Point> getCardinals() {
    return List.of(new Point(x, y - 1), new Point(x + 1, y), new Point(x, y + 1), new Point(x - 1, y));
  }

  // Returns all cardinal directions, x∈[0, x) and y∈[0, y)
  public List<Point> getCardinals(int x, int y) {
    return getCardinals().stream().filter(p -> p.x() >= 0 && p.y() >= 0 && p.x() < x && p.y() < y).toList();
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}

record Node(char name, Point pos) {
  @Override
  public String toString() {
    return name + "" + pos;
  }
}

class Graph {
  List<List<Node>> graph = new ArrayList<>();

  public List<Node> nodes() {
    return graph.stream().flatMap(List::stream).toList();
  }

  public void addNode(Node node) {
    Point pos = node.pos();
    while (graph.size() <= pos.y())
      graph.add(new ArrayList<>());
    while (graph.get(pos.y()).size() <= pos.x())
      graph.get(pos.y()).add(null);
    graph.get(pos.y()).set(pos.x(), node);
  }

  // Gets horizontal and vertical neighbors of a node with matching names
  public List<Node> getNeighbors(Node node) {
    return node.pos().getCardinals(graph.get(0).size(), graph.size()).stream().map(p -> graph.get(p.y()).get(p.x()))
        .filter(n -> n.name() == node.name()).toList();
  }
}
