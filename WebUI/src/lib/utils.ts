import type { Cell } from "../models/cell/Cell";
import { CodeCell } from "../models/cell/CodeCell";
import type { ID } from "../models/types/id";
import type { LanguageFn } from "highlight.js";
import type { Language } from "../models/types/execution";

// Separa as declarações env do início da célula (até o primeiro ";" fora de string).
// O que sai vira espaço, para o restante manter as mesmas linhas e colunas.
const splitEnvDeclarations = (content: string) => {
  if (!/^\s*env\b/.test(content)) return { declarations: "", rest: content };

  let inString = false;
  for (let i = 0; i < content.length; i++) {
    if (content[i] === '"') inString = !inString;
    else if (content[i] === ";" && !inString) {
      return {
        declarations: content.slice(0, i).trim(),
        rest: content.slice(0, i + 1).replace(/[^\n]/g, " ") + content.slice(i + 1),
      };
    }
  }
  return { declarations: "", rest: content };
};

export const buildNotebookScopeCode = (cells: Cell[], targetCellId: ID) => {
  const currentCell = cells.find((c) => c.id === targetCellId);
  if (!(currentCell instanceof CodeCell)) return "";

  const previouslyRun = cells
    .filter((c): c is CodeCell => c instanceof CodeCell && c.id !== targetCellId && c.executionOrder !== undefined)
    .sort((a, b) => a.executionOrder! - b.executionOrder!);

  // Na Imp2, declarações env só são aceitas no início do programa, fora do bloco
  const parts = [...previouslyRun, currentCell].map((c) => splitEnvDeclarations(c.content));
  const declarations = parts.map((p) => p.declarations).filter(Boolean).join(", ");
  const combined = parts.map((p) => p.rest).join("\n");

  return declarations ? `${declarations}; {\n${combined}\n}` : `{\n${combined}\n}`;
};

export const escapeHtml = (text: string) => {
  return text.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
};

export function languageToHljs(def: Language): LanguageFn {
  return (hljs) => ({
    name: def.name,
    aliases: [def.name],
    keywords: {
      keyword: def.bnf.keywords ?? [],
      literal: def.bnf.literals ?? [],
      type: def.bnf.types ?? [],
      built_in: def.bnf.builtins ?? [],
    },
    contains: [
      hljs.QUOTE_STRING_MODE,
      hljs.NUMBER_MODE,
      {
        className: "operator",
        match: /(:=|==|\+\+|\^\^|\.\.|[+\-*<>.:;,])/,
      },
    ],
  });
}

export const languageToShortName = (language: Language) => {
  const name = language.name;
  return `L${name[0]}${name[name.length - 1]}`;
};

export const languageToStyles = (language: Language) => {
  const stylesMap: Record<string, string> = {
    exp1: "bg-teal-600 text-white",
    exp2: "bg-green-600 text-white",
    func1: "bg-cyan-400 text-white",
    func2: "bg-blue-600 text-white",
    func3: "bg-red-600 text-white",
    imp1: "bg-pink-600 text-white",
    imp2: "bg-amber-600 text-white",
    oo1: "bg-purple-600 text-white",
    oo2: "bg-black text-white",
  };

  return stylesMap[language.name.toLowerCase()] ?? "bg-gray-400 text-white";
};
