# Relatório de Resgate
- Equipe: Equipe 2
- Branch de trabalho: `resgate/equipe-2` (criada a partir de `release-dev`, commit `799c5ec`)
- Conta Git usada nos commits: AugustoMorais222

| Papel | Integrante |
| --- | --- |
| Investigador Git | Augusto |
| Responsável técnico | Kaique |
| Responsável pela recuperação | Augusto |
| Relator/Apresentador | Marcos |

## Diagnóstico

### Estado inicial do repositório
- Branch inicialmente ativa: `release-dev`.
- 7 branches locais, nenhum remoto: `main`, `backup-antigo`, `release-dev`, `docs-readme`, `feature-cadastro`, `hotfix-login`, `teste-login-descartavel`.
- 2 tags: `v1.0.0-funcional` (`356ec6f`) e `commit-perigoso` (`6572d8a`).
- `main` e `backup-antigo` estavam paradas na versão funcional (`356ec6f`); todos os problemas foram introduzidos depois, na linha da `release-dev`.
- Branches com conteúdo útil: `docs-readme` (README completo), `hotfix-login` (nota com a regra correta do login), `main`/`backup-antigo`/tag `v1.0.0-funcional` (código que funcionava). A `feature-cadastro` (`2624e5c`, validação do status) não foi integrada por estar fora do escopo do resgate.

Histórico recebido (`git log --oneline --all --graph --decorate`):

```
* 799c5ec (release-dev) chore: adicionar instrucoes do resgate
* 64f88f6 refactor: remover classe aparentemente sem uso
* a70ee84 feat: acelerar cadastro de mercadorias
* 0cd80f6 docs: simplificar readme
* 6572d8a (tag: commit-perigoso) chore: salvar credenciais para facilitar testes
* 9a6d3b0 (teste-login-descartavel) fix: liberar login para homologacao
| * a6f356d (hotfix-login) fix: documentar correção validada para autenticação
|/
| * 2624e5c (feature-cadastro) feat: validar status da mercadoria
|/
| * 7fe8faa (docs-readme) docs: detalhar fluxo de contribuição
|/
* 356ec6f (tag: v1.0.0-funcional, main, backup-antigo) feat: implementar sistema de entregas funcional
* 4574eee chore: criar estrutura inicial do projeto
```

### Porta 1: o projeto não compilava
`mvn clean package` falhava em `EntregaService.java` com três grupos de erro:

```
EntregaService.java:[6,28]  package br.edu.entregas.util does not exist
EntregaService.java:[14,9]  cannot find symbol: variable Validador   (linhas 14 a 17)
EntregaService.java:[19,33] constructor Mercadoria ... cannot be applied to given types
                            required: long,String,String,double,double,String,Endereco
                            found:    long,String,String,double,double,String
EntregaService.java:[20,19] cannot find symbol: method gravar(Mercadoria)
```

- Causa 1, commit `a70ee84` (Henrique Nunes, "feat: acelerar cadastro de mercadorias"): removeu o `endereco` da chamada ao construtor de `Mercadoria` e trocou `repository.salvar` por `repository.gravar`, método que não existe.
- Causa 2, commit `64f88f6` (Igor Reis): excluiu a classe `Validador`, usada pelo `EntregaService` (ver Porta 2).

Evidência (`git diff v1.0.0-funcional -- .../EntregaService.java`):

```diff
-        Mercadoria mercadoria = new Mercadoria(id, nome, descricao, peso, valor, status, endereco);
-        repository.salvar(mercadoria);
+        Mercadoria mercadoria = new Mercadoria(id, nome, descricao, peso, valor, status);
+        repository.gravar(mercadoria);
```

Comandos executados para resolver:

```bash
git diff v1.0.0-funcional -- src/main/java/br/edu/entregas/service/EntregaService.java
git restore --source=v1.0.0-funcional -- src/main/java/br/edu/entregas/service/EntregaService.java
```

Commit resultante: `ad06669`. A compilação só voltou a passar depois da Porta 2, que devolve o `Validador`.

