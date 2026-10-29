# Guia Rápido de Execução e Demonstração (LI2 com `.env`)

Este guia explica como executar e demonstrar o funcionamento das variáveis de ambiente e do carregamento via `.env` na Linguagem Imperativa 2 (LI2).

---

## 1. O Arquivo de Entrada (`input`)

Por padrão, a CLI da LI2 lê o código-fonte a partir do arquivo **`input`**, localizado dentro do diretório do módulo:

📂 **Caminho**: `Imperativa2/input`

Basta abrir esse arquivo, colar o código LI2 desejado e salvar.

---

## 2. Como Executar

### Pré-requisito
Certifique-se de estar dentro do diretório `Imperativa2`:
```bash
cd Imperativa2
```

Se for a primeira vez ou tiver feito alterações de código, compile o projeto:
```bash
mvn compile
```

---

### Modo 1: Execução Padrão (Lendo `Imperativa2/input`)

Para rodar o programa contido no arquivo `input`:
```bash
mvn exec:java
```

---

### Modo 2: Passando Arquivo `.env` via Flag CLI (`--env`)

Você pode injetar variáveis de fora do código usando a flag `--env`:
```bash
mvn exec:java -Dexec.args="input --env dev.env"
```

Também suporta o formato `--env=arquivo`:
```bash
mvn exec:java -Dexec.args="input --env=dev.env"
```

---

## 3. Roteiros Prontos para Demonstração

Abaixo estão 4 cenários prontos para apresentação:

---

### 🌟 Cenário 1: Variáveis de Ambiente e Escopo Dinâmico (`with` e `$var`)

Demonstra a declaração com `env`, leitura com `$var` e a alteração de escopo temporário com `with`.

1. Edite o arquivo `Imperativa2/input`:
   ```li2
   env x : int = 3;
   (with x = 10 do write($x));
   write($x)
   ```

2. Execute:
   ```bash
   mvn exec:java
   ```

3. **Saída esperada**:
   ```text
   10 3
   ```
   *Explicação*: O comando `with` altera o valor de `$x` para 10 apenas no seu bloco local. Ao sair do bloco, `$x` volta automaticamente para o valor padrão 3.

---

### 🌟 Cenário 2: Carregamento de Arquivo `.env` via Código (`loadenv`)

Demonstra o comando `loadenv` importando configurações de um arquivo `.env`.

1. Crie o arquivo `Imperativa2/app.env` com o conteúdo:
   ```dotenv
   # Arquivo de configuracao
   PORTA=8080
   DEBUG=true
   TITULO="Linguagem Imperativa 2"
   ```

2. Edite o arquivo `Imperativa2/input`:
   ```li2
   loadenv "app.env",
   env tentativas : int = 3;
   write($PORTA);
   write($DEBUG);
   write($TITULO);
   write($tentativas)
   ```

3. Execute:
   ```bash
   mvn exec:java
   ```

4. **Saída esperada**:
   ```text
   8080 true Linguagem Imperativa 2 3
   ```
   *Explicação*: As variáveis `PORTA` (int), `DEBUG` (boolean) e `TITULO` (string) foram lidas do `app.env`, tiveram seus tipos inferidos automaticamente e ficaram acessíveis via `$VAR`.

---

### 🌟 Cenário 3: Sobrescrita do Valor Padrão pelo `.env`

Demonstra que o valor vindo do `.env` tem precedência e sobrescreve o valor padrão definido no código.

1. Crie o arquivo `Imperativa2/sobrescrita.env`:
   ```dotenv
   PORTA=9999
   ```

2. Edite o arquivo `Imperativa2/input`:
   ```li2
   loadenv "sobrescrita.env",
   env PORTA : int = 3000;
   write($PORTA)
   ```

3. Execute:
   ```bash
   mvn exec:java
   ```

4. **Saída esperada**:
   ```text
   9999
   ```
   *Explicação*: O valor padrão declarado no código era `3000`, mas o interpretador manteve o valor `9999` fornecido pelo arquivo `.env`.

---

### 🌟 Cenário 4: Injeção de `.env` via Flag CLI (`--env`)

Demonstra a carga de `.env` externamente via linha de comando, sem modificar o código-fonte.

1. Crie o arquivo `Imperativa2/prod.env`:
   ```dotenv
   PORTA=5000
   ```

2. Edite o arquivo `Imperativa2/input`:
   ```li2
   env PORTA : int = 8080;
   write($PORTA)
   ```

3. Execute passando a flag CLI:
   ```bash
   mvn exec:java -Dexec.args="input --env prod.env"
   ```

4. **Saída esperada**:
   ```text
   5000
   ```

---

### ⚠️ Cenário 5: Verificação Estática de Tipos (`checaTipo` rejeitando tipo incompatível)

Demonstra a robustez do sistema de tipos estático da PLP. Se o `.env` trouxer um tipo incompatível com o declarado no código, o interpretador **rejeita antes de executar**.

1. Crie o arquivo `Imperativa2/invalido.env`:
   ```dotenv
   PORTA="texto_em_vez_de_numero"
   ```

2. Edite o arquivo `Imperativa2/input`:
   ```li2
   loadenv "invalido.env",
   env PORTA : int = 3000;
   write($PORTA)
   ```

3. Execute:
   ```bash
   mvn exec:java
   ```

4. **Saída esperada**:
   ```text
   Type check error
   ```
   *Explicação*: `checaTipo` detectou que o `.env` forneceu uma `string` para uma variável declarada como `int`, impedindo a execução com erro estático de tipos.

---

## 4. Resumo de Comandos Rápidos

| Objetivo | Comando |
| :--- | :--- |
| **Compilar módulo** | `cd Imperativa2 && mvn compile` |
| **Executar programa padrão (`input`)** | `cd Imperativa2 && mvn exec:java` |
| **Executar com flag `--env`** | `cd Imperativa2 && mvn exec:java -Dexec.args="input --env arquivo.env"` |
| **Executar com argumentos para `read()`** | `cd Imperativa2 && mvn exec:java -Dexec.args="input 10 20"` |

