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
const distance = first
  .map((n, i) => Math.abs(n - second[i]))
  .reduce((a, b) => a + b);
console.log(distance);
