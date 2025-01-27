import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  static int A = 0, B = 0, C = 0;
  static List<Integer> programList = new ArrayList<>();
  static int programPointer = 0;
  static List<Integer> output = new ArrayList<>();

  public static void main(String[] args) throws FileNotFoundException {
    try (Scanner scan = readInput(args)) {
      while (scan.hasNextLine()) {
        String Astr = scan.nextLine().split(": ")[1]; // Register A: 729
        String Bstr = scan.nextLine().split(": ")[1]; // Register B: 0
        String Cstr = scan.nextLine().split(": ")[1]; // Register C: 0
        A = Integer.parseInt(Astr);
        B = Integer.parseInt(Bstr);
        C = Integer.parseInt(Cstr);
        scan.nextLine(); // empty line
        String[] program = scan.nextLine().split(": ")[1].split(","); // Program: 0,1,5,4,3,0

        for (int i = 0; i < program.length; i++) {
          int op = Integer.parseInt(program[i]);
          programList.add(op);
        }
      }
    }
    while (programPointer + 1 < programList.size()) {
      Instruction instruction = new Instruction(programList.get(programPointer), programList.get(programPointer + 1));
      int prevPtr = programPointer;
      instruction.execute();
      // Don't increment if we jumped
      if (prevPtr == programPointer) programPointer += 2;
    }
    System.out.println(output.stream().map(Object::toString).collect(Collectors.joining(",")));
  }

  static record Instruction(int op, int operand) {
    // Gets the value of the combo operand
    int combo() {
      return switch (operand) {
        case 0, 1, 2, 3 -> operand;
        case 4 -> A;
        case 5 -> B;
        case 6 -> C;
        case 7 -> throw new IllegalArgumentException("7 is reserved");
        default -> throw new IllegalArgumentException("Invalid operand");
      };
    }

    int dv() {
      int numerator = A;
      double denominator = Math.pow(2, combo());
      System.out.println("dv " + numerator + "/" + denominator + " = " + (int) (numerator / denominator));
      return (int) (numerator / denominator);
    }

    void execute() {
      switch (op) {
        case 0 -> { // adv
          System.out.print('a');
          A = dv();
        }
        case 1 -> { // bxl
          System.out.println("bxl: " + (B ^ operand));
          B ^= operand;
        }
        case 2 -> { // bst
          System.out.println("bst: " + (combo() % 8));
          B = combo() % 8;
        }
        case 3 -> { // jnz
          if (A == 0) return;
          System.out.println("jump to " + operand);
          programPointer = operand;
        }
        case 4 -> { // bxc -- note: ignores operand
          System.out.println("bxc: " + (B ^ C));
          B ^= C;
        }
        case 5 -> { // out
          System.out.println("output: " + combo() % 8);
          output.add(combo() % 8);
        }
        case 6 -> { // bdv
          System.out.print('b');
          B = dv();
        }
        case 7 -> { // cdv
          System.out.print('c');
          C = dv();
        }
        default -> throw new IllegalArgumentException("Unknown operation" + op);
      }
    }
  }
}
