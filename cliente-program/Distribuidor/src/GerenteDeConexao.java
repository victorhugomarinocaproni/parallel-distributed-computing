import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GerenteDeConexao extends Thread
{
    private ArrayList<Servidor> servidores;
    private Socket conexao;
    private Servidor servidor;


    public GerenteDeConexao(
            Socket conexao,
            ArrayList<Servidor> servidores) throws Exception
    {
        if (conexao == null) throw new Exception ("Conexão ausente");
        this.conexao = conexao;
        this.servidores = servidores;
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

        byte[] NUMEROS = new byte[] {1, 2, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 4, 5, 3, 4, 4, 3, 1, 1, 1, 2, 1,4 ,5, 6, 7, 8, 8};
        int NUMERO_DESEJADO = 1;

        try
        {
            long inicio = System.currentTimeMillis();
            System.out.println("Início: " + inicio);
            PedidoDeTarefa pedido = new PedidoDeTarefa(NUMEROS, NUMERO_DESEJADO);
            this.servidor = new Servidor(this.conexao, receptorDeComunicado, transmissorDeComunicado);
            synchronized (this.servidores)
            {
                this.servidores.add(this.servidor);
            }
            this.servidor.recebaComunicado(pedido);
            this.servidor.start();
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
                System.out.println("O total de vezes que o número: " + NUMERO_DESEJADO + " foi encontrado é: " + total);
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
