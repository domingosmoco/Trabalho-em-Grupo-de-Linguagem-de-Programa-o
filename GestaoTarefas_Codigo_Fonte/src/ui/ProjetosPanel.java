package ui;

import model.Projeto;
import model.Tarefa;
import model.Usuario;
import service.GestaoService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class ProjetosPanel extends JPanel {

    private GestaoService service;
    private MainFrame mainFrame;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblContador;

    private static final String[] COLUNAS = {"ID", "Nome", "Descrição", "Estado", "Nº Tarefas", "Progresso", "Criado em"};

    public ProjetosPanel(GestaoService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        setBackground(MainFrame.COR_FUNDO);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        criarComponentes();
    }

    private void criarComponentes() {
        // Topo
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titulo = new JLabel("📁 Gestão de Projetos");
        titulo.setFont(MainFrame.FONTE_TITULO);
        titulo.setForeground(MainFrame.COR_TEXTO);

        lblContador = new JLabel("0 projetos");
        lblContador.setFont(MainFrame.FONTE_PEQUENA);
        lblContador.setForeground(MainFrame.COR_TEXTO_CLARO);

        topo.add(titulo, BorderLayout.WEST);
        topo.add(lblContador, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        // Tabela
        modeloTabela = new DefaultTableModel(COLUNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(36);
        tabela.setFont(MainFrame.FONTE_NORMAL);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(MainFrame.COR_BORDA);
        tabela.setSelectionBackground(new Color(232, 240, 255));

        JTableHeader header = tabela.getTableHeader();
        header.setFont(MainFrame.FONTE_SUBTIT);
        header.setBackground(new Color(52, 58, 64));
        header.setForeground(Color.WHITE);

        int[] larguras = {70, 160, 200, 110, 80, 100, 140};
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }

        // Renderer para progresso (barra)
        tabela.getColumnModel().getColumn(5).setCellRenderer(new TableCellRenderer() {
            private final JProgressBar bar = new JProgressBar(0, 100);
            { bar.setStringPainted(true); bar.setForeground(MainFrame.COR_SECUNDARIA); }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                int val = v instanceof Integer ? (Integer) v : 0;
                bar.setValue(val);
                bar.setString(val + "%");
                bar.setBackground(sel ? new Color(200, 230, 255) : new Color(240, 240, 240));
                return bar;
            }
        });

        // Renderer para estado colorido
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            { setHorizontalAlignment(SwingConstants.CENTER); }
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String estado = v != null ? v.toString() : "";
                Color cor = switch (estado) {
                    case "Planeamento" -> new Color(33, 150, 243);
                    case "Ativo"       -> MainFrame.COR_SECUNDARIA;
                    case "Pausado"     -> new Color(255, 152, 0);
                    case "Concluído"   -> new Color(76, 175, 80);
                    case "Cancelado"   -> MainFrame.COR_PERIGO;
                    default            -> MainFrame.COR_TEXTO_CLARO;
                };
                if (!sel) { lbl.setForeground(cor.darker()); lbl.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 30)); }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.COR_BORDA));
        add(scroll, BorderLayout.CENTER);

        // Botões
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnPanel.setBackground(MainFrame.COR_FUNDO);

        JButton btnNovo    = MainFrame.criarBotao("➕ Novo Projeto", MainFrame.COR_PRIMARIA);
        JButton btnEstado  = MainFrame.criarBotao("🔄 Alterar Estado", new Color(255, 152, 0));
        JButton btnVerTar  = MainFrame.criarBotao("📋 Ver Tarefas", new Color(33, 150, 243));
        JButton btnExcluir = MainFrame.criarBotao("🗑️ Excluir", MainFrame.COR_PERIGO);

        btnNovo.addActionListener(e -> abrirDialogoNovoProjeto());
        btnEstado.addActionListener(e -> alterarEstado());
        btnVerTar.addActionListener(e -> verTarefasDoProjeto());
        btnExcluir.addActionListener(e -> excluirProjeto());

        btnPanel.add(btnNovo);
        btnPanel.add(btnEstado);
        btnPanel.add(btnVerTar);
        btnPanel.add(btnExcluir);
        add(btnPanel, BorderLayout.SOUTH);

        atualizar();
    }

    public void atualizar() {
        modeloTabela.setRowCount(0);
        List<Projeto> projetos = service.getProjetos();
        for (Projeto p : projetos) {
            int progresso = (int) service.getProgressoProjeto(p.getId());
            modeloTabela.addRow(new Object[]{
                p.getId(), p.getNome(), p.getDescricao(),
                p.getEstado().getDescricao(), p.getTotalTarefas(), progresso, p.getDataCriacao()
            });
        }
        lblContador.setText(projetos.size() + " projeto(s)");
    }

    private String getProjetoIdSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um projeto!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) modeloTabela.getValueAt(row, 0);
    }

    private void abrirDialogoNovoProjeto() {
        JDialog dialog = new JDialog(mainFrame, "➕ Novo Projeto", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(440, 260);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        JTextField txtNome = new JTextField(22);
        JTextArea txtDesc  = new JTextArea(3, 22);
        txtDesc.setLineWrap(true);

        adicionarCampo(form, gbc, "Nome: *", txtNome, 0);
        adicionarCampo(form, gbc, "Descrição:", new JScrollPane(txtDesc), 1);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        JButton btnSalvar   = MainFrame.criarBotao("💾 Criar", MainFrame.COR_PRIMARIA);
        JButton btnCancelar = MainFrame.criarBotao("Cancelar", new Color(108, 117, 125));

        btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "O nome é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Projeto novo = service.criarProjeto(nome, txtDesc.getText().trim());
            JOptionPane.showMessageDialog(dialog, "✅ Projeto criado! ID: " + novo.getId());
            mainFrame.atualizarTodasAbas();
            dialog.dispose();
        });
        btnCancelar.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnSalvar);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void alterarEstado() {
        String id = getProjetoIdSelecionado();
        if (id == null) return;
        Projeto.EstadoProjeto novoEstado = (Projeto.EstadoProjeto) JOptionPane.showInputDialog(
            this, "Selecione o novo estado:", "🔄 Alterar Estado",
            JOptionPane.PLAIN_MESSAGE, null,
            Projeto.EstadoProjeto.values(), Projeto.EstadoProjeto.ATIVO
        );
        if (novoEstado != null) {
            service.alterarEstadoProjeto(id, novoEstado);
            mainFrame.atualizarTodasAbas();
        }
    }

    private void verTarefasDoProjeto() {
        String id = getProjetoIdSelecionado();
        if (id == null) return;

        List<Tarefa> tarefas = service.getTarefasDoProjeto(id);
        Optional<Projeto> proj = service.buscarProjetoPorId(id);
        String nomeProjeto = proj.map(Projeto::getNome).orElse(id);

        if (tarefas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Projeto \"" + nomeProjeto + "\" não tem tarefas.", "Tarefas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("<html><b>Tarefas do projeto: " + nomeProjeto + "</b><br><br>");
        for (Tarefa t : tarefas) {
            String cor = switch (t.getStatus()) {
                case PENDENTE     -> "#FF9800";
                case EM_ANDAMENTO -> "#2196F3";
                case CONCLUIDA    -> "#00C896";
                case CANCELADA    -> "#DC3545";
            };
            sb.append(String.format("● <b>%s</b> [%s] — <font color='%s'>%s</font><br>",
                t.getTitulo(), t.getPrioridade().getDescricao(), cor, t.getStatus().getDescricao()));
        }
        double prog = service.getProgressoProjeto(id);
        sb.append(String.format("<br><b>Progresso: %.0f%%</b></html>", prog));

        JOptionPane.showMessageDialog(this, new JLabel(sb.toString()),
            "Tarefas - " + nomeProjeto, JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirProjeto() {
        String id = getProjetoIdSelecionado();
        if (id == null) return;
        int c = JOptionPane.showConfirmDialog(this, "Excluir projeto " + id + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            service.excluirProjeto(id);
            mainFrame.atualizarTodasAbas();
        }
    }

    private void adicionarCampo(JPanel form, GridBagConstraints gbc, String label, Component campo, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(MainFrame.FONTE_NORMAL);
        form.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(campo, gbc);
    }
}