### Porta 2: arquivo excluído
- Arquivo: `src/main/java/br/edu/entregas/util/Validador.java`.
- Commit da exclusão: `64f88f6`, autor Igor Reis <igor@entregas.local>, 03/09/2026, "refactor: remover classe aparentemente sem uso".
- Recuperado do commit pai (`64f88f6~1`) com `git restore --source`.


Comandos executados para resolver:

```bash
git log --diff-filter=D --summary
git show 64f88f6~1:src/main/java/br/edu/entregas/util/Validador.java
git restore --source=64f88f6~1 -- src/main/java/br/edu/entregas/util/Validador.java
```

Commit resultante: `4b4d6b7`.

### Porta 3: erro lógico no login
- Commit `9a6d3b0` (Felipe Rocha, "fix: liberar login para homologacao"), também apontado pela branch `teste-login-descartavel`.

```diff
-        return USUARIO.equals(usuario) && SENHA.equals(senha);
+        return USUARIO.equals(usuario) || senha == SENHA; // alteração urgente
```

- Com `||`, bastava o usuário ser `admin` para entrar com qualquer senha. Além disso, `senha == SENHA` compara referências de `String`, e não o conteúdo.
- A branch `hotfix-login` (`a6f356d`, Eva Martins) confirma a regra correta em `NOTA-HOTFIX.txt`: usar `equals` e exigir usuário e senha corretos.


Comandos executados para resolver:

```bash
git diff v1.0.0-funcional -- src/main/java/br/edu/entregas/service/LoginService.java
git diff v1.0.0-funcional hotfix-login
git restore --source=v1.0.0-funcional -- src/main/java/br/edu/entregas/service/LoginService.java
```

Commit resultante: `c518f86`.

### Porta 4: README prejudicado
- Commit `0cd80f6` (Gustavo Melo, "docs: simplificar readme") reduziu o README a "Pergunte ao desenvolvedor como executar" (1 linha inserida, 18 removidas).
- A versão mais completa está na branch `docs-readme` (`7fe8faa`, Carla Souza), que contém o README original mais a seção de fluxo de contribuição. Foi recuperada do histórico, sem reescrita.


Comandos executados para resolver:

```bash
git log --all -- README.md
git restore --source=docs-readme -- README.md
```

Commit resultante: `55f2bd9`.

### Porta 5: informação sensível versionada
- Arquivo: `config/application.properties`, contendo `db.user`, `db.password` e `api.token`.
- Commit: `6572d8a` (tag `commit-perigoso`), autor Felipe Rocha <felipe@entregas.local>, "chore: salvar credenciais para facilitar testes".
- Ação: arquivo removido da versão atual com `git rm` e incluído no `.gitignore`.
- **Risco remanescente:** apagar o arquivo cria apenas um novo commit sem ele. O commit `6572d8a` continua no histórico e qualquer pessoa com acesso ao repositório recupera o conteúdo com `git show commit-perigoso` ou `git show 6572d8a:config/application.properties`. Por isso a senha do banco e o token devem ser considerados comprometidos e **trocados**. Eliminar o segredo do histórico exigiria reescrevê-lo (`git filter-repo` ou BFG) e forçar o push, o que altera os hashes de todos os commits seguintes; não fizemos isso porque a atividade pede a preservação das evidências.


Comandos executados para resolver:

```bash
git show commit-perigoso
git rm config/application.properties
git check-ignore -v config/application.properties
git grep -n "SuperSenha123" HEAD
```

Commit resultante: `38020d9` (inclui a regra nova no `.gitignore`).

## Commits relevantes

### Investigados (origem dos problemas)
| Hash | Autor | Mensagem | Problema |
| --- | --- | --- | --- |
| `9a6d3b0` | Felipe Rocha | fix: liberar login para homologacao | Login aceita qualquer senha para `admin`. |
| `6572d8a` | Felipe Rocha | chore: salvar credenciais para facilitar testes | Senha e token versionados (tag `commit-perigoso`). |
| `0cd80f6` | Gustavo Melo | docs: simplificar readme | README esvaziado. |
| `a70ee84` | Henrique Nunes | feat: acelerar cadastro de mercadorias | Quebra a compilação e remove o endereço do cadastro. |
| `64f88f6` | Igor Reis | refactor: remover classe aparentemente sem uso | Exclui `Validador.java`. |

