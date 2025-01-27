import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static void main(String[] args) throws FileNotFoundException {
    List<String> availablePatterns = new ArrayList<>();
    List<String> desiredPatterns = new ArrayList<>();

    try (Scanner scan = readInput(args)) {
      availablePatterns.addAll(Arrays.asList(scan.nextLine().split(", "))); // r, wr, b, g, bwu, rb, gb, br
      scan.nextLine(); // empty line
      while (scan.hasNextLine()) {
        String pattern = scan.nextLine(); // brwrr
        desiredPatterns.add(pattern);
      }
    }

    System.out.println(desiredPatterns.stream().mapToLong(p -> isPossible(p, availablePatterns)).sum());
  }

  private static long isPossible(String design, List<String> patterns) {
    var lcm = new HashMap<Integer, Long>(Map.of(0, 1l));
    for (int i = 0; i < design.length(); i++) {
      for (String pattern : patterns) {
        int end = i + pattern.length();
        if (end > design.length()) continue;
        if (!design.startsWith(pattern, i)) continue;
        lcm.merge(end, lcm.getOrDefault(i, 0l), Long::sum);
      }
    }
    return lcm.get(design.length());
  }
}
