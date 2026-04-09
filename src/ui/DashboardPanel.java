package ui;

import service.GestaoService;
import model.Tarefa;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;

/**
 * DashboardPanel - Painel de visão geral do sistema.
 * Mostra estatísticas e resumo das tarefas recentes.
 */
public class DashboardPanel extends JPanel {

    private GestaoService service;
    private MainFrame mainFrame;
    private JPanel painelStats;
    private JPanel painelTarefasRecentes;

    public DashboardPanel(GestaoService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        setBackground(MainFrame.COR_FUNDO);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        criarComponentes();
    }

    private void criarComponentes() {
        // Título
        JLabel titulo = new JLabel("🏠 Dashboard — Visão Geral");
        titulo.setFont(MainFrame.FONTE_TITULO);
        titulo.setForeground(MainFrame.COR_TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(titulo, BorderLayout.NORTH);

        // Painel central com scroll
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBackground(MainFrame.COR_FUNDO);

        // Cards de estatísticas
        painelStats = new JPanel(new GridLayout(1, 6, 15, 0));
        painelStats.setBackground(MainFrame.COR_FUNDO);
        painelStats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        centro.add(painelStats);
        centro.add(Box.createVerticalStrut(20));

        // Duas colunas inferiores
        JPanel linhaInferior = new JPanel(new GridLayout(1, 2, 15, 0));
        linhaInferior.setBackground(MainFrame.COR_FUNDO);

        painelTarefasRecentes = criarCartaoTarefasRecentes();
        JPanel painelProgresso = criarCartaoProgresso();

        linhaInferior.add(painelTarefasRecentes);
        linhaInferior.add(painelProgresso);
        centro.add(linhaInferior);

        JScrollPane scroll = new JScrollPane(centro);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(MainFrame.COR_FUNDO);
        add(scroll, BorderLayout.CENTER);

        atualizar();
    }

    public void atualizar() {
        atualizarStats();
        atualizarTarefasRecentes();
        revalidate();
        repaint();
    }

    private void atualizarStats() {
        painelStats.removeAll();
        Map<String, Integer> stats = service.getEstatisticas();

        String[] icones = {"📋", "⏳", "🔄", "✅", "👥", "📁"};
        Color[] cores = {
            MainFrame.COR_PRIMARIA,
            new Color(255, 152, 0),
            new Color(33, 150, 243),
            MainFrame.COR_SECUNDARIA,
            new Color(156, 39, 176),
            new Color(233, 30, 99)
        };

        int i = 0;
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            painelStats.add(criarCardStat(icones[i], entry.getKey(), String.valueOf(entry.getValue()), cores[i]));
            i++;
        }
    }

