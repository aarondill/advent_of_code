// This is not complete.
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static void print(List<List<Character>> maze, Point p) {
    System.out.println(p.x() + "," + p.y() + ": ");
    for (int y = 0; y < maze.size(); y++) {
      for (int x = 0; x < maze.get(0).size(); x++) {
        if (p.x() == x && p.y() == y) {
          switch (p.dir()) {
            case UP -> System.out.print('^');
            case DOWN -> System.out.print('v');
            case LEFT -> System.out.print('<');
            case RIGHT -> System.out.print('>');
          }
          continue;
        }
        System.out.print(maze.get(y).get(x));
      }
      System.out.println();
    }
    System.out.println();
  }

  public static int follow(List<List<Character>> maze, Point pos, Set<Point> points, boolean hasAdded) {
    // Copy the maze
    maze = new ArrayList<>(maze.stream().map(row -> new ArrayList<>(row)).toList());
    int count = 0;
    while (pos.x() >= 0 && pos.y() >= 0 && pos.x() < maze.size() && pos.y() < maze.get(0).size()) {
      if (points.contains(pos)) return 1;
      System.out.println("Visiting " + pos + " points: " + points.size());
      points.add(pos);
      // If we rotate here, there's a loop
      Point next = pos.delta();
      if (next.x() < 0 || next.y() < 0 || next.x() >= maze.size() || next.y() >= maze.get(0).size()) break;
      if (!hasAdded) {
        char orig = maze.get(next.y()).set(next.x(), '#');
        int tmp = follow(maze, pos.rotate(), new HashSet<>(points), true);
        maze.get(next.y()).set(next.x(), orig);
        if (tmp > 0) count += tmp;
      }
      if (maze.get(next.y()).get(next.x()) == '#') pos = pos.rotate();
      else pos = next;
    }
    return count;
  }

  public static void main(String[] args) throws FileNotFoundException {
    // NOTE: indexing is [y][x]
    List<List<Character>> maze = new ArrayList<>();
    Point start = null;
    try (Scanner scan = readInput(args)) {
      int y = 0;
      while (scan.hasNextLine()) {
        String line = scan.nextLine();
        if (line.isEmpty()) continue;
        List<Character> row = new ArrayList<>();
        maze.add(row);
        int x = 0;
        for (char c : line.toCharArray()) {
          if (c == '^') {
            start = new Point(x, y, Direction.UP);
            c = '.';
          }
          row.add(c);
          x++;
        }
        y++;
      }
    }

    Point pos = start;
    Set<Point> points = new HashSet<>();
    int count = follow(Collections.unmodifiableList(maze), pos, points, false);
    System.out.println(count);
  }
}

record Point(int x, int y, Direction dir) {
  public Point delta() {
    return dir.delta(this);
  }

  public Point rotate() {
    return new Point(x, y, dir.rotate());
  }
}

enum Direction {
  UP, DOWN, LEFT, RIGHT;

  // Rotates the direction 90 degrees clockwise
  public Direction rotate() {
    return switch (this) {
      case UP -> RIGHT;
      case RIGHT -> DOWN;
      case DOWN -> LEFT;
      case LEFT -> UP;
    };
  }

  public Point delta(Point p) {
    return switch (this) {
      case UP -> new Point(p.x(), p.y() - 1, this);
      case DOWN -> new Point(p.x(), p.y() + 1, this);
      case RIGHT -> new Point(p.x() + 1, p.y(), this);
      case LEFT -> new Point(p.x() - 1, p.y(), this);
    };
  }
}
