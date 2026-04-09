package ui;

import model.Usuario;
import model.Tarefa;
import service.GestaoService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class UsuariosPanel extends JPanel {

    private GestaoService service;
    private MainFrame mainFrame;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblContador;

    private static final String[] COLUNAS = {"ID", "Nome", "Email", "Cargo", "Nº Tarefas"};

    public UsuariosPanel(GestaoService service, MainFrame mainFrame) {
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

        JLabel titulo = new JLabel("👥 Gestão de Usuários");
        titulo.setFont(MainFrame.FONTE_TITULO);
        titulo.setForeground(MainFrame.COR_TEXTO);

        lblContador = new JLabel("0 usuários");
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

        int[] larguras = {70, 180, 220, 150, 80};
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
        // Coluna nº tarefas centralizada
        tabela.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            { setHorizontalAlignment(SwingConstants.CENTER); }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.COR_BORDA));
        add(scroll, BorderLayout.CENTER);

        // Botões
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnPanel.setBackground(MainFrame.COR_FUNDO);

        JButton btnNovo   = MainFrame.criarBotao("➕ Novo Usuário", MainFrame.COR_PRIMARIA);
        JButton btnVerTar = MainFrame.criarBotao("📋 Ver Tarefas", new Color(33, 150, 243));
        JButton btnExcluir= MainFrame.criarBotao("🗑️ Excluir", MainFrame.COR_PERIGO);

        btnNovo.addActionListener(e -> abrirDialogoNovoUsuario());
        btnVerTar.addActionListener(e -> verTarefasDoUsuario());
        btnExcluir.addActionListener(e -> excluirUsuario());

        btnPanel.add(btnNovo);
        btnPanel.add(btnVerTar);
        btnPanel.add(btnExcluir);
        add(btnPanel, BorderLayout.SOUTH);

        atualizar();
    }

    public void atualizar() {
        modeloTabela.setRowCount(0);
        List<Usuario> usuarios = service.getUsuarios();
        for (Usuario u : usuarios) {
            modeloTabela.addRow(new Object[]{
                u.getId(), u.getNome(), u.getEmail(), u.getCargo(), u.getTotalTarefas()
            });
        }
        lblContador.setText(usuarios.size() + " usuário(s)");
    }

    private String getUsuarioIdSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return (String) modeloTabela.getValueAt(row, 0);
    }

    private void abrirDialogoNovoUsuario() {
        JDialog dialog = new JDialog(mainFrame, "➕ Novo Usuário", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(mainFrame);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        JTextField txtNome  = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtCargo = new JTextField(20);

        adicionarCampo(form, gbc, "Nome: *", txtNome, 0);
        adicionarCampo(form, gbc, "Email: *", txtEmail, 1);
        adicionarCampo(form, gbc, "Cargo:", txtCargo, 2);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        JButton btnSalvar   = MainFrame.criarBotao("💾 Criar", MainFrame.COR_PRIMARIA);
        JButton btnCancelar = MainFrame.criarBotao("Cancelar", new Color(108, 117, 125));

        btnSalvar.addActionListener(e -> {
            String nome  = txtNome.getText().trim();
            String email = txtEmail.getText().trim();
            String cargo = txtCargo.getText().trim();

            if (nome.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nome e Email são obrigatórios!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Usuario.emailValido(email)) {
                JOptionPane.showMessageDialog(dialog, "Email inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Usuario novo = service.criarUsuario(nome, email, cargo.isEmpty() ? "Não informado" : cargo);
            if (novo == null) {
                JOptionPane.showMessageDialog(dialog, "Email já cadastrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(dialog, "✅ Usuário criado! ID: " + novo.getId());
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

    private void verTarefasDoUsuario() {
        String id = getUsuarioIdSelecionado();
        if (id == null) return;

        List<Tarefa> tarefas = service.getTarefasDoUsuario(id);
        String nomeUsuario = service.buscarUsuarioPorId(id).map(u -> u.getNome()).orElse(id);

        if (tarefas.isEmpty()) {
            JOptionPane.showMessageDialog(this, nomeUsuario + " não tem tarefas atribuídas.", "Tarefas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("<html><b>Tarefas de " + nomeUsuario + ":</b><br><br>");
        for (Tarefa t : tarefas) {
            String cor = switch (t.getStatus()) {
                case PENDENTE     -> "#FF9800";
                case EM_ANDAMENTO -> "#2196F3";
                case CONCLUIDA    -> "#00C896";
                case CANCELADA    -> "#DC3545";
            };
            sb.append(String.format("● <b>%s</b> — <font color='%s'>%s</font><br>",
                t.getTitulo(), cor, t.getStatus().getDescricao()));
        }
        sb.append("</html>");

        JOptionPane.showMessageDialog(this, new JLabel(sb.toString()),
            "Tarefas de " + nomeUsuario, JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirUsuario() {
        String id = getUsuarioIdSelecionado();
        if (id == null) return;
        int c = JOptionPane.showConfirmDialog(this, "Excluir usuário " + id + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            service.excluirUsuario(id);
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
