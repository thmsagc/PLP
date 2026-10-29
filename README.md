# Variáveis de ambiente na Linguagem Imperativa 2

Projeto da disciplina Paradigmas de Linguagens de Programação (IN1007), pós-graduação em Ciência da Computação, UFPE. Professor Augusto Sampaio.

Equipe:

* Luiz Fernando Pereira do Nascimento, matrícula 20261036400
* Thomás Augusto Gouveia Chaves, matrícula 20261008030

Este repositório é um fork do projeto das linguagens da disciplina: https://github.com/AugustoSampaio/PLP

## Resumo

A ideia é acrescentar à Linguagem Imperativa 2 (LI2) um segundo espaço de nomes, separado das variáveis comuns, com regras de escopo diferentes. Chamamos essas variáveis de variáveis de ambiente. Elas são declaradas uma vez no início do programa, com tipo e valor padrão, podem ter o valor definido de fora do programa na hora da execução, e podem ser redefinidas temporariamente para um trecho de código, inclusive atravessando chamadas de procedimento sem precisar passar nada por parâmetro.

Na prática é o mesmo conceito que aparece em `getenv` do sistema operacional, nos parâmetros dinâmicos de Common Lisp, nas variáveis especiais de Clojure (`binding`) e no `Context` do Go e do React. A diferença para a variável comum é a regra de resolução: a variável comum é resolvida pelo bloco em que o código está escrito, e a variável de ambiente é resolvida pela cadeia de execução, ou seja, por quem chamou quem.

O ponto do projeto não é guardar um mapa de strings em algum lugar. É colocar uma segunda disciplina de ligação dentro da linguagem, com verificação estática de tipos, com escopo bem definido e com uma entrada vinda de fora do programa.

## Por que essa escolha

Algumas razões:

1. Olhando a lista de projetos anteriores da disciplina, entre 2002 e 2025.2, aparecem estruturas de dados (listas, mapas, pilhas, conjuntos, matrizes, árvores), mecanismos já bem cobertos (exceções, concorrência, coleta de lixo, avaliação preguiçosa, inferência de tipos, casamento de padrões, aspectos, mixins, contratos, genéricos, ownership, null safety) e algumas DSLs. Ligação dinâmica de nomes não aparece em nenhum deles. O que chega mais perto são os trabalhos antigos sobre escopo estático, que tratam do lado oposto do problema.

2. É um assunto que cabe bem na disciplina. Escopo estático contra escopo dinâmico, deep binding contra shallow binding e tempo de vida de uma ligação são tópicos clássicos de semântica de linguagens, e aqui eles ficam lado a lado no mesmo interpretador, dá para escrever um programa que roda diferente dependendo da regra usada.

3. Mexe em quase todas as camadas do interpretador: gramática, árvore sintática, verificador de tipos, ambiente de execução e semântica de chamada de procedimento. Não é uma funcionalidade que se resolve criando uma classe nova e pronto.

4. Tem demonstração fácil de entender, o que ajuda na apresentação.

Uma observação que encontramos lendo o código e que reforça a escolha: hoje, na LI2, a classe `ChamadaProcedimento` executa o corpo do procedimento em cima do próprio ambiente do chamador, apenas empilhando um escopo novo para os parâmetros. Ou seja, um procedimento já enxerga variáveis do chamador que não são parâmetros dele. Isso é um comportamento dinâmico que existe de forma implícita e não declarada. Parte do valor do projeto é justamente tornar essa ligação dinâmica explícita, tipada e delimitada, em vez de acidental.

## O que vamos implementar

### Declaração

A declaração fica junto das outras declarações do bloco, com tipo obrigatório e valor padrão obrigatório:

```
env tentativas : int = 3,
env modo : string = "producao",
```

O tipo é obrigatório porque é ele que permite ao verificador de tipos saber o tipo de qualquer leitura da variável em qualquer ponto do programa. O valor padrão é obrigatório para garantir que toda leitura tenha resposta, o que evita ter que decidir o que fazer quando a variável não foi ligada.

### Leitura

A leitura usa um marcador próprio, para deixar claro no código que aquilo não é uma variável comum:

```
write($tentativas);
x := $tentativas + 1
```

### Redefinição temporária

O comando `with` cria uma ligação nova que vale apenas durante a execução do comando que vem depois do `do`, e desfaz a ligação quando esse comando termina:

```
with tentativas = 10 do call conectar()
```

### Atribuição

A atribuição altera a ligação ativa mais interna, e não a ligação externa:

