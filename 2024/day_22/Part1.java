import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  static final int GENERATE_N = 2000;

  public static void main(String[] args) throws FileNotFoundException {
    List<Long> numbers = new ArrayList<>();
    try (Scanner scan = readInput(args)) {
      while (scan.hasNextLine())
        numbers.add(Long.parseLong(scan.nextLine()));
    }
    long sum = numbers.stream() // 
        .mapToLong(n -> Stream.iterate(n, Part1::nextSecret).limit(GENERATE_N + 1).skip(GENERATE_N).findFirst().get())
        .peek(System.out::println) //
        .sum();

    System.out.println(sum);
  }

  // Calculate the result of multiplying the secret number by 64. Then, mix this result into the secret number. Finally, prune the secret number.
  // Calculate the result of dividing the secret number by 32. Round the result down to the nearest integer. Then, mix this result into the secret number. Finally, prune the secret number.
  // Calculate the result of multiplying the secret number by 2048. Then, mix this result into the secret number. Finally, prune the secret number.
  public static long nextSecret(long n) {
    n = prune(mix(n, n * 64));
    n = prune(mix(n, n / 32));
    n = prune(mix(n, n * 2048));
    return n;
  }

  // To prune the secret number, calculate the value of the secret number modulo 16777216. 
  public static long prune(long n) {
    return n % 16777216;
  }

  // To mix a value into the secret number, calculate the bitwise XOR of the given value and the secret number. 
  public static long mix(long n, long m) {
    return n ^ m;
  }
}
