package ui;

import model.Tarefa;
import model.Usuario;
import model.Projeto;
import service.GestaoService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * TarefasPanel - Painel de gestão de tarefas.
 * Usa JTable para listar tarefas com filtros e CRUD completo.
 */
public class TarefasPanel extends JPanel {

    private GestaoService service;
    private MainFrame mainFrame;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JComboBox<String> filtroStatus;
    private JLabel lblContador;

    // Colunas da tabela
    private static final String[] COLUNAS = {
        "ID", "Título", "Prioridade", "Status", "Usuário Atribuído",
        "Projeto", "Criado em", "Concluído em"
    };

    public TarefasPanel(GestaoService service, MainFrame mainFrame) {
        this.service = service;
        this.mainFrame = mainFrame;
        setBackground(MainFrame.COR_FUNDO);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        criarComponentes();
    }

    private void criarComponentes() {
        // ---- TOPO ----
        JPanel topo = new JPanel(new BorderLayout(10, 0));
        topo.setOpaque(false);
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titulo = new JLabel("✅ Gestão de Tarefas");
        titulo.setFont(MainFrame.FONTE_TITULO);
        titulo.setForeground(MainFrame.COR_TEXTO);

        // Filtros
        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelFiltros.setOpaque(false);

        filtroStatus = new JComboBox<>(new String[]{
            "Todos os Status", "PENDENTE", "EM_ANDAMENTO", "CONCLUIDA", "CANCELADA"
        });
        filtroStatus.setFont(MainFrame.FONTE_NORMAL);
        filtroStatus.addActionListener(e -> atualizar());

        lblContador = new JLabel("0 tarefas");
        lblContador.setFont(MainFrame.FONTE_PEQUENA);
        lblContador.setForeground(MainFrame.COR_TEXTO_CLARO);

        painelFiltros.add(new JLabel("Filtrar:"));
        painelFiltros.add(filtroStatus);
        painelFiltros.add(lblContador);

        topo.add(titulo, BorderLayout.WEST);
        topo.add(painelFiltros, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        // ---- TABELA ----
        modeloTabela = new DefaultTableModel(COLUNAS, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tabela = new JTable(modeloTabela);
        estilizarTabela();

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.COR_BORDA));
        add(scroll, BorderLayout.CENTER);

        // ---- BOTÕES ----
        JPanel btnPanel = criarPainelBotoes();
        add(btnPanel, BorderLayout.SOUTH);

        atualizar();
    }

    private void estilizarTabela() {
        tabela.setRowHeight(36);
        tabela.setFont(MainFrame.FONTE_NORMAL);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(MainFrame.COR_BORDA);
        tabela.setSelectionBackground(new Color(232, 240, 255));
        tabela.setSelectionForeground(MainFrame.COR_TEXTO);

        JTableHeader header = tabela.getTableHeader();
        header.setFont(MainFrame.FONTE_SUBTIT);
        header.setBackground(new Color(52, 58, 64));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Larguras das colunas
        int[] larguras = {70, 200, 90, 120, 160, 120, 140, 140};
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }

