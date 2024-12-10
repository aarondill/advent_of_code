// https://adventofcode.com/2024/day/1

import { readInput } from "../../utils/utils";

const input = await readInput();
const pat = /mul\((\d+),(\d+)\)/g;
const sum = [...input.matchAll(pat)]
  .map(m => +m[1] * +m[2])
  .reduce((a, b) => a + b);
console.log(sum);
