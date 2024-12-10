import { readInput } from "../../utils/utils";

const input = await readInput();
const [orderingLines, updateLines] = input
  .trim()
  .split("\n\n")
  .map(s => s.split("\n"));
if (!orderingLines || !updateLines) throw new Error("Invalid input");

const orderings: { [key: number]: number[] } = orderingLines
  .map(s => s.split("|").map(Number))
  .reduce(
    (acc, [num, before]) => ((acc[num!] ??= []).push(before!), acc),
    {} as Record<number, number[]>
  );
const updates = updateLines.map(s => s.split(",").map(Number));
const not =
  <Args extends unknown[]>(f: (...a: Args) => boolean) =>
  (...a: Args) =>
    !f(...a);

const res = updates
  .filter(
    not(u => {
      const indices = u.reduce(
        (acc, n, i) => ((acc[n] = i), acc),
        {} as { [key: number]: number }
      );
      for (let i = 0; i < u.length; i++) {
        const n = u[i]!;
        // This number isn't in the order, so we don't care about it.
        if (!orderings[n]) continue;
        // The number it needs to be before
        const befores = orderings[n];
        for (const before of befores) {
          // The other number isn't in the list
          if (indices[before] === undefined) continue;
          // The index of the number it needs to be before
          const index = indices[before];
          // If we're above the index, we're in the wrong place
          if (i > index) return false;
        }
      }
      return true;
    })
  )
  .map(u =>
    u.sort((a, b) => {
      if (orderings[a] === undefined && orderings[b] === undefined) return 0;
      if ((orderings[a] ?? []).includes(b)) return -1;
      if ((orderings[b] ?? []).includes(a)) return 1;
      return 0;
    })
  )
  .map(u => u[Math.floor(u.length / 2)])
  .filter(n => n !== undefined)
  .reduce((a, b) => a + b);
console.log(res);
