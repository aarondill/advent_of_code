// https://adventofcode.com/2024/day/4

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Part2 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static void main(String[] args) throws FileNotFoundException {
    // Count number of times an X-MAS is found in the wordsearch
    // An X-mas is a cross of the letters MAS, meeting at the coinciding 'A' letters.
    List<List<Character>> letters = new ArrayList<>();
    try (Scanner scan = readInput(args)) {
      while (scan.hasNextLine()) {
        String line = scan.nextLine();
        if (line.isEmpty()) continue;
        List<Character> word = new ArrayList<>();
        for (char c : line.toCharArray())
          word.add(c);
        letters.add(word);
      }
    }

    int count = 0;
    char[] xmas = "MAS".toCharArray();
    // Note: i is y, j is x
    for (int i = 1; i < letters.size() - 1; i++) {
      for (int j = 1; j < letters.get(i).size() - 1; j++) {
        int i_copy = i, j_copy = j;
        // Only two of these conditions can be true at once (since if top-left-to-bottom-right is true, then bottom-right-to-top-left is always false)
        int conditions = 0;
        // top-left-to-bottom-right
        if (IntStream.range(0, xmas.length).allMatch(k -> letters.get(i_copy + k - 1).get(j_copy + k - 1) == xmas[k]))
          conditions++;
        // top-right-to-bottom-left
        if (IntStream.range(0, xmas.length).allMatch(
            k -> letters.get(i_copy + xmas.length - 1 - k - 1).get(j_copy + xmas.length - 1 - k - 1) == xmas[k]))
          conditions++;
        // bottom-left-to-top-right
        if (IntStream.range(0, xmas.length)
            .allMatch(k -> letters.get(i_copy + xmas.length - 1 - k - 1).get(j_copy + k - 1) == xmas[k]))
          conditions++;
        // bottom-right-to-top-left
        if (IntStream.range(0, xmas.length)
            .allMatch(k -> letters.get(i_copy + k - 1).get(j_copy + xmas.length - 1 - k - 1) == xmas[k]))
          conditions++;
        if (conditions > 1) count++;

      }
    }
    System.out.println(count);

  }
}
