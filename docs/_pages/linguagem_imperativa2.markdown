---
layout: linguagem
permalink: /linguagens/imperativa2
linguagem: Imperativa 2
short: Imp2
order: 7
---


* Estende a Linguagem Imperativa 1 com procedimentos parametrizados e recursivos
* O corpo de um procedimento é um comando e a chamada de um procedimento idem
* O contexto também é estendido com:
    * um mapeamento de identificadores (nomes de procedimentos) em definições de procedimentos
* Procedimentos não possuem status de valor
* Um programa é um comando, opcionalmente precedido por declarações de variáveis de ambiente
* Extensão deste fork: variáveis de ambiente, com escopo dinâmico
    * declaradas uma vez no início do programa, com tipo e valor padrão: `env x : int = 3;`
    * suporte a carregamento externo de arquivos `.env` via `loadenv ".env"` (na CLI) ou flag `--env`
    * lidas com `$x` e alteradas com `$x := e`
    * redefinidas temporariamente com `with x = e do c`, ligação que vale também dentro dos procedimentos chamados por `c`
    * ficam em uma pilha própria do contexto, separada das variáveis comuns, que só muda com o `with`


## Exemplos de Programas para a IDE Web

> **Nota:** Na [IDE Web]({{site.baseurl}}/ide/), o interpretador é executado diretamente no navegador pelo TeaVM, sem acesso ao sistema de arquivos do computador local. Por isso, os exemplos para a IDE utilizam declarações padrão com `env` (sem `loadenv`), que podem ser copiados e executados diretamente no editor.

### 1. Escopo Dinâmico e Reversão com `with`
Demonstra a alteração temporária de uma variável de ambiente e a restauração automática do valor original ao término do comando `with`.

```
env tentativas : int = 3;
write($tentativas);
(with tentativas = 10 do write($tentativas));
write($tentativas)
```
**Saída esperada:** `3 10 3`

### 2. Ligação Dinâmica Através de Procedimentos
O procedimento `conectar` não recebe parâmetros, mas enxerga a redefinição de `$tentativas` estabelecida pelo chamador através do `with`, restaurando o valor original em seguida.

```
env tentativas : int = 3;
{
   proc conectar () {
      write($tentativas)
   };

   call conectar();
   (with tentativas = 10 do call conectar());
   call conectar()
}
```
**Saída esperada:** `3 10 3`

### 3. Atribuição com `$id := Expressao`
A atribuição altera a ligação mais interna ativa. Quando executada dentro de um `with`, a alteração é descartada junto com o escopo do `with`.

```
env contador : int = 1;
write($contador);
$contador := 2;
write($contador);
(with contador = 10 do {
   $contador := $contador + 5;
   write($contador)
});
write($contador)
```
**Saída esperada:** `1 2 15 2`

### 4. Múltiplas Variáveis de Ambientes e Tipos Distintos
Uso de variáveis dos tipos `string`, `int` e `boolean`, com declarações que utilizam valores de declarações anteriores.

```
env app : string = "MeuApp", env porta : int = 8080, env portaSegura : int = $porta + 443, env debug : boolean = true;
write($app);
write($porta);
write($portaSegura);
write($debug)
```
**Saída esperada:** `"MeuApp" 8080 8523 true`


## Fontes

