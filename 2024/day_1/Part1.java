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

    first.sort(Integer::compareTo);
    second.sort(Integer::compareTo);
    int sum = 0;
    for (int i = 0; i < first.size(); i++)
      sum += Math.abs(first.get(i) - second.get(i));
    System.out.println(sum);

  }
}
