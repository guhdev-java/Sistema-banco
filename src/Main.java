import java.math.BigDecimal;
import java.util.Scanner;

public class Main {

    private static int lerInt(Scanner scan, String prompt) {
        while (true) {                                                                      
            System.out.print(prompt);
            try {
                int valor = scan.nextInt();
                scan.nextLine();
                return valor;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
                scan.nextLine();                                                            
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

        boolean sairPrograma = false;                                                           

        while (!sairPrograma) {
            System.out.print("Digite seu nome para login: ");
            String nomeLogin = scan.nextLine().trim();

            Cliente cliente = null;                                                             
            while (cliente == null) {
                System.out.print("Digite seu CPF para login (11 dígitos): ");
                String cpfLogin = scan.nextLine().trim();
                try {
                    cliente = new Cliente(nomeLogin, cpfLogin);
                } catch (IllegalArgumentException e) {
                    System.out.println("CPF inválido: " + e.getMessage() + " Tente novamente.");
                }
            }

            ContaPoupanca contaPoupanca = new ContaPoupanca(cliente, BigDecimal.valueOf(1000.00), BigDecimal.valueOf(0.05));                        
            ContaCorrente contaCorrente = new ContaCorrente(cliente, BigDecimal.valueOf(500.00));                                         
            ContaBancaria contaSelecionada = null;
            boolean logado = true;

            System.out.println("Login realizado com sucesso! Bem-vindo, " + cliente.getNome() + "!");

            while (logado) {                                                                                        
                if (contaSelecionada == null) {
                    System.out.println("\nEscolha o tipo de conta: " +
                            "\n1 - Conta Poupança" +
                            "\n2 - Conta Corrente");
                    int opcaoConta = lerInt(scan, "Opção de conta: ");

                    if (opcaoConta == 1) {                                                                  
                        contaSelecionada = contaPoupanca;
                    } else if (opcaoConta == 2) {                                                                                                      
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

                System.out.println("\nEscolha entre as opções: " +                                          
                        "\n1 - Sacar" +
                        "\n2 - Depositar" +
                        "\n3 - Transferência entre contas" +
                        "\n4 - Consultar Saldo" +
                        "\n5 - Trocar de Conta" +
                        "\n6 - Logout");
                int opcao = lerInt(scan, "Opção do menu: ");

                try {
                    switch (opcao) {                                
                        case 1 -> {
                            double valorSaque = lerDouble(scan, "Digite o valor para saque: ");
                            contaSelecionada.sacar(BigDecimal.valueOf(valorSaque));
                            contaSelecionada.consultarSaldo();
                        }
                        case 2 -> {
                            double valorDeposito = lerDouble(scan, "Digite o valor para depósito: ");
                            contaSelecionada.depositar(BigDecimal.valueOf(valorDeposito));
                            contaSelecionada.consultarSaldo();
                        }
                        case 3 -> {
                            double valorTransferencia = lerDouble(scan, "Digite o valor para transferência: ");
                            ContaBancaria destino = contaSelecionada == contaPoupanca ? contaCorrente : contaPoupanca;
                            destino.depositar(BigDecimal.valueOf(valorTransferencia), contaSelecionada);
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

            System.out.println("\nDeseja iniciar sessão com outro usuário? (S/N)");                 
            String resposta = scan.nextLine().trim().toUpperCase();
            if (!resposta.equals("S") && !resposta.equals("SIM")) {
                sairPrograma = true;
                System.out.println("Programa encerrado. Obrigado por usar o Sistema.");
            }
        }

        scan.close();                                   
    }
}
 