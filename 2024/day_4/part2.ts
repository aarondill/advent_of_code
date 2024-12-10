// https://adventofcode.com/2024/day/1

import { readInput } from "../../utils/utils";

const input = await readInput();
const letters = input
  .split("\n")
  .filter(Boolean)
  .map(l => l.split(""));

let count = 0;
const xmas = "MAS".split("");
// Note: i is y, j is x
for (let i = 1; i < letters.length - 1; i++) {
  for (let j = 1; j < letters[i].length - 1; j++) {
    let conditions = 0;
    if (xmas.every((c, k) => letters[i + k - 1][j + k - 1] == c)) conditions++;
    if (
      xmas.every(
        (c, k) =>
          letters[i + xmas.length - 1 - k - 1][j + xmas.length - 1 - k - 1] == c
      )
    )
      conditions++;
    if (
      xmas.every((c, k) => letters[i + xmas.length - 1 - k - 1][j + k - 1] == c)
    )
      conditions++;
    if (
      xmas.every((c, k) => letters[i + k - 1][j + xmas.length - 1 - k - 1] == c)
    )
      conditions++;
    if (conditions > 1) count++;
  }
}
console.log(count);
