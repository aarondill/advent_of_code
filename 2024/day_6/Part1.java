import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
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
            start = new Point(x, y);
            c = '.';
          }
          row.add(c);
          x++;
        }
        y++;
      }
    }

    Point pos = start;
    Direction dir = Direction.UP;
    Set<Point> points = new HashSet<>();
    while (pos.x() >= 0 && pos.y() >= 0 && pos.x() < maze.size() && pos.y() < maze.get(0).size()) {
      points.add(pos);
      Point next = dir.delta(pos);
      if (next.x() < 0 || next.y() < 0 || next.x() >= maze.size() || next.y() >= maze.get(0).size()) break;
      if (maze.get(next.y()).get(next.x()) == '#') {
        dir = dir.rotate();
      } else pos = next;
    }
    System.out.println(points.size());

  }
}

record Point(int x, int y) {}

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
      case UP -> new Point(p.x(), p.y() - 1);
      case DOWN -> new Point(p.x(), p.y() + 1);
      case RIGHT -> new Point(p.x() + 1, p.y());
      case LEFT -> new Point(p.x() - 1, p.y());
    };
  }
}
