import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class CriadoraDeConexao extends Thread
{
    private ArrayList<Servidor> servidores;
    private GerenteDeConexao gerenteDeConexao;

    private int tamanhoDoArray;

    public CriadoraDeConexao(
            ArrayList<Servidor> servidores,
            int tamanhoDoArray
    ) throws Exception
    {
        if (servidores == null) throw new Exception ("Instâncie um ArrayList de servidores.");
        this.servidores = servidores;
        this.tamanhoDoArray = tamanhoDoArray;

        GerenteDeConexao gerenteDeConexao = null;
        try
        {
            gerenteDeConexao = new GerenteDeConexao(servidores, this.tamanhoDoArray);
        }
        catch(Exception error)
        {
            System.out.println("Error: " + error.getMessage());
        }
        this.gerenteDeConexao =  gerenteDeConexao;
        this.gerenteDeConexao.start();
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
            this.gerenteDeConexao.adicionaNovaConexaoDeServidor(novaConexao);
        }
        catch(Exception erro)
        {
            throw new Exception("Host ou porta inválidos.");
        }
    }

    public GerenteDeConexao getGerenteDeConexao()
    {
        return this.gerenteDeConexao;
    }
}
