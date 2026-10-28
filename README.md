# Variáveis de ambiente na Linguagem Imperativa 2

Projeto da disciplina Paradigmas de Linguagens de Programação (IN1007), pós-graduação em Ciência da Computação, UFPE. Professor Augusto Sampaio.

Equipe:

* Luiz Fernando Pereira do Nascimento, matrícula 20261036400
* Thomás Augusto Gouveia Chaves, matrícula 20261008030

Este repositório é um fork do projeto das linguagens da disciplina: https://github.com/AugustoSampaio/PLP

## Resumo

A ideia é acrescentar à Linguagem Imperativa 2 (LI2) um segundo espaço de nomes, separado das variáveis comuns, com regras de escopo diferentes. Chamamos essas variáveis de variáveis de ambiente. Elas são declaradas uma vez no início do programa, com tipo e valor padrão, e podem ser redefinidas temporariamente para um trecho de código, inclusive atravessando chamadas de procedimento sem precisar passar nada por parâmetro.

Na prática é o mesmo conceito que aparece em `getenv` do sistema operacional, nos parâmetros dinâmicos de Common Lisp, nas variáveis especiais de Clojure (`binding`), no `ScopedValue` do Java e no `Context` do Go e do React. A diferença para a variável comum é a regra de resolução: a variável comum é resolvida pelo bloco em que o código está escrito, e a variável de ambiente é resolvida pela cadeia de execução, ou seja, por quem chamou quem.

O ponto do projeto não é guardar um mapa de strings em algum lugar. É colocar uma segunda disciplina de ligação dentro da linguagem, com verificação estática de tipos e com escopo bem definido.

## Por que essa escolha

Algumas razões:

1. Olhando a lista de projetos anteriores da disciplina, entre 2002 e 2025.2, aparecem estruturas de dados (listas, mapas, pilhas, conjuntos, matrizes, árvores), mecanismos já bem cobertos (exceções, concorrência, coleta de lixo, avaliação preguiçosa, inferência de tipos, casamento de padrões, aspectos, mixins, contratos, genéricos, ownership, null safety) e algumas DSLs. Ligação dinâmica de nomes não aparece em nenhum deles. O que chega mais perto são os trabalhos antigos sobre escopo estático, que tratam do lado oposto do problema.

2. É um assunto que cabe bem na disciplina. Escopo estático contra escopo dinâmico, deep binding contra shallow binding e tempo de vida de uma ligação são tópicos clássicos de semântica de linguagens, e aqui eles ficam lado a lado no mesmo interpretador, dá para escrever um programa que roda diferente dependendo da regra usada.

3. Mexe em quase todas as camadas do interpretador: gramática, árvore sintática, verificador de tipos, ambiente de execução e semântica de chamada de procedimento. Não é uma funcionalidade que se resolve criando uma classe nova e pronto.

4. Tem demonstração fácil de entender, o que ajuda na apresentação.

