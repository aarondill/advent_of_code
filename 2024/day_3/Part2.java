import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Part2 {
  private static String readInput(String[] args) throws IOException {
    if (args.length == 0) return new String(System.in.readAllBytes());
    return new String(Files.readAllBytes(Paths.get(args[0])));
  }

  public static String substring(String s, int start, int end) {
    return s.substring(start, end > s.length() ? s.length() : end);
  }

  public static void main(String[] args) throws IOException {
    String input = readInput(args);
    int sum = 0;
    boolean shouldDo = true;
    for (int i = 0; i < input.length(); i++) {
      if (substring(input, i, i + "don't()".length()).equals("don't()")) {
        shouldDo = false;
        i += "don't()".length() - 1;
        continue;
      }
      if (substring(input, i, i + "do()".length()).equals("do()")) {
        shouldDo = true;
        i += "do()".length() - 1;
        continue;
      }
      if (substring(input, i, i + "mul(".length()).equals("mul(")) {
        int nextClose = input.indexOf(")", i + "mul(".length());
        String sub = substring(input, i + "mul(".length(), nextClose);
        String[] parts = sub.split(",");
        int a, b;
        try {
          a = Integer.parseInt(parts[0]);
          b = Integer.parseInt(parts[1]);
        } catch (Exception e) {
          i += "mul(".length() - 1;
          continue;
        }
        sum += shouldDo ? a * b : 0;
        i = nextClose;
      }
    }
    System.out.println(sum);

  }
}
