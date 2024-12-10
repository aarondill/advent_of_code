import { once } from "node:events";
import fs from "node:fs/promises";
/** Read the input from stdin */
export async function readInput(): Promise<string> {
  return process.argv[2] !== undefined
    ? await fs.readFile(process.argv[2], "utf8")
    : await (async () => {
        const chunks: Buffer[] = [];
        process.stdin.on("data", chunk => chunks.push(chunk));
        await once(process.stdin, "end");
        return Buffer.concat(chunks).toString("utf8");
      })();
}
