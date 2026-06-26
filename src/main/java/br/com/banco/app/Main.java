package br.com.banco.app;

import br.com.banco.model.Cliente;
import br.com.banco.model.ContaBancaria;
import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import br.com.banco.service.BancoService;
import br.com.banco.ui.BancoUI;
import br.com.banco.util.FormatadorMoeda;
import java.math.BigDecimal;
import java.util.Scanner;
import javax.swing.SwingUtilities;

public class Main {

    private static String lerString(Scanner scan, String prompt) {
        System.out.print(prompt);
        return scan.nextLine().trim();
    }

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

    private static BigDecimal lerBigDecimal(Scanner scan, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String linha = scan.nextLine().trim().replace(',', '.');
                BigDecimal valor = new BigDecimal(linha);
                if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Valor deve ser maior que zero.");
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número válido (ex: 100.50).");
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 0 || !"--cli".equalsIgnoreCase(args[0])) {
            SwingUtilities.invokeLater(() -> new BancoUI().show());
            return;
        }
        executarModoConsole();
    }

    private static void executarModoConsole() {
        Scanner scan = new Scanner(System.in);
        BancoService service = new BancoService();
        boolean sairPrograma = false;

        while (!sairPrograma) {
            String nomeLogin = lerString(scan, "Digite seu nome: ");
            if (nomeLogin.isEmpty()) {
                System.out.println("Nome não pode ser vazio.");
                continue;
            }

            String cpfLogin = lerString(scan, "Digite seu CPF: ");
            String senhaLogin = lerString(scan, "Digite sua senha: ");

            Cliente cliente;
            try {
                cliente = service.autenticar(nomeLogin, cpfLogin, senhaLogin);
            } catch (IllegalArgumentException e) {
                System.out.println("Erro no login: " + e.getMessage());
                continue;
            }

            ContaPoupanca contaPoupanca = service.getContaPoupanca(cliente);
            ContaCorrente contaCorrente = service.getContaCorrente(cliente);
            ContaBancaria contaSelecionada = null;
            boolean logado = true;

            System.out.println("Login realizado! Bem-vindo, " + cliente.getNome() + "!");

            while (logado) {
                if (contaSelecionada == null) {
                    System.out.println("\nEscolha o tipo de conta:" +
                            "\n1 - Conta Poupança (" + contaPoupanca.getNumeroConta() + ")" +
                            "\n2 - Conta Corrente (" + contaCorrente.getNumeroConta() + ")");
                    int opcaoConta = lerInt(scan, "Opção de conta: ");

                    if (opcaoConta == 1) {
                        contaSelecionada = contaPoupanca;
                    } else if (opcaoConta == 2) {
                        contaSelecionada = contaCorrente;
                    } else {
                        System.out.println("Opção inválida.");
                        continue;
                    }

                    System.out.println("Conta selecionada: " + contaSelecionada);
                    contaSelecionada.calcularJuros();
                    contaSelecionada.calcularTaxa();
                    System.out.println("Saldo atual: " + FormatadorMoeda.formatar(contaSelecionada.getSaldo()));
                }

                System.out.println("\nEscolha entre as opções:" +
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
                            BigDecimal valorSaque = lerBigDecimal(scan, "Digite o valor para saque: ");
                            contaSelecionada.sacar(valorSaque);
                            System.out.println("Saque de " + FormatadorMoeda.formatar(valorSaque) + " realizado.");
                            System.out.println("Saldo atual: " + FormatadorMoeda.formatar(contaSelecionada.getSaldo()));
                            service.salvarDados();
                        }
                        case 2 -> {
                            BigDecimal valorDeposito = lerBigDecimal(scan, "Digite o valor para depósito: ");
                            contaSelecionada.depositar(valorDeposito);
                            System.out.println("Depósito de " + FormatadorMoeda.formatar(valorDeposito) + " realizado.");
                            System.out.println("Saldo atual: " + FormatadorMoeda.formatar(contaSelecionada.getSaldo()));
                            service.salvarDados();
                        }
                        case 3 -> {
                            BigDecimal valorTransferencia = lerBigDecimal(scan, "Digite o valor para transferência: ");
                            ContaBancaria destino = contaSelecionada == contaPoupanca ? contaCorrente : contaPoupanca;
                            contaSelecionada.transferir(valorTransferencia, destino);
                            System.out.println("Transferência de " + FormatadorMoeda.formatar(valorTransferencia) + " concluída.");
                            System.out.println("Saldo conta origem: " + FormatadorMoeda.formatar(contaSelecionada.getSaldo()));
                            System.out.println("Saldo conta destino: " + FormatadorMoeda.formatar(destino.getSaldo()));
                            service.salvarDados();
                        }
                        case 4 -> System.out.println("Saldo atual: " + FormatadorMoeda.formatar(contaSelecionada.getSaldo()));
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

            System.out.print("\nDeseja iniciar sessão com outro usuário? (S/N): ");
            String resposta = scan.nextLine().trim().toUpperCase();
            if (!resposta.equals("S") && !resposta.equals("SIM")) {
                sairPrograma = true;
                System.out.println("Programa encerrado. Obrigado por usar o Sistema.");
            }
        }
        scan.close();
    }
}