Uma observação que encontramos lendo o código e que reforça a escolha: na LI2, a classe `ChamadaProcedimento` executa o corpo do procedimento em cima do próprio ambiente do chamador, apenas empilhando um escopo novo para os parâmetros. Ou seja, um procedimento já enxerga variáveis do chamador que não são parâmetros dele. Isso é um comportamento dinâmico que existe de forma implícita e não declarada, e que inclusive escapa do verificador de tipos (ver [Limitações conhecidas](#limitações-conhecidas)). Parte do valor do projeto é justamente tornar essa ligação dinâmica explícita, tipada e delimitada, em vez de acidental.

## A extensão

### Declaração

As declarações ficam no início do programa, antes do comando, separadas por vírgula e terminadas por ponto e vírgula. O tipo e o valor padrão são obrigatórios:

```
env tentativas : int = 3, env modo : string = "producao";
```

O tipo é obrigatório porque é ele que permite ao verificador de tipos saber o tipo de qualquer leitura da variável em qualquer ponto do programa. O valor padrão é obrigatório para garantir que toda leitura tenha resposta, o que evita ter que decidir o que fazer quando a variável não foi ligada. Uma declaração pode usar as anteriores, como em `env a : int = 2, env b : int = $a + 1;`.

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

A expressão do `with` é avaliada antes de a ligação nova ser criada, então `with x = $x + 1 do ...` usa o valor de fora.

### Atribuição

A atribuição altera a ligação ativa mais interna, e não a ligação externa:

```
$tentativas := $tentativas - 1
```

Se a ligação mais interna foi criada por um `with`, a alteração é descartada quando o `with` termina. É o comportamento de `setf` sobre variável especial em Common Lisp e de `setq` em Emacs Lisp, e é parecido com o `set!` de Clojure, que só é permitido dentro de um `binding` ativo. Discutimos isso na seção [Decisões tomadas depois do feedback](#decisões-tomadas-depois-do-feedback).

### Exemplo completo

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

A saída é `3 10 3`. O procedimento `conectar` não recebe parâmetro nenhum e mesmo assim enxerga o valor que o chamador estabeleceu, e o valor volta ao normal quando o `with` termina.

Os parênteses em volta do `with` são necessários pelo mesmo motivo que já são em `while` e `if`: o comando depois do `do` vai até o fim da sequência, então sem eles o último `call conectar()` também ficaria dentro do `with`.

## Sintaxe abstrata

Abaixo está a BNF da LI2 já com as extensões. A notação é a mesma usada na página da disciplina, em que cada não terminal corresponde a uma classe do interpretador. As produções novas e as alternativas novas estão marcadas com o comentário `(novo)`.

```
Programa                        ::= [ ListaDeclaracaoVariavelAmbiente ";" ] Comando  (novo)

ListaDeclaracaoVariavelAmbiente ::= DeclaracaoVariavelAmbiente                     (novo)
                                  | DeclaracaoVariavelAmbiente ","
                                    ListaDeclaracaoVariavelAmbiente
DeclaracaoVariavelAmbiente      ::= "env" Id ":" Tipo "=" Expressao                (novo)

Comando                         ::= Atribuicao
                                  | AtribuicaoAmbiente                             (novo)
                                  | ComandoDeclaracao
                                  | ComandoWith                                    (novo)
                                  | While
                                  | IfThenElse
                                  | IO
                                  | Skip
                                  | ChamadaProcedimento
                                  | Comando ";" Comando

Skip                            ::=
Atribuicao                      ::= Id ":=" Expressao
AtribuicaoAmbiente              ::= "$" Id ":=" Expressao                          (novo)
ComandoWith                     ::= "with" Id "=" Expressao "do" Comando           (novo)
While                           ::= "while" Expressao "do" Comando
IfThenElse                      ::= "if" Expressao "then" Comando "else" Comando
IO                              ::= "write" "(" Expressao ")"
                                  | "read" "(" Id ")"
ChamadaProcedimento             ::= "call" Id "(" [ ListaExpressao ] ")"
ListaExpressao                  ::= Expressao
                                  | Expressao "," ListaExpressao

ComandoDeclaracao               ::= "{" Declaracao ";" Comando "}"
Declaracao                      ::= DeclaracaoVariavel
                                  | DeclaracaoProcedimento
                                  | DeclaracaoComposta
DeclaracaoComposta              ::= Declaracao "," Declaracao
DeclaracaoVariavel              ::= "var" Id "=" Expressao
DeclaracaoProcedimento          ::= "proc" Id "(" [ ListaDeclaracaoParametro ] ")"
                                           "{" Comando "}"
ListaDeclaracaoParametro        ::= Tipo Id
                                  | Tipo Id "," ListaDeclaracaoParametro
Tipo                            ::= "int" | "boolean" | "string"

Expressao                       ::= Valor
                                  | ExpUnaria
                                  | ExpBinaria
                                  | Id
                                  | ExpVariavelAmbiente                            (novo)
ExpVariavelAmbiente             ::= "$" Id                                         (novo)
Valor                           ::= ValorConcreto
ValorConcreto                   ::= ValorInteiro | ValorBooleano | ValorString
ExpUnaria                       ::= "-" Expressao
                                  | "not" Expressao
                                  | "length" Expressao
ExpBinaria                      ::= Expressao "+" Expressao
                                  | Expressao "-" Expressao
                                  | Expressao "and" Expressao
                                  | Expressao "or" Expressao
                                  | Expressao "==" Expressao
                                  | Expressao "++" Expressao
```

Duas observações sobre essa gramática.

A primeira é que a restrição de que variáveis de ambiente só podem ser declaradas no bloco mais externo está na própria BNF: `DeclaracaoVariavelAmbiente` não é alternativa de `Declaracao`, e só aparece na lista opcional do começo de `Programa`. Na proposta original tínhamos colocado essa regra no verificador de tipos, por achar que levar para a gramática obrigaria a duplicar toda a cadeia de declarações só para separar bloco externo de bloco interno. Depois da sugestão do professor vimos que não é preciso duplicar nada: basta uma lista própria na frente do programa. Com isso, `{ env x : int = 1; ... }` e `env` depois de um comando passam a ser erro de sintaxe, e a classe `DeclaracaoVariavelAmbiente` deixou de estender `Declaracao`.

A segunda é que `ComandoWith` termina em `Comando`, e não em `ComandoSimples`. Isso significa que em `with x = 1 do c1; c2` os dois comandos ficam dentro do `with`, exatamente como já acontece com `while` e com `if`. Mantivemos o comportamento igual ao do resto da linguagem em vez de inventar uma regra diferente.

## Mudanças na gramática JavaCC

O parser da LI2 fica em `Imperativa2/src/li2/plp/imperative2/parser/Imperative2.jj` e é gerado pelo JavaCC no `generate-sources` do Maven.

Três tokens novos: `<ENV: "env">` e `<WITH: "with">` junto dos tokens da Imperativa 2, e `<DOLLAR: "$">` junto dos separadores.

Cinco produções novas: `PListaDeclaracaoVariavelAmbiente()`, `PDeclaracaoVariavelAmbiente()`, `PComandoWith()`, `PAtribuicaoAmbiente()` e `PExpVariavelAmbiente()`.

E três pontos de encaixe em produções que já existiam:

* `PPrograma()` ganhou a lista opcional `[ PListaDeclaracaoVariavelAmbiente() <SEMICOLON> ]` antes do comando. Como a lista começa por `<ENV>`, que não inicia nenhum comando, não é preciso `LOOKAHEAD`.
* `PComandoSimples()` ganhou as alternativas `PComandoWith()` e `PAtribuicaoAmbiente()`. As duas começam por token próprio, `<WITH>` e `<DOLLAR>`, então o `LOOKAHEAD(PComandoSimples() <SEMICOLON>)` que já existe em `PComando()` continua funcionando.
* `PExpPrimaria()` ganhou a alternativa `PExpVariavelAmbiente()`. Também começa por `<DOLLAR>`, então as escolhas de `PExpBinaria()`, que fazem `LOOKAHEAD(PExpPrimaria() <PLUS>)` e parecidos, continuam válidas sem alteração.

Tem um detalhe de análise léxica que vale registrar porque é uma armadilha real. Na gramática original, o caractere `$` fazia parte de `<#LETTER>`. Do jeito que estava, `$tentativas` seria lido como um único `IDENTIFIER` e o token `<DOLLAR>` nunca seria reconhecido. Por isso tiramos `"$"` de `<#LETTER>`, o que reserva o `$` para o uso novo. Nenhum dos programas de exemplo em `Testes` usava `$` dentro de identificador.

A gramática do debugger da IDE web, `WebDebug/src/plp/debug/imperativa2/parser/Imperative2Debug.jj`, é uma cópia da gramática da linguagem e recebeu exatamente as mesmas mudanças.

## Semântica

* As variáveis de ambiente vivem em uma pilha própria (`ContextoVariaveisAmbiente`), separada da pilha de variáveis comuns. Declarar `env x` e declarar `var x` não são a mesma coisa e não conflitam.
* Essa pilha não acompanha o `incrementa()`/`restaura()` dos blocos nem o das chamadas de procedimento. Ela só muda com o `with`. É isso que faz a ligação atravessar as chamadas.
* A leitura de `$x` procura a ligação mais recente na pilha. Se não houver nenhuma criada por `with`, o resultado é o valor padrão declarado.
* O `with` avalia a expressão, empilha uma ligação, executa o comando e desempilha, sempre, dentro de um `try/finally`.
* A ligação criada pelo `with` vale para tudo que for executado dentro dele, inclusive para procedimentos chamados lá dentro e para procedimentos chamados por esses procedimentos. Essa é a diferença central em relação a uma variável comum.
* A atribuição `$x := e` escreve na ligação mais interna que estiver ativa. Quando o `with` correspondente termina, a alteração é descartada.

## Verificação de tipos

O verificador de tipos mantém os tipos das variáveis de ambiente em `ContextoCompilacaoImperativa2`, montados a partir das declarações `env`, e checa:

* leitura de variável de ambiente não declarada é erro de compilação;
* `with` e atribuição sobre nome não declarado são erro de compilação;
* declarar a mesma variável de ambiente duas vezes é erro de compilação;
* o tipo do valor padrão, da expressão do `with` e da expressão da atribuição precisam ser iguais ao tipo declarado;
* o tipo de `$x` em uma expressão é o tipo declarado, e as regras de tipo das expressões existentes continuam valendo sem alteração.

Como as declarações só existem no início do programa e o `with` não muda o tipo, o contexto de tipos tem um nível só. Um procedimento que lê `$tentativas` é verificado uma única vez, no ponto da declaração, independente de quem vai chamá-lo, e não é preciso analisar todas as cadeias de chamada possíveis.

Os erros de nome usam exceções próprias, `VariavelAmbienteNaoDeclaradaException` e `VariavelAmbienteJaDeclaradaException`, com mensagens como `Variavel de ambiente $y nao declarada.`, no mesmo formato de `ProcedimentoNaoDeclaradoException`.

## Onde o código foi mexido

Tudo concentrado no módulo `Imperativa2`, que tem sua própria cópia dos pacotes e por isso não afeta as outras linguagens:

* `parser/Imperative2.jj`, com as mudanças descritas acima;
* `Programa`, que agora guarda a lista de declarações `env` em um campo próprio e a elabora antes do comando;
* `declaration/DeclaracaoVariavelAmbiente` e `declaration/ListaDeclaracaoVariavelAmbiente`;
* `command/ComandoWith` e `command/AtribuicaoAmbiente`;
* `expression/ExpVariavelAmbiente`;
* `memory/ContextoVariaveisAmbiente`, `memory/VariavelAmbienteNaoDeclaradaException` e `memory/VariavelAmbienteJaDeclaradaException`;
* `memory/AmbienteExecucaoImperativa2` e `ContextoExecucaoImperativa2`, com `incrementaEscopoAmbiente`, `restauraEscopoAmbiente`, `mapVariavelAmbiente`, `getVariavelAmbiente` e `changeVariavelAmbiente`;
* `memory/AmbienteCompilacaoImperativa2` e `ContextoCompilacaoImperativa2`, com `mapVariavelAmbiente` e `getTipoVariavelAmbiente`;
* `command/ChamadaProcedimento`, que não mudou de comportamento: ela não mexe na pilha das variáveis de ambiente, e é justamente por isso que a ligação atravessa a chamada. Deixamos isso registrado em um comentário.

Fora do módulo, só o necessário para as IDEs continuarem funcionando:

* `AppUI/.../MultiInterpretador.java` e `WebAPI/.../PlpWebApi.java` passaram a fazer a verificação de tipos com `ContextoCompilacaoImperativa2`;
* `WebDebug/.../imperativa2/AmbienteCompilacaoImperativaDebug.java` passou a implementar `AmbienteCompilacaoImperativa2`, e a gramática `Imperative2Debug.jj` recebeu as mesmas mudanças da gramática da linguagem;
* `WebUI/src/config/languages.ts` ganhou `env` e `with` no realce de sintaxe da Imp2.

## Decisões tomadas depois do feedback

Na conversa com o professor surgiram cinco pontos. Registramos aqui o que decidimos e por quê.

**Atribuição.** O professor sugeriu manter a atribuição só se ela fosse comum nas linguagens que serviram de inspiração. Ela é: Common Lisp (`setf`) e Emacs Lisp (`setq`) alteram a ligação mais interna, e Clojure permite `set!` dentro de um `binding`. As linguagens mais recentes vão no sentido contrário: `Context` do Go e do React e `ScopedValue` do Java (que existe justamente para substituir o `ThreadLocal` sem o `set()`) só permitem criar uma ligação nova. Decidimos manter a atribuição, porque ela permite mostrar um ponto semântico interessante, o de que a alteração vale só para a ligação ativa e é descartada no fim do `with` (testes `09` e `10`).

**`read()`.** O `read(Id)` continua aceitando só variável comum. Permitir `read($x)` seria uma segunda forma de atribuição, e a checagem de tipo do `read` na LI2 só acontece em tempo de execução (`Read.checaTipo` retorna sempre `true`), o que enfraqueceria a garantia estática das variáveis de ambiente. Para receber um valor de fora, basta ler em uma variável comum e repassar com `with`, que já é checado: `{ var n = 0; read(n); with x = n do ... }` (teste `11`).

**Declaração só no bloco externo.** Feito na BNF, sem duplicar a cadeia de declarações, como explicado na seção de sintaxe abstrata.

**`$` ou `@`.** Mantivemos o `$`. A mudança no léxico foi uma linha, nenhum programa existente usava `$` em identificador, e `$` é o marcador que todo mundo associa a variável de ambiente (`$HOME`, `$PATH`). O `@` tem significados conflitantes em outras linguagens (variável de instância em Ruby, anotação em Java, decorator em Python), o que confundiria se o recurso for levado para a LOO1.

**Nomes dos métodos.** Os métodos do ambiente se chamavam `mapAmbiente`, `getAmbiente` e parecidos, o que confundia com o próprio ambiente do interpretador (`AmbienteExecucao`). Renomeamos para `mapVariavelAmbiente`, `getVariavelAmbiente`, `getTipoVariavelAmbiente`, `incrementaEscopoAmbiente` etc.

**Valores vindos de fora.** Ficaram para depois, como o professor sugeriu. O `Programa` já guarda as declarações `env` em um campo próprio, então a extensão vai ser substituir os valores padrão ali antes de elaborar.

## Testes

Os programas de teste ficam em `Testes/Imperativa2Env`. Cada um traz a saída esperada em um comentário `// esperado:` na primeira linha (e a entrada do `read`, quando tem, em `// entrada:`). Os arquivos `01` a `16` são programas que devem rodar, e os `e01` a `e12` são programas que devem ser rejeitados, com a mensagem esperada.

Os arquivos `15` e `16` são a comparação lado a lado: o mesmo programa, com um nível de log que só o procedimento mais interno usa, escrito com parâmetros (só a LI2 original) e com variável de ambiente. Os dois têm a mesma saída. Na versão com parâmetros, `validar`, `calcularFrete` e `consultarTabela` recebem `nivel` só para repassar; na versão com `env`, nenhum deles sabe que o nível existe, e o modo detalhado vale só dentro do `with`. O script também roda o `Testes/TesteImperativa2.txt` original, para garantir que a LI2 continua funcionando.

```bash
cd Imperativa2
mvn clean generate-sources compile
cd ..
bash Testes/Imperativa2Env/executar.sh
```

## Limitações conhecidas

São comportamentos da LI2 original, que não mudamos, mas que vale conhecer ao escrever exemplos.

* **Sombreamento de variável comum em procedimento.** O corpo do procedimento é checado no ambiente da declaração e executado no ambiente de quem chama. Em `{ var x = 1, proc p () { write(x) }; { var x = "a"; call p() } }` a verificação de tipos passa e a saída é `"a"`. Uma comparação lado a lado entre variável comum e variável de ambiente não deve usar sombreamento, porque as duas vão se comportar de forma parecida. Esse é justamente o comportamento dinâmico acidental comentado no início.
* **Ordem dos procedimentos.** Um procedimento só pode chamar outro declarado antes dele, porque as declarações são checadas em ordem.
* **Escopo de notebook da IDE web.** Com a opção de escopo de notebook ligada, a IDE junta as células dentro de `{ ... }`, e aí uma declaração `env` vira erro de sintaxe. Para usar variáveis de ambiente, deixe essa opção desligada ou coloque o programa em uma célula só.
* **Rastreamento do parser.** A gramática original tem `DEBUG_PARSER = true`, então a linha de comando imprime o rastreamento do parser antes da saída. Mantivemos como no repositório da disciplina.

## Como executar

O procedimento é o mesmo do repositório original. O arquivo `Imperativa2/input` tem um exemplo pronto.

```bash
cd Imperativa2
mvn clean generate-sources compile exec:java
```

## Referências

* Notas e material da disciplina IN1007
* Common Lisp, variáveis especiais e `let` dinâmico
* Clojure, `^:dynamic`, `binding` e `set!`
* Emacs Lisp, ligação dinâmica e ligação léxica no mesmo interpretador
* Java, `ScopedValue` (JEP 506)
* Scott, Programming Language Pragmatics, capítulo de nomes, escopos e ligações
