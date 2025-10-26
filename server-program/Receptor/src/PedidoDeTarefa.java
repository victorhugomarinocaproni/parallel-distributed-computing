import java.util.ArrayList;

public class PedidoDeTarefa extends Comunicado
{
    private ArrayList<Integer> pacoteCompletoDeNumeros;
    private int numeroDesejado;

    public PedidoDeTarefa(
            ArrayList<Integer> arrayDeNumeros,
            int numeroDesejado
    ) throws Exception
    {
        if (arrayDeNumeros == null) throw new Exception("O Cliente deve mandar um pacote de números instânciado");
        this.pacoteCompletoDeNumeros = arrayDeNumeros;
        this.numeroDesejado = numeroDesejado;
    }

    public ArrayList<Integer> getPacoteCompletoDeNumeros()
    {
        return this.pacoteCompletoDeNumeros;
    }

    public int getNumeroDesejado()
    {
        return this.numeroDesejado;
    }
}