    private JPanel criarCardStat(String icone, String titulo, String valor, Color cor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.COR_CARTAO);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, cor),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        lblIcone.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValor.setForeground(cor);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(MainFrame.FONTE_PEQUENA);
        lblTitulo.setForeground(MainFrame.COR_TEXTO_CLARO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblIcone);
        card.add(Box.createVerticalStrut(5));
        card.add(lblValor);
        card.add(lblTitulo);
        return card;
    }

    private JPanel criarCartaoTarefasRecentes() {
        JPanel cartao = MainFrame.criarCartao("📋 Tarefas Recentes");
        return cartao;
    }

    private void atualizarTarefasRecentes() {
        painelTarefasRecentes.removeAll();

        JLabel titulo = new JLabel("📋 Tarefas Recentes");
        titulo.setFont(MainFrame.FONTE_SUBTIT);
        titulo.setForeground(MainFrame.COR_TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        painelTarefasRecentes.add(titulo, BorderLayout.NORTH);

        List<Tarefa> tarefas = service.getTarefas();
        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(MainFrame.COR_CARTAO);

        int exibir = Math.min(tarefas.size(), 7);
        // Mostra as últimas tarefas (mais recentes primeiro)
        for (int i = tarefas.size() - 1; i >= tarefas.size() - exibir; i--) {
            Tarefa t = tarefas.get(i);
            lista.add(criarLinhaLista(t));
            lista.add(new JSeparator());
        }

        if (tarefas.isEmpty()) {
            JLabel vazia = new JLabel("  Nenhuma tarefa criada ainda.");
            vazia.setFont(MainFrame.FONTE_NORMAL);
            vazia.setForeground(MainFrame.COR_TEXTO_CLARO);
            lista.add(vazia);
        }

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        painelTarefasRecentes.setBackground(MainFrame.COR_CARTAO);
        painelTarefasRecentes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COR_BORDA),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        painelTarefasRecentes.add(scroll, BorderLayout.CENTER);
    }

    private JPanel criarLinhaLista(Tarefa t) {
        JPanel linha = new JPanel(new BorderLayout(10, 0));
        linha.setBackground(MainFrame.COR_CARTAO);
        linha.setBorder(BorderFactory.createEmptyBorder(6, 5, 6, 5));

        // Badge de status colorido
        Color corStatus = getCorStatus(t.getStatus());
        JLabel badge = new JLabel("● ");
        badge.setForeground(corStatus);
        badge.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblTitulo = new JLabel(t.getTitulo());
        lblTitulo.setFont(MainFrame.FONTE_NORMAL);

        JLabel lblStatus = new JLabel(t.getStatus().getDescricao());
        lblStatus.setFont(MainFrame.FONTE_PEQUENA);
        lblStatus.setForeground(corStatus);

        JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esq.setOpaque(false);
        esq.add(badge);
        esq.add(lblTitulo);

        linha.add(esq, BorderLayout.WEST);
        linha.add(lblStatus, BorderLayout.EAST);
        return linha;
    }

    private JPanel criarCartaoProgresso() {
        JPanel cartao = new JPanel(new BorderLayout());
        cartao.setBackground(MainFrame.COR_CARTAO);
        cartao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.COR_BORDA),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titulo = new JLabel("📊 Distribuição por Status");
        titulo.setFont(MainFrame.FONTE_SUBTIT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        cartao.add(titulo, BorderLayout.NORTH);

        // Painel com barras de progresso para cada status
        JPanel barras = new JPanel(new GridLayout(0, 1, 0, 12));
        barras.setBackground(MainFrame.COR_CARTAO);

        List<Tarefa> todas = service.getTarefas();
        int total = todas.isEmpty() ? 1 : todas.size();

        for (Tarefa.Status status : Tarefa.Status.values()) {
            int count = (int) todas.stream().filter(t -> t.getStatus() == status).count();
            int pct = (int) ((double) count / total * 100);

            JPanel linhaBar = new JPanel(new BorderLayout(8, 0));
            linhaBar.setBackground(MainFrame.COR_CARTAO);

            JLabel lbl = new JLabel(status.getDescricao() + " (" + count + ")");
            lbl.setFont(MainFrame.FONTE_PEQUENA);
            lbl.setPreferredSize(new Dimension(140, 20));

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(pct);
            bar.setStringPainted(true);
            bar.setString(pct + "%");
            bar.setForeground(getCorStatus(status));
            bar.setBackground(new Color(240, 240, 240));

            linhaBar.add(lbl, BorderLayout.WEST);
            linhaBar.add(bar, BorderLayout.CENTER);
            barras.add(linhaBar);
        }

        cartao.add(barras, BorderLayout.CENTER);

        // Dica educacional
        JLabel dica = new JLabel("<html><i>💡 Os dados são salvos automaticamente em ficheiros JSON</i></html>");
        dica.setFont(MainFrame.FONTE_PEQUENA);
        dica.setForeground(MainFrame.COR_TEXTO_CLARO);
        dica.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        cartao.add(dica, BorderLayout.SOUTH);

        return cartao;
    }

    private Color getCorStatus(Tarefa.Status status) {
        return switch (status) {
            case PENDENTE    -> new Color(255, 152, 0);
            case EM_ANDAMENTO-> new Color(33, 150, 243);
            case CONCLUIDA   -> MainFrame.COR_SECUNDARIA;
            case CANCELADA   -> MainFrame.COR_PERIGO;
        };
    }
}
