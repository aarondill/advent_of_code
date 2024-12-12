import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static void main(String[] args) throws FileNotFoundException {
    int[] input = null;
    try (Scanner scan = readInput(args)) {
      while (scan.hasNextLine()) {
        String line = scan.nextLine();
        if (line.isEmpty()) continue;
        input = line.chars().map(c -> c - '0').toArray();
      }
    }
    List<FileByte> files = new ArrayList<>();
    {
      boolean free = false;
      int id = 0;
      for (int size : input) {
        for (int i = 0; i < size; i++)
          files.add(free ? FileByte.FREE : new FileByte(id));
        // Every other section is free space, if the next section is occupied, it's id is incremented
        free = !free;
        if (!free) id++;
      }
    }
    // Move the files back to free spaces
    for (int i = files.size() - 1; i >= 0; i--) {
      FileByte file = files.get(i);
      if (file == FileByte.FREE) continue; // Skip free spaces at end
      // find the first free space before this one
      int freeIndex = files.indexOf(FileByte.FREE);
      if (freeIndex == -1) throw new IllegalStateException("No free space found");
      if (freeIndex == i) throw new IllegalStateException("Found free space at same index: " + i);
      if (freeIndex > i) break; // there's no more free space to degrag into
      Collections.swap(files, i, freeIndex);
    }

    long checksum = 0;
    for (int i = 0; i < files.size(); i++) {
      FileByte file = files.get(i);
      if (file == FileByte.FREE) continue;
      checksum += file.id() * i;
    }
    System.out.println(checksum);
  }

}

record FileByte(int id) {
  static final FileByte FREE = new FileByte(-1);
}
