package service;

import model.Tarefa;
import model.Usuario;
import model.Projeto;
import util.JsonUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe GestaoService - Camada de serviço (regras de negócio).
 *
 * CONCEITOS APLICADOS:
 * - Separação de responsabilidades: UI não acessa os dados diretamente
 * - Padrão Facade: interface simples para operações complexas
 * - Streams Java: filtragem e transformação de coleções
 */
public class GestaoService {

    // =============================================
    // LISTAS EM MEMÓRIA (estado da aplicação)
    // =============================================
    private List<Tarefa> tarefas;
    private List<Usuario> usuarios;
    private List<Projeto> projetos;

    // =============================================
    // CONSTRUTOR - carrega dados do disco
    // =============================================
    public GestaoService() {
        JsonUtil.inicializar();
        this.tarefas = JsonUtil.carregarTarefas();
        this.usuarios = JsonUtil.carregarUsuarios();
        this.projetos = JsonUtil.carregarProjetos();
    }

    // =============================================
    // OPERAÇÕES DE TAREFAS
    // =============================================

    public Tarefa criarTarefa(String titulo, String descricao, Tarefa.Prioridade prioridade) {
        Tarefa nova = new Tarefa(titulo, descricao, prioridade);
        tarefas.add(nova);
        salvarTudo();
        return nova;
    }

    public boolean excluirTarefa(String id) {
        Optional<Tarefa> t = buscarTarefaPorId(id);
        if (t.isPresent()) {
            tarefas.remove(t.get());
            // Remove da lista de todos os usuários
            usuarios.forEach(u -> u.removerTarefa(id));
            // Remove de todos os projetos
            projetos.forEach(p -> p.removerTarefa(id));
            salvarTudo();
            return true;
        }
        return false;
    }

    public boolean alterarStatusTarefa(String id, Tarefa.Status novoStatus) {
        Optional<Tarefa> t = buscarTarefaPorId(id);
        if (t.isPresent()) {
            t.get().alterarStatus(novoStatus);
            salvarTudo();
            return true;
        }
        return false;
    }

    public boolean editarTarefa(String id, String novoTitulo, String novaDescricao, Tarefa.Prioridade prioridade) {
        Optional<Tarefa> t = buscarTarefaPorId(id);
        if (t.isPresent()) {
            t.get().atualizarTitulo(novoTitulo);
            t.get().atualizarDescricao(novaDescricao);
            t.get().setPrioridade(prioridade);
            salvarTudo();
            return true;
        }
        return false;
    }

    public boolean atribuirTarefaAUsuario(String tarefaId, String usuarioId) {
        Optional<Tarefa> t = buscarTarefaPorId(tarefaId);
        Optional<Usuario> u = buscarUsuarioPorId(usuarioId);
        if (t.isPresent() && u.isPresent()) {
            u.get().atribuirTarefa(t.get());
            salvarTudo();
            return true;
        }
        return false;
    }

    public boolean adicionarTarefaAoProjeto(String tarefaId, String projetoId) {
        Optional<Tarefa> t = buscarTarefaPorId(tarefaId);
        Optional<Projeto> p = buscarProjetoPorId(projetoId);
        if (t.isPresent() && p.isPresent()) {
            p.get().adicionarTarefa(t.get());
            salvarTudo();
            return true;
        }
        return false;
    }

    public Optional<Tarefa> buscarTarefaPorId(String id) {
        return tarefas.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    public List<Tarefa> getTarefas() { return Collections.unmodifiableList(tarefas); }

    public List<Tarefa> getTarefasPorStatus(Tarefa.Status status) {
        return tarefas.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Tarefa> getTarefasDoUsuario(String usuarioId) {
        Optional<Usuario> u = buscarUsuarioPorId(usuarioId);
        if (u.isEmpty()) return Collections.emptyList();
        List<String> ids = u.get().getTarefasIds();
        return tarefas.stream()
                .filter(t -> ids.contains(t.getId()))
                .collect(Collectors.toList());
    }

    public List<Tarefa> getTarefasDoProjeto(String projetoId) {
        Optional<Projeto> p = buscarProjetoPorId(projetoId);
        if (p.isEmpty()) return Collections.emptyList();
        List<String> ids = p.get().getTarefasIds();
        return tarefas.stream()
                .filter(t -> ids.contains(t.getId()))
                .collect(Collectors.toList());
    }

    // =============================================
    // OPERAÇÕES DE USUÁRIOS
    // =============================================

    public Usuario criarUsuario(String nome, String email, String cargo) {
        if (!Usuario.emailValido(email)) return null;
        boolean emailExiste = usuarios.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (emailExiste) return null;
        Usuario novo = new Usuario(nome, email, cargo);
        usuarios.add(novo);
        salvarTudo();
        return novo;
    }

    public boolean excluirUsuario(String id) {
        Optional<Usuario> u = buscarUsuarioPorId(id);
        if (u.isPresent()) {
            usuarios.remove(u.get());
            salvarTudo();
            return true;
        }
        return false;
    }

    public Optional<Usuario> buscarUsuarioPorId(String id) {
        return usuarios.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public List<Usuario> getUsuarios() { return Collections.unmodifiableList(usuarios); }

    // =============================================
    // OPERAÇÕES DE PROJETOS
    // =============================================

    public Projeto criarProjeto(String nome, String descricao) {
        Projeto novo = new Projeto(nome, descricao);
        projetos.add(novo);
        salvarTudo();
        return novo;
    }

    public boolean excluirProjeto(String id) {
        Optional<Projeto> p = buscarProjetoPorId(id);
        if (p.isPresent()) {
            projetos.remove(p.get());
            salvarTudo();
            return true;
        }
        return false;
    }

    public boolean alterarEstadoProjeto(String id, Projeto.EstadoProjeto novoEstado) {
        Optional<Projeto> p = buscarProjetoPorId(id);
        if (p.isPresent()) {
            p.get().alterarEstado(novoEstado);
            salvarTudo();
            return true;
        }
        return false;
    }

    public Optional<Projeto> buscarProjetoPorId(String id) {
        return projetos.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public List<Projeto> getProjetos() { return Collections.unmodifiableList(projetos); }

    public double getProgressoProjeto(String projetoId) {
        Optional<Projeto> p = buscarProjetoPorId(projetoId);
        return p.map(proj -> proj.calcularProgresso(tarefas)).orElse(0.0);
    }

    // =============================================
    // ESTATÍSTICAS DO DASHBOARD
    // =============================================

    public Map<String, Integer> getEstatisticas() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("Total Tarefas", tarefas.size());
        stats.put("Pendentes", (int) tarefas.stream().filter(t -> t.getStatus() == Tarefa.Status.PENDENTE).count());
        stats.put("Em Andamento", (int) tarefas.stream().filter(t -> t.getStatus() == Tarefa.Status.EM_ANDAMENTO).count());
        stats.put("Concluídas", (int) tarefas.stream().filter(t -> t.getStatus() == Tarefa.Status.CONCLUIDA).count());
        stats.put("Total Usuários", usuarios.size());
        stats.put("Total Projetos", projetos.size());
        return stats;
    }

    // =============================================
    // PERSISTÊNCIA
    // =============================================

    private void salvarTudo() {
        JsonUtil.salvarTarefas(tarefas);
        JsonUtil.salvarUsuarios(usuarios);
        JsonUtil.salvarProjetos(projetos);
    }
}
