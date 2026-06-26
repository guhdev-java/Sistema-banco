package br.com.banco.ui;

import br.com.banco.model.Cliente;
import br.com.banco.model.ContaBancaria;
import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import br.com.banco.service.BancoService;
import br.com.banco.util.FormatadorMoeda;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

public class BancoUI {

    private static final Color BG_TOP = new Color(10, 37, 64);
    private static final Color BG_BOTTOM = new Color(24, 76, 102);
    private static final Color CARD_BG = new Color(245, 249, 252);
    private static final Color PRIMARY = new Color(8, 104, 129);

    private final BancoService service = new BancoService();

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel root;

    private Cliente cliente;
    private ContaPoupanca contaPoupanca;
    private ContaCorrente contaCorrente;
    private ContaBancaria contaSelecionada;

    private JLabel tituloContaLabel;
    private JLabel cpfLabel;
    private JLabel saldoPrincipalLabel;
    private JLabel saldoPoupancaLabel;
    private JLabel saldoCorrenteLabel;
    private JLabel badgeContaAtiva;
    private JPanel cardPoupancaPanel;
    private JPanel cardCorrentePanel;
    private JTextArea historicoArea;
    private JTextField valorField;
    private JLabel toastLabel;
    private BigDecimal saldoExibido = BigDecimal.ZERO;

    private boolean houveSelecaoPoupanca;
    private boolean houveSelecaoCorrente;

    public void show() {
        configurarLookAndFeel();

        frame = new JFrame("Sistema Banco | Painel Digital");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(980, 640));

