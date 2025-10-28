import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Servidor extends Thread {
    private Socket conexao;
    private ObjectInputStream receptorDeComunicado;
    private ObjectOutputStream transmissorDeComunicado;

    private Comunicado proximoComunicado = null;
    private Semaphore lockMutex = new Semaphore(1, true);

    private PedidoDeTarefa tarefa = null;
    private Resposta resposta = null;

    private final Object lockResposta = new Object();

    private ServidorListener listener;

    public Servidor(
            Socket conexao,
            ObjectInputStream receptorDeComunicado,
            ObjectOutputStream transmissorDeComunicado
    ) throws Exception {
        if (conexao == null)
            throw new Exception("Conexao ausente");

        if (receptorDeComunicado == null)
            throw new Exception("Receptor ausente");

        if (transmissorDeComunicado == null)
            throw new Exception("Transmissor ausente");

        this.conexao = conexao;
        this.receptorDeComunicado = receptorDeComunicado;
        this.transmissorDeComunicado = transmissorDeComunicado;
    }

    public void recebaComunicado(Comunicado comunicado) throws Exception {
        try {
            if (comunicado instanceof PedidoDeTarefa) {
                this.tarefa = (PedidoDeTarefa) comunicado;
            }
            this.transmissorDeComunicado.writeObject(comunicado);
            this.transmissorDeComunicado.flush();
        } catch (IOException e) {
            throw new Exception("Erro de transmissao");
        }
    }

    public Comunicado envieComunicado() throws Exception {
        try {
            if (this.proximoComunicado == null)
                this.proximoComunicado = (Comunicado) this.receptorDeComunicado.readObject();
            Comunicado comunicado = this.proximoComunicado;
            this.proximoComunicado = null;
            return comunicado;
        } catch (IOException e) {
            throw new Exception("Erro de recepção");
        }
    }

    public void adeus() throws Exception {
        try {
            this.transmissorDeComunicado.close();
            this.receptorDeComunicado.close();
            this.conexao.close();
        } catch (IOException e) {
            throw new Exception("Erro de desconexão");
        }
    }

    public Resposta getResposta() {
        synchronized (this.lockResposta) {
            return this.resposta;
        }
    }

    public void setResposta(Resposta resposta) {
        synchronized (this.lockResposta) {
            this.resposta = resposta;
            lockResposta.notifyAll();
        }
    }

    public void setServidorListener(ServidorListener listener) {
        this.listener = listener;
    }

    public Socket getConexao() {
        return this.conexao;
    }

    public void fechaConexao() {
        try {
            this.conexao.close();
        } catch (Exception e) {
        }
    }

    public void fechaCanalDeInput() {
        try {
            this.receptorDeComunicado.close();
        } catch (Exception e) {
        }
    }

    public void fechaCanalDeOutput() {
        try {
            this.transmissorDeComunicado.close();
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        for (; ; ) {
            Comunicado comunicado = null;
            try {
                comunicado = this.envieComunicado();
            } catch (Exception error) {
            }

            if (comunicado instanceof Resposta) {
                this.setResposta((Resposta) comunicado);
                synchronized (this)
                {
                    this.notify();
                }
            }
            else if (comunicado instanceof ComunicadoEncerramento)
            {
                System.out.println("Servidor " + this.conexao.getInetAddress() + " desligado.");
                try {
                    this.adeus();
                    if (listener != null) {
                        listener.onServidorDesligado(this);
                    }
                } catch (Exception error) {
                }
                return;
            }
        }
    }
}
