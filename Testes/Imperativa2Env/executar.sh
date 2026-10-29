#!/usr/bin/env bash
# Roda os programas desta pasta no interpretador da Imperativa2 e compara com
# o comentario "// esperado:" de cada um. "// entrada:" e a entrada do read.
# Antes: cd Imperativa2 && mvn clean generate-sources compile
DIR="$(cd "$(dirname "$0")" && pwd)"
CP="$DIR/../../Imperativa2/target/classes"
JAVA="${JAVA_HOME:+$JAVA_HOME/bin/}java"
falhas=0
for f in "$DIR"/*.imp2 "$DIR"/../TesteImperativa2.txt; do
  esperado=$(sed -n 's|^// esperado: *||p' "$f")
  entrada=$(sed -n 's|^// entrada: *||p' "$f")
  if [ "$(basename "$f")" = "TesteImperativa2.txt" ]; then esperado="2 7"; entrada="7"; fi
  saida=$("$JAVA" -cp "$CP" li2.plp.imperative2.parser.Imp2Parser "$f" $entrada 2>&1 \
    | grep -Ev '^\s*(Call|Return|Consumed|Visited)|Reading from|parsed successfully|^\s+at |^\s*$')
  case "$esperado" in
    ERRO\ *) grep -qF "${esperado#ERRO }" <<<"$saida" && ok=1 || ok=0 ;;
    *) [ "$(tail -n1 <<<"$saida" | sed 's/ *$//')" = "$esperado" ] && ok=1 || ok=0 ;;
  esac
  if [ $ok = 1 ]; then echo "ok    $(basename "$f")"
  else echo "FALHA $(basename "$f")"; echo "      esperado: $esperado"; echo "      obtido:   $(head -n2 <<<"$saida" | tr '\n' ' ')"; falhas=$((falhas+1)); fi
done
echo; [ $falhas = 0 ] && echo "todos os testes passaram" || { echo "$falhas falha(s)"; exit 1; }
