import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class GerenteDeConexao extends Thread
{
    private ArrayList<Cliente> clientes;
    private Socket conexao;
    private Cliente cliente;

    private ArrayList<Pedido> tarefas = new ArrayList<>();

    public GerenteDeConexao(
            Socket conexao,
            ArrayList<Cliente> clientes) throws Exception
    {
        if (conexao==null) throw new Exception ("Conexão ausente");
        this.conexao = conexao;
        this.clientes = clientes;
    }

    public void run()
    {
        ObjectOutputStream transmissorDeComunicado = null;
        try
        {
            transmissorDeComunicado = new ObjectOutputStream(this.conexao.getOutputStream());
        } catch (IOException e) {
            return;
        }
        System.out.println("[R] Transmissor de Comunicado criado.");

        ObjectInputStream receptorDeComunicado = null;
        try
        {
            receptorDeComunicado= new ObjectInputStream(this.conexao.getInputStream());
        }
        catch (Exception error)
        {
            try
            {
                transmissorDeComunicado.close();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread

            return;
        }
        System.out.println("[R] Receptor de Comunicado criado.");

        try
        {
            this.cliente = new Cliente(this.conexao, receptorDeComunicado, transmissorDeComunicado);
        }
        catch(Exception error)
        {
            // Não vai dar erro, confia!
        }
        System.out.println("[R] Instância do Cliente criada com sucesso.");

        try
        {
            this.clientes.add(this.cliente);
            System.out.println("[R] Instância do Cliente adicionada à lista de conexões.\n");
            for(;;)
            {
                if (this.clientes.size() == 0) continue;
                Comunicado comunicado = this.cliente.envieComunicado();

                if (comunicado == null) return;

                if (comunicado instanceof PedidoDeTarefa)
                {
                    PedidoDeTarefa pedidoDeTarefa = (PedidoDeTarefa)comunicado;
                    System.out.println("\n[R] Pedido de Tarefa Recebido:");
                    System.out.println("  > " + pedidoDeTarefa.toString());

                    int quantidadeDeProcessadores = Runtime.getRuntime().availableProcessors();
                    System.out.println("[R] Processadores disponíveis na máquina: " + quantidadeDeProcessadores);

                    byte[] pacoteCompleto = pedidoDeTarefa.getNumeros();

                    System.out.println("[R] Dividindo pacote completo em sub-pacotes...");
                    int tamanhoDoSubPacote = pacoteCompleto.length / quantidadeDeProcessadores;
                    ArrayList<byte[]> subPacotes = new ArrayList<>();

                    for (int i = 0; i < quantidadeDeProcessadores; i++) {
                        int start = i * tamanhoDoSubPacote;
                        int end = (i == quantidadeDeProcessadores - 1) ? pacoteCompleto.length : start + tamanhoDoSubPacote;
                        subPacotes.add(Arrays.copyOfRange(pacoteCompleto, start, end));
                    }
                    System.out.println("[R] Sub-pacotes criados.");

                    System.out.println("[R] Criando Threads de Processamento...");
                    for(int i = 0; i < quantidadeDeProcessadores; i++)
                    {
                        Pedido novaTarefa = new Pedido(subPacotes.get(i), (byte)pedidoDeTarefa.getProcurado());
                        novaTarefa.start();
                        this.tarefas.add(novaTarefa);
                    }

                    for (Pedido tarefa : this.tarefas) {
                        tarefa.join();
                    }

                    System.out.println("[R] ...");
                    System.out.println("[R] Threads finalizadas.");

                    int totalEncontrado = 0;
                    for (Pedido tarefa : this.tarefas) {
                        totalEncontrado += tarefa.getTotalDaContagem();
                    }
                    this.tarefas.clear();

                    System.out.println("[R] Total encontrado: " +  totalEncontrado);
                    Resposta resposta = new Resposta(totalEncontrado);
                    System.out.println("[R] Enviando Resposta para Programa Cliente...");
                    cliente.recebaComunicado(resposta);
                    System.out.println("[R] Resposta enviada para Programa Cliente.");
                    System.out.println("\nO servidor esta ativo! Para desativa-lo,");
                    System.out.println("use o comando \"desativar\"\n");
                    System.out.print("> ");
                }
                else if (comunicado instanceof ComunicadoEncerramento)
                {
                    System.out.println("\n[R] Pedido de Saída Recebido");
                    System.out.println("[R] Removendo Cliente da Lista de Conexões...");
                    this.clientes.remove(this.cliente);
                    this.cliente.adeus();
                    System.out.println("\nO servidor esta ativo! Para desativa-lo,");
                    System.out.println("use o comando \"desativar\"\n");
                    System.out.print("> ");
                }
            }
        }
        catch(Exception error)
        {
            System.out.println("[R] Erro inesperado, tentando encerrar conexões...");
            try
            {
                transmissorDeComunicado.close();
                receptorDeComunicado.close();
            }
            catch(Exception falha)
            {} // so tentando fechar antes de acabar a thread
            return;
        }

    }
}
