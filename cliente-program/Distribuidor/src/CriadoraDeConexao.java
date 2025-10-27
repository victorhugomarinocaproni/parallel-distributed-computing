import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class CriadoraDeConexao extends Thread
{
    private ArrayList<Servidor> servidores;

    private int tamanhoDoArray;
    private int numeroDesejado;

    public CriadoraDeConexao(
            ArrayList<Servidor> servidores,
            int tamanhoDoArray,
            int numeroDesejado
    ) throws Exception
    {
        if (servidores == null) throw new Exception ("Instâncie um ArrayList de servidores.");
        this.servidores = servidores;
        this.tamanhoDoArray = tamanhoDoArray;
        this.numeroDesejado = numeroDesejado;
    }

    public void criaConexao(
            String host,
            int porta
    ) throws Exception
    {
        Socket novaConexao = null;
        try
        {
            novaConexao = new Socket(host, porta);
        }
        catch(Exception erro)
        {
            throw new Exception("Host ou porta inválidos.");
        }

        GerenteDeConexao gerenteDeConexao = null;
        try
        {
            gerenteDeConexao = new GerenteDeConexao(novaConexao, servidores, this.tamanhoDoArray, this.numeroDesejado);
        }
        catch(Exception error)
        {
            System.out.println("Error: " + error.getMessage());
        }
        gerenteDeConexao.start();
    }
}
