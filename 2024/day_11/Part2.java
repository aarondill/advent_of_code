import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static Stream<Long> transform(long stone) {
    if (stone == 0) return Stream.of(1l);
    // If even number of digits, split into two halves
    else if ((Math.floor(Math.log10(stone)) + 1) % 2 == 0) {
      String s = Long.toString(stone);
      String[] parts = {s.substring(0, s.length() / 2), s.substring(s.length() / 2)};
      return Arrays.stream(parts).map(Long::parseLong);
    } else return Stream.of(stone * 2024);
  }

  // map of [stone, blink times] -> number of blinks
  public static Map<CacheKey, Long> cache = new HashMap<>();

  public static long getStonesCount(Stream<Long> stones, int blinkTimes) {
    if (blinkTimes == 0) return stones.count();
    return stones.mapToLong(stone -> {
      CacheKey key = new CacheKey(stone, blinkTimes);
      if (cache.containsKey(key)) return cache.get(key);
      Stream<Long> next = transform(stone);
      long result = getStonesCount(next, blinkTimes - 1);
      cache.put(key, result);
      return result;
    }).sum();
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
    System.out.println(getStonesCount(stones.stream(), 75));

  }
}

record CacheKey(long stone, int blinkTimes) {}
