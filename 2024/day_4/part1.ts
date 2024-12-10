// https://adventofcode.com/2024/day/1

import { readInput } from "../../utils/utils";

const input = await readInput();
const letters = input
  .split("\n")
  .filter(Boolean)
  .map(l => l.split(""));

let count = 0;
const xmas = "XMAS".split("");
// Note: i is y, j is x
for (let i = 0; i < letters.length; i++) {
  for (let j = 0; j < letters[i].length; j++) {
    const horizontalInRange = xmas.length - 1 + j < letters[i].length;
    const verticalInRange = xmas.length - 1 + i < letters.length;
    if (horizontalInRange) {
      if (xmas.every((c, k) => letters[i][j + k] == c)) count++;
      if (xmas.every((c, k) => letters[i][j + xmas.length - 1 - k] == c))
        count++;
    }
    if (verticalInRange) {
      if (xmas.every((c, k) => letters[i + k][j] == c)) count++;
      if (xmas.every((c, k) => letters[i + xmas.length - 1 - k][j] == c))
        count++;
    }

    if (horizontalInRange && verticalInRange) {
      if (xmas.every((c, k) => letters[i + k][j + k] == c)) count++;
      if (
        xmas.every(
          (c, k) =>
            letters[i + xmas.length - 1 - k][j + xmas.length - 1 - k] == c
        )
      )
        count++;
      if (xmas.every((c, k) => letters[i + xmas.length - 1 - k][j + k] == c))
        count++;
      if (xmas.every((c, k) => letters[i + k][j + xmas.length - 1 - k] == c))
        count++;
    }
  }
}
console.log(count);
