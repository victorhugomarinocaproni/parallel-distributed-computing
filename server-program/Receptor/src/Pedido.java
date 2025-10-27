import java.util.Arrays;

public class Pedido extends Thread
{
    private byte[] numeros;
    private byte procurado;

    private int contagemDoNumeroDesejado = 0;

    public Pedido(
            byte[] numeros,
            byte procurado
    ) throws Exception
    {
        if (numeros == null) throw new Exception("Pacote de números ausente");
        this.numeros = numeros;
        this.procurado = procurado;
    }

    public int getTotalDaContagem()
    {
        return this.contagemDoNumeroDesejado;
    }

    @Override
    public void run() {
        System.out.println("[R] Thread lançada, analisando sub-pacote:\n    > " + Arrays.toString(this.numeros));
        for(byte numero : numeros)
        {
            if (numero == this.procurado) this.contagemDoNumeroDesejado++;
        }
    }
}
