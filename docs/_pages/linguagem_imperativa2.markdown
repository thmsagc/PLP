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
    * lidas com `$x` e alteradas com `$x := e`
    * redefinidas temporariamente com `with x = e do c`, ligação que vale também dentro dos procedimentos chamados por `c`
    * ficam em uma pilha própria do contexto, separada das variáveis comuns, que só muda com o `with`


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
{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/memory/VariavelAmbienteNaoDeclaradaException.java" %}


## Parser

{% include code-link.html path="Imperativa2/src/li2/plp/imperative2/parser/Imperative2.jj" %}\