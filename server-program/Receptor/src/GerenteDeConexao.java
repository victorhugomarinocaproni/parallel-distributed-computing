import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GerenteDeConexao extends Thread
{
    private ArrayList<Cliente> clientes;
    private Socket conexao;
    private Cliente cliente;

    private ArrayList<TarefaContadora> tarefas = new ArrayList<>();

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

        try
        {
            this.cliente = new Cliente(this.conexao, receptorDeComunicado, transmissorDeComunicado);
        }
        catch(Exception error)
        {
            // Não vai dar erro, confia!
        }

        try
        {
            this.clientes.add(this.cliente);

            for(;;)
            {
                Comunicado comunicado = this.cliente.envieComunicado();

                if (comunicado == null) return;

                if (comunicado instanceof PedidoDeTarefa)
                {
                    PedidoDeTarefa pedidoDeTarefa = (PedidoDeTarefa)comunicado;
                    System.out.printf(pedidoDeTarefa.toString());

                    int quantidadeDeProcessadores = Runtime.getRuntime().availableProcessors();

                    byte[] pacoteCompleto = pedidoDeTarefa.getPacoteCompletoDeNumeros();

                    int tamanhoDoSubPacote = pacoteCompleto.length / quantidadeDeProcessadores;
                    ArrayList<byte[]> subPacotes = new ArrayList<>();

                    for (int i = 0; i < quantidadeDeProcessadores; i++) {
                        int start = i * tamanhoDoSubPacote;
                        int end = (i == quantidadeDeProcessadores - 1) ? pacoteCompleto.length : start + tamanhoDoSubPacote;
                        subPacotes.add(Arrays.copyOfRange(pacoteCompleto, start, end));
                    }

                    for(int i = 0; i < quantidadeDeProcessadores; i++)
                    {
                        TarefaContadora novaTarefa = new TarefaContadora(subPacotes.get(i), pedidoDeTarefa.getNumeroDesejado());
                        novaTarefa.start();
                        this.tarefas.add(novaTarefa);
                    }

                    for (TarefaContadora tarefa : this.tarefas) {
                        tarefa.join();
                    }

                    int totalEncontrado = 0;
                    for (TarefaContadora tarefa : this.tarefas) {
                        totalEncontrado += tarefa.getTotalDaContagem();
                    }

                    Resposta resposta = new Resposta(totalEncontrado);
                    cliente.recebaComunicado(resposta);
                }
                else if (comunicado instanceof PedidoParaSair)
                {
                    this.clientes.remove(this.cliente);
                    this.cliente.adeus();
                }
            }
        }
        catch(Exception error)
        {
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