```
$tentativas := $tentativas - 1
```

### Valores vindos de fora

O interpretador vai aceitar uma lista de pares `nome=valor` na chamada. Esses valores substituem os valores padrão declarados no programa, no nível mais externo. Como vêm de fora, a conversão e a checagem de tipo acontecem na carga, antes de o programa começar a executar, e um valor incompatível com o tipo declarado é um erro reportado nesse momento.

Isso vale tanto para a execução por linha de comando quanto para a IDE web, que já recebe hoje uma string de entrada para o comando `read` e vai passar a receber também essa lista.

### Exemplo completo

```
{
   env tentativas : int = 3,

   proc conectar () {
      write($tentativas)
   };

   call conectar();
   with tentativas = 10 do call conectar();
   call conectar()
}
```

A saída esperada é 3, depois 10, depois 3 de novo. O procedimento `conectar` não recebe parâmetro nenhum e mesmo assim enxerga o valor que o chamador estabeleceu, e o valor volta ao normal quando o `with` termina.

## Sintaxe abstrata

Abaixo está a BNF da LI2 já com as extensões. A notação é a mesma usada na página da disciplina, em que cada não terminal corresponde a uma classe do interpretador. As quatro produções novas e as três alternativas novas estão marcadas com o comentário `(novo)`.

```
Programa                   ::= Comando

Comando                    ::= Atribuicao
                             | AtribuicaoAmbiente                                   (novo)
                             | ComandoDeclaracao
                             | ComandoWith                                          (novo)
                             | While
                             | IfThenElse
                             | IO
                             | Skip
                             | ChamadaProcedimento
                             | Comando ";" Comando

Skip                       ::=
Atribuicao                 ::= Id ":=" Expressao
AtribuicaoAmbiente         ::= "$" Id ":=" Expressao                                (novo)
ComandoWith                ::= "with" Id "=" Expressao "do" Comando                 (novo)
While                      ::= "while" Expressao "do" Comando
IfThenElse                 ::= "if" Expressao "then" Comando "else" Comando
IO                         ::= "write" "(" Expressao ")"
                             | "read" "(" Id ")"
ChamadaProcedimento        ::= "call" Id "(" [ ListaExpressao ] ")"
ListaExpressao             ::= Expressao
                             | Expressao "," ListaExpressao

ComandoDeclaracao          ::= "{" Declaracao ";" Comando "}"
Declaracao                 ::= DeclaracaoVariavel
                             | DeclaracaoVariavelAmbiente                           (novo)
                             | DeclaracaoProcedimento
                             | DeclaracaoComposta
DeclaracaoComposta         ::= Declaracao "," Declaracao
DeclaracaoVariavel         ::= "var" Id "=" Expressao
DeclaracaoVariavelAmbiente ::= "env" Id ":" Tipo "=" Expressao                      (novo)
DeclaracaoProcedimento     ::= "proc" Id "(" [ ListaDeclaracaoParametro ] ")"
                                      "{" Comando "}"
ListaDeclaracaoParametro   ::= Tipo Id
                             | Tipo Id "," ListaDeclaracaoParametro
Tipo                       ::= "int" | "boolean" | "string"

Expressao                  ::= Valor
                             | ExpUnaria
                             | ExpBinaria
                             | Id
                             | ExpVariavelAmbiente                                  (novo)
ExpVariavelAmbiente        ::= "$" Id                                               (novo)
Valor                      ::= ValorConcreto
ValorConcreto              ::= ValorInteiro | ValorBooleano | ValorString
ExpUnaria                  ::= "-" Expressao
                             | "not" Expressao
                             | "length" Expressao
ExpBinaria                 ::= Expressao "+" Expressao
                             | Expressao "-" Expressao
                             | Expressao "and" Expressao
                             | Expressao "or" Expressao
                             | Expressao "==" Expressao
                             | Expressao "++" Expressao
```

Duas observações sobre essa gramática.

A primeira é que `DeclaracaoVariavelAmbiente` aparece como alternativa de `Declaracao`, ou seja, sintaticamente ela é aceita em qualquer bloco, inclusive dentro de procedimento. A restrição de que a declaração só vale no bloco mais externo não está na gramática, e sim no verificador de tipos. Fizemos assim de propósito, porque colocar isso na gramática duplicaria toda a cadeia de declarações só para diferenciar bloco externo de bloco interno, e a mensagem de erro do verificador fica muito melhor do que um erro de parser.

