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

public class Part1 {

  private static Region addToRegion(Region r, Node node, Graph graph) {
    r.add(node);
    graph.getNeighbors(node).stream().filter(Predicate.not(r::contains)).forEach(n -> addToRegion(r, n, graph));
    return r;
  }

  public static void main(String[] args) throws FileNotFoundException {
    Graph graph = new Graph();
    try (Scanner scan = readInput(args)) {
      for (int y = 0; scan.hasNextLine(); y++) {
        String line = scan.nextLine();
        for (int x = 0; x < line.length(); x++)
          graph.addNode(new Node(line.charAt(x), x, y));
      }
    }

    List<Region> regions = new ArrayList<>();
    Set<Node> visited = new HashSet<>();
    for (Node node : graph.nodes()) {
      if (visited.contains(node)) continue;
      Region region = addToRegion(new Region(), node, graph);
      regions.add(region);
      visited.addAll(region.nodes());
    }

    System.out.println(regions.stream().mapToInt(Region::price).sum());
  }

  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }
}

class Region {
  private HashMap<Point, Node> map = new HashMap<>();
  Set<Node> nodes = new HashSet<>();

  public Set<Node> nodes() {
    return Collections.unmodifiableSet(nodes);
  }

  Region add(Node node) {
    nodes.add(node);
    map.computeIfAbsent(node.pos(), k -> node);
    return this;
  }

  boolean contains(Node node) {
    return nodes.contains(node);
  }

  int area() {
    return nodes.size();
  }

  int perimeter() {
    // Each node perimeter is 4 - (number of neighbors)
    return nodes.stream().mapToInt(n -> 4 - getNeighbors(n).size()).sum();
  }

  int price() {
    return perimeter() * area();
  }

  private List<Node> getNeighbors(Node node) {
    return node.pos().getCardinals().stream().filter(map::containsKey).map(map::get).toList();
  }

  @Override
  public String toString() {
    return String.format("%s{%s * %s = %s}", nodes.toString(), perimeter(), area(), price());
  }

}

record Point(int x, int y) {
  public List<Point> getCardinals() {
    return List.of(new Point(x - 1, y), new Point(x + 1, y), new Point(x, y - 1), new Point(x, y + 1));
  }

  // Returns all cardinal directions, x∈[0, max.x) and y∈[0, max.y)
  public List<Point> getCardinals(Point excludeMax) {
    return getCardinals().stream()
        .filter(p -> p.x() >= 0 && p.y() >= 0 && p.x() < excludeMax.x() && p.y() < excludeMax.y()).toList();
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}

record Node(char name, Point pos) {
  Node(char name, int x, int y) {
    this(name, new Point(x, y));
  }

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
    return node.pos().getCardinals(new Point(graph.get(0).size(), graph.size())).stream()
        .map(p -> graph.get(p.y()).get(p.x())).filter(n -> n.name() == node.name()).toList();
  }
}
