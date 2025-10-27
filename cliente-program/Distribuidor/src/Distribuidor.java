import java.util.ArrayList;

public class Distribuidor {
    public static final String HOST_PADRAO = "localhost";
    public static final int PORTA_PADRAO = 3000;

    static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("APLICAÇÃO CLIENTE");
        System.out.println("========================================");
        System.out.println("Digite quantas máquinas servidores estarão rodando:");
        System.out.print("> ");

        int quantidadeDeServidores = 0;
        try {
            quantidadeDeServidores = Teclado.getUmInt();
        } catch (Exception erro) {
        }

        ArrayList<String> serversConnection = new ArrayList<>();

        for(int i = 0; i < quantidadeDeServidores; i++)
        {
            int numeroDoServidor = i + 1;
            System.out.println("Digite o IP do Servidor " + numeroDoServidor + " e a Porta do Servidor no formato XXX.XXX.XXX.XXX:XXXX :");
            System.out.print("> ");
            String ipEPorta = null;
            try {
                ipEPorta = Teclado.getUmString();
            } catch (Exception erro) {
            }
            serversConnection.add(ipEPorta);
        }

        System.out.println("Digite quantos elementos o \"array grande\" deve ter:");
        System.out.print("> ");

        int quantidadeDeElementos = 0;
        try {
            quantidadeDeElementos = Teclado.getUmInt();
        } catch (Exception erro) {
        }

        System.out.println("Digite qual número você deseja procurar:");
        System.out.print("> ");

        int numeroDesejado = 0;
        try {
            numeroDesejado = Teclado.getUmInt();
        } catch (Exception erro) {
        }

        ArrayList<Servidor> servidores = new ArrayList<Servidor>();
        CriadoraDeConexao criadoraDeConexao = null;

        try
        {
            criadoraDeConexao = new CriadoraDeConexao(servidores, quantidadeDeElementos, numeroDesejado);
        }
        catch(Exception erro)
        {}

        for (String server : serversConnection) {

            String[] partes = server.split(":");
            String host = partes[0];
            int porta = Integer.parseInt(partes[1]);

            try {
                criadoraDeConexao.criaConexao(host, porta);
            } catch (Exception erro) {
                System.err.println("Verifique se o host e a porta passados condizem com o servidor ligado!\n");
                return;
            }

            System.out.println("O Cliente se conectou ao servidor: " + host + ":" + porta);
        }
    }
}
