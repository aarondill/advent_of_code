import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static void main(String[] args) throws FileNotFoundException {
    List<Integer> first = new ArrayList<>(), second = new ArrayList<>();

    try (Scanner scan = readInput(args)) {
      while (scan.hasNextLine()) {
        String line = scan.nextLine();
        if (line.isEmpty()) continue;
        String[] parts = line.split("\\s+");
        first.add(Integer.parseInt(parts[0]));
        second.add(Integer.parseInt(parts[1]));
      }
    }

    var counts = second.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    var similarity = first.stream().map(n -> n * counts.getOrDefault(n, 0l)).reduce(0l, Long::sum);
    System.out.println(similarity);

  }
}