A segunda é que `ComandoWith` termina em `Comando`, e não em `ComandoSimples`. Isso significa que em `with x = 1 do c1; c2` os dois comandos ficam dentro do `with`, exatamente como já acontece hoje com `while` e com `if`. Mantivemos o comportamento igual ao do resto da linguagem em vez de inventar uma regra diferente.

## Impacto na gramática JavaCC

O parser da LI2 fica em `Imperativa2/src/li2/plp/imperative2/parser/Imperative2.jj` e é gerado pelo JavaCC no `generate-sources` do Maven. As mudanças previstas são estas.

Três tokens novos:

```
TOKEN : /* TOKENS DAS VARIAVEIS DE AMBIENTE */
{
  < ENV: "env" >
| < WITH: "with" >
| < DOLLAR: "$" >
}
```

Quatro produções novas:

```
DeclaracaoVariavelAmbiente PDeclaracaoVariavelAmbiente() :
{
    Id id;
    Tipo tipo;
    Expressao exp;
}
{
    <ENV> id = PId() <COLON> tipo = PTipo() <ASSIGN> exp = PExpressao()
    { return new DeclaracaoVariavelAmbiente(id, tipo, exp); }
}

ComandoWith PComandoWith() :
{
    Id id;
    Expressao exp;
    Comando comando;
}
{
    <WITH> id = PId() <ASSIGN> exp = PExpressao() <DO> comando = PComando()
    { return new ComandoWith(id, exp, comando); }
}

AtribuicaoAmbiente PAtribuicaoAmbiente() :
{
    Id id;
    Expressao exp;
}
{
    <DOLLAR> id = PId() <ATTRIB> exp = PExpressao()
    { return new AtribuicaoAmbiente(id, exp); }
}

ExpVariavelAmbiente PExpVariavelAmbiente() :
{
    Id id;
}
{
    <DOLLAR> id = PId()
    { return new ExpVariavelAmbiente(id); }
}
```

E quatro pontos de encaixe em produções que já existem:

* `PComandoSimples()` ganha as alternativas `PComandoWith()` e `PAtribuicaoAmbiente()`. Como as duas começam por token próprio, `<WITH>` e `<DOLLAR>`, não é preciso `LOOKAHEAD` novo, e o `LOOKAHEAD(PComandoSimples() <SEMICOLON>)` que já existe em `PComando()` continua funcionando.
* `PExpPrimaria()` ganha a alternativa `PExpVariavelAmbiente()`. Também começa por `<DOLLAR>`, então as escolhas de `PExpBinaria()`, que hoje fazem `LOOKAHEAD(PExpPrimaria() <PLUS>)` e parecidos, continuam válidas sem alteração.
* `PDeclaracao()` e `PDeclaracaoComposta()` ganham a alternativa da declaração nova, seguindo o mesmo padrão `LOOKAHEAD(PDeclaracaoVariavelAmbiente() <COMMA>)` que já é usado para variável e para procedimento.

Ainda sobre o parser, tem um detalhe de análise léxica que vale registrar porque é uma armadilha real. Na gramática atual, o caractere `$` faz parte de `<#LETTER>`, que inclui `"$"`. Do jeito que está, `$tentativas` seria lido como um único `IDENTIFIER` e o token `<DOLLAR>` nunca seria reconhecido nessa posição. Então a introdução do marcador exige tirar `"$"` da produção `<#LETTER>`, o que na prática reserva o `$` para o uso novo. Verificamos que nenhum dos programas de exemplo em `Testes` usa `$` dentro de identificador, então o impacto é nulo. Se na avaliação esse ponto for considerado invasivo demais, a alternativa é usar `@` como marcador, que não é usado em lugar nenhum da gramática atual e não precisa de mudança no léxico.

## Semântica pretendida

* As variáveis de ambiente vivem em uma pilha própria, separada da pilha de variáveis comuns. Declarar `env x` e declarar `var x` não são a mesma coisa e não conflitam.
* A leitura de `$x` procura a ligação mais recente na pilha dinâmica. Se não houver nenhuma criada por `with`, o resultado é o valor inicial, que vem de fora do programa ou, na falta dele, do valor padrão declarado.
* O `with` empilha uma ligação antes de executar o comando e desempilha depois, sempre, inclusive quando o comando termina por caminhos diferentes.
* A ligação criada pelo `with` vale para tudo que for executado dentro dele, inclusive para procedimentos chamados lá dentro e para procedimentos chamados por esses procedimentos. Essa é a diferença central em relação a uma variável comum.
* A atribuição `$x := e` escreve na ligação mais interna que estiver ativa. Quando o `with` correspondente termina, a alteração é descartada.

