// https://adventofcode.com/2024/day/1

import { readInput } from "../../utils/utils";

const input = await readInput();
const lines = input.split("\n").filter(Boolean);
const [inputLine] = lines;
if (!inputLine) throw new Error("No input");

let free = true; // Note: this is inverted *before* use
let id = 0;
type File = { size: number } & ({ free: false; id: number } | { free: true });
const files = inputLine
  .split("")
  .map(Number)
  .map<File>(size =>
    (free = !free) ? { free, size } : { free, id: id++, size }
  );

let minProcessedFile = files
  .filter(f => !f.free)
  .map(f => f.id)
  .reduce((a, b) => Math.max(a, b));
for (let i = files.length - 1; i >= 0; i--) {
  const file = files[i]!;
  if (file.free) continue; // Skip free spaces at end
  if (file.id > minProcessedFile) continue; // we've already moved this file
  minProcessedFile = file.id;

  // find the first free space before this one
  const freeIndex = files.findIndex(f => f.free && f.size >= file.size);
  if (freeIndex === -1 || freeIndex > i) continue; // there's no more free space to degrag into
  if (files[freeIndex].size === file.size) {
    // Swap the files if they're the same size
    [files[i], files[freeIndex]] = [files[freeIndex], files[i]];
  } else {
    // Subtract the size of the file we're moving from the free space
    files[freeIndex] = { free: true, size: files[freeIndex].size - file.size };
    // The file is now free, so we can move it to the free space
    files[i] = { free: true, size: file.size };
    // Put the file before the (new) free space
    files.splice(freeIndex, 0, file);
  }
}

let byteIndex = 0;
const checksum = files
  .flatMap(file => {
    if (file.free) {
      byteIndex += file.size;
      return 0;
    }
    return Array.from({ length: file.size }).map(() => byteIndex++ * file.id);
  })
  .reduce((a, b) => a + b);

console.log(checksum);
