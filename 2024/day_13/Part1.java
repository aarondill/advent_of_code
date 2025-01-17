import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  private static Point parsePoint(String s) {
    Pattern matcher = Pattern.compile("X[+=](\\d+), Y[+=](\\d+)");
    Matcher match = matcher.matcher(s);
    if (!match.find()) throw new IllegalArgumentException("Invalid button format: " + s);
    return new Point(Integer.parseInt(match.group(1)), Integer.parseInt(match.group(2)));
  }

  private static final int BUTTON_A_COST = 3;
  private static final int BUTTON_B_COST = 1;

  public static void main(String[] args) throws FileNotFoundException {
    List<Machine> machines = new ArrayList<>();
    try (Scanner scan = readInput(args)) {
      while (scan.hasNextLine()) {
        String buttonA = scan.nextLine(); // Button A: X+94, Y+34
        String buttonB = scan.nextLine(); // Button B: X+22, Y+67
        String prize = scan.nextLine(); // Prize: X=8400, Y=5400
        if (scan.hasNextLine()) scan.nextLine(); // empty line

        machines.add(new Machine(new Button(parsePoint(buttonA), BUTTON_A_COST),
            new Button(parsePoint(buttonB), BUTTON_B_COST), parsePoint(prize)));
      }
    }
    System.out.println(machines.stream().peek(System.out::println).mapToInt(Part1::cost)
        .filter(x -> x != Integer.MAX_VALUE).peek(System.out::println).sum());
  }

  public static int cost(Machine m) {
    Map<Point, Integer> cache = new HashMap<>();
    return cost(m, new Point(0, 0), 0, cache);
  }

  // Recusively try to find the shortest path to the prize
  // Try A, then B. If either X or Y is > machine.prizeLocation, then we can't reach the prize
  // if we've reached the prize, then we can return the cost
  // return Integer.MAX_VALUE if we can't reach the prize (for convenience with Math.min)
  public static int cost(Machine m, Point state, int cost, Map<Point, Integer> cache) {
    if (cache.containsKey(state)) return cache.get(state);
    if (state.equals(m.prizeLocation())) return cost;
    if (state.x() > m.prizeLocation().x() || state.y() > m.prizeLocation().y()) return Integer.MAX_VALUE;
    int aCost = cost(m, state.add(m.a().movement()), cost + m.a().cost(), cache);
    int bCost = cost(m, state.add(m.b().movement()), cost + m.b().cost(), cache);
    int res = Math.min(aCost, bCost);
    cache.put(state, res);
    return res;
  }

}

record Button(Point movement, int cost) {}

record Machine(Button a, Button b, Point prizeLocation) {}

record Point(int x, int y) {
  public Point add(Point other) {
    return new Point(x + other.x, y + other.y);
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
