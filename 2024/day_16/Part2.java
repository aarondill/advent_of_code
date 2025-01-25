import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static void main(String[] args) throws FileNotFoundException {
    // Note: walls are represented by nulls
    List<List<Node>> maze = new ArrayList<>();
    Node start = null, end = null;
    try (Scanner scan = readInput(args)) {
      int y = 0;
      while (scan.hasNextLine()) {
        String line = scan.nextLine();
        if (line.isEmpty()) continue;
        List<Node> row = new ArrayList<>(line.length());
        int x = 0;
        for (char c : line.toCharArray()) {
          if (c == '#') {
            row.add(null);
          } else {
            Node n = new Node(new Point(x, y));
            if (c == 'E') end = n;
            else if (c == 'S') start = n;
            row.add(n); // Start and end are spaces
          }
          x++;
        }
        maze.add(row);
        y++;
      }
    }
    if (start == null || end == null) throw new IllegalStateException("No start or end");
    // connect adjecent nodes
    for (int i = 0; i < maze.size(); i++) {
      for (int j = 0; j < maze.get(i).size(); j++) {
        Node node = maze.get(i).get(j);
        if (node == null) continue;
        if (j - 1 >= 0) node.connect(maze.get(i).get(j - 1)); // left
        if (j + 1 < maze.get(i).size()) node.connect(maze.get(i).get(j + 1)); // right
        if (i - 1 >= 0) node.connect(maze.get(i - 1).get(j)); // top
        if (i + 1 < maze.size()) node.connect(maze.get(i + 1).get(j)); // bottom
      }
    }
    // make all nodes immutable
    maze.stream().flatMap(List::stream).filter(Objects::nonNull).forEach(Node::finish);

    Set<Node> result = solve(maze, start, end);

    List<List<String>> maze2 = new ArrayList<>();
    for (int y = 0; y < maze.size(); y++) {
      List<String> row = new ArrayList<>();
      for (int x = 0; x < maze.get(y).size(); x++) {
        Node n = maze.get(y).get(x);
        if (n == null) row.add("#");
        else if (result.contains(n)) row.add(ANSI_RED + 'O' + ANSI_RESET);
        else row.add(".");
      }
      maze2.add(row);
    }
    for (List<String> row : maze2) {
      System.out.println(String.join("", row));
    }
    System.out.println(result.size());
  }

  static final String ANSI_RESET = "\u001B[0m", ANSI_RED = "\u001B[31m";

  public static Set<Node> solve(List<List<Node>> maze, Node start, Node end) {
    List<Path> paths = solve(maze, start, end, new HashSet<>(), 0);
    paths.stream().mapToInt(p -> p.score).min().ifPresent(min -> paths.removeIf(p -> p.score > min));
    return paths.stream().map(Path::nodes).flatMap(List::stream).collect(Collectors.toSet());
  }

  // Find shortest path from start to end, note: 90 degree turns cost 1000
  // Returns a list of all paths from start to end
  public static List<Path> solve(List<List<Node>> maze, Node start, Node end, Set<Node> currentPath, int depth) {
    if (start == end) return List.of(Path.of(start));
    currentPath.add(start);
    System.out.println("depth: " + depth + " start: " + start);
    List<Path> paths = new ArrayList<>();
    for (Node n : start.neighbors()) {
      if (currentPath.contains(n)) continue;
      List<Path> newPaths = solve(maze, n, end, new HashSet<>(currentPath), depth + 1);
      newPaths.stream().map(p -> p.withStart(start)).forEach(paths::add);
    }

    return paths.stream().toList();
  }
}

// an immutable path
class Path {
  public final Node start;
  public final Node end;
  private final List<Node> nodes;
  private final Map<Node, Direction> directions = new HashMap<>();
  public final int score; // minimize score

  public List<Node> nodes() {
    return Collections.unmodifiableList(this.nodes);
  }

  public Map<Node, Direction> directions() {
    return Collections.unmodifiableMap(this.directions);
  }

  private static Map<List<Node>, Path> cache = new HashMap<>();

  public Path withStart(Node start) {
    List<Node> nodes = new ArrayList<>(this.nodes);
    nodes.add(0, start);
    return of(nodes);
  }

  public static Path of(Node node) {
    return of(List.of(node));
  }

  // Returns memoized paths
  public static Path of(List<Node> nodes) {
    return cache.computeIfAbsent(nodes, Path::new);
  }

  public Path(List<Node> nodes) {
    if (nodes.isEmpty())
      throw new IllegalArgumentException("Cannot create path with no nodes (create a path with one node instead)");
    for (Node n : nodes)
      if (!n.isFinished()) throw new IllegalArgumentException("Cannot create path with unfinished node");
    this.nodes = nodes;
    this.start = nodes.getFirst();
    this.end = nodes.getLast();
    if (nodes.size() == 1) {
      this.score = 0;
    } else {
      int dist = nodes.size() - 1; // minus 1 because we don't count the start node
      directions.put(nodes.get(0), Direction.EAST); // We start facing east
      // -1 because we don't have to turn on the end node
      for (int i = 0; i < nodes.size() - 1; i++) {
        Direction thisDir = directions.get(nodes.get(i));
        Direction nextDir = Direction.getFromPoints(nodes.get(i).position(), nodes.get(i + 1).position());
        directions.put(nodes.get(i + 1), nextDir);
        if (nextDir != thisDir) dist += 1000; // treat turns as 1000 distance
      }
      this.score = dist;
    }
  }

  public String toString() {
    return score + nodes.stream().map(n -> n.position().toString()).collect(Collectors.joining("->"));
  };
}

record Result(Map<Node, Integer> distances, List<Node> path, Map<Node, Direction> directions) {}

class Node {
  private static int globalId = 0;
  private final transient int id = globalId++; // unique id, used to calculate equality. Note: this isn't preserved in serialization
  private final Point position;
  // This list is guaranteed to never contain nulls
  private List<Node> neighbors = new ArrayList<>();
  private boolean isFinished = false;

  public List<Node> neighbors() {
    return this.neighbors;
  }

  public boolean isFinished() {
    return this.isFinished;
  }

  public void connect(Node a) {
    if (a == null) return; // ignore walls
    this.neighbors.add(a);
  }

  // Makes this node immutable
  public void finish() {
    if (isFinished) return;
    this.neighbors = Collections.unmodifiableList(this.neighbors);
    this.isFinished = true;
  }

  public Point position() {
    return position;
  }

  Node(Point position) {
    this.position = position;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    return prime + id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Node other = (Node) obj;
    if (id != other.id) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Node{" + "position=" + position + ", neighbors=" + neighbors.size() + '}';
  }
}

enum Direction {
  NORTH, SOUTH, EAST, WEST;

  // NOTE: only works for orthogonal directions
  // return the direction from a to b
  public static Direction getFromPoints(Point a, Point b) {
    if (a.equals(b)) throw new IllegalArgumentException("Cannot get direction from same point");
    if (a.x() == b.x()) return a.y() > b.y() ? NORTH : SOUTH;
    return a.x() < b.x() ? EAST : WEST;
  }
}

// NOTE: -y is up, +y is down, +x is right, -x is left
record Point(int x, int y) {
  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
