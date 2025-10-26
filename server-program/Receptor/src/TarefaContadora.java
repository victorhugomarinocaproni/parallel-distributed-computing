public class TarefaContadora extends Thread
{
    private byte[] pacoteDeNumeros;
    private int numeroDesejado;

    private int contagemDoNumeroDesejado = 0;

    public TarefaContadora(
            byte[] pacoteDeNumeros,
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
        for(byte numero : pacoteDeNumeros)
        {
            if (numero == this.numeroDesejado) this.contagemDoNumeroDesejado++;
        }
    }
}
