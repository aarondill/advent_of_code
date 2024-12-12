import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static java.util.function.Predicate.not;

public class Part2 {
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
        files.add(free ? FileByte.free(size) : new FileByte(id++, size));
        // Every other section is free space, if the next section is occupied, it's id is incremented
        free = !free;
      }
    }
    // Move the files back to free spaces
    int minProcessedFile = files.stream().filter(not(FileByte::isFree)).mapToInt(FileByte::id).max().getAsInt();
    for (int i = files.size() - 1; i >= 0; i--) {
      FileByte file = files.get(i);
      if (file.isFree()) continue; // Skip free spaces at end
      { // We've already moved this file
        if (file.id() > minProcessedFile) continue;
        minProcessedFile = file.id();
      }

      // Find first free space >= this one *before* this one
      int freeIndex = IntStream.range(0, i).filter(j -> files.get(j).isFree())
          .filter(j -> files.get(j).size() >= file.size()).findFirst().orElse(-1);
      if (freeIndex == -1) continue; // no free space found
      if (files.get(freeIndex).size() == file.size()) {
        Collections.swap(files, i, freeIndex);
      } else {
        FileByte freeFile = files.get(freeIndex);
        // subtract the size of the file we're moving from the free space
        files.set(freeIndex, FileByte.free(freeFile.size() - file.size()));
        // The file is now free, so we can move it to the free space
        files.set(i, FileByte.free(file.size()));
        // Put the file before the (new) free space
        files.add(freeIndex, file);

      }
    }

    // Check sum is id*byte index for every byte
    long checksum = 0;
    int byteIndex = 0;
    for (int i = 0; i < files.size(); i++) {
      FileByte file = files.get(i);
      if (file.isFree()) {
        byteIndex += file.size();
        continue;
      }
      for (int j = 0; j < file.size(); j++) {
        checksum += byteIndex++ * file.id();
      }
    }
    System.out.println(checksum);
  }

}

record FileByte(int id, int size) {
  private static final int FREE = -1;

  public static FileByte free(int size) {
    return new FileByte(FREE, size);
  }

  public boolean isFree() {
    return id == FREE;
  }

  public String toString() {
    return (id == FREE ? "." : id + "").repeat(size);
  }
}
