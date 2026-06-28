<div align="center">
  <br>
  <h1>✦ Aurora Bank</h1>
  <p><strong>Sistema Bancário com Interface Futurista</strong></p>

  <p>
    <img src="https://img.shields.io/badge/Java-21-purple?style=for-the-badge&logo=openjdk">
    <img src="https://img.shields.io/badge/Swing-UI-8A2BE2?style=for-the-badge">
    <img src="https://img.shields.io/badge/Maven-build-blue?style=for-the-badge&logo=apachemaven">
    <img src="https://img.shields.io/badge/status-finalizado-success?style=for-the-badge">
  </p>

  <br>

  <p align="center">
    <img src="https://img.shields.io/badge/license-MIT-green">
  </p>

  <br>
</div>

## Sobre o Projeto

O **Aurora Bank** é uma aplicação bancária moderna desenvolvida em **Java Swing** com interface inspirada no estilo **Nubank** — tema escuro, gradientes roxos e rosas, animações suaves e experiência fluida.

O projeto simula operações bancárias reais aplicando conceitos de **Programação Orientada a Objetos (POO)** com foco em boas práticas e evolução contínua.

---

## Interface

### Tela de Login
- Fundo com partículas animadas
- Card central com gradiente roxo/rosa
- Campos estilizados com borda roxa no foco
- Cadastro automático ao fazer login

### Painel Principal
- Avatar circular com a inicial do usuário
- Cards de conta interativos com hover e status
- Saldo com animação de contagem (easing)
- Botões com gradiente e efeito de clique
- Toast notifications no centro da tela

---

## Funcionalidades

| Operação | Descrição |
|----------|-----------|
| **Cadastro** | Automático ao informar nome, CPF e senha |
| **Login** | Autenticação com senha criptografada (BCrypt) |
| **Depósito** | Adiciona valor à conta selecionada |
| **Saque** | Remove valor com validação de saldo |
| **Transferência** | Entre conta poupança e corrente |
| **Saldo** | Consulta com animação |
| **Juros** | Aplicação automática na poupança |
| **Taxas** | Cobrança na conta corrente |

---

## Tecnologias

- **Java 21** — LTS com recursos modernos
- **Java Swing** — Interface gráfica customizada com `Graphics2D`
- **Maven** — Gerenciamento de dependências e build
- **Gson** — Persistência em JSON
- **jBCrypt** — Criptografia de senhas
- **JUnit 5** — Testes unitários

---

## Estrutura do Projeto

```
src/main/java/br/com/banco/
├── app/
│   └── Main.java              # Ponto de entrada
├── model/
│   ├── Cliente.java           # Cliente com senha hash
│   ├── ContaBancaria.java     # Classe abstrata base
│   ├── ContaCorrente.java     # Conta com taxas
│   ├── ContaPoupanca.java     # Conta com juros
│   └── TaxaOperacional.java   # Interface funcional
├── service/
│   └── BancoService.java      # Lógica de negócio
├── ui/
│   └── BancoUI.java           # Interface gráfica Swing
└── util/
    ├── CpfValidator.java      # Validação de CPF
    └── FormatadorMoeda.java   # Formatação monetária
```

---

## Como Executar

### Pré-requisitos

- **Java 21+** instalado
- **Maven** instalado (ou use o wrapper)

### Via Maven

```bash
git clone https://github.com/guhdev-java/Sistema-banco.git
cd Sistema-banco
mvn clean compile exec:java
```

Ou gere o JAR com dependências:

```bash
mvn clean package -DskipTests
java -jar target/sistema-banco-1.0.0-jar-with-dependencies.jar
```

### Via terminal (modo console)

```bash
java -jar target/sistema-banco-1.0.0-jar-with-dependencies.jar --cli
```

### Via IntelliJ

1. Abra o projeto no IntelliJ IDEA
2. Aguarde o Maven indexar as dependências
3. Execute `Main.java` (ou `BancoUI.java`)

---

## Captura de Funcionalidades

| Funcionalidade | Detalhe Técnico |
|---|---|
| **Persistência** | Dados salvos em `dados_bancarios.json` via Gson |
| **Senha** | Hash com BCrypt (`org.mindrot.jbcrypt`) |
| **Saldo** | `BigDecimal` para precisão financeira |
| **Juros** | 5% ao mês na poupança |
| **Taxas** | R$10,00 de manutenção + R$2,00 por saque na corrente |

---

## Conceitos POO Aplicados

- **Abstração** — `ContaBancaria` como classe abstrata
- **Herança** — `ContaCorrente` e `ContaPoupanca` estendem `ContaBancaria`
- **Polimorfismo** — Métodos sobrescritos (`sacar`, `calcularJuros`)
- **Encapsulamento** — Atributos privados com getters/setters
- **Interface** — `TaxaOperacional` com `calcularTaxa()`
- **Exceções** — Validações com `IllegalArgumentException` e tratamento adequado

---

## Autor

**Gustavo Maquias**

- ✉️ guuuhh666@gmail.com
- GitHub: [@guhdev-java](https://github.com/guhdev-java)
- LinkedIn: [Gustavo Maquias](https://www.linkedin.com/in/gustavo-maquias-975a27321)

---

<div align="center">
  <sub>Feito com ☕ e Java</sub>
</div>
