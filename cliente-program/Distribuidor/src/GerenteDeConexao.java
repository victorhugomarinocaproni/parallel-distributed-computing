import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GerenteDeConexao extends Thread
{
    private ArrayList<Servidor> servidores;
    private Socket conexao;
    private Servidor servidor;

    private int tamanhoDoArray;
    private int numeroDesejado;

    public GerenteDeConexao(
            Socket conexao,
            ArrayList<Servidor> servidores,
            int tamanhoDoArray,
            int numeroDesejado
    ) throws Exception
    {
        if (conexao == null) throw new Exception ("Conexão ausente");
        this.conexao = conexao;
        this.servidores = servidores;
        this.tamanhoDoArray = tamanhoDoArray;
        this.numeroDesejado = numeroDesejado;
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

        int MAX = 100;
        int MIN = -100;
        byte[] numeros = new byte[this.tamanhoDoArray];

        for(int i = 0; i < this.tamanhoDoArray; i++)
        {
            int aleatorio = ((int)(Math.random()*(MAX-MIN)))+MIN;
            numeros[i] = (byte)aleatorio;
        }

        long inicio = System.currentTimeMillis();
        System.out.println("Início: " + inicio);

        try
        {
            int tamanhoDoSubPacote = numeros.length / this.servidores.size();
            ArrayList<byte[]> subPacotes = new ArrayList<>();

            for (int i = 0; i < this.servidores.size(); i++) {
                int start = i * tamanhoDoSubPacote;
                int end = (i == this.servidores.size() - 1) ? numeros.length : start + tamanhoDoSubPacote;
                subPacotes.add(Arrays.copyOfRange(numeros, start, end));
            }

            for(int i = 0; i < this.servidores.size(); i++)
            {
                byte[] subPacoteDoPedido = subPacotes.get(i);
                PedidoDeTarefa pedido = new PedidoDeTarefa(subPacoteDoPedido, this.numeroDesejado);
                this.servidor = new Servidor(this.conexao, receptorDeComunicado, transmissorDeComunicado);
                synchronized (this.servidores)
                {
                    this.servidores.add(this.servidor);
                }
                this.servidor.recebaComunicado(pedido);
                this.servidor.start();
            }
        }
        catch(Exception error)
        {
            // Não vai dar erro, confia!
        }

        try
        {
            for(;;)
            {
                for(Servidor servidor : this.servidores)
                {
                    servidor.join();
                }

                int total = 0;
                for (Servidor servidor : this.servidores) {
                    total += servidor.getResposta().getContagem();
                }
                long fim = System.currentTimeMillis();
                System.out.println("Fim: " + fim);
                System.out.println("O total de vezes que o número: " + this.numeroDesejado + " foi encontrado é: " + total);
                System.exit(0);
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
