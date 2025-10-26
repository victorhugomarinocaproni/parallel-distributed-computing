import java.util.ArrayList;

public class Distribuidor {
    public static final String HOST_PADRAO = "localhost";
    public static final int PORTA_PADRAO = 3000;

    static void main(String[] args) {

        if (args.length > 2) {
            System.err.println("Uso esperado: java Cliente [HOST [PORTA]]\n");
            return;
        }

        String host = Distribuidor.HOST_PADRAO;
        int porta = Distribuidor.PORTA_PADRAO;

        if (args.length > 0)
            host = args[0];

        if (args.length == 2)
            porta = Integer.parseInt(args[1]);


        ArrayList<Servidor> servidores = new ArrayList<Servidor>();
        CriadoraDeConexao criadoraDeConexao = null;

        try {
            criadoraDeConexao = new CriadoraDeConexao(servidores);
            criadoraDeConexao.criaConexao(host, porta);
        } catch (Exception erro) {
            System.err.println("Verifique se o host e a porta passados condizem com o servidor ligado!\n");
            return;
        }

        System.out.println("O Cliente se conectou ao servidor: " + host + ":" + porta);
    }
}
