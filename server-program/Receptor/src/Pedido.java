public class Pedido extends Comunicado
{
    private byte[] numeros;
    private byte procurado;

    public Pedido(
            byte[] numeros,
            byte procurado
    ) throws  Exception
    {
        if (numeros == null) throw new Exception("Passe um vetor de número");
        this.numeros = numeros;
        this.procurado = procurado;
    }

    public int contar()
    {
        int contador = 0;
        for (byte numero : this.numeros)
        {
            if (numero == this.procurado)
                contador++;
        }
        return contador;
    }
}
