import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import static java.util.function.Predicate.not;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static void main(String[] args) throws FileNotFoundException {
    // An ordering of the number and which number(s) it must be before
    Map<Integer, List<Integer>> ordering = new HashMap<>();

    List<List<Integer>> updates = new ArrayList<>();
    try (Scanner scan = readInput(args)) {
      String line = scan.nextLine();
      do {
        int[] parts = Arrays.stream(line.split("\\|")).mapToInt(Integer::parseInt).toArray();
        ordering.computeIfAbsent(parts[0], n -> new ArrayList<>()).add(parts[1]);
      } while (!(line = scan.nextLine()).isEmpty());

      while (scan.hasNextLine()) {
        updates.add(Arrays.stream(scan.nextLine().split(",")).map(Integer::parseInt).toList());
      }
    }
    int res = updates.stream().filter(not(u -> {
      Map<Integer, Integer> indices = new HashMap<>();
      for (int i = 0; i < u.size(); i++)
        if (indices.put(u.get(i), i) != null) throw new IllegalStateException("Duplicate number: " + u.get(i));
      for (int i = 0; i < u.size(); i++) {
        int n = u.get(i);
        // This number isn't in the order, so we don't care about it.
        if (!ordering.containsKey(n)) continue;
        // The number it needs to be before
        List<Integer> befores = ordering.get(n);
        for (int before : befores) {
          // The other number isn't in the list
          if (!indices.containsKey(before)) continue;
          // The index of the number it needs to be before
          int index = indices.get(before);
          // If we're above the index, we're in the wrong place
          if (i > index) return false;
        }
      }
      return true;
    })).map(u -> u.stream().sorted((a, b) -> {
      // No ordering, we don't care the order
      if (!ordering.containsKey(a) && !ordering.containsKey(b)) return 0;
      // A has an ordering, which includes b, so a must be before b
      if (ordering.getOrDefault(a, List.of()).contains(b)) return -1;
      // B has an ordering, which includes a, so a must be after b
      if (ordering.getOrDefault(b, List.of()).contains(a)) return 1;
      // neither order contains the other, so they're equal
      return 0;
    }).toList()).mapToInt(u -> u.get(u.size() / 2)).sum();
    System.out.println(res);

  }
}
