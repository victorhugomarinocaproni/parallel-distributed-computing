import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AceitadoraDeConexao extends Thread
{
    private ServerSocket pedido;
    private ArrayList<Cliente> clientes;

    public AceitadoraDeConexao(
            String porta,
            ArrayList<Cliente> clientes
    ) throws Exception
    {
        if (porta==null) throw new Exception ("Porta ausente");

        try
        {
            this.pedido = new ServerSocket(Integer.parseInt(porta));
            this.clientes = clientes;
        }
        catch (Exception e)
        {
            throw new Exception("Porta inválida.", e);
        }
    }

    public void run ()
    {
        for(;;)
        {
            Socket conexao = null;
            try
            {
                conexao = this.pedido.accept();
                System.out.println("\n[R] Cliente conectado com sucesso!");
            }
            catch(Exception error)
            {
                System.out.println("Error: " + error.getMessage());
                continue;
            }

            GerenteDeConexao gerenteDeConexao = null;
            try
            {
                gerenteDeConexao = new GerenteDeConexao(conexao, this.clientes);
            }
            catch(Exception error)
            {
                System.out.println("Error: " + error.getMessage());
            }
            gerenteDeConexao.start();
        }
    }
}
