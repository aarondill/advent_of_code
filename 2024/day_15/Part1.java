import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static void main(String[] args) throws FileNotFoundException {
    List<List<Space>> grid = new ArrayList<>(); // Index [y][x]
    List<Point> directions = new ArrayList<>();
    Point robot = null;
    try (Scanner scan = readInput(args)) {
      String line;
      for (int y = 0; (line = scan.nextLine()).trim().length() > 0; y++) {
        List<Space> row = new ArrayList<>();
        for (int x = 0; x < line.length(); x++) {
          if (line.charAt(x) == '@') {
            robot = new Point(x, y);
            row.add(Space.EMPTY);
          } else row.add(Space.fromChar(line.charAt(x)));
        }
        grid.add(row);
      }

      while (scan.hasNextLine())
        scan.nextLine().trim().chars().mapToObj(c -> (char) c).map(Point::fromChar).forEach(directions::add);
    }

    // Do movements
    for (Point dir : directions) {
      Point next = robot.add(dir);
      switch (grid.get(next.y()).get(next.x())) {
        case BOX:
          if (!moveBox(grid, robot, next, dir)) continue;
          // Fall through! the next space is now empty
        case EMPTY:
          System.out.println("Moving robot " + robot + " in direction " + dir + " to " + next);
          robot = next;
        case WALL:
          continue;
      }
    }
    // Print the grid
    for (int y = 0; y < grid.size(); y++) {
      for (int x = 0; x < grid.get(y).size(); x++) {
        if (robot.equals(new Point(x, y))) System.out.print("@");
        else System.out.print(grid.get(y).get(x));
      }
      System.out.println();
    }

    List<Point> boxes = new ArrayList<>();
    for (int y = 0; y < grid.size(); y++) {
      for (int x = 0; x < grid.get(y).size(); x++) {
        if (grid.get(y).get(x) == Space.BOX) boxes.add(new Point(x, y));
      }
    }
    // The GPS coordinate of a box is equal to 100 times its distance from the
    // top edge of the map plus its distance from the left edge of the map
    System.out.println(boxes.stream().mapToInt(box -> 100 * box.y() + box.x()).sum());
  }

  private static boolean moveBox(List<List<Space>> grid, Point robot, Point box, Point dir) {
    // check next space
    Point next = box.add(dir);
    switch (grid.get(next.y()).get(next.x())) {
      case WALL:
        return false;
      case BOX:
        if (!moveBox(grid, robot, next, dir)) return false;
        // Fall through! the next space is now empty
      case EMPTY:
        grid.get(box.y()).set(box.x(), Space.EMPTY);
        grid.get(next.y()).set(next.x(), Space.BOX);
        return true;
      default:
        throw new IllegalStateException("Unexpected space: " + grid.get(next.y()).get(next.x()));
    }
  }
}

enum Space {
  EMPTY, WALL, BOX;

  static Space fromChar(char c) {
    return switch (c) {
      case '#' -> WALL;
      case '.' -> EMPTY;
      case 'O' -> BOX;
      default -> throw new IllegalArgumentException("Invalid char: " + c);
    };
  }

  @Override
  public String toString() {
    return switch (this) {
      case EMPTY -> ".";
      case WALL -> "#";
      case BOX -> "O";
    };
  }
}

record Point(int x, int y) {
  public Point add(Point other) {
    return new Point(x + other.x, y + other.y);
  }

  static Point fromChar(char c) {
    return switch (c) {
      case '>' -> new Point(1, 0);
      case '<' -> new Point(-1, 0);
      case 'v' -> new Point(0, 1);
      case '^' -> new Point(0, -1);
      default -> throw new IllegalArgumentException("Invalid char: " + c);
    };
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
