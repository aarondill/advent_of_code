import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Part1 {
  private static String readInput(String[] args) throws IOException {
    if (args.length == 0) return new String(System.in.readAllBytes());
    return new String(Files.readAllBytes(Paths.get(args[0])));
  }

  public final static int MULT_LEN = "mul(".length();

  public static void main(String[] args) throws IOException {
    String input = readInput(args);
    int sum = 0;
    for (int i = 0; i < input.length() - MULT_LEN; i++) {
      if (input.substring(i, i + MULT_LEN).equals("mul(")) {
        int nextClose = input.indexOf(")", i + MULT_LEN);
        String sub = input.substring(i + MULT_LEN, nextClose);
        String[] parts = sub.split(",");
        int a, b;
        try {
          a = Integer.parseInt(parts[0]);
          b = Integer.parseInt(parts[1]);
        } catch (Exception e) {
          i += MULT_LEN - 1;
          continue;
        }
        sum += a * b;
        i = nextClose;
      }
    }
    System.out.println(sum);

  }
}
