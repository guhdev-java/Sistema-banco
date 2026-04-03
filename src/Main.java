import java.util.Scanner;

public class Main {

    private static int lerInt(Scanner scan, String prompt) {
        while (true) {                                                                      // Loop para garantir que o usuário digite um número inteiro válido
            System.out.print(prompt);
            try {
                int valor = scan.nextInt();
                scan.nextLine();
                return valor;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
                scan.nextLine();                                                            // Limpa o buffer do scanner para evitar loop infinito
            }
        }
    }

    private static double lerDouble(Scanner scan, String prompt) {
        while (true) {
            System.out.print(prompt);                  
            try {
                double valor = scan.nextDouble();
                scan.nextLine();
                return valor;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número válido (ex: 100.50).");
                scan.nextLine();     
            }
        }
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        boolean sairPrograma = false;                                                           // Variável para controlar o loop principal do programa, permitindo que o usuário faça login com diferentes contas sem reiniciar o programa

        while (!sairPrograma) {
            System.out.print("Digite seu nome para login: ");
            String nomeLogin = scan.nextLine().trim();

            Cliente cliente = null;                                                             // Loop para garantir que o usuário digite um CPF válido, criando o objeto Cliente apenas quando um CPF correto for fornecido
            while (cliente == null) {
                System.out.print("Digite seu CPF para login (11 dígitos): ");
                String cpfLogin = scan.nextLine().trim();
                try {
                    cliente = new Cliente(nomeLogin, cpfLogin);
                } catch (IllegalArgumentException e) {
                    System.out.println("CPF inválido: " + e.getMessage() + " Tente novamente.");
                }
            }

            ContaPoupanca contaPoupanca = new ContaPoupanca(cliente, 1000.00, 0.05);                        // Criação de uma conta poupança com saldo inicial e taxa de juros personalizada para o cliente logado
            ContaCorrente contaCorrente = new ContaCorrente(cliente, 500.00);                                         // Criação de uma conta corrente com saldo inicial para o cliente logado
            ContaBancaria contaSelecionada = null;
            boolean logado = true;

            System.out.println("Login realizado com sucesso! Bem-vindo, " + cliente.getNome() + "!");

            while (logado) {                                                                                        // Loop para exibir o menu de operações bancárias, permitindo que o usuário escolha entre as opções disponíveis e interaja com suas contas
                if (contaSelecionada == null) {
                    System.out.println("\nEscolha o tipo de conta: " +
                            "\n1 - Conta Poupança" +
                            "\n2 - Conta Corrente");
                    int opcaoConta = lerInt(scan, "Opção de conta: ");

                    if (opcaoConta == 1) {                                                                  // Seleção da conta poupança se o usuário escolher a opção 1
                        contaSelecionada = contaPoupanca;
                    } else if (opcaoConta == 2) {                                                           // Seleção da conta corrente se o usuário escolher a opção 2                                           
                        contaSelecionada = contaCorrente;
                    } else {
                        System.out.println("Opção inválida. Tente novamente.");
                        continue;
                    }

                    System.out.println("Conta selecionada: " + contaSelecionada);
                    contaSelecionada.calcularJuros();
                    contaSelecionada.calcularTaxa();
                    contaSelecionada.consultarSaldo();
                }

                System.out.println("\nEscolha entre as opções: " +                                          // Menu de operações bancárias, permitindo que o usuário escolha entre sacar, depositar, transferir, consultar saldo, trocar de conta ou fazer logout
                        "\n1 - Sacar" +
                        "\n2 - Depositar" +
                        "\n3 - Transferência entre contas" +
                        "\n4 - Consultar Saldo" +
                        "\n5 - Trocar de Conta" +
                        "\n6 - Logout");
                int opcao = lerInt(scan, "Opção do menu: ");

                try {
                    switch (opcao) {                                // Estrutura switch para executar a operação escolhida pelo usuário, com tratamento de exceções para garantir que erros sejam informados de forma clara e o programa continue funcionando sem travar
                        case 1 -> {
                            double valorSaque = lerDouble(scan, "Digite o valor para saque: ");
                            contaSelecionada.sacar(valorSaque);
                            contaSelecionada.consultarSaldo();
                        }
                        case 2 -> {
                            double valorDeposito = lerDouble(scan, "Digite o valor para depósito: ");
                            contaSelecionada.depositar(valorDeposito);
                            contaSelecionada.consultarSaldo();
                        }
                        case 3 -> {
                            double valorTransferencia = lerDouble(scan, "Digite o valor para transferência: ");
                            ContaBancaria destino = contaSelecionada == contaPoupanca ? contaCorrente : contaPoupanca;
                            destino.depositar(valorTransferencia, contaSelecionada);
                            System.out.println("Transferência concluída.");
                            System.out.println("Saldo conta origem:");
                            contaSelecionada.consultarSaldo();
                            System.out.println("Saldo conta destino:");
                            destino.consultarSaldo();
                        }
                        case 4 -> contaSelecionada.consultarSaldo();
                        case 5 -> {
                            contaSelecionada = null;
                            System.out.println("Escolha nova conta.");
                        }
                        case 6 -> {
                            System.out.println("Logout realizado. Volte sempre!");
                            logado = false;
                        }
                        default -> System.out.println("Opção inválida.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Erro: " + e.getMessage());
                }
            }

            System.out.println("\nDeseja iniciar sessão com outro usuário? (S/N)");                 // Pergunta ao usuário se deseja fazer login com outro usuário, permitindo que o programa continue rodando para múltiplos logins sem precisar ser reiniciado
            String resposta = scan.nextLine().trim().toUpperCase();
            if (!resposta.equals("S") && !resposta.equals("SIM")) {
                sairPrograma = true;
                System.out.println("Programa encerrado. Obrigado por usar o Sistema.");
            }
        }

        scan.close();                                   // Fechamento do scanner para liberar recursos, garantindo que o programa seja encerrado de forma limpa e sem vazamentos de memória
    }
}
 