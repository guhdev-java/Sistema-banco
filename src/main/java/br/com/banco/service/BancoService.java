package br.com.banco.service;

import br.com.banco.model.Cliente;
import br.com.banco.model.ContaBancaria;
import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BancoService {
    private static final String DATA_FILE = "dados_bancarios.json";
    private static final String AGENCIA_PADRAO = "0001";

    private final Gson gson;
    private final List<Cliente> clientes;
    private final Map<String, List<ContaBancaria>> contasPorCliente;
    private final AtomicInteger proximoNumeroConta;

    public BancoService() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.clientes = new ArrayList<>();
        this.contasPorCliente = new HashMap<>();
        this.proximoNumeroConta = new AtomicInteger(1);
        carregarDados();
    }

    public Cliente autenticar(String nome, String cpf, String senha) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        Cliente existente = buscarPorCpf(cpfLimpo);
        if (existente != null) {
            if (!existente.verificarSenha(senha)) {
                throw new IllegalArgumentException("Senha incorreta.");
            }
            return existente;
        }
        return criarNovoCliente(nome, cpfLimpo, senha);
    }

    private Cliente criarNovoCliente(String nome, String cpf, String senha) {
        Cliente cliente = new Cliente(nome, cpf, senha);
        clientes.add(cliente);

        String numPoupanca = String.format("%04d-%d", proximoNumeroConta.getAndIncrement(), 1);
        String numCorrente = String.format("%04d-%d", proximoNumeroConta.getAndIncrement(), 2);

        ContaPoupanca poupanca = new ContaPoupanca(cliente, new BigDecimal("1000.00"), new BigDecimal("0.05"), numPoupanca, AGENCIA_PADRAO);
        ContaCorrente corrente = new ContaCorrente(cliente, new BigDecimal("500.00"), numCorrente, AGENCIA_PADRAO);

        List<ContaBancaria> contas = new ArrayList<>();
        contas.add(poupanca);
        contas.add(corrente);
        contasPorCliente.put(cliente.getId(), contas);

        salvarDados();
        return cliente;
    }

    public ContaPoupanca getContaPoupanca(Cliente cliente) {
        List<ContaBancaria> contas = contasPorCliente.get(cliente.getId());
        if (contas == null) return null;
        return (ContaPoupanca) contas.stream()
                .filter(c -> c instanceof ContaPoupanca)
                .findFirst().orElse(null);
    }

    public ContaCorrente getContaCorrente(Cliente cliente) {
        List<ContaBancaria> contas = contasPorCliente.get(cliente.getId());
        if (contas == null) return null;
        return (ContaCorrente) contas.stream()
                .filter(c -> c instanceof ContaCorrente)
                .findFirst().orElse(null);
    }

    public void salvarDados() {
        DadosBancarios dados = new DadosBancarios();
        dados.clientes = clientes;
        dados.proximoNumeroConta = proximoNumeroConta.get();

        for (Cliente c : clientes) {
            List<ContaBancaria> contas = contasPorCliente.get(c.getId());
            if (contas != null) {
                for (ContaBancaria conta : contas) {
                    DadosBancarios.ContaDTO dto = new DadosBancarios.ContaDTO();
                    dto.tipo = conta instanceof ContaPoupanca ? "POUPANCA" : "CORRENTE";
                    dto.numeroConta = conta.getNumeroConta();
                    dto.agencia = conta.getAgencia();
                    dto.saldo = conta.getSaldo();
                    dto.titularId = conta.getTitularId();
                    if (conta instanceof ContaPoupanca cp) {
                        dto.taxaJuros = cp.getTaxaJuros();
                    }
                    dados.contas.add(dto);
                }
            }
        }

        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            gson.toJson(dados, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    private void carregarDados() {
        File arquivo = new File(DATA_FILE);
        if (!arquivo.exists()) return;

        try (FileReader reader = new FileReader(arquivo)) {
            DadosBancarios dados = gson.fromJson(reader, DadosBancarios.class);
            if (dados == null) return;

            this.proximoNumeroConta.set(dados.proximoNumeroConta);
            this.clientes.clear();
            this.contasPorCliente.clear();

            Map<String, Cliente> clientesMap = new HashMap<>();
            if (dados.clientes != null) {
                this.clientes.addAll(dados.clientes);
                for (Cliente c : dados.clientes) {
                    clientesMap.put(c.getId(), c);
                }
            }

            if (dados.contas != null) {
                for (DadosBancarios.ContaDTO dto : dados.contas) {
                    Cliente titular = clientesMap.get(dto.titularId);
                    if (titular == null) continue;

                    ContaBancaria conta;
                    if ("POUPANCA".equals(dto.tipo)) {
                        ContaPoupanca cp = new ContaPoupanca(titular, dto.saldo,
                                dto.taxaJuros != null ? dto.taxaJuros : new BigDecimal("0.05"),
                                dto.numeroConta, dto.agencia);
                        conta = cp;
                    } else {
                        conta = new ContaCorrente(titular, dto.saldo, dto.numeroConta, dto.agencia);
                    }
                    conta.setTitular(titular);

                    contasPorCliente.computeIfAbsent(dto.titularId, k -> new ArrayList<>()).add(conta);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados: " + e.getMessage());
        }
    }

    private Cliente buscarPorCpf(String cpf) {
        return clientes.stream()
                .filter(c -> c.getCpf().equals(cpf))
                .findFirst().orElse(null);
    }

    private static class DadosBancarios {
        List<Cliente> clientes = new ArrayList<>();
        List<ContaDTO> contas = new ArrayList<>();
        int proximoNumeroConta = 1;

        static class ContaDTO {
            String tipo;
            String numeroConta;
            String agencia;
            BigDecimal saldo;
            String titularId;
            BigDecimal taxaJuros;
        }
    }
}
