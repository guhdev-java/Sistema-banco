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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class BancoUI {

    private static final Color PURPLE_DEEP = new Color(10, 4, 22);
    private static final Color PURPLE_MID = new Color(26, 10, 48);
    private static final Color PURPLE_ACCENT = new Color(130, 50, 210);
    private static final Color PURPLE_ACCENT2 = new Color(90, 30, 150);
    private static final Color PINK_ACCENT = new Color(230, 70, 150);
    private static final Color CARD_BORDER = new Color(55, 25, 95);
    private static final Color TEXT_PRIMARY = new Color(245, 240, 255);
    private static final Color TEXT_SECONDARY = new Color(185, 168, 212);
    private static final Color TEXT_MUTED = new Color(130, 108, 162);
    private static final Color GREEN_UP = new Color(0, 200, 120);
    private static final Color RED_DOWN = new Color(240, 80, 90);

    private final BancoService service = new BancoService();

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel root;
    private JPanel glassPane;
    private JLabel toastLabel;
    private Timer toastTimer;
    private Point mouseOffset;

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
    private BigDecimal saldoExibido = BigDecimal.ZERO;
    private JLabel avatarLabel;
    private JPanel contaIndicator;

    private boolean houveSelecaoPoupanca;
    private boolean houveSelecaoCorrente;

    private Font fontInter;
    private Font fontInterBold;
    private Font fontInterSemi;

    public void show() {
        configurarLookAndFeel();
        carregarFontes();

        frame = new JFrame("Banco Aurora");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1080, 740));
        frame.setUndecorated(true);

        cardLayout = new CardLayout();
        root = new JPanel(cardLayout);

        JPanel login = criarTelaLogin();
        JPanel painel = criarTelaPainel();
        root.add(login, "login");
        root.add(painel, "painel");

        glassPane = new JPanel(new GridBagLayout());
        glassPane.setOpaque(false);
        glassPane.setVisible(false);

        toastLabel = new JLabel("", SwingConstants.CENTER);
        toastLabel.setOpaque(true);
        toastLabel.setVisible(false);
        toastLabel.setFont(fontInterSemi.deriveFont(13f));
        toastLabel.setBorder(BorderFactory.createEmptyBorder(14, 32, 14, 32));

        RoundedPanel toastBg = new RoundedPanel(16, new Color(0, 0, 0, 0), new Color(0, 0, 0, 0));
        toastBg.setLayout(new BorderLayout());
        toastBg.add(toastLabel, BorderLayout.CENTER);

        glassPane.add(toastBg);
        frame.setGlassPane(glassPane);

        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        cardLayout.show(root, "login");
        ((ParticlePanel) login).iniciarAnimacao();
    }

    private void carregarFontes() {
        try {
            InputStream is = getClass().getResourceAsStream("/fonts/Inter-Regular.ttf");
            if (is != null) fontInter = Font.createFont(Font.TRUETYPE_FONT, is);
            is = getClass().getResourceAsStream("/fonts/Inter-Bold.ttf");
            if (is != null) fontInterBold = Font.createFont(Font.TRUETYPE_FONT, is);
            is = getClass().getResourceAsStream("/fonts/Inter-SemiBold.ttf");
            if (is != null) fontInterSemi = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException ignored) {}
        if (fontInter == null) fontInter = new Font("SansSerif", Font.PLAIN, 14);
        if (fontInterBold == null) fontInterBold = new Font("SansSerif", Font.BOLD, 14);
        if (fontInterSemi == null) fontInterSemi = new Font("SansSerif", Font.BOLD, 14);
    }

    private JPanel criarTelaLogin() {
        ParticlePanel panel = new ParticlePanel(PURPLE_DEEP, new Color(18, 7, 36));
        panel.setLayout(new GridBagLayout());

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        RoundedPanel card = new RoundedPanel(24, new Color(20, 8, 40), new Color(30, 14, 55));
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 35, 150, 80), 1),
                BorderFactory.createEmptyBorder(44, 40, 36, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel logo = new JLabel("✦", SwingConstants.CENTER);
        logo.setFont(fontInterBold.deriveFont(44f));
        logo.setForeground(PINK_ACCENT);
        card.add(logo, gbc);

        gbc.gridy++;
        JLabel titulo = new JLabel("Aurora", SwingConstants.CENTER);
        titulo.setFont(fontInterBold.deriveFont(34f));
        titulo.setForeground(TEXT_PRIMARY);
        card.add(titulo, gbc);

        gbc.gridy++;
        JLabel sub = new JLabel("Experiencia financeira do futuro", SwingConstants.CENTER);
        sub.setForeground(TEXT_SECONDARY);
        sub.setFont(fontInter.deriveFont(13f));
        card.add(sub, gbc);

        gbc.insets = new Insets(16, 5, 5, 5);
        gbc.gridy++;
        JTextField nomeField = criarCampo("Seu nome completo");
        card.add(nomeField, gbc);

        gbc.insets = new Insets(6, 5, 5, 5);
        gbc.gridy++;
        JTextField cpfField = criarCampo("CPF (apenas numeros)");
        card.add(cpfField, gbc);

        gbc.gridy++;
        JPasswordField senhaField = criarCampoSenha("Crie uma senha");
        card.add(senhaField, gbc);

        gbc.insets = new Insets(18, 5, 5, 5);
        gbc.gridy++;
        JButton entrarBtn = criarBotaoGradiente("Entrar");
        card.add(entrarBtn, gbc);

        gbc.insets = new Insets(6, 5, 5, 5);
        gbc.gridy++;
        JLabel dica = new JLabel("Novo? Cadastre-se automaticamente ao entrar", SwingConstants.CENTER);
        dica.setForeground(TEXT_MUTED);
        dica.setFont(fontInter.deriveFont(11f));
        card.add(dica, gbc);

        entrarBtn.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String cpf = cpfField.getText().trim();
            String senha = new String(senhaField.getPassword()).trim();
            if (nome.isEmpty() || senha.isEmpty()) {
                mostrarErro("Preencha todos os campos");
                return;
            }
            try {
                iniciarSessao(nome, cpf, senha);
                cardLayout.show(root, "painel");
                animarToast("Bem-vindo(a), " + nome.split(" ")[0] + "!", GREEN_UP);
            } catch (IllegalArgumentException ex) {
                mostrarErro(ex.getMessage());
            }
        });

        wrapper.add(card);
        panel.add(wrapper);
        return panel;
    }

    private JPanel criarTelaPainel() {
        RoundedPanel painel = new RoundedPanel(0, new Color(12, 4, 26), new Color(20, 8, 40));
        painel.setLayout(new BorderLayout(0, 0));

        painel.add(criarBarraSuperior(), BorderLayout.NORTH);

        JPanel corpo = new JPanel(new BorderLayout(0, 12));
        corpo.setOpaque(false);
        corpo.setBorder(BorderFactory.createEmptyBorder(4, 28, 20, 28));

        corpo.add(criarCabecalhoPainel(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 18, 0));
        centro.setOpaque(false);
        centro.add(criarPainelContas());
        centro.add(criarPainelOperacoes());
        corpo.add(centro, BorderLayout.CENTER);

        painel.add(corpo, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setPreferredSize(new Dimension(0, 40));
        barra.setBackground(new Color(8, 3, 18));

        JLabel tituloJanela = new JLabel("  ✦ Aurora");
        tituloJanela.setFont(fontInterSemi.deriveFont(12f));
        tituloJanela.setForeground(TEXT_MUTED);
        barra.add(tituloJanela, BorderLayout.WEST);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        botoes.setOpaque(false);

        JButton minBtn = criarBotaoJanela("─", e -> frame.setState(java.awt.Frame.ICONIFIED));
        JButton closeBtn = criarBotaoJanela("✕", e -> {
            service.salvarDados();
            System.exit(0);
        });

        botoes.add(minBtn);
        botoes.add(closeBtn);
        barra.add(botoes, BorderLayout.EAST);

        MouseAdapter drag = new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseOffset = e.getPoint(); }
            public void mouseDragged(MouseEvent e) {
                if (mouseOffset != null) {
                    Point p = frame.getLocation();
                    frame.setLocation(p.x + e.getX() - mouseOffset.x, p.y + e.getY() - mouseOffset.y);
                }
            }
        };
        barra.addMouseListener(drag);
        barra.addMouseMotionListener(drag);

        return barra;
    }

    private JButton criarBotaoJanela(String texto, java.awt.event.ActionListener acao) {
        JButton btn = new JButton(texto) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) g2.setColor(new Color(255, 255, 255, 30));
                else g2.setColor(new Color(0, 0, 0, 0));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(TEXT_MUTED);
                g2.setFont(fontInter.deriveFont(14f));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() / 2) / 2 - 1;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(46, 40));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.addActionListener(acao);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel criarCabecalhoPainel() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);

        avatarLabel = new JLabel("A", SwingConstants.CENTER);
        avatarLabel.setFont(fontInterBold.deriveFont(20f));
        avatarLabel.setForeground(TEXT_PRIMARY);
        avatarLabel.setPreferredSize(new Dimension(48, 48));

        JPanel avatarWrap = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PURPLE_ACCENT, getWidth(), getHeight(), PINK_ACCENT);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatarWrap.setPreferredSize(new Dimension(48, 48));
        avatarWrap.add(avatarLabel, BorderLayout.CENTER);

        JPanel textoBox = new JPanel(new GridLayout(2, 1));
        textoBox.setOpaque(false);
        textoBox.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        tituloContaLabel = new JLabel("Visao Geral");
        tituloContaLabel.setFont(fontInterBold.deriveFont(24f));
        tituloContaLabel.setForeground(TEXT_PRIMARY);

        cpfLabel = new JLabel("Titular: -");
        cpfLabel.setForeground(TEXT_SECONDARY);
        cpfLabel.setFont(fontInter.deriveFont(13f));
        textoBox.add(tituloContaLabel);
        textoBox.add(cpfLabel);

        JPanel esquerda = new JPanel(new BorderLayout(12, 0));
        esquerda.setOpaque(false);
        esquerda.add(avatarWrap, BorderLayout.WEST);
        esquerda.add(textoBox, BorderLayout.CENTER);

        RoundedPanel saldoCard = new RoundedPanel(16, new Color(25, 10, 50), new Color(18, 7, 36));
        saldoCard.setLayout(new BorderLayout(0, 2));
        saldoCard.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

        JPanel saldoLinha = new JPanel(new BorderLayout(10, 0));
        saldoLinha.setOpaque(false);
        JLabel saldoTitulo = new JLabel("Saldo disponivel");
        saldoTitulo.setForeground(TEXT_SECONDARY);
        saldoTitulo.setFont(fontInter.deriveFont(12f));
        saldoPrincipalLabel = new JLabel(FormatadorMoeda.formatar(BigDecimal.ZERO));
        saldoPrincipalLabel.setFont(fontInterBold.deriveFont(28f));
        saldoPrincipalLabel.setForeground(TEXT_PRIMARY);
        saldoLinha.add(saldoTitulo, BorderLayout.WEST);
        saldoLinha.add(saldoPrincipalLabel, BorderLayout.EAST);

        badgeContaAtiva = new JLabel("nenhuma conta selecionada");
        badgeContaAtiva.setForeground(TEXT_MUTED);
        badgeContaAtiva.setFont(fontInter.deriveFont(11f));

        saldoCard.add(saldoLinha, BorderLayout.CENTER);
        saldoCard.add(badgeContaAtiva, BorderLayout.SOUTH);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        direita.setOpaque(false);
        JButton logoutBtn = criarBotaoSimples("Sair");
        logoutBtn.addActionListener(e -> {
            service.salvarDados();
            limparSessao();
            cardLayout.show(root, "login");
            animarToast("Sessao encerrada", RED_DOWN);
        });
        direita.add(logoutBtn);

        header.add(esquerda, BorderLayout.WEST);
        header.add(saldoCard, BorderLayout.CENTER);
        header.add(direita, BorderLayout.EAST);

        return header;
    }

    private JPanel criarPainelContas() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        JLabel titulo = new JLabel("Suas contas");
        titulo.setFont(fontInterSemi.deriveFont(17f));
        titulo.setForeground(TEXT_PRIMARY);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(2, 1, 0, 14));
        cards.setOpaque(false);

        cardPoupancaPanel = criarCardConta("Poupanca", "Rende automaticamente", "★", new Color(230, 70, 150), "poupanca");
        saldoPoupancaLabel = (JLabel) cardPoupancaPanel.getClientProperty("saldoLabel");

        cardCorrentePanel = criarCardConta("Corrente", "Dia a dia", "◆", new Color(130, 50, 210), "corrente");
        saldoCorrenteLabel = (JLabel) cardCorrentePanel.getClientProperty("saldoLabel");

        cards.add(cardPoupancaPanel);
        cards.add(cardCorrentePanel);
        panel.add(cards, BorderLayout.CENTER);

        contaIndicator = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        contaIndicator.setOpaque(false);
        JLabel dica = new JLabel("Clique em uma conta para ativa-la");
        dica.setFont(fontInter.deriveFont(11f));
        dica.setForeground(TEXT_MUTED);
        contaIndicator.add(dica);
        panel.add(contaIndicator, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelOperacoes() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        JLabel titulo = new JLabel("Operacoes");
        titulo.setFont(fontInterSemi.deriveFont(17f));
        titulo.setForeground(TEXT_PRIMARY);
        panel.add(titulo, BorderLayout.NORTH);

        RoundedPanel formCard = new RoundedPanel(18, new Color(22, 9, 42), new Color(16, 6, 32));
        formCard.setLayout(new BorderLayout(0, 14));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JPanel opsHeader = new JPanel(new BorderLayout(0, 4));
        opsHeader.setOpaque(false);
        JLabel opsSub = new JLabel("Selecione uma conta e realize suas acoes");
        opsSub.setForeground(TEXT_SECONDARY);
        opsSub.setFont(fontInter.deriveFont(12f));
        opsHeader.add(opsSub, BorderLayout.CENTER);

        valorField = criarCampo("Valor (ex: 250.00)");
        valorField.setFont(fontInter.deriveFont(14f));

        JPanel gradeBotoes = new JPanel(new GridLayout(2, 2, 10, 10));
        gradeBotoes.setOpaque(false);

        JButton depBtn = criarBotaoGradiente("Depositar");
        JButton saqBtn = criarBotaoSimples("Sacar");
        JButton trfBtn = criarBotaoSimples("Transferir");
        JButton salBtn = criarBotaoSimples("Saldo");

        depBtn.addActionListener(e -> executarOperacao("deposito"));
        saqBtn.addActionListener(e -> executarOperacao("saque"));
        trfBtn.addActionListener(e -> executarOperacao("transferencia"));
        salBtn.addActionListener(e -> executarOperacao("saldo"));

        gradeBotoes.add(depBtn);
        gradeBotoes.add(saqBtn);
        gradeBotoes.add(trfBtn);
        gradeBotoes.add(salBtn);

        JLabel dicaOp = new JLabel("Transferencia: da conta ativa para a outra");
        dicaOp.setForeground(TEXT_MUTED);
        dicaOp.setFont(fontInter.deriveFont(11f));

        formCard.add(opsHeader, BorderLayout.NORTH);
        formCard.add(valorField, BorderLayout.CENTER);
        formCard.add(gradeBotoes, BorderLayout.SOUTH);

        historicoArea = new JTextArea();
        historicoArea.setEditable(false);
        historicoArea.setLineWrap(true);
        historicoArea.setWrapStyleWord(true);
        historicoArea.setFont(fontInter.deriveFont(12f));
        historicoArea.setForeground(TEXT_SECONDARY);
        historicoArea.setBackground(new Color(16, 6, 32));
        historicoArea.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        historicoArea.setSelectionColor(new Color(130, 50, 210, 80));

        JScrollPane scroll = new JScrollPane(historicoArea);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                BorderFactory.createEmptyBorder()));
        scroll.getVerticalScrollBar().setBackground(PURPLE_DEEP);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            protected void configureScrollBarColors() {
                this.thumbColor = PURPLE_ACCENT2;
                this.trackColor = PURPLE_DEEP;
            }
            protected JButton createDecreaseButton(int o) { return botaoScrollVazio(); }
            protected JButton createIncreaseButton(int o) { return botaoScrollVazio(); }
            protected void paintThumb(Graphics g, JComponent c, java.awt.Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(r.x + 2, r.y, r.width - 4, r.height, 5, 5);
                g2.dispose();
            }
        });

        JPanel bottom = new JPanel(new BorderLayout(0, 10));
        bottom.setOpaque(false);
        bottom.add(formCard, BorderLayout.NORTH);
        bottom.add(scroll, BorderLayout.CENTER);

        panel.add(bottom, BorderLayout.CENTER);
        return panel;
    }

    private JButton botaoScrollVazio() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        return b;
    }

    private JPanel criarCardConta(String nome, String desc, String icone, Color corIcone, String tipo) {
        RoundedPanel card = new RoundedPanel(18, new Color(26, 11, 50), new Color(20, 8, 40));
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel topo = new JPanel(new BorderLayout(12, 0));
        topo.setOpaque(false);

        JLabel iconeLb = new JLabel(icone);
        iconeLb.setFont(fontInter.deriveFont(26f));
        iconeLb.setForeground(corIcone);

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        JLabel nomeLb = new JLabel(nome);
        nomeLb.setFont(fontInterSemi.deriveFont(16f));
        nomeLb.setForeground(TEXT_PRIMARY);
        JLabel descLb = new JLabel(desc);
        descLb.setForeground(TEXT_MUTED);
        descLb.setFont(fontInter.deriveFont(12f));
        textos.add(nomeLb);
        textos.add(descLb);

        topo.add(iconeLb, BorderLayout.WEST);
        topo.add(textos, BorderLayout.CENTER);

        JLabel saldoLb = new JLabel(FormatadorMoeda.formatar(BigDecimal.ZERO));
        saldoLb.setFont(fontInterBold.deriveFont(20f));
        saldoLb.setForeground(TEXT_PRIMARY);
        saldoLb.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel statusDot = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 30, 110));
                g2.fillOval(2, 2, 8, 8);
                g2.dispose();
            }
        };
        statusDot.setPreferredSize(new Dimension(12, 12));
        statusDot.setOpaque(false);

        JPanel rodape = new JPanel(new BorderLayout(10, 0));
        rodape.setOpaque(false);
        rodape.add(statusDot, BorderLayout.WEST);
        rodape.add(saldoLb, BorderLayout.CENTER);

        card.add(topo, BorderLayout.NORTH);
        card.add(rodape, BorderLayout.SOUTH);

        card.putClientProperty("tipo", tipo);
        card.putClientProperty("saldoLabel", saldoLb);
        card.putClientProperty("statusDot", statusDot);

        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selecionarConta(tipo); }
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(100, 45, 180), 1),
                        BorderFactory.createEmptyBorder(18, 20, 18, 20)));
            }
            public void mouseExited(MouseEvent e) {
                boolean ativa = (tipo.equals("poupanca") && contaSelecionada == contaPoupanca) ||
                                (tipo.equals("corrente") && contaSelecionada == contaCorrente);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ativa ? PURPLE_ACCENT : CARD_BORDER, ativa ? 2 : 1),
                        BorderFactory.createEmptyBorder(18, 20, 18, 20)));
            }
        });

        return card;
    }

    private JTextField criarCampo(String placeholder) {
        JTextField f = new JTextField() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(45, 20, 80, 100));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(PINK_ACCENT);
        f.setFont(fontInter.deriveFont(13f));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 30, 140, 80), 1),
                BorderFactory.createEmptyBorder(11, 14, 11, 14)));
        f.setBackground(new Color(0, 0, 0, 0));
        f.setSelectionColor(new Color(130, 50, 210, 100));
        f.putClientProperty("placeholder", placeholder);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PURPLE_ACCENT, 2),
                        BorderFactory.createEmptyBorder(10, 13, 10, 13)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 30, 140, 80), 1),
                        BorderFactory.createEmptyBorder(11, 14, 11, 14)));
            }
        });
        return f;
    }

    private JPasswordField criarCampoSenha(String ph) {
        JPasswordField f = new JPasswordField() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(45, 20, 80, 100));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(PINK_ACCENT);
        f.setFont(fontInter.deriveFont(13f));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 30, 140, 80), 1),
                BorderFactory.createEmptyBorder(11, 14, 11, 14)));
        f.setEchoChar('●');
        f.setSelectionColor(new Color(130, 50, 210, 100));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PURPLE_ACCENT, 2),
                        BorderFactory.createEmptyBorder(10, 13, 10, 13)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 30, 140, 80), 1),
                        BorderFactory.createEmptyBorder(11, 14, 11, 14)));
            }
        });
        return f;
    }

    private JButton criarBotaoGradiente(String texto) {
        JButton btn = new JButton(texto) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setPaint(new GradientPaint(0, 0, PURPLE_ACCENT2, getWidth(), getHeight(), new Color(180, 50, 120)));
                } else if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, PINK_ACCENT, getWidth(), getHeight(), PURPLE_ACCENT));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, PURPLE_ACCENT, getWidth(), getHeight(), PINK_ACCENT));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Color.WHITE);
                g2.setFont(fontInterSemi.deriveFont(13f));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() / 2) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(0, 46));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton criarBotaoSimples(String texto) {
        JButton btn = new JButton(texto) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(35, 15, 65, 220));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(50, 22, 95, 200));
                } else {
                    g2.setColor(new Color(40, 16, 78, 160));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(PURPLE_ACCENT.getRed(), PURPLE_ACCENT.getGreen(), PURPLE_ACCENT.getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(TEXT_PRIMARY);
                g2.setFont(fontInterSemi.deriveFont(12f));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() / 2) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(0, 44));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void iniciarSessao(String nome, String cpf, String senha) {
        cliente = service.autenticar(nome, cpf, senha);
        contaPoupanca = service.getContaPoupanca(cliente);
        contaCorrente = service.getContaCorrente(cliente);
        contaSelecionada = null;
        houveSelecaoPoupanca = false;
        houveSelecaoCorrente = false;

        String inicial = cliente.getNome().trim().isEmpty() ? "?" : String.valueOf(cliente.getNome().charAt(0)).toUpperCase();
        avatarLabel.setText(inicial);
        tituloContaLabel.setText("Bem-vindo, " + cliente.getNome().split(" ")[0]);
        cpfLabel.setText(cliente.getNome() + "  |  " + cliente.getCpfFormatado());
        historicoArea.setText("");
        registrarHistorico("Sessao iniciada para " + cliente.getNome());
        atualizarSaldos(true);
        atualizarDestaqueConta();
    }

    private void selecionarConta(String tipo) {
        if ("poupanca".equals(tipo)) {
            contaSelecionada = contaPoupanca;
            badgeContaAtiva.setText("Poupanca ativa");
            if (!houveSelecaoPoupanca) {
                contaPoupanca.calcularJuros();
                contaPoupanca.calcularTaxa();
                houveSelecaoPoupanca = true;
                registrarHistorico("Poupanca selecionada. Juros aplicados.");
            } else {
                registrarHistorico("Poupanca selecionada");
            }
        } else {
            contaSelecionada = contaCorrente;
            badgeContaAtiva.setText("Corrente ativa");
            if (!houveSelecaoCorrente) {
                contaCorrente.calcularJuros();
                contaCorrente.calcularTaxa();
                houveSelecaoCorrente = true;
                registrarHistorico("Corrente selecionada. Taxa aplicada.");
            } else {
                registrarHistorico("Corrente selecionada");
            }
        }
        atualizarSaldos(false);
        atualizarDestaqueConta();
        animarToast("Conta " + tipo + " ativada", PURPLE_ACCENT);
    }

    private void executarOperacao(String tipo) {
        if (contaSelecionada == null) {
            mostrarErro("Selecione uma conta primeiro");
            return;
        }
        try {
            switch (tipo) {
                case "deposito" -> {
                    BigDecimal v = lerValor();
                    contaSelecionada.depositar(v);
                    registrarHistorico("Deposito: +" + FormatadorMoeda.formatar(v));
                    animarToast("Deposito realizado!", GREEN_UP);
                    service.salvarDados();
                }
                case "saque" -> {
                    BigDecimal v = lerValor();
                    contaSelecionada.sacar(v);
                    registrarHistorico("Saque: -" + FormatadorMoeda.formatar(v));
                    animarToast("Saque realizado!", RED_DOWN);
                    service.salvarDados();
                }
                case "transferencia" -> {
                    BigDecimal v = lerValor();
                    ContaBancaria dest = contaSelecionada == contaPoupanca ? contaCorrente : contaPoupanca;
                    contaSelecionada.transferir(v, dest);
                    registrarHistorico("Transferencia: " + FormatadorMoeda.formatar(v) + " p/ " + nomeConta(dest));
                    animarToast("Transferencia concluida!", PURPLE_ACCENT);
                    service.salvarDados();
                }
                case "saldo" -> {
                    registrarHistorico("Saldo " + nomeConta(contaSelecionada) + ": " + FormatadorMoeda.formatar(contaSelecionada.getSaldo()));
                    animarToast("Saldo: " + FormatadorMoeda.formatar(contaSelecionada.getSaldo()), PURPLE_ACCENT);
                }
            }
            atualizarSaldos(false);
            valorField.setText("");
        } catch (IllegalArgumentException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private BigDecimal lerValor() {
        String t = valorField.getText().trim().replace(',', '.');
        if (t.isEmpty()) throw new IllegalArgumentException("Informe um valor");
        try {
            BigDecimal v = new BigDecimal(t);
            if (v.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Valor deve ser maior que zero");
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor invalido. Ex: 100.50");
        }
    }

    private void atualizarSaldos(boolean semAnim) {
        if (saldoPoupancaLabel == null) return;
        saldoPoupancaLabel.setText(FormatadorMoeda.formatar(contaPoupanca.getSaldo()));
        saldoCorrenteLabel.setText(FormatadorMoeda.formatar(contaCorrente.getSaldo()));
        BigDecimal alvo = contaSelecionada != null ? contaSelecionada.getSaldo() : BigDecimal.ZERO;
        if (semAnim) {
            saldoPrincipalLabel.setText(FormatadorMoeda.formatar(alvo));
            saldoExibido = alvo;
        } else {
            animarSaldo(alvo);
        }
    }

    private void animarSaldo(BigDecimal alvo) {
        double inicio = saldoExibido.doubleValue();
        double fim = alvo.doubleValue();
        if (Math.abs(inicio - fim) < 0.01) {
            saldoPrincipalLabel.setText(FormatadorMoeda.formatar(alvo));
            saldoExibido = alvo;
            return;
        }
        int passos = 20;
        int[] idx = {0};
        Timer t = new Timer(16, null);
        t.addActionListener(e -> {
            idx[0]++;
            double p = idx[0] / (double) passos;
            double eased = 1 - (float) Math.pow(1 - p, 4);
            double v = inicio + (fim - inicio) * eased;
            saldoPrincipalLabel.setText(FormatadorMoeda.formatar(BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_EVEN)));
            if (idx[0] >= passos) {
                saldoPrincipalLabel.setText(FormatadorMoeda.formatar(alvo));
                saldoExibido = alvo;
                t.stop();
            }
        });
        t.start();
    }

    private String nomeConta(ContaBancaria c) {
        return c == contaPoupanca ? "Poupanca" : "Corrente";
    }

    private void registrarHistorico(String msg) {
        historicoArea.append("  " + msg + "\n");
        historicoArea.setCaretPosition(historicoArea.getDocument().getLength());
    }

    private void limparSessao() {
        cliente = null;
        contaPoupanca = null;
        contaCorrente = null;
        contaSelecionada = null;
        if (historicoArea != null) historicoArea.setText("");
        if (saldoPrincipalLabel != null) {
            saldoPrincipalLabel.setText(FormatadorMoeda.formatar(BigDecimal.ZERO));
        }
        saldoExibido = BigDecimal.ZERO;
        if (badgeContaAtiva != null) badgeContaAtiva.setText("nenhuma conta selecionada");
        if (avatarLabel != null) avatarLabel.setText("A");
        atualizarDestaqueConta();
    }

    private void atualizarDestaqueConta() {
        if (cardPoupancaPanel == null || cardCorrentePanel == null) return;
        aplicarDestaque(cardPoupancaPanel, "poupanca");
        aplicarDestaque(cardCorrentePanel, "corrente");
        atualizarStatusDot(cardPoupancaPanel, contaSelecionada == contaPoupanca);
        atualizarStatusDot(cardCorrentePanel, contaSelecionada == contaCorrente);
    }

    private void aplicarDestaque(JPanel card, String tipo) {
        boolean ativa = (tipo.equals("poupanca") && contaSelecionada == contaPoupanca) ||
                        (tipo.equals("corrente") && contaSelecionada == contaCorrente);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ativa ? PURPLE_ACCENT : CARD_BORDER, ativa ? 2 : 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));
    }

    private void atualizarStatusDot(JPanel card, boolean ativa) {
        JPanel dot = (JPanel) card.getClientProperty("statusDot");
        if (dot != null) {
            Graphics2D g2 = (Graphics2D) dot.getGraphics();
            if (g2 != null) {
                dot.repaint();
            }
            dot.putClientProperty("ativo", ativa);
        }
    }

    private void mostrarErro(String msg) {
        animarToast(msg, RED_DOWN);
    }

    private void animarToast(String mensagem, Color cor) {
        if (toastTimer != null && toastTimer.isRunning()) toastTimer.stop();

        toastLabel.setText("  " + mensagem + "  ");
        toastLabel.setForeground(TEXT_PRIMARY);
        toastLabel.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 230));

        toastLabel.setVisible(true);
        glassPane.setVisible(true);

        toastTimer = new Timer(2400, null);
        toastTimer.setRepeats(false);
        toastTimer.addActionListener(e -> {
            toastLabel.setVisible(false);
            glassPane.setVisible(false);
        });
        toastTimer.start();
    }

    private void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    private static class RoundedPanel extends JPanel {
        private int radius;
        private final Color c1;
        private final Color c2;

        RoundedPanel(int r, Color c1, Color c2) {
            this.radius = r;
            this.c1 = c1;
            this.c2 = c2;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(255, 255, 255, 8));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }

    private static class ParticlePanel extends JPanel {
        private final Color bg1;
        private final Color bg2;
        private final float[][] particles;
        private final Random rand = new Random();
        private Timer animTimer;

        ParticlePanel(Color bg1, Color bg2) {
            this.bg1 = bg1;
            this.bg2 = bg2;
            particles = new float[35][4];
            for (int i = 0; i < particles.length; i++) {
                particles[i] = new float[]{
                    rand.nextFloat() * 1200, rand.nextFloat() * 800,
                    0.5f + rand.nextFloat() * 1.5f,
                    0.2f + rand.nextFloat() * 0.5f
                };
            }
            setOpaque(false);
        }

        void iniciarAnimacao() {
            if (animTimer != null && animTimer.isRunning()) animTimer.stop();
            animTimer = new Timer(40, e -> {
                for (float[] p : particles) {
                    p[1] -= p[2] * 0.15f;
                    p[0] += Math.sin(p[1] * 0.01f) * 0.2f;
                    if (p[1] < -20) { p[1] = getHeight() + 20; p[0] = rand.nextFloat() * getWidth(); }
                }
                repaint();
            });
            animTimer.start();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, bg1, getWidth(), getHeight(), bg2));
            g2.fillRect(0, 0, getWidth(), getHeight());

            for (float[] p : particles) {
                float alpha = p[3] * 0.3f;
                g2.setColor(new Color(180, 130, 230, (int) (alpha * 255)));
                int size = (int) (p[2] * 2.5f);
                g2.fillOval((int) p[0], (int) p[1], size, size);
            }

            g2.setColor(new Color(130, 50, 210, 12));
            g2.fillOval(getWidth() / 2 - 250, -100, 500, 500);
            g2.setColor(new Color(230, 70, 150, 8));
            g2.fillOval(getWidth() / 2 + 100, getHeight() - 300, 350, 350);

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BancoUI().show());
    }
}
