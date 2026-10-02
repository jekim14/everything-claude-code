#!/usr/bin/env node
// 쑥쑥 놀이터 고품질 음성 만들기 (Genspark CLI `gsk audio` 사용)
//
//   1. Genspark CLI 설치와 로그인:  npm install -g @genspark/cli  →  gsk login
//   2. 목소리 들어 보기:            node tools/make-voice.mjs --sample
//   3. 전체 만들기:                 node tools/make-voice.mjs --name 하늘
//
// voice/lines.txt 의 문장마다 음성 파일을 하나씩 만들어
//   app/src/main/assets/voice/          (일반 문장, 저장소에 올려도 되는 파일)
//   app/src/main/assets/voice_personal/ (아이 이름이 들어간 문장, .gitignore 로 올라가지 않음)
// 에 저장합니다. 이미 있는 파일은 건너뛰므로 중간에 멈춰도 다시 실행하면 이어서 만듭니다.
// 앱은 파일이 있으면 이 음성을, 없으면 기기의 음성 합성(TTS)을 씁니다.
//
// 파일 이름 규칙(FNV-1a 64비트 해시)은 app/.../content/VoiceScript.kt 의 VoiceKey 와 같아야 합니다.

import { spawn, spawnSync } from "node:child_process";
import { existsSync, mkdirSync, readFileSync, renameSync, rmSync, statSync } from "node:fs";
import { createRequire } from "node:module";
import { dirname, join, resolve } from "node:path";
import { createInterface } from "node:readline/promises";
import { fileURLToPath } from "node:url";

const here = dirname(fileURLToPath(import.meta.url));
const projectDir = resolve(here, "..");
const linesFile = join(projectDir, "voice", "lines.txt");
const assetsDir = join(projectDir, "app", "src", "main", "assets");

const DEFAULTS = {
  model: "google/gemini-3.8-flash-tts",
  voice: "Sulafat",
  instructions:
    "밝고 다정한 유치원 선생님 목소리로, 만 4~5세 아이에게 말하듯 조금 천천히 또박또박 읽어 주세요. " +
    "문장 끝은 부드럽게, 칭찬은 진심으로 기쁘게. 작은따옴표 안의 글자('그', '나')는 그 소리만 또렷하게 읽어 주세요.",
  concurrency: 3,
};

// ── 명령줄 옵션 ─────────────────────────────────────────────────────
function parseArgs(argv) {
  const opts = { ...DEFAULTS, name: "", sample: false, dryRun: false, yes: false, limit: 0, params: "", rawPrompt: false, gsk: "", listVoices: false };
  for (let i = 0; i < argv.length; i++) {
    const a = argv[i];
    const next = () => {
      if (i + 1 >= argv.length) throw new Error(`${a} 뒤에 값이 필요합니다`);
      return argv[++i];
    };
    switch (a) {
      case "--name": opts.name = next().trim(); break;
      case "--model": opts.model = next(); break;
      case "--voice": opts.voice = next(); break;
      case "--instructions": opts.instructions = next(); break;
      case "--params": opts.params = next(); break;
      case "--raw-prompt": opts.rawPrompt = true; break;
      case "--concurrency": opts.concurrency = Math.max(1, parseInt(next(), 10) || 1); break;
      case "--limit": opts.limit = Math.max(0, parseInt(next(), 10) || 0); break;
      case "--sample": opts.sample = true; break;
      case "--dry-run": opts.dryRun = true; break;
      case "--yes": case "-y": opts.yes = true; break;
      case "--gsk": opts.gsk = next(); break;
      case "--list-voices": opts.listVoices = true; break;
      case "--help": case "-h": printHelp(); process.exit(0); break;
      default: throw new Error(`알 수 없는 옵션: ${a} (--help 참고)`);
    }
  }
  return opts;
}

function printHelp() {
  console.log(`사용법: node tools/make-voice.mjs [옵션]

  --name <이름>        아이 이름 인사("안녕, 하늘아!")도 만듭니다 (voice_personal/ 에 저장)
  --sample             몇 문장만 voice/samples/ 에 만들어 목소리를 먼저 들어 봅니다
  --list-voices        모델 정보(목소리 목록, 매개변수)를 보여 줍니다 (gsk model-info)
  --model <id>         음성 모델 (기본 ${DEFAULTS.model})
  --voice <이름>       목소리 (기본 ${DEFAULTS.voice})
  --instructions <글>  말투 지시 (Gemini TTS)
  --params <JSON>      모델 매개변수를 직접 지정 (--model 을 바꿀 때; gsk model-info 참고)
  --raw-prompt         "Speaker1: " 없이 문장만 보냅니다 (Gemini 외 모델)
  --concurrency <n>    동시에 만들 개수 (기본 ${DEFAULTS.concurrency})
  --limit <n>          앞에서부터 n개만 만듭니다
  --dry-run            만들 목록과 예상 크레딧만 보여 줍니다
  --yes                확인 질문 없이 바로 시작합니다
  --gsk <경로>         gsk 실행 파일(또는 @genspark/cli 폴더) 경로를 직접 지정합니다`);
}

