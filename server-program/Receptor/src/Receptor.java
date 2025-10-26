import java.util.ArrayList;

public class Receptor {
    public static String PORTA_PADRAO = "3000";

    static void main(String[] args) {

        if (args.length > 1) {
            System.err.println("Uso esperado: java Servidor [PORTA]\n");
            return;
        }

        String porta = Receptor.PORTA_PADRAO;

        if (args.length == 1)
            porta = args[0];

        ArrayList<Cliente> clientes = new ArrayList<Cliente>();
        AceitadoraDeConexao aceitadoraDeConexao = null;

        try {
            aceitadoraDeConexao = new AceitadoraDeConexao(porta, clientes);
            aceitadoraDeConexao.start();
        } catch (Exception erro) {
            System.err.println("Escolha uma porta apropriada e liberada para uso!\n");
            return;
        }

        for (;;)
        {
            System.out.println("O servidor esta ativo! Para desativa-lo,");
            System.out.println("use o comando \"desativar\"\n");
            System.out.print("> ");

            String comando = null;
            try {
                comando = Teclado.getUmString();
            } catch (Exception erro) {
            }

            if (comando.toLowerCase().equals("desativar")) {

                ComunicadoEncerramento comunicadoEncerramento = new ComunicadoEncerramento();

                for (Cliente cliente : clientes)
                {
                    try
                    {
                        cliente.recebaComunicado(comunicadoEncerramento);
                        cliente.adeus();
                    }
                    catch (Exception erro)
                    {}
                }

                System.out.println("O servidor foi desativado!\n");
                System.exit(0);
            } else
                System.err.println("Comando invalido!\n");
        }
    }
}