### Usados como fonte da recuperação
| Hash | Referência | Uso |
| --- | --- | --- |
| `356ec6f` | tag `v1.0.0-funcional`, `main`, `backup-antigo` | Versão funcional de `EntregaService` e `LoginService`. |
| `64f88f6~1` (`a70ee84`) | pai do commit de exclusão | Fonte do `Validador.java`. |
| `7fe8faa` | branch `docs-readme` | README completo. |
| `a6f356d` | branch `hotfix-login` | Confirmação da regra correta do login. |

### Commits da equipe na branch `resgate/equipe-2`
| Hash | Mensagem |
| --- | --- |
| `ad06669` | fix: restaurar cadastro de mercadoria com endereco |
| `4b4d6b7` | fix: recuperar classe Validador excluida |
| `c518f86` | fix: exigir usuario e senha corretos no login |
| `55f2bd9` | docs: recuperar README completo da branch docs-readme |
| `38020d9` | chore: remover credenciais versionadas e atualizar .gitignore |

O commit deste relatório e o commit de merge na `main` aparecem em `git log --oneline --graph main`. Nenhum commit foi revertido ou reescrito e nenhuma branch foi apagada: os commits problemáticos continuam no histórico como evidência.

## Validação final

Ambiente: Maven 3.9.11 com JDK 17.0.17 (Eclipse Adoptium), Windows 11.

| Item | Como foi validado | Resultado |
| --- | --- | --- |
| Compilação | `mvn clean package` | Concluído sem erro (código de saída 0). |
| Login correto | `java -cp target/classes br.edu.entregas.Main` com `admin` / `12345678` | "Acesso autorizado." |
| Senha incorreta | `admin` / `errada` | "Acesso negado." |
| Usuário incorreto | `joao` / `12345678` | "Acesso negado." |
| Usuário e senha incorretos | `joao` / `errada` | "Acesso negado." |
| Cadastro com endereço | Saída do login correto | Mercadoria listada com o endereço de entrega (abaixo). |
| README | Leitura do arquivo recuperado | Informa requisitos, compilação, execução e credenciais de demonstração. |
| Segurança | `git ls-files config` e `git grep "SuperSenha123" HEAD` sem resultado; `git check-ignore` aponta a regra do `.gitignore` | Credenciais fora da versão atual e arquivo ignorado. |

Saída do sistema com login válido:

```
=== SISTEMA DE ENTREGAS ===
Usuário: Senha: Acesso autorizado. Mercadorias cadastradas:
#1 | Notebook | Notebook corporativo | 2.1 kg | R$ 4500,00 | AGUARDANDO ENVIO | Entrega: Av. Goiás, 1000 - Sala 8, Goiânia/GO - CEP: 74000-000
```

Observação: na máquina usada, o `java` do PATH era o Java 8, que não executa classes compiladas para Java 17 (`UnsupportedClassVersionError`). A execução foi feita com o `java` do JDK 17 (`%JAVA_HOME%\bin\java`).

## Pergunta de encerramento
*Como o uso adequado do controle de configuração reduz riscos técnicos, facilita auditorias e permite recuperar um projeto mesmo depois de alterações incorretas?*

Neste resgate, o Git respondeu a três perguntas que o código sozinho não responde:

- **Risco técnico:** a tag `v1.0.0-funcional` deu uma linha de base confiável. Com `git diff` contra ela, cada regressão foi localizada em poucas linhas, sem precisar reescrever nada por tentativa e erro. Trabalhar em uma branch de resgate e integrar com `merge --no-ff` manteve a `main` estável até a validação.
- **Auditoria:** cada alteração tem autor, data, mensagem e hash. Foi possível dizer quem liberou o login, quem versionou as credenciais e quem excluiu a classe, e provar isso com `git log` e `git show`.
- **Recuperação:** nada do que foi commitado se perdeu. O arquivo excluído, o README e o código funcional voltaram com `git restore --source`, a partir de commits, tags e branches antigos.

O mesmo mecanismo que permite recuperar também explica o risco de segurança: o histórico não esquece, então um segredo commitado continua acessível mesmo depois de apagado, e a única correção real é trocar a credencial.
