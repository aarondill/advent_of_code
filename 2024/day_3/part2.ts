// https://adventofcode.com/2024/day/1

import { readInput } from "../../utils/utils";

const input = await readInput();

let sum = 0;
let shouldDo = true;
for (let i = 0; i < input.length; i++) {
  if (input.substring(i, i + "don't()".length) === "don't()") {
    shouldDo = false;
    i += "don't()".length - 1;
    continue;
  }
  if (input.substring(i, i + "do()".length) === "do()") {
    shouldDo = true;
    i += "do()".length - 1;
    continue;
  }
  if (input.substring(i, i + "mul(".length) === "mul(") {
    const nextClose = input.indexOf(")", i + "mul(".length);
    const match = input.substring(i, nextClose + 1).match(/mul\((\d+),(\d+)\)/);
    if (!match) {
      i += "mul(".length - 1;
      continue;
    }
    sum += shouldDo ? +match[1] * +match[2] : 0;
    i = nextClose;
  }
}
console.log(sum);
