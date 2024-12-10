// https://adventofcode.com/2024/day/1

import { readInput } from "../../utils/utils";

const input = await readInput();
const lines = input.split("\n").filter(Boolean);
const [first, second] = lines
  .map(line => line.split(/\s+/).map(Number))
  .reduce((acc: number[][], line) => {
    // Transpose the arrays
    for (let i = 0; i < line.length; i++) (acc[i] ??= []).push(line[i]);
    return acc;
  }, []);
first.sort((a, b) => a - b);
second.sort((a, b) => a - b);

const counts = {};
for (const n of second) counts[n] = (counts[n] ?? 0) + 1;

const similarity = first.map(n => n * (counts[n] ?? 0)).reduce((a, b) => a + b);

console.log(similarity);
