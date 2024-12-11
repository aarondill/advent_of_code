// https://adventofcode.com/2024/day/1

import { readInput } from "../../utils/utils";

const input = await readInput();
const lines = input.split("\n").filter(Boolean);
const nums = lines.map(line => line.split(/:?\s+/).map(Number));

function evaluate(nums: number[], target: number, current = 0): boolean {
  const [first, ...rest] = nums;
  if (first == undefined) return target === current;
  if (evaluate(rest, target, current + first)) return true;
  if (evaluate(rest, target, current * first)) return true;
  if (evaluate(rest, target, +(current + "" + first))) return true;
  return false;
}

const result = nums
  .filter(n => evaluate(n.slice(1), n[0]!))
  .map(n => n[0]!)
  .reduce((a, b) => a + b);
console.log(result);
