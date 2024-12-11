import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static boolean eval(List<Long> nums, long target) {
    return eval(nums, target, 0);
  }

  public static boolean eval(List<Long> nums, long target, long current) {
    // we're out of numbers, if we've reached the target, we're done
    if (nums.size() == 0) return current == target;
    long op = nums.get(0);
    List<Long> rest = nums.subList(1, nums.size());

    if (eval(rest, target, current + op)) return true;
    if (eval(rest, target, current * op)) return true;
    return false;
  }

  public static void main(String[] args) throws FileNotFoundException {
    List<List<Long>> equations = new ArrayList<>();
    try (Scanner scan = readInput(args)) {
      while (scan.hasNextLine()) {
        String line = scan.nextLine();
        if (line.isEmpty()) continue;
        String[] parts = line.replaceFirst(":", "").split("\\s+");
        equations.add(Arrays.stream(parts).map(Long::parseLong).collect(Collectors.toCollection(ArrayList::new)));
      }
    }

    long res = equations.stream().filter(nums -> eval(nums.subList(1, nums.size()), nums.get(0)))
        .mapToLong(List::getFirst).sum();
    System.out.println(res);

  }
}
