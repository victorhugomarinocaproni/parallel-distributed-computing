import java.util.Arrays;

public class PedidoDeTarefa extends Comunicado {
    private byte[] pacoteCompletoDeNumeros;
    private int numeroDesejado;

    public PedidoDeTarefa(
            byte[] arrayDeNumeros,
            int numeroDesejado
    ) throws Exception {
        if (arrayDeNumeros == null) throw new Exception("O Cliente deve mandar um pacote de números instânciado");
        this.pacoteCompletoDeNumeros = arrayDeNumeros;
        this.numeroDesejado = numeroDesejado;
    }

    public byte[] getPacoteCompletoDeNumeros() {
        return this.pacoteCompletoDeNumeros;
    }

    public int getNumeroDesejado() {
        return this.numeroDesejado;
    }

    @Override
    public String toString() {
        return "numeroDesejado: " + this.numeroDesejado +
                ", pacoteCompletoDeNumeros: " + Arrays.toString(this.pacoteCompletoDeNumeros);
    }
}
