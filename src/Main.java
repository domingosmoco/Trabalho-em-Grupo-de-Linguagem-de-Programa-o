import ui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * ============================================================
 *  SISTEMA DE GESTÃO DE TAREFAS — Java Swing + JSON
 * ============================================================
 *
 * ESTRUTURA DO PROJETO:
 * ├── Main.java                  ← Ponto de entrada
 * ├── model/
 * │   ├── Tarefa.java            ← Classe modelo de Tarefa (com ENUMs Status e Prioridade)
 * │   ├── Usuario.java           ← Classe modelo de Usuário
 * │   └── Projeto.java           ← Classe modelo de Projeto
 * ├── service/
 * │   └── GestaoService.java     ← Regras de negócio / lógica da aplicação
 * ├── util/
 * │   └── JsonUtil.java          ← Persistência em ficheiros JSON (sem bibliotecas externas)
 * └── ui/
 *     ├── MainFrame.java         ← Janela principal (JFrame)
 *     ├── DashboardPanel.java    ← Aba Dashboard com estatísticas
 *     ├── TarefasPanel.java      ← Aba de gestão de tarefas
 *     ├── UsuariosPanel.java     ← Aba de gestão de usuários
 *     └── ProjetosPanel.java     ← Aba de gestão de projetos
 *
 * CONCEITOS DE POO APLICADOS:
 * - Encapsulamento   : atributos privados com getters/setters
 * - Herança          : panels estendem JPanel, MainFrame estende JFrame
 * - Composição       : Usuario TEM Tarefas, Projeto TEM Tarefas
 * - Polimorfismo     : renderers personalizados na JTable
 * - Abstração        : GestaoService abstrai operações complexas
 * - ENUMs            : Status, Prioridade, EstadoProjeto
 * - Streams          : filtragem funcional de coleções
 * - Persistência JSON: leitura/escrita manual de JSON
 *
 * FICHEIROS DE DADOS (criados automaticamente em ./dados/):
 * - tarefas.json
 * - usuarios.json
 * - projetos.json
 *
 * @author Sistema Educacional POO - Java
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {
        // SwingUtilities.invokeLater garante que a UI seja criada
        // na Event Dispatch Thread (EDT) — boa prática em Swing
        SwingUtilities.invokeLater(() -> {
            System.out.println("=== Sistema de Gestão de Tarefas ===");
            System.out.println("Iniciando interface gráfica...");
            new MainFrame();
        });
    }
}
