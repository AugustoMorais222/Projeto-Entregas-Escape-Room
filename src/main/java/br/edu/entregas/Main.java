package br.edu.entregas;

import br.edu.entregas.model.Endereco;
import br.edu.entregas.service.EntregaService;
import br.edu.entregas.service.LoginService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LoginService login = new LoginService();
        System.out.println("=== SISTEMA DE ENTREGAS ===");
        System.out.print("Usuário: ");
        String usuario = sc.nextLine();
        System.out.print("Senha: ");
        String senha = sc.nextLine();
        if (!login.autenticar(usuario, senha)) {
            System.out.println("Acesso negado.");
            return;
        }

        EntregaService service = new EntregaService();
        Endereco endereco = new Endereco("Av. Goiás", "Sala 8", "1000",
                                        "74000-000", "Goiânia", "GO");
        service.cadastrar(1, "Notebook", "Notebook corporativo", 2.1,
                          4500.00, "AGUARDANDO ENVIO", endereco);
        System.out.println("Acesso autorizado. Mercadorias cadastradas:");
        service.listar().forEach(System.out::println);
    }
}
