import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  static final Point GRID_SIZE = new Point(101, 103);
  static final int STEPS = 100;

  public static void debug(List<Robot> robots) {
    Map<Point, Long> counts = robots.stream().collect(Collectors.groupingBy(Robot::position, Collectors.counting()));

    for (int y = 0; y < GRID_SIZE.y(); y++) {
      for (int x = 0; x < GRID_SIZE.x(); x++) {
        Point p = new Point(x, y);
        Long count = counts.get(p);
        String s = count == null ? "." : count.toString();
        System.out.print(s);
      }
      System.out.println();
    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    List<Robot> robots = new ArrayList<>();
    try (Scanner scan = readInput(args)) {
      Pattern pattern = Pattern.compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");
      while (scan.hasNextLine()) {
        String line = scan.nextLine(); // p=0,4 v=3,-3
        Matcher matcher = pattern.matcher(line);
        if (!matcher.matches()) throw new IllegalArgumentException("Invalid input: " + line);
        Point p = new Point(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))),
            v = new Point(Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
        robots.add(new Robot(p, v));
      }
    }

    var end = GRID_SIZE.x() * GRID_SIZE.y();
    for (int i = 0; i < end; i++, robots = robots.stream().map(r -> r.move(1, GRID_SIZE)).toList()) {
      Map<Point, Long> counts = robots.stream().collect(Collectors.groupingBy(Robot::position, Collectors.counting()));
      if (counts.values().stream().anyMatch(c -> c > 1)) continue;
      System.out.printf("%d/%d:\n", i, end);
      debug(robots);
      System.out.println();
    }

  }
}

// Represents a point or vector in 2D space.
record Point(int x, int y) {
  public Point add(Point other) {
    return new Point(x + other.x, y + other.y);
  }

  public Point multiply(int scalar) {
    return new Point(x * scalar, y * scalar);
  }

  // Returns the 8 points surrounding this point
  public List<Point> neighbors() {
    return IntStream.range(-1, 1).boxed().flatMap(x -> IntStream.range(-1, 1).mapToObj(y -> new Point(x, y))).toList();
  }

  // Handle wrapping around the grid
  public Point wrap(Point bounds) {
    // This handles negative numbers by adding until it is positive
    int newX = (x + (bounds.x * (-x / bounds.x + 1))) % bounds.x;
    int newY = (y + (bounds.y * (-y / bounds.y + 1))) % bounds.y;
    Point newPoint = new Point(newX, newY);
    if (newPoint.x < 0 || newPoint.y < 0 || newPoint.x >= bounds.x || newPoint.y >= bounds.y) {
      throw new AssertionError("Point is outside of bounds: " + newPoint + " with bounds " + bounds);
    }
    return newPoint;
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }
}

record Robot(Point position, Point velocity) {
  public Robot move(int steps, Point gridSize) {
    return new Robot(position.add(velocity.multiply(steps)).wrap(gridSize), velocity);
  }
}