        // Renderer personalizado para colorir status
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setOpaque(true);
                String status = v != null ? v.toString() : "";
                Color cor = switch (status) {
                    case "Pendente"     -> new Color(255, 152, 0);
                    case "Em Andamento" -> new Color(33, 150, 243);
                    case "Concluída"    -> new Color(0, 180, 130);
                    case "Cancelada"    -> new Color(220, 53, 69);
                    default             -> MainFrame.COR_TEXTO_CLARO;
                };
                if (!sel) {
                    lbl.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 30));
                    lbl.setForeground(cor.darker());
                }
                return lbl;
            }
        });

        // Renderer para prioridade
        tabela.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                String prio = v != null ? v.toString() : "";
                Color cor = switch (prio) {
                    case "Baixa"    -> MainFrame.COR_SECUNDARIA;
                    case "Média"    -> new Color(33, 150, 243);
                    case "Alta"     -> new Color(255, 152, 0);
                    case "Crítica"  -> MainFrame.COR_PERIGO;
                    default         -> MainFrame.COR_TEXTO;
                };
                lbl.setForeground(sel ? Color.WHITE : cor);
                return lbl;
            }
        });

        // Linhas alternadas
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                return comp;
            }
        });
        // Reaplicar renderers específicos
        tabela.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setOpaque(true);
                String status = v != null ? v.toString() : "";
                Color cor = switch (status) {
                    case "Pendente"     -> new Color(255, 152, 0);
                    case "Em Andamento" -> new Color(33, 150, 243);
                    case "Concluída"    -> new Color(0, 180, 130);
                    case "Cancelada"    -> new Color(220, 53, 69);
                    default             -> MainFrame.COR_TEXTO_CLARO;
                };
                if (!sel) {
                    lbl.setBackground(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 40));
                    lbl.setForeground(cor.darker());
                } else {
                    lbl.setForeground(Color.WHITE);
                }
                return lbl;
            }
        });
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painel.setBackground(MainFrame.COR_FUNDO);

        JButton btnNova     = MainFrame.criarBotao("➕ Nova Tarefa", MainFrame.COR_PRIMARIA);
        JButton btnEditar   = MainFrame.criarBotao("✏️ Editar", new Color(108, 117, 125));
        JButton btnStatus   = MainFrame.criarBotao("🔄 Alterar Status", new Color(255, 152, 0));
        JButton btnAtribuir = MainFrame.criarBotao("👤 Atribuir Usuário", new Color(156, 39, 176));
        JButton btnProjeto  = MainFrame.criarBotao("📁 Adicionar a Projeto", new Color(233, 30, 99));
        JButton btnExcluir  = MainFrame.criarBotao("🗑️ Excluir", MainFrame.COR_PERIGO);

        btnNova.addActionListener(e -> abrirDialogoNovaTarefa());
        btnEditar.addActionListener(e -> abrirDialogoEditarTarefa());
        btnStatus.addActionListener(e -> alterarStatus());
        btnAtribuir.addActionListener(e -> atribuirUsuario());
        btnProjeto.addActionListener(e -> adicionarAProjeto());
        btnExcluir.addActionListener(e -> excluirTarefa());

        painel.add(btnNova);
        painel.add(btnEditar);
        painel.add(btnStatus);
        painel.add(btnAtribuir);
        painel.add(btnProjeto);
        painel.add(btnExcluir);
        return painel;
    }

    public void atualizar() {
        modeloTabela.setRowCount(0);
        List<Tarefa> tarefas = service.getTarefas();

        String filtroSel = filtroStatus.getSelectedItem() != null ?
                filtroStatus.getSelectedItem().toString() : "Todos os Status";

        int count = 0;
        for (Tarefa t : tarefas) {
            if (!"Todos os Status".equals(filtroSel) && !t.getStatus().name().equals(filtroSel)) continue;

            // Busca nome do usuário
            String nomeUsuario = "—";
            if (t.getUsuarioAtribuido() != null) {
                nomeUsuario = service.getUsuarios().stream()
                    .filter(u -> u.getEmail().equals(t.getUsuarioAtribuido()))
                    .map(u -> u.getNome())
                    .findFirst().orElse(t.getUsuarioAtribuido());
            }

            // Busca nome do projeto
            String nomeProjeto = "—";
            if (t.getProjetoId() != null) {
                nomeProjeto = service.getProjetos().stream()
                    .filter(p -> p.getId().equals(t.getProjetoId()))
                    .map(p -> p.getNome())
                    .findFirst().orElse(t.getProjetoId());
            }

            modeloTabela.addRow(new Object[]{
                t.getId(), t.getTitulo(), t.getPrioridade().getDescricao(),
                t.getStatus().getDescricao(), nomeUsuario, nomeProjeto,
                t.getDataCreacao(), t.getDataConclusao() != null ? t.getDataConclusao() : "—"
            });
            count++;
        }
        lblContador.setText(count + " tarefa(s)");
    }

    private String getTarefaIdSelecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) modeloTabela.getValueAt(row, 0);
    }

    private void abrirDialogoNovaTarefa() {
        JDialog dialog = new JDialog(mainFrame, "➕ Nova Tarefa", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(480, 340);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtTitulo = new JTextField(25);
        JTextArea txtDescricao = new JTextArea(3, 25);
        txtDescricao.setLineWrap(true);
        JComboBox<Tarefa.Prioridade> cbPrioridade = new JComboBox<>(Tarefa.Prioridade.values());

        adicionarCampo(form, gbc, "Título: *", txtTitulo, 0);
        adicionarCampo(form, gbc, "Descrição:", new JScrollPane(txtDescricao), 1);
        adicionarCampo(form, gbc, "Prioridade:", cbPrioridade, 2);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        JButton btnSalvar = MainFrame.criarBotao("💾 Criar Tarefa", MainFrame.COR_PRIMARIA);
        JButton btnCancelar = MainFrame.criarBotao("Cancelar", new Color(108,117,125));

        btnSalvar.addActionListener(e -> {
            String titulo = txtTitulo.getText().trim();
            if (titulo.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "O título é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Tarefa.Prioridade prio = (Tarefa.Prioridade) cbPrioridade.getSelectedItem();
            Tarefa nova = service.criarTarefa(titulo, txtDescricao.getText().trim(), prio);
            JOptionPane.showMessageDialog(dialog, "✅ Tarefa criada! ID: " + nova.getId());
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

    private void abrirDialogoEditarTarefa() {
        String id = getTarefaIdSelecionada();
        if (id == null) return;
        Optional<Tarefa> opt = service.buscarTarefaPorId(id);
        if (opt.isEmpty()) return;
        Tarefa t = opt.get();

        JDialog dialog = new JDialog(mainFrame, "✏️ Editar Tarefa", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(480, 340);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField txtTitulo = new JTextField(t.getTitulo(), 25);
        JTextArea txtDescricao = new JTextArea(t.getDescricao(), 3, 25);
        txtDescricao.setLineWrap(true);
        JComboBox<Tarefa.Prioridade> cbPrioridade = new JComboBox<>(Tarefa.Prioridade.values());
        cbPrioridade.setSelectedItem(t.getPrioridade());

        adicionarCampo(form, gbc, "Título: *", txtTitulo, 0);
        adicionarCampo(form, gbc, "Descrição:", new JScrollPane(txtDescricao), 1);
        adicionarCampo(form, gbc, "Prioridade:", cbPrioridade, 2);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        JButton btnSalvar = MainFrame.criarBotao("💾 Salvar", MainFrame.COR_PRIMARIA);
        JButton btnCancelar = MainFrame.criarBotao("Cancelar", new Color(108,117,125));

        btnSalvar.addActionListener(e -> {
            String titulo = txtTitulo.getText().trim();
            if (titulo.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "O título é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            service.editarTarefa(id, titulo, txtDescricao.getText().trim(),
                    (Tarefa.Prioridade) cbPrioridade.getSelectedItem());
            JOptionPane.showMessageDialog(dialog, "✅ Tarefa atualizada com sucesso!");
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

    private void alterarStatus() {
        String id = getTarefaIdSelecionada();
        if (id == null) return;

        Tarefa.Status novoStatus = (Tarefa.Status) JOptionPane.showInputDialog(
            this, "Selecione o novo status:", "🔄 Alterar Status",
            JOptionPane.PLAIN_MESSAGE, null,
            Tarefa.Status.values(), Tarefa.Status.PENDENTE
        );
        if (novoStatus != null) {
            service.alterarStatusTarefa(id, novoStatus);
            mainFrame.atualizarTodasAbas();
        }
    }

    private void atribuirUsuario() {
        String id = getTarefaIdSelecionada();
        if (id == null) return;
        List<Usuario> usuarios = service.getUsuarios();
        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário cadastrado!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario selecionado = (Usuario) JOptionPane.showInputDialog(
            this, "Selecione o usuário:", "👤 Atribuir Usuário",
            JOptionPane.PLAIN_MESSAGE, null,
            usuarios.toArray(), usuarios.get(0)
        );
        if (selecionado != null) {
            service.atribuirTarefaAUsuario(id, selecionado.getId());
            mainFrame.atualizarTodasAbas();
        }
    }

    private void adicionarAProjeto() {
        String id = getTarefaIdSelecionada();
        if (id == null) return;
        List<Projeto> projetos = service.getProjetos();
        if (projetos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum projeto cadastrado!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Projeto selecionado = (Projeto) JOptionPane.showInputDialog(
            this, "Selecione o projeto:", "📁 Adicionar a Projeto",
            JOptionPane.PLAIN_MESSAGE, null,
            projetos.toArray(), projetos.get(0)
        );
        if (selecionado != null) {
            service.adicionarTarefaAoProjeto(id, selecionado.getId());
            mainFrame.atualizarTodasAbas();
        }
    }

    private void excluirTarefa() {
        String id = getTarefaIdSelecionada();
        if (id == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir a tarefa " + id + "?",
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            service.excluirTarefa(id);
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
