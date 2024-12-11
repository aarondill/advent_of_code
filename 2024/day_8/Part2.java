import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static boolean isAntinode(int x, int y, Map<Character, List<Point>> antennae) {
    // System.out.println("Testing " + x + "," + y);
    for (List<Point> points : antennae.values()) {
      if (points.contains(new Point(x, y))) return true;
      // the angle to two points is the same , we are in the antennae
      for (int i = 0; i < points.size(); i++) {
        for (int j = i + 1; j < points.size(); j++) {
          // System.out.printf("Testing(%d,%d): %s, %s\n", i, j, points.get(i), points.get(j));
          Point p1 = points.get(i), p2 = points.get(j);

          // Check both in same direction
          double angleToP1 = Math.atan2(y - p1.y(), x - p1.x());
          double angleToP2 = Math.atan2(y - p2.y(), x - p2.x());
          // System.out.printf("Angle: %f, %f\n", angleToP1, angleToP2);
          if (angleToP1 != angleToP2) continue;
          return true;
        }
      }
    }
    return false;
  }

  public static void main(String[] args) throws FileNotFoundException {
    // Grid index is [y][x]
    List<List<Character>> grid = new ArrayList<>();

    try (Scanner scan = readInput(args)) {
      while (scan.hasNextLine()) {
        String line = scan.nextLine();
        if (line.isEmpty()) continue;
        line = line.replaceAll("#", "."); // This is an artifact of the input format
        grid.add(line.chars().mapToObj(x -> (char) x).toList());
      }
    }
    // Cache the points of the antennae
    Map<Character, List<Point>> antennae = new HashMap<>();
    for (int y = 0; y < grid.size(); y++) {
      for (int x = 0; x < grid.get(y).size(); x++) {
        char c = grid.get(y).get(x);
        if (c == '.') continue;
        antennae.computeIfAbsent(c, k -> new ArrayList<>()).add(new Point(x, y));
      }
    }

    long count = IntStream.range(0, grid.size()).boxed().flatMap(y -> IntStream.range(0, grid.get(y).size())
        .filter(x -> isAntinode(x, y, antennae)).mapToObj(x -> new Point(x, y))).distinct().count();
    System.out.println(count);

  }
}

record Point(int x, int y) {}