        cardLayout = new CardLayout();
        root = new JPanel(cardLayout);
        root.add(criarTelaLogin(), "login");
        root.add(criarTelaPainel(), "painel");

        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cardLayout.show(root, "login");
    }

    private JPanel criarTelaLogin() {
        GradientPanel panel = new GradientPanel(BG_TOP, BG_BOTTOM);
        panel.setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(430, 380));
        card.setBackground(new Color(255, 255, 255, 230));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 180), 1),
                BorderFactory.createEmptyBorder(24, 24, 20, 24)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titulo = new JLabel("Banco Aurora");
        titulo.setFont(new Font("Georgia", Font.BOLD, 30));
        titulo.setForeground(new Color(20, 50, 72));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titulo, gbc);

        gbc.gridy++;
        JLabel subtitulo = new JLabel("Acesso seguro e intuitivo");
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        subtitulo.setForeground(new Color(68, 90, 108));
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        card.add(subtitulo, gbc);

        gbc.gridy++;
        JTextField nomeField = new JTextField();
        nomeField.setBorder(BorderFactory.createTitledBorder("Nome"));
        card.add(nomeField, gbc);

        gbc.gridy++;
        JTextField cpfField = new JTextField();
        cpfField.setBorder(BorderFactory.createTitledBorder("CPF"));
        card.add(cpfField, gbc);

        gbc.gridy++;
        JPasswordField senhaField = new JPasswordField();
        senhaField.setBorder(BorderFactory.createTitledBorder("Senha"));
        card.add(senhaField, gbc);

        gbc.gridy++;
        JButton entrarBtn = criarBotao("Entrar");
        card.add(entrarBtn, gbc);

        gbc.gridy++;
        JLabel dica = new JLabel("Novo? Basta informar seus dados para criar conta");
        dica.setHorizontalAlignment(SwingConstants.CENTER);
        dica.setForeground(new Color(88, 106, 121));
        card.add(dica, gbc);

        entrarBtn.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String cpf = cpfField.getText().trim();
            String senha = new String(senhaField.getPassword()).trim();

            if (nome.isEmpty()) {
                mostrarErro("Informe o nome para continuar.");
                return;
            }
            if (senha.isEmpty()) {
                mostrarErro("Informe a senha para continuar.");
                return;
            }

            try {
                iniciarSessao(nome, cpf, senha);
                cardLayout.show(root, "painel");
                animarToast("Login realizado com sucesso.", new Color(28, 132, 82));
            } catch (IllegalArgumentException ex) {
                mostrarErro(ex.getMessage());
            }
        });

        iniciarAnimacaoTitulo(titulo);
        panel.add(card);
        return panel;
    }

    private JPanel criarTelaPainel() {
        GradientPanel painel = new GradientPanel(new Color(237, 244, 249), new Color(222, 236, 245));
        painel.setLayout(new BorderLayout(18, 18));
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel topo = new JPanel(new BorderLayout(10, 10));
        topo.setOpaque(false);

        JPanel tituloBox = new JPanel(new GridLayout(3, 1));
        tituloBox.setOpaque(false);

        tituloContaLabel = new JLabel("Visao Geral");
        tituloContaLabel.setFont(new Font("Georgia", Font.BOLD, 28));
        tituloContaLabel.setForeground(new Color(22, 52, 74));

        badgeContaAtiva = new JLabel("Conta ativa: nenhuma");
        badgeContaAtiva.setFont(new Font("SansSerif", Font.BOLD, 13));
        badgeContaAtiva.setForeground(new Color(57, 93, 117));

        cpfLabel = new JLabel("Titular: -");
        cpfLabel.setForeground(new Color(74, 96, 112));

        tituloBox.add(tituloContaLabel);
        tituloBox.add(badgeContaAtiva);
        tituloBox.add(cpfLabel);

        JPanel saldoBox = criarCard();
        saldoBox.setLayout(new GridLayout(2, 1));
        JLabel saldoTitulo = new JLabel("Saldo da conta selecionada");
        saldoTitulo.setForeground(new Color(62, 90, 108));
        saldoPrincipalLabel = new JLabel(FormatadorMoeda.formatar(BigDecimal.ZERO));
        saldoPrincipalLabel.setFont(new Font("Georgia", Font.BOLD, 28));
        saldoPrincipalLabel.setForeground(new Color(16, 70, 97));
        saldoBox.add(saldoTitulo);
        saldoBox.add(saldoPrincipalLabel);

        JPanel topoDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topoDireito.setOpaque(false);
        JButton logoutBtn = criarBotao("Logout");
        logoutBtn.addActionListener(e -> {
            service.salvarDados();
            limparSessao();
            cardLayout.show(root, "login");
            animarToast("Sessao encerrada.", new Color(138, 89, 29));
        });
        topoDireito.add(logoutBtn);

        topo.add(tituloBox, BorderLayout.WEST);
        topo.add(saldoBox, BorderLayout.CENTER);
        topo.add(topoDireito, BorderLayout.EAST);

        JPanel centro = new JPanel(new GridLayout(1, 2, 14, 14));
        centro.setOpaque(false);

        JPanel contasPanel = criarCard();
        contasPanel.setLayout(new BorderLayout(10, 10));
        JLabel contasTitulo = new JLabel("Seletor de contas");
        contasTitulo.setFont(new Font("Georgia", Font.BOLD, 20));
        contasTitulo.setForeground(new Color(26, 59, 84));
        contasPanel.add(contasTitulo, BorderLayout.NORTH);

        JPanel cardsContas = new JPanel(new GridLayout(2, 1, 10, 10));
        cardsContas.setOpaque(false);

        cardPoupancaPanel = criarCardConta("Conta Poupanca", "Rendimento automatico", () -> selecionarConta("poupanca"));
        saldoPoupancaLabel = (JLabel) cardPoupancaPanel.getClientProperty("saldoLabel");

        cardCorrentePanel = criarCardConta("Conta Corrente", "Movimentacao diaria", () -> selecionarConta("corrente"));
        saldoCorrenteLabel = (JLabel) cardCorrentePanel.getClientProperty("saldoLabel");

        cardsContas.add(cardPoupancaPanel);
        cardsContas.add(cardCorrentePanel);

        contasPanel.add(cardsContas, BorderLayout.CENTER);

        JPanel operacoesPanel = criarCard();
        operacoesPanel.setLayout(new BorderLayout(10, 12));
        JLabel opTitulo = new JLabel("Operacoes");
        opTitulo.setFont(new Font("Georgia", Font.BOLD, 20));
        opTitulo.setForeground(new Color(26, 59, 84));
        JLabel opSubtitulo = new JLabel("Escolha uma conta ativa e depois selecione a acao");
        opSubtitulo.setForeground(new Color(90, 110, 125));

        JPanel opHeader = new JPanel(new GridLayout(2, 1));
        opHeader.setOpaque(false);
        opHeader.add(opTitulo);
        opHeader.add(opSubtitulo);
        operacoesPanel.add(opHeader, BorderLayout.NORTH);

        JPanel form = new JPanel(new BorderLayout(0, 10));
        form.setOpaque(false);

        valorField = new JTextField();
        valorField.setBorder(BorderFactory.createTitledBorder("Valor (deposito, saque e transferencia)"));

        JButton saqueBtn = criarBotao("Sacar");
        JButton depositoBtn = criarBotao("Depositar");
        JButton transfBtn = criarBotao("Transferir entre contas");
        JButton saldoBtn = criarBotao("Consultar saldo");

        saqueBtn.addActionListener(e -> executarOperacao("saque"));
        depositoBtn.addActionListener(e -> executarOperacao("deposito"));
        transfBtn.addActionListener(e -> executarOperacao("transferencia"));
        saldoBtn.addActionListener(e -> executarOperacao("saldo"));

        JPanel botoes = new JPanel(new GridLayout(2, 2, 8, 8));
        botoes.setOpaque(false);
        botoes.add(depositoBtn);
        botoes.add(saqueBtn);
        botoes.add(transfBtn);
        botoes.add(saldoBtn);

        JLabel dicaOperacoes = new JLabel("Transferencia envia da conta ativa para a outra conta.");
        dicaOperacoes.setForeground(new Color(84, 105, 121));

        form.add(valorField, BorderLayout.NORTH);
        form.add(botoes, BorderLayout.CENTER);
        form.add(dicaOperacoes, BorderLayout.SOUTH);

        historicoArea = new JTextArea();
        historicoArea.setEditable(false);
        historicoArea.setLineWrap(true);
        historicoArea.setWrapStyleWord(true);
        historicoArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        historicoArea.setBackground(new Color(252, 254, 255));
        historicoArea.setBorder(BorderFactory.createTitledBorder("Historico da sessao"));

        JScrollPane scroll = new JScrollPane(historicoArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        operacoesPanel.add(form, BorderLayout.NORTH);
        operacoesPanel.add(scroll, BorderLayout.CENTER);

        centro.add(contasPanel);
        centro.add(operacoesPanel);

        toastLabel = new JLabel(" ", SwingConstants.CENTER);
        toastLabel.setOpaque(true);
        toastLabel.setVisible(false);
        toastLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        toastLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        painel.add(topo, BorderLayout.NORTH);
        painel.add(centro, BorderLayout.CENTER);
        painel.add(toastLabel, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarCardConta(String titulo, String descricao, Runnable acaoSelecionar) {
        JPanel card = criarCard();
        card.setLayout(new BorderLayout(8, 8));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        tituloLabel.setForeground(new Color(23, 57, 83));

        JLabel descricaoLabel = new JLabel(descricao);
        descricaoLabel.setForeground(new Color(76, 98, 114));

        JLabel saldoLabel = new JLabel(FormatadorMoeda.formatar(BigDecimal.ZERO));
        saldoLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        saldoLabel.setForeground(new Color(17, 88, 118));

        JButton selecionarBtn = criarBotao("Selecionar");
        selecionarBtn.addActionListener(e -> acaoSelecionar.run());
        selecionarBtn.setPreferredSize(new Dimension(140, 36));

        JPanel text = new JPanel(new GridLayout(3, 1));
        text.setOpaque(false);
        text.add(tituloLabel);
        text.add(descricaoLabel);
        text.add(saldoLabel);

        card.add(text, BorderLayout.CENTER);
        card.add(selecionarBtn, BorderLayout.SOUTH);
        card.putClientProperty("saldoLabel", saldoLabel);
        return card;
    }

    private JPanel criarCard() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(217, 227, 236), 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        return panel;
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(false);
        botao.setForeground(Color.WHITE);
        botao.setBackground(PRIMARY);
        botao.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        botao.setFont(new Font("SansSerif", Font.BOLD, 13));
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                botao.setBackground(PRIMARY.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                botao.setBackground(PRIMARY);
            }
        });
        return botao;
    }

    private void iniciarSessao(String nome, String cpf, String senha) {
        cliente = service.autenticar(nome, cpf, senha);
        contaPoupanca = service.getContaPoupanca(cliente);
        contaCorrente = service.getContaCorrente(cliente);
        contaSelecionada = null;
        houveSelecaoPoupanca = false;
        houveSelecaoCorrente = false;

        tituloContaLabel.setText("Bem-vindo, " + cliente.getNome());
        cpfLabel.setText("Titular: " + cliente.getNome() + " | CPF: " + cliente.getCpfFormatado());
        historicoArea.setText("");
        registrarHistorico("Sessao iniciada para " + cliente.getNome() + ".");

        atualizarSaldos(true);
        atualizarDestaqueConta();
    }

    private void selecionarConta(String tipo) {
        if ("poupanca".equals(tipo)) {
            contaSelecionada = contaPoupanca;
            badgeContaAtiva.setText("Conta ativa: Poupanca (" + contaPoupanca.getNumeroConta() + ")");
            if (!houveSelecaoPoupanca) {
                contaPoupanca.calcularJuros();
                contaPoupanca.calcularTaxa();
                houveSelecaoPoupanca = true;
                registrarHistorico("Poupanca selecionada. Juros iniciais aplicados.");
            } else {
                registrarHistorico("Poupanca selecionada.");
            }
        } else {
            contaSelecionada = contaCorrente;
            badgeContaAtiva.setText("Conta ativa: Corrente (" + contaCorrente.getNumeroConta() + ")");
            if (!houveSelecaoCorrente) {
                contaCorrente.calcularJuros();
                contaCorrente.calcularTaxa();
                houveSelecaoCorrente = true;
                registrarHistorico("Corrente selecionada. Taxa inicial aplicada quando possivel.");
            } else {
                registrarHistorico("Corrente selecionada.");
            }
        }
        atualizarSaldos(false);
        atualizarDestaqueConta();
        animarToast("Conta alterada com sucesso.", new Color(19, 101, 136));
    }

    private void executarOperacao(String tipo) {
        if (contaSelecionada == null) {
            mostrarErro("Selecione uma conta antes de operar.");
            return;
        }

        try {
            switch (tipo) {
                case "deposito" -> {
                    BigDecimal valor = lerValor();
                    contaSelecionada.depositar(valor);
                    registrarHistorico("Deposito de " + FormatadorMoeda.formatar(valor) + " realizado.");
                    animarToast("Deposito realizado.", new Color(28, 132, 82));
                    service.salvarDados();
                }
                case "saque" -> {
                    BigDecimal valor = lerValor();
                    contaSelecionada.sacar(valor);
                    registrarHistorico("Saque solicitado de " + FormatadorMoeda.formatar(valor) + ".");
                    animarToast("Saque realizado.", new Color(178, 95, 31));
                    service.salvarDados();
                }
                case "transferencia" -> {
                    BigDecimal valor = lerValor();
                    ContaBancaria destino = contaSelecionada == contaPoupanca ? contaCorrente : contaPoupanca;
                    contaSelecionada.transferir(valor, destino);
                    registrarHistorico("Transferencia de " + FormatadorMoeda.formatar(valor) + " para " + nomeConta(destino) + ".");
                    animarToast("Transferencia concluida.", new Color(32, 115, 129));
                    service.salvarDados();
                }
                case "saldo" -> {
                    registrarHistorico("Saldo atual de " + nomeConta(contaSelecionada) + ": " + FormatadorMoeda.formatar(contaSelecionada.getSaldo()) + ".");
                    animarToast("Saldo atualizado.", new Color(19, 101, 136));
                }
                default -> {}
            }

            atualizarSaldos(false);
            valorField.setText("");
        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private BigDecimal lerValor() {
        String texto = valorField.getText().trim().replace(',', '.');
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Informe um valor para a operacao.");
        }

        try {
            BigDecimal valor = new BigDecimal(texto);
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor deve ser maior que zero.");
            }
            return valor;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor invalido. Exemplo: 100.50");
        }
    }

    private void atualizarSaldos(boolean semAnimacao) {
        if (saldoPoupancaLabel == null) {
            return;
        }

        saldoPoupancaLabel.setText(FormatadorMoeda.formatar(contaPoupanca.getSaldo()));
        saldoCorrenteLabel.setText(FormatadorMoeda.formatar(contaCorrente.getSaldo()));

        BigDecimal alvo = contaSelecionada != null ? contaSelecionada.getSaldo() : BigDecimal.ZERO;
        if (semAnimacao) {
            saldoPrincipalLabel.setText(FormatadorMoeda.formatar(alvo));
            saldoExibido = alvo;
        } else {
            animarSaldo(alvo);
        }
    }

    private void animarSaldo(BigDecimal alvo) {
        final double inicio = saldoExibido.doubleValue();
        final double fim = alvo.doubleValue();
        if (Math.abs(inicio - fim) < 0.01) {
            saldoPrincipalLabel.setText(FormatadorMoeda.formatar(alvo));
            saldoExibido = alvo;
            return;
        }
        final int passos = 24;
        final int[] indice = {0};

        Timer timer = new Timer(18, null);
        timer.addActionListener(e -> {
            indice[0]++;
            double t = indice[0] / (double) passos;
            double eased = 1 - Math.pow(1 - t, 3);
            double valorAnimado = inicio + (fim - inicio) * eased;
            saldoPrincipalLabel.setText(FormatadorMoeda.formatar(BigDecimal.valueOf(valorAnimado).setScale(2, RoundingMode.HALF_EVEN)));

            if (indice[0] >= passos) {
                saldoPrincipalLabel.setText(FormatadorMoeda.formatar(alvo));
                saldoExibido = alvo;
                timer.stop();
            }
        });
        timer.start();
    }

    private String nomeConta(ContaBancaria conta) {
        return conta == contaPoupanca ? "Conta Poupanca" : "Conta Corrente";
    }

    private void registrarHistorico(String mensagem) {
        historicoArea.append("- " + mensagem + "\n");
        historicoArea.setCaretPosition(historicoArea.getDocument().getLength());
    }

    private void limparSessao() {
        cliente = null;
        contaPoupanca = null;
        contaCorrente = null;
        contaSelecionada = null;

        if (historicoArea != null) {
            historicoArea.setText("");
        }
        if (saldoPrincipalLabel != null) {
            saldoPrincipalLabel.setText(FormatadorMoeda.formatar(BigDecimal.ZERO));
        }
        saldoExibido = BigDecimal.ZERO;
        if (badgeContaAtiva != null) {
            badgeContaAtiva.setText("Conta ativa: nenhuma");
        }
        atualizarDestaqueConta();
    }

    private void atualizarDestaqueConta() {
        if (cardPoupancaPanel == null || cardCorrentePanel == null) {
            return;
        }

        Color normal = new Color(217, 227, 236);
        Color ativo = new Color(8, 104, 129);

        cardPoupancaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(contaSelecionada == contaPoupanca ? ativo : normal, contaSelecionada == contaPoupanca ? 2 : 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        cardCorrentePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(contaSelecionada == contaCorrente ? ativo : normal, contaSelecionada == contaCorrente ? 2 : 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(frame, mensagem, "Atencao", JOptionPane.WARNING_MESSAGE);
    }

    private void animarToast(String mensagem, Color cor) {
        if (toastLabel == null) {
            return;
        }

        toastLabel.setText(mensagem);
        toastLabel.setForeground(Color.WHITE);
        toastLabel.setBackground(cor);
        toastLabel.setVisible(true);

        Timer timer = new Timer(1800, e -> toastLabel.setVisible(false));
        timer.setRepeats(false);
        timer.start();
    }

    private void iniciarAnimacaoTitulo(JLabel titulo) {
        final int[] direcao = {1};
        final int[] brilho = {180};

        Timer timer = new Timer(35, e -> {
            brilho[0] += 2 * direcao[0];
            if (brilho[0] > 235 || brilho[0] < 150) {
                direcao[0] = -direcao[0];
            }
            titulo.setForeground(new Color(20, 50, 72, Math.max(120, Math.min(brilho[0], 245))));
        });
        timer.start();
    }

    private void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    private static class GradientPanel extends JPanel {
        private final Color inicio;
        private final Color fim;

        private GradientPanel(Color inicio, Color fim) {
            this.inicio = inicio;
            this.fim = fim;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, inicio, getWidth(), getHeight(), fim);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(255, 255, 255, 34));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(-80, -40, 380, 380);
            g2.drawOval(getWidth() - 280, getHeight() - 220, 340, 340);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BancoUI().show());
    }
}
