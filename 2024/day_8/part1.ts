// https://adventofcode.com/2024/day/1

import { readInput } from "../../utils/utils";

const input = await readInput();
const lines = input.split("\n").filter(Boolean);
const spaces = lines.map(line => line.split(""));
type Point = { x: number; y: number };
const antenae = spaces
  .flatMap((line, y) =>
    line
      .map((char, x) => (![".", "#"].includes(char) ? { x, y, char } : null))
      .filter(x => x !== null)
  )
  .reduce(
    (acc, cur) => ((acc[cur.char] ??= []).push(cur), acc),
    {} as { [char: string]: Point[] }
  );

const dist = (x1: number, y1: number, x2: number, y2: number) =>
  Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
function isAntinode(x: number, y: number): boolean {
  for (const points of Object.values(antenae)) {
    for (let i = 0; i < points.length; i++) {
      for (let j = i + 1; j < points.length; j++) {
        const p1 = points[i]!,
          p2 = points[j]!;
        const distanceToClosestPoint = Math.min(
          dist(p1.x, p1.y, x, y),
          dist(p2.x, p2.y, x, y)
        );
        const distanceBetweenPoints = dist(p1.x, p1.y, p2.x, p2.y);
        if (distanceToClosestPoint !== distanceBetweenPoints) continue;

        const angle1 = Math.atan2(y - p1.y, x - p1.x);
        const angle2 = Math.atan2(y - p2.y, x - p2.x);
        if (angle1 !== angle2) continue;
        return true;
      }
    }
  }
  return false;
}

// Note, points can't be equal
const antinodes = new Set<string>();
for (let y = 0; y < spaces.length; y++) {
  for (let x = 0; x < spaces[y]!.length; x++) {
    if (isAntinode(x, y)) antinodes.add(`${x},${y}`);
  }
}
console.log(antinodes.size);
