import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GerenteDeConexao extends Thread {
    private ArrayList<Servidor> servidores;

    private int tamanhoDoArray;
    private int numeroDesejado;

    private volatile Comunicado proximoComunicado = null;
    private byte[] pacoteDeNumerosASerProcessado = null;

    private final Object travaCompartilhada = new Object();

    public GerenteDeConexao(
            ArrayList<Servidor> servidores,
            int tamanhoDoArray
    ) throws Exception {
        this.servidores = servidores;
        this.tamanhoDoArray = tamanhoDoArray;
    }

    public void adicionaNovaConexaoDeServidor(Socket conexao) {
        ObjectOutputStream transmissorDeComunicado = null;
        try {
            transmissorDeComunicado = new ObjectOutputStream(conexao.getOutputStream());
        } catch (IOException e) {
            return;
        }

        ObjectInputStream receptorDeComunicado = null;
        try {
            receptorDeComunicado = new ObjectInputStream(conexao.getInputStream());
        } catch (Exception error) {
            try {
                transmissorDeComunicado.close();
            } catch (Exception falha) {
            } // so tentando fechar antes de acabar a thread

            return;
        }
        Servidor servidor = null;
        try {
            servidor = new Servidor(conexao, receptorDeComunicado, transmissorDeComunicado);
        } catch (Exception error) {
        }
        synchronized (this.servidores) {
            this.servidores.add(servidor);
        }
    }

    public void setNumeroDesejado(int numeroDesejado) {
        this.numeroDesejado = numeroDesejado;
    }

    public void enviaPedidoDeTarefaParaServidores() {
        this.proximoComunicado = new PedirPorTarefas();
    }

    public void exibePacoteDeNumerosASerProcessado() {
        System.out.println("Pacote de numeros: " + Arrays.toString(this.pacoteDeNumerosASerProcessado));
    }

    public Object getTravaCompartilhada()
    {
        return this.travaCompartilhada;
    }

    public void run() {
        int MAX = 100;
        int MIN = -100;
        byte[] numeros = new byte[this.tamanhoDoArray];

        for (int i = 0; i < this.tamanhoDoArray; i++) {
            int aleatorio = ((int) (Math.random() * (MAX - MIN))) + MIN;
            numeros[i] = (byte) aleatorio;
        }
        this.pacoteDeNumerosASerProcessado = numeros;

        try {
            for (;;) {
                if (this.proximoComunicado instanceof PedirPorTarefas) {
                    long inicio = System.currentTimeMillis();
                    System.out.println("[D] Início da(s) Tarefa(s): " + inicio);
                    try {
                        int tamanhoDoSubPacote = numeros.length / this.servidores.size();
                        ArrayList<byte[]> subPacotes = new ArrayList<>();

                        for (int i = 0; i < this.servidores.size(); i++) {
                            int start = i * tamanhoDoSubPacote;
                            int end = (i == this.servidores.size() - 1) ? numeros.length : start + tamanhoDoSubPacote;
                            subPacotes.add(Arrays.copyOfRange(numeros, start, end));
                        }

                        for (int i = 0; i < this.servidores.size(); i++) {
                            Servidor servidorAtual = this.servidores.get(i);
                            byte[] subPacoteDoPedido = subPacotes.get(i);

                            PedidoDeTarefa pedido = new PedidoDeTarefa(subPacoteDoPedido, this.numeroDesejado);
                            System.out.println("[D] Pedido de Tarefa enviado para o servidor " + servidorAtual.getConexao().getInetAddress() + ". O sub-pacote enviado foi: " + Arrays.toString(subPacoteDoPedido));

                            servidorAtual.recebaComunicado(pedido);
                            servidorAtual.start();
                        }
                    } catch (Exception error) {
                        // Não vai dar erro, confia!
                    }

                    for (Servidor servidor : this.servidores) {
                        servidor.join();
                    }

                    int total = 0;
                    for (Servidor servidor : this.servidores) {
                        total += servidor.getResposta().getContagem();
                    }
                    long fim = System.currentTimeMillis();
                    System.out.println("[D] Fim da(s) Tarefa(s): " + fim);
                    System.out.println("O total de vezes que o número: " + this.numeroDesejado + " foi encontrado é: " + total);
                    this.proximoComunicado = null;
                    synchronized (this.travaCompartilhada)
                    {
                        this.travaCompartilhada.notify();
                    }
                }
                else
                {
                    Thread.yield();
                }
            }
        } catch (Exception error) {
            try {
                for (Servidor servidor : this.servidores) {
                    servidor.fechaCanalDeInput();
                    servidor.fechaCanalDeOutput();
                    servidor.fechaConexao();
                }
            } catch (Exception falha) {
            } // so tentando fechar antes de acabar a thread
            return;
        }

    }
}