// ── 파일 이름 규칙 (VoiceKey 와 같음) ───────────────────────────────
export function normalize(text) {
  return text.trim().replace(/\s+/g, " ");
}

export function fnv1a64(text) {
  let h = 0xcbf29ce484222325n;
  for (const b of Buffer.from(normalize(text), "utf8")) {
    h ^= BigInt(b);
    h = (h * 0x100000001b3n) & 0xffffffffffffffffn;
  }
  return h.toString(16).padStart(16, "0");
}

export const fileNameFor = (segment) => `v_${fnv1a64(segment)}.mp3`;

// 받침이 있으면 "아", 없으면 "야" (Korean.vocative 와 같음)
export function vocative(name) {
  const last = [...name].reverse().find((ch) => ch.trim());
  if (!last) return name;
  const code = last.codePointAt(0);
  const hangul = code >= 0xac00 && code <= 0xd7a3;
  const batchim = hangul && (code - 0xac00) % 28 !== 0;
  return name + (batchim ? "아" : "야");
}

// ── 문장 목록 ───────────────────────────────────────────────────────
function loadJobs(opts) {
  const raw = readFileSync(linesFile, "utf8").split(/\r?\n/);
  const jobs = [];
  for (const line of raw) {
    if (!line.trim() || line.startsWith("#")) continue;
    if (line.startsWith("@name ")) {
      if (!opts.name) continue;
      const text = line.slice("@name ".length).replaceAll("{A}", vocative(opts.name));
      jobs.push({ text, dir: join(assetsDir, "voice_personal") });
    } else {
      jobs.push({ text: normalize(line), dir: join(assetsDir, "voice") });
    }
  }
  if (opts.sample) {
    const picks = ["딩동댕!", "잘 보고 골랐구나.", "기역은 '그' 소리.", "느, 아, 나!", "하나, 둘, 셋.", "모두 세 개!"];
    const sample = jobs.filter((j) => picks.includes(j.text) || j.dir.endsWith("voice_personal"));
    return sample.map((j) => ({ ...j, dir: join(projectDir, "voice", "samples") }));
  }
  return opts.limit ? jobs.slice(0, opts.limit) : jobs;
}

// ── Genspark CLI 찾기 ───────────────────────────────────────────────
function findGsk(opts) {
  const fromPackageDir = (dir) => {
    const pkgPath = join(dir, "package.json");
    if (!existsSync(pkgPath)) return null;
    const pkg = JSON.parse(readFileSync(pkgPath, "utf8"));
    const bin = typeof pkg.bin === "string" ? pkg.bin : pkg.bin?.gsk ?? Object.values(pkg.bin ?? {})[0];
    return bin ? join(dir, bin) : null;
  };
  if (opts.gsk) {
    const p = resolve(opts.gsk);
    if (existsSync(p) && statSync(p).isDirectory()) return fromPackageDir(p);
    return p;
  }
  try {
    const require = createRequire(join(process.cwd(), "noop.js"));
    const dir = dirname(require.resolve("@genspark/cli/package.json"));
    const bin = fromPackageDir(dir);
    if (bin) return bin;
  } catch {}
  const npmRoot = spawnSync(process.platform === "win32" ? "npm.cmd" : "npm", ["root", "-g"], {
    encoding: "utf8",
    shell: process.platform === "win32",
  });
  if (npmRoot.status === 0) {
    const bin = fromPackageDir(join(npmRoot.stdout.trim(), "@genspark", "cli"));
    if (bin) return bin;
  }
  return null;
}

// 셸을 거치지 않고 node 로 직접 실행하므로 따옴표·느낌표가 든 한국어 문장도 그대로 전달됩니다.
function runGsk(gskBin, args) {
  return new Promise((resolvePromise) => {
    const child = spawn(process.execPath, [gskBin, ...args], { stdio: ["ignore", "pipe", "pipe"] });
    let out = "";
    let err = "";
    child.stdout.on("data", (d) => (out += d));
    child.stderr.on("data", (d) => (err += d));
    child.on("close", (code) => resolvePromise({ code, out, err }));
  });
}

function hasFfmpeg() {
  return spawnSync("ffmpeg", ["-version"], { stdio: "ignore" }).status === 0;
}

