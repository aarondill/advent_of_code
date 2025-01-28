import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Part1 {
  private static Scanner readInput(String[] args) throws FileNotFoundException {
    if (args.length == 0) return new Scanner(System.in);
    return new Scanner(new File(args[0]));
  }

  public static void main(String[] args) throws FileNotFoundException {
    // We only need a list of wires, since wires hold and resolve gates.
    Map<String, Wire> wires = new HashMap<>();

    try (Scanner scan = readInput(args)) {
      String line;
      while (!(line = scan.nextLine()).isBlank()) { // x00: 1
        String[] parts = line.split(": ");
        wires.put(parts[0], new Wire(parts[0], parts[1].equals("1")));
      }
      while (scan.hasNextLine()) { // x00 AND y00 -> z00
        String[] parts = scan.nextLine().split(" ");
        String a = parts[0], b = parts[2];
        Operation op = Operation.valueOf(parts[1]);
        String destination = parts[4];

        // These may exist as static inputs, or as outputs from gates that haven't been seen yet.
        Wire a_wire = wires.computeIfAbsent(a, Wire::new), b_wire = wires.computeIfAbsent(b, Wire::new);
        Gate gate = new Gate(a_wire, b_wire, op);
        Wire destination_wire = wires.computeIfAbsent(destination, Wire::new); // this wire may exist already as an input from another gate
        destination_wire.setGate(gate);
      }
    }
    String n = wires.values().stream() //
        .filter(wire -> wire.name().startsWith("z")) // 
        .sorted(Comparator.comparing(Wire::name, Comparator.reverseOrder())) //
        .map(Wire::value) //
        .map(b -> b ? "1" : "0") //
        .collect(Collectors.joining(""));
    System.out.println(Long.parseLong(n, 2));
  }
}

// A wire is a named boolean value that can be set by the user or by a gate.
// If needsInput is true, the value comes from the gate. Otherwise it is the `value` field.
class Wire {
  private final String name;

  public String name() {
    return name;
  }

  private boolean needsInput;
  private Gate gate;
  private boolean value;

  public void setGate(Gate gate) {
    this.gate = gate;
    needsInput = true;
  }

  public boolean value() {
    if (needsInput) {
      if (gate == null) throw new IllegalStateException("No gate set for " + name);
      value = gate.apply();
      needsInput = false;
    }
    return value;

  }

  // If this is called, the wire *has* to have the gate set later!
  public Wire(String name) {
    this.name = name;
    this.needsInput = true;
  }

  public Wire(String name, boolean value) {
    this.name = name;
    this.value = value;
    this.needsInput = false;
  }

  public String toString() {
    return gate == null ? name : gate + " -> " + name;
  }
}

record Gate(Wire a, Wire b, Operation operation) {
  public boolean apply() {
    return operation.apply(a.value(), b.value());
  }

  public String toString() {
    return a.name() + " " + operation.name() + " " + b.name();
  }
}

enum Operation {
  AND, OR, XOR;

  public boolean apply(boolean a, boolean b) {
    return switch (this) {
      case AND -> a && b;
      case OR -> a || b;
      case XOR -> a ^ b;
    };
  }
}
