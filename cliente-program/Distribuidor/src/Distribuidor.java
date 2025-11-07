import java.util.ArrayList;

public class Distribuidor {
    public static void main(String[] args) {

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

        System.out.println("Digite quantos elementos o \"array grande\" deve ter. Se quiser o maior tamanho possível, digite [-1]: ");
        System.out.print("> ");

        int quantidadeDeElementos = 0;
        try {
            quantidadeDeElementos = Teclado.getUmInt();
        } catch (Exception erro) {
        }

        ArrayList<Servidor> servidores = new ArrayList<Servidor>();
        CriadoraDeConexao criadoraDeConexao = null;

        try
        {
            criadoraDeConexao = new CriadoraDeConexao(servidores, quantidadeDeElementos);
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

        for(;;)
        {
            System.out.println("Você deseja:");
            System.out.println("  [1] - Listar o pacote completo que será analisado.");
            System.out.println("  [2] - Enviar Pedido de Tarefa para o(s) Servidor(es).");
            System.out.println("  [3] - Rodar Testes com Vetor \"Controlado\".");
            System.out.println("  [4] - Rodar sem Paralelismo e sem Distribuição de Carga.");
            System.out.println("  [5] - Finalizar serviço.");
            System.out.print("> ");

            int opcaoEscolhida = -1;
            try {
                opcaoEscolhida = Teclado.getUmInt();
            } catch (Exception erro) {
            }

            if (opcaoEscolhida == 1)
            {
                criadoraDeConexao.getGerenteDeConexao().exibePacoteDeNumerosASerProcessado();
                continue;
            }

            if (opcaoEscolhida == 2)
            {

                System.out.println("Digite qual número você deseja procurar:");
                System.out.print("> ");

                int numeroDesejado = 0;
                try {
                    numeroDesejado = Teclado.getUmInt();
                } catch (Exception erro) {
                }

                criadoraDeConexao.getGerenteDeConexao().setNumeroDesejado(numeroDesejado);
                criadoraDeConexao.getGerenteDeConexao().enviaPedidoDeTarefaParaServidores();
                Object travaCompartilhada = null;
                try
                {
                    travaCompartilhada = criadoraDeConexao.getGerenteDeConexao().getTravaCompartilhada();
                }
                catch(Exception erro){}
                synchronized (travaCompartilhada)
                {
                    try
                    {
                        travaCompartilhada.wait();
                    } catch (InterruptedException e) {}
                }
            }

            if (opcaoEscolhida == 3)
            {
                criadoraDeConexao.getGerenteDeConexao().rodarTestes();
                Object travaCompartilhada = null;
                try
                {
                    travaCompartilhada = criadoraDeConexao.getGerenteDeConexao().getTravaCompartilhada();
                }
                catch(Exception erro){}
                synchronized (travaCompartilhada)
                {
                    try
                    {
                        travaCompartilhada.wait();
                    } catch (InterruptedException e) {}
                }
            }

            if (opcaoEscolhida == 4)
            {

                System.out.println("Digite qual número você deseja procurar:");
                System.out.print("> ");

                int numeroDesejado = 0;
                try {
                    numeroDesejado = Teclado.getUmInt();
                } catch (Exception erro) {
                }

                int max = 100;
                int min = -100;
                byte[] numeros = null;
                int tamanhoMaximo = Integer.MAX_VALUE;
                int tamanho = tamanhoMaximo;
                try {
                    numeros = new byte[tamanho];
                    System.out.printf("Vetor de %,d bytes alocado (limite do Java)%n", tamanho);
                } catch (OutOfMemoryError e) {
                    System.out.println("Não foi possível alocar vetor máximo. Tente um valor menor.");
                    numeros = new byte[1_000_000];
                }

                for (int i = 0; i < numeros.length; i++) {
                    int aleatorio = ((int) (Math.random() * (max - min))) + min;
                    numeros[i] = (byte) aleatorio;
                }

                long inicio = System.currentTimeMillis();
                System.out.println("[D] Início da(s) Tarefa(s): " + inicio);

                int contador = 0;
                for(byte numero : numeros)
                {
                    if (numero == numeroDesejado)
                    {
                        contador++;
                    }
                }

                long fim = System.currentTimeMillis();
                System.out.println("[D] Fim da(s) Tarefa(s): " + fim);

                System.out.println("O total de vezes que o número: " + numeroDesejado + " foi encontrado é: " + contador);
            }

            if (opcaoEscolhida == 5)
            {
                criadoraDeConexao.getGerenteDeConexao().enviaPedidoDeSaidaParaServidor();
                Object travaCompartilhada = null;
                try
                {
                    travaCompartilhada = criadoraDeConexao.getGerenteDeConexao().getTravaCompartilhada();
                }
                catch(Exception erro){}
                synchronized (travaCompartilhada)
                {
                    try
                    {
                        travaCompartilhada.wait();
                    } catch (InterruptedException e) {}
                }
                System.out.println("[D] Terminando todos os processos do programa...");
                System.out.println("========================================");
                System.exit(0);
            }
        }
    }
}
