// https://adventofcode.com/2024/day/4

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
    // Count number of times XMAS is found in the wordsearch
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
    char[] xmas = "XMAS".toCharArray();
    // Note: i is y, j is x
    for (int i = 0; i < letters.size(); i++) {
      for (int j = 0; j < letters.get(i).size(); j++) {
        int i_copy = i, j_copy = j;
        boolean horizontalInRange = xmas.length - 1 + j < letters.get(i).size();
        boolean verticalInRange = xmas.length - 1 + i < letters.size();
        if (horizontalInRange) {
          if (IntStream.range(0, xmas.length).allMatch(k -> letters.get(i_copy).get(j_copy + k) == xmas[k])) count++;
          if (IntStream.range(0, xmas.length)
              .allMatch(k -> letters.get(i_copy).get(j_copy + xmas.length - 1 - k) == xmas[k]))
            count++;
        }
        if (verticalInRange) {
          if (IntStream.range(0, xmas.length).allMatch(k -> letters.get(i_copy + k).get(j_copy) == xmas[k])) count++;
          if (IntStream.range(0, xmas.length)
              .allMatch(k -> letters.get(i_copy + xmas.length - 1 - k).get(j_copy) == xmas[k]))
            count++;
        }

        if (horizontalInRange && verticalInRange) {
          if (IntStream.range(0, xmas.length).allMatch(k -> letters.get(i_copy + k).get(j_copy + k) == xmas[k]))
            count++;
          if (IntStream.range(0, xmas.length).allMatch(
              k -> letters.get(i_copy + xmas.length - 1 - k).get(j_copy + xmas.length - 1 - k) == xmas[k]))
            count++;
          if (IntStream.range(0, xmas.length)
              .allMatch(k -> letters.get(i_copy + xmas.length - 1 - k).get(j_copy + k) == xmas[k]))
            count++;
          if (IntStream.range(0, xmas.length)
              .allMatch(k -> letters.get(i_copy + k).get(j_copy + xmas.length - 1 - k) == xmas[k]))
            count++;
        }

      }
    }
    System.out.println(count);

  }
}
