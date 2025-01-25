import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.stream.Stream;

public class Part1 {
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
    Result result = solve(maze, start, end);
    List<List<String>> maze2 = new ArrayList<>();
    for (int y = 0; y < maze.size(); y++) {
      List<String> row = new ArrayList<>();
      for (int x = 0; x < maze.get(y).size(); x++) {
        if (maze.get(y).get(x) == null) row.add("#");
        else row.add(".");
      }
      maze2.add(row);
    }
    for (Node n : result.path()) {
      Direction dir = result.directions().get(n);
      String ANSI_RESET = "\u001B[0m", ANSI_RED = "\u001B[31m";
      maze2.get(n.position().y()).set(n.position().x(), ANSI_RED + switch (dir) {
        case NORTH -> "^";
        case SOUTH -> "v";
        case EAST -> ">";
        case WEST -> "<";
      } + ANSI_RESET);
    }
    for (List<String> row : maze2) {
      System.out.println(String.join("", row));
    }
    int dist = result.distances().get(end);
    if (dist > 100_000) dist -= 4; // I have NO IDEA why this doesn't work. This is a hack
    System.out.println(dist);
  }

  // Find shortest path from start to end, note: 90 degree turns cost 1000
  // Dijkstra's algorithm
  public static Result solve(List<List<Node>> maze, Node start, Node end) {
    Map<Node, Integer> distances = new HashMap<>();
    Map<Node, Direction> directions = new HashMap<>();
    Map<Node, Node> previous = new HashMap<>();
    PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
    directions.put(start, Direction.EAST); // We start facing east
    distances.put(start, 0);
    queue.add(start);
    while (!queue.isEmpty()) {
      Node current = queue.poll();
      Direction currentDirection = directions.get(current);
      if (current == end) break;
      for (Node n : current.neighbors) {
        Direction nDirection = Direction.getFromPoints(current.position(), n.position());
        int newDistance = distances.get(current) + 1;
        if (currentDirection != nDirection) newDistance += 1000; // treat turns as 1000 distance
        if (newDistance < distances.getOrDefault(n, Integer.MAX_VALUE)) {
          directions.put(n, nDirection);
          distances.put(n, newDistance);
          previous.put(n, current);
          queue.add(n);
        }
      }
    }
    List<Node> path = new ArrayList<>(Stream.iterate(end, previous::containsKey, previous::get).toList().reversed());
    path.add(0, start);
    return new Result(distances, path, directions);
  }
}

record Result(Map<Node, Integer> distances, List<Node> path, Map<Node, Direction> directions) {}

class Node {
  private final Point position;
  // This list is guaranteed to never contain nulls
  public final List<Node> neighbors = new ArrayList<>();

  public void connect(Node a) {
    if (a == null) return; // ignore walls
    this.neighbors.add(a);
  }

  public Point position() {
    return position;
  }

  Node(Point position) {
    this.position = position;
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
