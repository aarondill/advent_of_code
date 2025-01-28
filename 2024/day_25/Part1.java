import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static void main(String[] args) throws FileNotFoundException {
    List<Key> keys = new ArrayList<>();
    List<Lock> locks = new ArrayList<>();
    int schematicHeight = 0;
    try (Scanner scan = readInput(args)) {
      while (scan.hasNextLine()) {
        List<String> lines = new ArrayList<>();
        String line;
        while (scan.hasNextLine() && !(line = scan.nextLine()).isBlank())
          lines.add(line);

        List<Integer> heights = new ArrayList<>();
        // Count the number of # in each column
        for (int x = 0; x < lines.getFirst().length(); x++) {
          int xInner = x;
          int height = (int) IntStream.range(0, lines.size()).mapToObj(i -> lines.get(i).charAt(xInner))
              .filter(c -> c == '#').count();
          heights.add(height);
        }

        schematicHeight = lines.size();
        if (lines.get(0).charAt(0) == '#') { // the top is a #, so this is a lock
          locks.add(new Lock(heights));
        } else { // the bottom is a #, so this is a key
          keys.add(new Key(heights));
        }
      }
    }
    System.out.println(keys);
    System.out.println(locks);
    int finalSchematicHeight = schematicHeight;
    long combos = locks.stream().mapToLong(l -> {
      return keys.stream().filter(k -> {
        boolean ok = IntStream.range(0, l.heights().size())
            .allMatch(i -> l.heights().get(i) + k.heights().get(i) <= finalSchematicHeight);
        // if (ok)
        System.out.println("Lock " + l.heights() + " and key " + k.heights() + " are ok: " + ok);
        return ok;
      }).count();
    }).sum();
    System.out.println(combos);
  }
}

record Key(List<Integer> heights) {}

record Lock(List<Integer> heights) {}
