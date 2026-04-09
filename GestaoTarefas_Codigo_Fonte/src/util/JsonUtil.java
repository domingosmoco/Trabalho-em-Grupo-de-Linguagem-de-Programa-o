package util;

import model.Tarefa;
import model.Usuario;
import model.Projeto;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Classe JsonUtil - Responsável pela persistência em ficheiros JSON.
 *
 * CONCEITOS APLICADOS:
 * - Responsabilidade Única (SRP): faz apenas leitura/escrita de JSON
 * - Serialização manual de objetos Java para JSON (sem bibliotecas externas)
 * - Tratamento de exceções com try-catch-finally
 */
public class JsonUtil {

    private static final String DATA_DIR = "dados";
    private static final String TAREFAS_FILE = DATA_DIR + "/tarefas.json";
    private static final String USUARIOS_FILE = DATA_DIR + "/usuarios.json";
    private static final String PROJETOS_FILE = DATA_DIR + "/projetos.json";

    // =============================================
    // INICIALIZAÇÃO
    // =============================================

    /** Cria os diretórios e ficheiros se não existirem */
    public static void inicializar() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            criarFicheiroSeNaoExistir(TAREFAS_FILE, "[]");
            criarFicheiroSeNaoExistir(USUARIOS_FILE, "[]");
            criarFicheiroSeNaoExistir(PROJETOS_FILE, "[]");
        } catch (IOException e) {
            System.err.println("Erro ao inicializar dados: " + e.getMessage());
        }
    }

    private static void criarFicheiroSeNaoExistir(String caminho, String conteudoPadrao) throws IOException {
        File ficheiro = new File(caminho);
        if (!ficheiro.exists()) {
            try (FileWriter fw = new FileWriter(ficheiro)) {
                fw.write(conteudoPadrao);
            }
        }
    }

    // =============================================
    // PERSISTÊNCIA DE TAREFAS
    // =============================================

    public static void salvarTarefas(List<Tarefa> tarefas) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < tarefas.size(); i++) {
            sb.append(tarefaParaJson(tarefas.get(i)));
            if (i < tarefas.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        escreverFicheiro(TAREFAS_FILE, sb.toString());
    }

    public static List<Tarefa> carregarTarefas() {
        List<Tarefa> tarefas = new ArrayList<>();
        String conteudo = lerFicheiro(TAREFAS_FILE);
        if (conteudo == null || conteudo.trim().equals("[]")) return tarefas;

        List<Map<String, String>> objetos = parsearJsonArray(conteudo);
        for (Map<String, String> obj : objetos) {
            Tarefa t = new Tarefa();
            t.setId(obj.getOrDefault("id", ""));
            t.setTitulo(obj.getOrDefault("titulo", ""));
            t.setDescricao(obj.getOrDefault("descricao", ""));
            t.setDataCreacao(obj.getOrDefault("dataCreacao", ""));
            t.setDataConclusao("null".equals(obj.get("dataConclusao")) ? null : obj.get("dataConclusao"));
            t.setUsuarioAtribuido("null".equals(obj.get("usuarioAtribuido")) ? null : obj.get("usuarioAtribuido"));
            t.setProjetoId("null".equals(obj.get("projetoId")) ? null : obj.get("projetoId"));
            try {
                t.setStatus(Tarefa.Status.valueOf(obj.getOrDefault("status", "PENDENTE")));
                t.setPrioridade(Tarefa.Prioridade.valueOf(obj.getOrDefault("prioridade", "MEDIA")));
            } catch (IllegalArgumentException e) {
                t.setStatus(Tarefa.Status.PENDENTE);
                t.setPrioridade(Tarefa.Prioridade.MEDIA);
            }
            tarefas.add(t);
        }
        return tarefas;
    }

    private static String tarefaParaJson(Tarefa t) {
        return String.format(
            "  {\"id\":\"%s\",\"titulo\":\"%s\",\"descricao\":\"%s\"," +
            "\"status\":\"%s\",\"prioridade\":\"%s\"," +
            "\"dataCreacao\":\"%s\",\"dataConclusao\":\"%s\"," +
            "\"usuarioAtribuido\":\"%s\",\"projetoId\":\"%s\"}",
            escapar(t.getId()), escapar(t.getTitulo()), escapar(t.getDescricao()),
            t.getStatus().name(), t.getPrioridade().name(),
            t.getDataCreacao(),
            t.getDataConclusao() != null ? t.getDataConclusao() : "null",
            t.getUsuarioAtribuido() != null ? t.getUsuarioAtribuido() : "null",
            t.getProjetoId() != null ? t.getProjetoId() : "null"
        );
    }

    // =============================================
    // PERSISTÊNCIA DE USUÁRIOS
    // =============================================

    public static void salvarUsuarios(List<Usuario> usuarios) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < usuarios.size(); i++) {
            sb.append(usuarioParaJson(usuarios.get(i)));
            if (i < usuarios.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        escreverFicheiro(USUARIOS_FILE, sb.toString());
    }

    public static List<Usuario> carregarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String conteudo = lerFicheiro(USUARIOS_FILE);
        if (conteudo == null || conteudo.trim().equals("[]")) return usuarios;

        List<Map<String, String>> objetos = parsearJsonArray(conteudo);
        for (Map<String, String> obj : objetos) {
            Usuario u = new Usuario();
            u.setId(obj.getOrDefault("id", ""));
            u.setNome(obj.getOrDefault("nome", ""));
            u.setEmail(obj.getOrDefault("email", ""));
            u.setCargo(obj.getOrDefault("cargo", ""));
            String ids = obj.getOrDefault("tarefasIds", "");
            if (!ids.isEmpty()) {
                String[] partes = ids.split(",");
                for (String p : partes) {
                    String trim = p.trim();
                    if (!trim.isEmpty()) u.getTarefasIds().add(trim);
                }
            }
            usuarios.add(u);
        }
        return usuarios;
    }

    private static String usuarioParaJson(Usuario u) {
        String idsJuntos = String.join(",", u.getTarefasIds());
        return String.format(
            "  {\"id\":\"%s\",\"nome\":\"%s\",\"email\":\"%s\",\"cargo\":\"%s\",\"tarefasIds\":\"%s\"}",
            escapar(u.getId()), escapar(u.getNome()), escapar(u.getEmail()),
            escapar(u.getCargo()), idsJuntos
        );
    }

    // =============================================
    // PERSISTÊNCIA DE PROJETOS
    // =============================================

    public static void salvarProjetos(List<Projeto> projetos) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < projetos.size(); i++) {
            sb.append(projetoParaJson(projetos.get(i)));
            if (i < projetos.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        escreverFicheiro(PROJETOS_FILE, sb.toString());
    }

    public static List<Projeto> carregarProjetos() {
        List<Projeto> projetos = new ArrayList<>();
        String conteudo = lerFicheiro(PROJETOS_FILE);
        if (conteudo == null || conteudo.trim().equals("[]")) return projetos;

        List<Map<String, String>> objetos = parsearJsonArray(conteudo);
        for (Map<String, String> obj : objetos) {
            Projeto p = new Projeto();
            p.setId(obj.getOrDefault("id", ""));
            p.setNome(obj.getOrDefault("nome", ""));
            p.setDescricao(obj.getOrDefault("descricao", ""));
            p.setDataCriacao(obj.getOrDefault("dataCriacao", ""));
            p.setDataFimPrevista(obj.getOrDefault("dataFimPrevista", null));
            p.setGerenteId(obj.getOrDefault("gerenteId", null));
            try {
                p.setEstado(Projeto.EstadoProjeto.valueOf(obj.getOrDefault("estado", "PLANEAMENTO")));
            } catch (IllegalArgumentException e) {
                p.setEstado(Projeto.EstadoProjeto.PLANEAMENTO);
            }
            String ids = obj.getOrDefault("tarefasIds", "");
            if (!ids.isEmpty()) {
                String[] partes = ids.split(",");
                for (String part : partes) {
                    String trim = part.trim();
                    if (!trim.isEmpty()) p.getTarefasIds().add(trim);
                }
            }
            projetos.add(p);
        }
        return projetos;
    }

    private static String projetoParaJson(Projeto p) {
        String idsJuntos = String.join(",", p.getTarefasIds());
        return String.format(
            "  {\"id\":\"%s\",\"nome\":\"%s\",\"descricao\":\"%s\"," +
            "\"estado\":\"%s\",\"dataCriacao\":\"%s\"," +
            "\"dataFimPrevista\":\"%s\",\"gerenteId\":\"%s\",\"tarefasIds\":\"%s\"}",
            escapar(p.getId()), escapar(p.getNome()), escapar(p.getDescricao()),
            p.getEstado().name(), p.getDataCriacao(),
            p.getDataFimPrevista() != null ? p.getDataFimPrevista() : "",
            p.getGerenteId() != null ? p.getGerenteId() : "",
            idsJuntos
        );
    }

    // =============================================
    // HELPERS DE IO E PARSE JSON SIMPLES
    // =============================================

    private static void escreverFicheiro(String caminho, String conteudo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminho))) {
            bw.write(conteudo);
        } catch (IOException e) {
            System.err.println("Erro ao escrever " + caminho + ": " + e.getMessage());
        }
    }

    private static String lerFicheiro(String caminho) {
        try {
            return new String(Files.readAllBytes(Paths.get(caminho)));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Parser JSON simples para arrays de objetos planos.
     * Suporta apenas strings como valores (suficiente para este projeto educacional).
     */
    private static List<Map<String, String>> parsearJsonArray(String json) {
        List<Map<String, String>> lista = new ArrayList<>();
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

        // Separa objetos individuais
        List<String> objetos = separarObjetos(json);
        for (String obj : objetos) {
            Map<String, String> mapa = parsearObjeto(obj.trim());
            if (!mapa.isEmpty()) lista.add(mapa);
        }
        return lista;
    }

    private static List<String> separarObjetos(String json) {
        List<String> objetos = new ArrayList<>();
        int depth = 0;
        int inicio = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) inicio = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && inicio != -1) {
                    objetos.add(json.substring(inicio, i + 1));
                    inicio = -1;
                }
            }
        }
        return objetos;
    }

    private static Map<String, String> parsearObjeto(String obj) {
        Map<String, String> mapa = new LinkedHashMap<>();
        if (!obj.startsWith("{") || !obj.endsWith("}")) return mapa;
        obj = obj.substring(1, obj.length() - 1).trim();

        // Extrai pares chave:valor com regex simples
        boolean emChave = false, emValor = false;
        StringBuilder chave = new StringBuilder(), valor = new StringBuilder();
        boolean emString = false;
        char[] chars = obj.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '"' && (i == 0 || chars[i - 1] != '\\')) {
                emString = !emString;
                if (!emString) {
                    if (emChave) { emChave = false; }
                    else if (emValor) {
                        mapa.put(chave.toString(), valor.toString());
                        chave = new StringBuilder();
                        valor = new StringBuilder();
                        emValor = false;
                    }
                } else {
                    if (!emValor) emChave = true;
                }
            } else if (emString) {
                if (emChave) chave.append(c);
                else if (emValor) valor.append(c);
            } else if (c == ':') {
                emValor = true;
            }
        }
        return mapa;
    }

    private static String escapar(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
