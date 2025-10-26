import java.util.ArrayList;

public class TarefaContadora extends Thread
{
    private ArrayList<Integer> pacoteDeNumeros;
    private int numeroDesejado;

    private int contagemDoNumeroDesejado = 0;

    public TarefaContadora(
            ArrayList<Integer> pacoteDeNumeros,
            int numeroDesejado
    ) throws Exception
    {
        if (pacoteDeNumeros == null) throw new Exception("Pacote de números ausente");
        this.pacoteDeNumeros = pacoteDeNumeros;
        this.numeroDesejado = numeroDesejado;
    }

    public int getTotalDaContagem()
    {
        return this.contagemDoNumeroDesejado;
    }

    @Override
    public void run() {
        for(Integer numero : pacoteDeNumeros)
        {
            if (numero == this.numeroDesejado) this.contagemDoNumeroDesejado++;
        }
    }
}
