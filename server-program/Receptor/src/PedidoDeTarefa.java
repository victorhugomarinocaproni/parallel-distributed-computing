import java.util.Arrays;

public class PedidoDeTarefa extends Comunicado {
    private byte[] numeros;
    private byte procurado;

    public PedidoDeTarefa(
            byte[] numeros,
            byte procurado
    ) throws Exception {
        if (numeros == null) throw new Exception("O Cliente deve mandar um pacote de números instânciado");
        this.numeros = numeros;
        this.procurado = procurado;
    }

    public byte[] getNumeros() {
        return this.numeros;
    }

    public int getProcurado() {
        return this.procurado;
    }

    @Override
    public String toString() {
        return "Procurado: " + this.procurado + "\n  > Números: " + Arrays.toString(this.numeros);
    }
}
