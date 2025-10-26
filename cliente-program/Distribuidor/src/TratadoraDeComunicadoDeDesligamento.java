import java.net.*;

public class TratadoraDeComunicadoDeDesligamento extends Thread
{
    private Servidor servidor;

    public TratadoraDeComunicadoDeDesligamento (Servidor servidor) throws Exception
    {
        if (servidor==null)
            throw new Exception ("Porta invalida");

        this.servidor = servidor;
    }

    public void run ()
    {
        for(;;)
        {
            try
            {
                if (this.servidor.espieComunicado() instanceof ComunicadoDeDesligamento)
                {
                    System.out.println ("\nO servidor vai ser desligado agora;");
                    System.err.println ("volte mais tarde!\n");
                }
            }
            catch (Exception erro)
            {}
        }
    }
}
