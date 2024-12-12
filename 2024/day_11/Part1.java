import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static Stream<Long> iter(Stream<Long> stones) {
    return stones.mapMulti((stone, submit) -> {
      if (stone == 0) submit.accept(1l);
      // If even number of digits, split into two halves
      else if ((Math.floor(Math.log10(stone)) + 1) % 2 == 0) {
        String s = Long.toString(stone);
        String[] parts = {s.substring(0, s.length() / 2), s.substring(s.length() / 2)};
        for (String part : parts) {
          submit.accept(Long.parseLong(part));
        }
      } else submit.accept(stone * 2024);
    });
  }

  public static void main(String[] args) throws FileNotFoundException {
    List<Long> stones = null;

    try (Scanner scan = readInput(args)) {
      while (scan.hasNextLine()) {
        String line = scan.nextLine();
        if (line.isEmpty()) continue;
        stones = Arrays.stream(line.split(" ")).map(Long::parseLong).toList();
      }
    }
    var stonesStream = stones.stream();
    for (int i = 0; i < 25; i++) {
      stonesStream = iter(stonesStream);
    }
    System.out.println(stonesStream.count());

  }
}