{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/Programa.java" %} ::= [ {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/declaration/ListaDeclaracaoVariavelAmbiente.java" %} ";" ] {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/command/Comando.java" %}

ListaDeclaracaoVariavelAmbiente ::= {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/declaration/DeclaracaoVariavelAmbiente.java" %}\
{% include code-indent.html %} | DeclaracaoVariavelAmbiente "," ListaDeclaracaoVariavelAmbiente

DeclaracaoVariavelAmbiente ::= {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/declaration/DeclaracaoVariavelAmbiente.java" content='"env" Id ":" Tipo "=" Expressao' %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/declaration/DeclaracaoLoadEnv.java" content='"loadenv" Expressao' %}

Comando ::= {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/command/Atribuicao.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/command/AtribuicaoAmbiente.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/command/ComandoDeclaracao.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/command/ComandoWith.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/command/While.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/command/IfThenElse.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/command/IO.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/command/SequenciaComando.java" content='Comando ";" Comando' %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/command/Skip.java" content='Skip' %} \
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/command/ChamadaProcedimento.java" content='ChamadaProcedimento' %}

Skip ::=

Atribuicao ::= {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/Id.java" %} ":=" {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/Expressao.java" %}

AtribuicaoAmbiente ::= {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/command/AtribuicaoAmbiente.java" content='"$" Id ":=" Expressao' %}

ComandoWith ::= {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/command/ComandoWith.java" content='"with" Id "=" Expressao "do" Comando' %}

Expressao ::= {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/Valor.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ExpUnaria.java" %} | {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ExpBinaria.java" %} | Id\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/expression/ExpVariavelAmbiente.java" content='"$" Id' %}

Valor ::= {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ValorConcreto.java" %}

ValorConcreto ::= {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ValorInteiro.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ValorBooleano.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ValorString.java" %}

ExpUnaria ::= {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ExpMenos.java" content=' "-" Expressao ' %} \
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ExpNot.java" content=' "not" Expressao ' %} \
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ExpLength.java" content=' "length" Expressao ' %}

ExpBinaria ::= {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ExpSoma.java" content=' Expressao "+" Expressao ' %} \
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ExpSub.java" content=' Expressao "-" Expressao ' %} \
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ExpAnd.java" content=' Expressao "and" Expressao ' %} \
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ExpOr.java" content=' Expressao "or" Expressao ' %} \
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ExpEquals.java" content=' Expressao "==" Expressao ' %} \
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/ExpConcat.java" content=' Expressao "++" Expressao ' %}

ComandoDeclaracao ::= "{" {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/declaration/Declaracao.java" %} ";" Comando "}"

Declaracao ::= {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/declaration/DeclaracaoVariavel.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/declaration/DeclaracaoProcedimento.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/declaration/DeclaracaoComposta.java" %}

DeclaracaoVariavel ::= "var" Id "=" Expressao

DeclaracaoComposta ::= Declaracao "," Declaracao

DeclaracaoProcedimento ::= {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/declaration/DeclaracaoProcedimento.java" content='"proc" Id "(" [ ListaDeclaracaoParametro ] ")" "{" Comando "}"' %}

ListaDeclaracaoParametro ::= {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/declaration/DeclaracaoParametro.java" content='Tipo Id' %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/declaration/ListaDeclaracaoParametro.java" content='Tipo Id "," ListaDeclaracaoParametro' %} 

Tipo ::= "string" \| "int" \| "boolean"

While ::= "while" Expressao "do" Comando

IfThenElse ::= "if" Expressao "then" Comando "else" Comando

IO ::= {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/command/Write.java" content='"write" "(" Expressao ")"' %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative1/command/Read.java" content='"read" "(" Id ")"' %}

ChamadaProcedimento ::= "call" Id "(" [ {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/command/ListaExpressao.java" %} ] ")"

ListaExpressao ::= Expressao \| Expressao, ListaExpressao


## Sintaxe Abstrata do Arquivo .env

{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/dotenv/model/ArquivoDotenv.java" %} ::= [ {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/dotenv/model/ListaEntradaDotenv.java" %} ]

ListaEntradaDotenv ::= {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/dotenv/model/EntradaDotenv.java" %}\
{% include code-indent.html %} | EntradaDotenv ListaEntradaDotenv

EntradaDotenv ::= {% include code-link.html path="Imperativa2/src/li2/plp/expressions2/expression/Id.java" %} "=" [ {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/dotenv/model/ValorDotenv.java" %} ]

ValorDotenv ::= {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/dotenv/model/ValorAspado.java" %}\
{% include code-indent.html %} | {% include code-link.html path="Imperativa2/src/li2/plp/imperative2/dotenv/model/ValorSimples.java" %}

ValorAspado ::= '"' ... '"' \| "'" ... "'"

ValorSimples ::= texto literal sem aspas ate quebra de linha ou comentario


## Classes Auxiliares

{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/memory/AmbienteExecucaoImperativa2.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/memory/ContextoExecucaoImperativa2.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative1/memory/ListaValor.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/declaration/DefProcedimento.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/memory/ProcedimentoJaDeclaradoException.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/memory/ProcedimentoNaoDeclaradoException.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/memory/AmbienteCompilacaoImperativa2.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/memory/ContextoCompilacaoImperativa2.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/memory/ContextoVariaveisAmbiente.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/memory/VariavelAmbienteJaDeclaradaException.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/memory/VariavelAmbienteNaoDeclaradaException.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/dotenv/resolver/DotenvInferrer.java" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/dotenv/exception/DotenvException.java" %}


## Parser

{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/parser/Imperative2.jj" %}\
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/dotenv/parser/Dotenv.jj" %}
