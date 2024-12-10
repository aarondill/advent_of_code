// https://adventofcode.com/2024/day/1

import { readInput } from "../../utils/utils";

const input = await readInput();
const lines = input.split("\n").filter(Boolean);
const reports = lines.map(line => line.split(/\s+/).map(Number));

function isSafe(levels: number[]): boolean {
  if (levels[0] > levels[1]) {
    if (levels.some((level, i) => level < levels[i + 1])) return false;
  } else {
    if (levels.some((level, i) => level > levels[i + 1])) return false;
  }

  if (
    levels.some(
      (l, i) =>
        Math.abs(l - levels[i + 1]) < 1 || Math.abs(l - levels[i + 1]) > 3
    )
  )
    return false;
  return true;
}

console.log(
  reports.filter(levels => {
    if (isSafe(levels)) return true;
    for (let i = 0; i < levels.length; i++) {
      const [l] = levels.splice(i, 1);
      if (isSafe(levels)) return true;
      levels.splice(i, 0, l);
    }
    return false;
  }).length
);
