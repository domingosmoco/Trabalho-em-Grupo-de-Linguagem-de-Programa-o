package ui;

import service.GestaoService;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame - Janela principal da aplicação.
 *
 * CONCEITOS DE POO APLICADOS:
 * - Herança: MainFrame extends JFrame
 * - Composição: contém painéis e outros componentes Swing
 * - Injeção de dependência: GestaoService passado para sub-painéis
 */
public class MainFrame extends JFrame {

    private GestaoService service;
    private JTabbedPane abas;

    // Painéis das abas
    private DashboardPanel dashboardPanel;
    private TarefasPanel tarefasPanel;
    private UsuariosPanel usuariosPanel;
    private ProjetosPanel projetosPanel;

    // =============================================
    // CORES E ESTILOS (constantes de design)
    // =============================================
    public static final Color COR_PRIMARIA     = new Color(41, 98, 255);
    public static final Color COR_SECUNDARIA   = new Color(0, 200, 150);
    public static final Color COR_PERIGO       = new Color(220, 53, 69);
    public static final Color COR_AVISO        = new Color(255, 193, 7);
    public static final Color COR_FUNDO        = new Color(245, 247, 250);
    public static final Color COR_CARTAO       = Color.WHITE;
    public static final Color COR_TEXTO        = new Color(33, 37, 41);
    public static final Color COR_TEXTO_CLARO  = new Color(108, 117, 125);
    public static final Color COR_BORDA        = new Color(222, 226, 230);

    public static final Font FONTE_TITULO  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONTE_SUBTIT  = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONTE_NORMAL  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 11);

    // =============================================
    // CONSTRUTOR
    // =============================================
    public MainFrame() {
        this.service = new GestaoService();
        configurarJanela();
        criarComponentes();
        pack();
        setLocationRelativeTo(null); // centraliza na tela
        setVisible(true);
    }

    private void configurarJanela() {
        setTitle("📋 Sistema de Gestão de Tarefas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setPreferredSize(new Dimension(1200, 780));
        getContentPane().setBackground(COR_FUNDO);

        // Ícone da janela (usando texto emoji como fallback)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Usa look padrão se falhar
        }
    }

    private void criarComponentes() {
        setLayout(new BorderLayout());

        // ---- CABEÇALHO ----
        JPanel header = criarCabecalho();
        add(header, BorderLayout.NORTH);

        // ---- ABAS PRINCIPAIS ----
        abas = new JTabbedPane(JTabbedPane.TOP);
        abas.setFont(FONTE_SUBTIT);
        abas.setBackground(COR_FUNDO);

        dashboardPanel = new DashboardPanel(service, this);
        tarefasPanel   = new TarefasPanel(service, this);
        usuariosPanel  = new UsuariosPanel(service, this);
        projetosPanel  = new ProjetosPanel(service, this);

        abas.addTab("  🏠 Dashboard  ", dashboardPanel);
        abas.addTab("  ✅ Tarefas    ", tarefasPanel);
        abas.addTab("  👥 Usuários   ", usuariosPanel);
        abas.addTab("  📁 Projetos   ", projetosPanel);

        add(abas, BorderLayout.CENTER);

        // ---- RODAPÉ ----
        JPanel rodape = criarRodape();
        add(rodape, BorderLayout.SOUTH);
    }

    private JPanel criarCabecalho() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COR_PRIMARIA);
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel titulo = new JLabel("📋 Sistema de Gestão de Tarefas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Gerencie tarefas, usuários e projetos com persistência em JSON");
        subtitulo.setFont(FONTE_PEQUENA);
        subtitulo.setForeground(new Color(200, 220, 255));

        JPanel esquerda = new JPanel(new GridLayout(2, 1));
        esquerda.setOpaque(false);
        esquerda.add(titulo);
        esquerda.add(subtitulo);

        JButton btnAtualizar = criarBotao("🔄 Atualizar", COR_SECUNDARIA);
        btnAtualizar.addActionListener(e -> atualizarTodasAbas());

        header.add(esquerda, BorderLayout.WEST);
        header.add(btnAtualizar, BorderLayout.EAST);
        return header;
    }

    private JPanel criarRodape() {
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.setBackground(new Color(52, 58, 64));
        rodape.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JLabel label = new JLabel("💾 Dados persistidos automaticamente em JSON  |  POO com Java & Swing  |  2025");
        label.setFont(FONTE_PEQUENA);
        label.setForeground(new Color(180, 180, 180));
        rodape.add(label);
        return rodape;
    }

    // =============================================
    // MÉTODOS PÚBLICOS USADOS PELOS SUB-PAINÉIS
    // =============================================

    /** Atualiza todos os painéis (chamado após criar/editar dados) */
    public void atualizarTodasAbas() {
        dashboardPanel.atualizar();
        tarefasPanel.atualizar();
        usuariosPanel.atualizar();
        projetosPanel.atualizar();
    }

    public GestaoService getService() { return service; }

    /** Helper para criar botões estilizados */
    public static JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_NORMAL);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        // Hover effect
        Color corEscura = cor.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(corEscura); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(cor); }
        });
        return btn;
    }

    /** Helper para criar cartões com borda arredondada */
    public static JPanel criarCartao(String titulo) {
        JPanel cartao = new JPanel(new BorderLayout());
        cartao.setBackground(COR_CARTAO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDA, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        if (titulo != null && !titulo.isEmpty()) {
            JLabel lbl = new JLabel(titulo);
            lbl.setFont(FONTE_SUBTIT);
            lbl.setForeground(COR_TEXTO);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            cartao.add(lbl, BorderLayout.NORTH);
        }
        return cartao;
    }
}
