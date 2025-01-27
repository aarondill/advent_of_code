import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static final int INPUT_SIZE = 1024; // 1024
  public static final int GRID_SIZE = 71; // 71
  public static final Point START = new Point(0, 0);
  public static final Point END = new Point(GRID_SIZE - 1, GRID_SIZE - 1);

  public static void main(String[] args) throws FileNotFoundException {
    List<Point> points;
    try (Scanner scan = readInput(args)) {
      points = IntStream.range(0, INPUT_SIZE).mapToObj(i -> scan.nextLine().split(","))
          .map(coords -> new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]))).toList();
    }
    // Indexed by y, then x
    List<List<Space>> grid = IntStream.range(0, GRID_SIZE).mapToObj(i -> Collections.nCopies(GRID_SIZE, Space.EMPTY))
        .<List<Space>>map(ArrayList::new).toList();
    for (Point point : points)
      grid.get(point.y()).set(point.x(), Space.CORRUPTED);

    // Walls are represented as null
    List<List<Node>> nodes = new ArrayList<>();
    for (int y = 0; y < grid.size(); y++) {
      List<Node> row = new ArrayList<>();
      for (int x = 0; x < grid.get(y).size(); x++) {
        if (grid.get(y).get(x) == Space.CORRUPTED) row.add(null);
        else row.add(new Node(x, y));
      }
      nodes.add(row);
    }
    // Connect nodes
    for (int y = 0; y < nodes.size(); y++) {
      for (int x = 0; x < nodes.get(y).size(); x++) {
        Node thisNode = nodes.get(y).get(x);
        if (thisNode == null) continue;
        for (Point cardinal : thisNode.pos().cardinals(nodes.get(y).size(), nodes.size())) {
          thisNode.connect(nodes.get(cardinal.y()).get(cardinal.x()));
        }
      }
    }
    Node start = nodes.get(START.y()).get(START.x());
    Node end = nodes.get(END.y()).get(END.x());
    Result distance = dijkstra(start, end);
    List<Node> path = Stream.iterate(end, distance.previous()::containsKey, distance.previous()::get)
        .collect(Collectors.toCollection(ArrayList::new)).reversed();
    path.add(0, start);
    for (Node node : path) {
      grid.get(node.pos().y()).set(node.pos().x(), Space.WALK);
    }
    for (int y = 0; y < grid.size(); y++) {
      for (int x = 0; x < grid.get(y).size(); x++) {
        switch (grid.get(y).get(x)) {
          case Space.WALK -> System.out.print("0");
          case Space.CORRUPTED -> System.out.print("#");
          default -> System.out.print(".");
        }
      }
      System.out.println();
    }

    System.out.println(distance.distances().get(end));
  }

  public static Result dijkstra(Node start, Node end) {
    Map<Node, Integer> distances = new HashMap<>();
    Map<Node, Node> previous = new HashMap<>();
    PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
    distances.put(start, 0);
    queue.add(start);
    while (!queue.isEmpty()) {
      Node current = queue.poll();
      if (current == end) break;
      for (Node neighbor : current.neighbors()) {
        int newDistance = distances.get(current) + 1;
        if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
          distances.put(neighbor, newDistance);
          previous.put(neighbor, current);
          queue.add(neighbor);
        }
      }
    }

    return new Result(distances, previous);
  }

}

record Result(Map<Node, Integer> distances, Map<Node, Node> previous) {}

record Node(Point pos, List<Node> neighbors) {
  public void connect(Node a) {
    if (a == null || a == this) return;
    this.neighbors.add(a);
    a.neighbors.add(this);
  }

  public Node(int x, int y) {
    this(new Point(x, y), new ArrayList<>());
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this;
  }
}

enum Space {
  EMPTY, CORRUPTED, WALK
}

record Point(int x, int y) {
  public List<Point> cardinals() {
    return List.of(new Point(x, y - 1), // North
        new Point(x + 1, y), // East
        new Point(x, y + 1), // South
        new Point(x - 1, y) // West
    );
  }

  public List<Point> cardinals(int exclusiveX, int exclusiveY) {
    return cardinals().stream().filter(p -> p.x() >= 0 && p.y() >= 0 && p.x() < exclusiveX && p.y() < exclusiveY)
        .toList();
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
