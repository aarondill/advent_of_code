import static java.util.function.Predicate.not;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  private static boolean isSafe(List<Integer> levels) {
    boolean ascending = levels.get(0) < levels.get(1);
    for (int i = 1; i < levels.size(); i++) {
      if (ascending && levels.get(i) < levels.get(i - 1)) return false;
      else if (!ascending && levels.get(i) > levels.get(i - 1)) return false;
    }

    for (int i = 1; i < levels.size(); i++) {
      int diff = Math.abs(levels.get(i) - levels.get(i - 1));
      if (diff < 1 || diff > 3) return false;
    }
    return true;
  }

  public static void main(String[] args) throws FileNotFoundException {
    System.out.println(readInput(args).useDelimiter("\n").tokens().filter(not(String::isBlank)).filter(line -> {
      List<Integer> levels =
          Arrays.stream(line.split("\\s+")).map(Integer::parseInt).collect(Collectors.toCollection(LinkedList::new));
      if (isSafe(levels)) return true;
      for (int i = 0; i < levels.size(); i++) {
        int l = levels.remove(i);
        if (isSafe(levels)) return true;
        levels.add(i, l);
      }
      return false;
    }).count());
  }

}
