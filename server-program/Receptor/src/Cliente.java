import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Cliente
{
    private Socket conexao;
    private ObjectInputStream receptorDeComunicado;
    private ObjectOutputStream transmissorDeComunicado;
    private Comunicado proximoComunicado = null;

    public Cliente(
            Socket conexao,
            ObjectInputStream receptorDeComunicado,
            ObjectOutputStream transmissorDeComunicado
    ) throws Exception {
        if (conexao == null) throw new Exception("Conexão ausente");
        if (receptorDeComunicado == null) throw new Exception("Receptor de comunicado ausente");
        if (transmissorDeComunicado == null) throw new Exception("Transmissor de comunicado ausente");
        this.conexao = conexao;
        this.receptorDeComunicado = receptorDeComunicado;
        this.transmissorDeComunicado = transmissorDeComunicado;
    }

    public void recebaComunicado(Comunicado comunicado) throws Exception
    {
        try
        {
            this.transmissorDeComunicado.writeObject(comunicado);
            this.transmissorDeComunicado.flush();
        }
        catch (IOException e)
        {
            throw new Exception("Erro de transmissão de comunicado.", e);
        }
    }

    public Comunicado espieComunicado() throws Exception
    {
        try
        {
            if (this.proximoComunicado == null) this.proximoComunicado = (Comunicado)this.receptorDeComunicado.readObject();
            return this.proximoComunicado;
        }
        catch (IOException e)
        {
            throw new Exception("Erro de recepção de comunicado.", e);
        }
    }

    public Comunicado envieComunicado() throws Exception
    {
        try
        {
            if (this.proximoComunicado == null) this.proximoComunicado = (Comunicado)this.receptorDeComunicado.readObject();

            Comunicado comunicadoASerEnviado = this.proximoComunicado;
            this.proximoComunicado = null;
            return comunicadoASerEnviado;
        }
        catch (IOException e)
        {
            throw new Exception("Erro de recepção de comunicado.", e);
        }
    }

    public void adeus() throws Exception
    {
        try
        {
            System.out.println("[R] Finalizando Streams de Comunicação e Encerrando conexão...");
            this.transmissorDeComunicado.close();
            this.receptorDeComunicado.close();
            this.conexao.close();
            System.out.println("[R] A Conexão com o Cliente foi Encerrada.");
        }
        catch (IOException e)
        {
            throw new Exception("Erro ao encerrar conexão.", e);
        }
    }
}
