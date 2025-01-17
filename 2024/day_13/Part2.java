import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Note that this can solve part 1 as well, just remove the add(new
// Point(10000000000000l, 10000000000000l)) from the method.
public class Part2 {
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

        machines.add(
            new Machine(new Button(parsePoint(buttonA), BUTTON_A_COST), new Button(parsePoint(buttonB), BUTTON_B_COST),
                parsePoint(prize).add(new Point(10000000000000l, 10000000000000l))));
      }
    }
    System.out.println(machines.stream().mapToLong(Part2::cost).filter(x -> x >= 0).sum());
  }

  public static long cost(Machine m) {
    Point a = m.a().movement(), b = m.b().movement(), p = m.prizeLocation();
    // xAₓ + yBₓ = Pₓ
    // xAᵧ - xPᵧ = Pᵧ
    double bxby = (double) b.x() / b.y();
    long x = Math.round(-(bxby * p.y() - p.x()) / (a.x() - bxby * a.y()));
    long y = (p.x() - x * a.x()) / b.x();
    Point loc = new Point(0, 0).add(a.multiply(x)).add(b.multiply(y));
    if (!loc.equals(p)) return -1;
    return m.a().cost() * x + m.b().cost() * y;
  }

}

record Button(Point movement, int cost) {}

record Machine(Button a, Button b, Point prizeLocation) {}

record Point(long x, long y) {
  public Point add(Point other) {
    return new Point(x + other.x, y + other.y);
  }

  public Point multiply(long n) {
    return new Point(x * n, y * n);
  }

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