## Verificação de tipos

Essa parte é o que separa o trabalho de um simples dicionário de strings. O verificador de tipos vai manter um contexto dinâmico próprio, montado a partir das declarações `env`, e vai checar:

* leitura de variável de ambiente não declarada é erro de compilação;
* `with` sobre nome não declarado é erro de compilação;
* o tipo da expressão do `with` precisa ser igual ao tipo declarado;
* o tipo da expressão da atribuição precisa ser igual ao tipo declarado;
* declaração `env` fora do bloco mais externo é erro de compilação, conforme comentado na seção da BNF;
* o tipo de `$x` em uma expressão é o tipo declarado, e as regras de tipo das expressões existentes continuam valendo sem alteração.

Com isso, um procedimento que lê `$tentativas` é verificado uma única vez, independente de quem vai chamá-lo, e não é preciso analisar todas as cadeias de chamada possíveis.

## Onde o código vai ser mexido

Tudo concentrado no módulo `Imperativa2`, que tem sua própria cópia dos pacotes e por isso não afeta as outras linguagens:

* `Imperativa2/src/li2/plp/imperative2/parser/Imperative2.jj`, com as mudanças descritas acima;
* um pacote novo para o ambiente dinâmico, com as classes de contexto, ambiente de execução, ambiente de compilação e as exceções próprias, seguindo o mesmo formato de `Contexto` e `Ambiente` que já existe em `li2.plp.expressions2.memory`;
* `DeclaracaoVariavelAmbiente`, implementando `Declaracao`, com `elabora` e `checaTipo`;
* `ComandoWith` e `AtribuicaoAmbiente`, implementando `Comando`, com `executar` e `checaTipo`;
* `ExpVariavelAmbiente`, implementando `Expressao`, com `avaliar`, `getTipo` e `checaTipo`;
* `ContextoExecucaoImperativa2` e `ContextoCompilacaoImperativa`, para carregarem o ambiente dinâmico junto;
* `ChamadaProcedimento`, para deixar explícito que o ambiente dinâmico atravessa a chamada;
* `WebAPI/src/main/java/plp/web/PlpWebApi.java`, para receber os valores externos;
* `Testes`, com os programas de exemplo.

Se sobrar tempo, a intenção é levar o mesmo mecanismo para a LOO1, onde ele fica interessante porque métodos de objetos diferentes passam a compartilhar um contexto de execução sem precisar de atributo global. Isso é um objetivo secundário, não faz parte do escopo mínimo.

## O que não faz parte do escopo

* Não vamos ler as variáveis de ambiente reais do sistema operacional. O interpretador roda também no navegador, compilado para JavaScript, onde isso não existe. Os valores externos entram pela chamada do interpretador ou pela IDE.
* Não vamos permitir declaração de variável de ambiente dentro de procedimento. A declaração é única e fica no bloco mais externo, o que mantém a verificação de tipos simples.
* Não vamos mexer no escopo das variáveis comuns nem mudar o comportamento atual da linguagem para programas que não usam o recurso novo. Fora da reserva do caractere `$`, todo programa LI2 que funciona hoje continua funcionando igual.

## Etapas

1. Levantamento e definição da sintaxe concreta e das regras de tipo, com exemplos escritos à mão do que cada programa deve imprimir.
2. Gramática e árvore sintática, ainda sem semântica, só confirmando que o parser aceita e rejeita o que deveria.
3. Ambiente dinâmico e execução, fazendo os exemplos rodarem.
4. Verificação de tipos.
5. Entrada externa de valores e integração com a WebAPI.
6. Programas de teste, comparação lado a lado com a solução equivalente usando parâmetros, e escrita da documentação e da apresentação.

## Como executar

O código do programa LI2 a ser executado deve ser colocado no arquivo **`Imperativa2/input`**. Para mais detalhes e roteiros de demonstração passo a passo, veja o guia [**COMO-EXECUTAR.md**](COMO-EXECUTAR.md).

```bash
cd Imperativa2
mvn clean generate-sources compile exec:java
```

## Referências

* Notas e material da disciplina IN1007
* Common Lisp, variáveis especiais e `let` dinâmico
* Clojure, `^:dynamic` e `binding`
* Emacs Lisp, ligação dinâmica e ligação léxica no mesmo interpretador
* Scott, Programming Language Pragmatics, capítulo de nomes, escopos e ligações
