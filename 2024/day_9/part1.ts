// https://adventofcode.com/2024/day/1

import { readInput } from "../../utils/utils";

const input = await readInput();
const lines = input.split("\n").filter(Boolean);
const [inputLine] = lines;
if (!inputLine) throw new Error("No input");

const repeated = <T>(count: number, t: T): T[] =>
  Array.from({ length: count }, () => t);

let free = true; // Note: this is inverted *before* use
let id = 0;
type File = { free: false; id: number } | { free: true };
const files = inputLine
  .split("")
  .flatMap<File>(count =>
    repeated(+count, (free = !free) ? { free } : { free, id: id++ })
  );

for (let i = files.length - 1; i >= 0; i--) {
  const file = files[i]!;
  if (file.free) continue; // Skip free spaces at end
  //   // find the first free space before this one
  const freeIndex = files.findIndex(f => f.free);
  if (freeIndex === -1) throw new Error("No free space found");
  if (freeIndex > i) break; // there's no more free space to degrag into
  [files[i], files[freeIndex]] = [files[freeIndex], files[i]];
}

const checksum = files
  .map((file, i) => (file.free ? 0 : file.id * i))
  .reduce((a, b) => a + b);

console.log(checksum);