function toMp3(input, output) {
  // 한 목소리 문장은 모노 24kHz 48kbps 로도 충분히 또렷하고, 앱 크기를 작게 유지합니다.
  const r = spawnSync("ffmpeg", ["-y", "-loglevel", "error", "-i", input, "-ac", "1", "-ar", "24000", "-b:a", "48k", output]);
  return r.status === 0;
}

function paramsFor(opts) {
  if (opts.params) return JSON.parse(opts.params);
  return { speakers: [{ speaker: "Speaker1", voice_name: opts.voice }], instructions: opts.instructions };
}

async function main() {
  const opts = parseArgs(process.argv.slice(2));
  const gskBin = findGsk(opts);

  if (opts.listVoices) {
    if (!gskBin) throw new Error("gsk 를 찾지 못했습니다. npm install -g @genspark/cli 후 다시 실행해 주세요.");
    const r = await runGsk(gskBin, ["model-info", opts.model]);
    console.log(r.out || r.err);
    return;
  }

  const jobs = loadJobs(opts);
  const todo = jobs.filter((j) => !existsSync(join(j.dir, fileNameFor(j.text))));
  const chars = todo.reduce((n, j) => n + j.text.length, 0);
  console.log(`문장 ${jobs.length}개 중 새로 만들 것 ${todo.length}개 (글자 ${chars}자)`);
  if (opts.model.startsWith("google/gemini-3.8-flash")) {
    console.log(`예상 크레딧: 약 ${Math.ceil(chars / 20)} (Gemini 3.8 Flash TTS 는 20자당 1크레딧)`);
  }
  if (opts.name) console.log(`이름 인사: "${vocative(opts.name)}" → app/src/main/assets/voice_personal/`);
  if (opts.dryRun) {
    todo.slice(0, 15).forEach((j) => console.log(`  ${fileNameFor(j.text)}  ${j.text}`));
    if (todo.length > 15) console.log(`  … 외 ${todo.length - 15}개`);
    return;
  }
  if (todo.length === 0) return console.log("모두 만들어져 있습니다.");
  if (!gskBin) throw new Error("gsk 를 찾지 못했습니다. npm install -g @genspark/cli 로 설치하고 gsk login 을 먼저 해 주세요.");
  if (!opts.yes) {
    const rl = createInterface({ input: process.stdin, output: process.stdout });
    const answer = (await rl.question("시작할까요? (y/N) ")).trim().toLowerCase();
    rl.close();
    if (answer !== "y" && answer !== "yes") return console.log("취소했습니다.");
  }

  const ffmpeg = hasFfmpeg();
  if (!ffmpeg) console.warn("ffmpeg 가 없어 받은 파일을 그대로 저장합니다. 파일이 크면 ffmpeg 를 설치하고 다시 만들어 주세요.");
  const params = JSON.stringify(paramsFor(opts));
  const failed = [];
  let done = 0;

  async function makeOne(job) {
    mkdirSync(job.dir, { recursive: true });
    const out = join(job.dir, fileNameFor(job.text));
    const tmp = `${out}.download`;
    const prompt = opts.rawPrompt ? job.text : `Speaker1: ${job.text}`;
    for (let attempt = 1; attempt <= 3; attempt++) {
      rmSync(tmp, { force: true });
      const r = await runGsk(gskBin, ["--no-input", "audio", prompt, "-m", opts.model, "-p", params, "-f", fileNameFor(job.text), "-o", tmp]);
      if (r.code === 0 && existsSync(tmp) && statSync(tmp).size > 0) {
        if (ffmpeg) {
          if (!toMp3(tmp, out)) renameSync(tmp, out);
        } else {
          renameSync(tmp, out);
        }
        rmSync(tmp, { force: true });
        done++;
        console.log(`[${done}/${todo.length}] ${job.text}`);
        return;
      }
      if (attempt === 3) {
        failed.push(job.text);
        console.error(`실패: ${job.text}\n${(r.err || r.out).trim().slice(0, 500)}`);
      } else {
        await new Promise((ok) => setTimeout(ok, 2000 * attempt));
      }
    }
  }

  const queue = [...todo];
  await Promise.all(
    Array.from({ length: Math.min(opts.concurrency, queue.length) }, async () => {
      while (queue.length) await makeOne(queue.shift());
    }),
  );
  console.log(`끝: ${done}개 만듦, ${failed.length}개 실패`);
  if (failed.length) {
    console.log("실패한 문장은 다시 실행하면 그것만 다시 만듭니다.");
    process.exitCode = 1;
  }
}

if (process.argv[1] && resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  main().catch((e) => {
    console.error(e.message);
    process.exit(1);
  });
}
