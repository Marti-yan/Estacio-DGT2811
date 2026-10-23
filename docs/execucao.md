# Executando o projeto

## Ambiente

- JDK 17 e Apache Ant 1.10.15 ou posterior no PATH.
- GlassFish 7.1.1 (distribuição completa, com EJB).
- SQL Server 2022, com TCP/IP habilitado.
- NetBeans com suporte a Java Web/Enterprise, se quiser usar a IDE.

O roteiro mistura Jakarta EE 8 com imports `jakarta.*`. Aqui a combinação é Jakarta EE 10 / GlassFish 7.1.1 / JDK 17. Os descritores XML seguem essa versão. `javax.sql.DataSource` continua correto: pertence ao Java SE, não foi renomeado para `jakarta.sql`.

## 1. Banco e conexão no NetBeans

No SQL Server Management Studio, execute `database/loja.sql`. Ele cria a base `loja`, a tabela `Produto` e três exemplos, sem apagar tabelas existentes. Como o banco da prática anterior não foi fornecido, este script contém somente a tabela usada no cadastro. Se for usar uma base anterior, confira os nomes e tipos das colunas antes de executar.

Em Segurança > Logons, crie o login SQL `loja` com uma senha própria e mapeie-o para o banco `loja`. Conceda somente SELECT, INSERT, UPDATE e DELETE na tabela `dbo.Produto` (Propriedades do usuário > Protegíveis), sem conceder sysadmin. Não use a senha de exemplo do roteiro em um banco exposto à rede.

Baixe o [driver JDBC da Microsoft](https://learn.microsoft.com/pt-br/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server). Foi testado o `mssql-jdbc-12.10.2.jre11.jar`, compatível com JDK 17.

No NetBeans: Serviços > Banco de Dados > Drivers > Novo Driver. Adicione o JAR e escolha `com.microsoft.sqlserver.jdbc.SQLServerDriver`. Crie uma conexão com usuário `loja`, sua senha e a URL:

```text
jdbc:sqlserver://localhost:1433;databaseName=loja;encrypt=true;trustServerCertificate=true
```

`trustServerCertificate=true` atende ao certificado local de desenvolvimento. Em um ambiente publicado, use um certificado válido e retire essa opção. A aplicação é uma prática local, sem autenticação de usuários.

## 2. GlassFish

Copie o JAR do driver para `glassfish/domains/domain1/lib` dentro da instalação do GlassFish e inicie/reinicie o domínio:

```text
asadmin start-domain
```

No PowerShell, defina a senha somente na sessão e rode o script (informe a pasta que contém `bin/asadmin.bat`):

```powershell
$senha = Read-Host 'Senha do usuário loja' -AsSecureString
$env:LOJA_PASSWORD = [System.Net.NetworkCredential]::new('', $senha).Password
./scripts/configurar-glassfish.ps1 -GlassFishHome 'C:/servidores/glassfish7'
Remove-Item Env:LOJA_PASSWORD
```

O script cria `SQLServerPool`, registra `jdbc/loja` e executa o ping. Rode uma vez por domínio. Para um pool existente, altere suas propriedades no console administrativo e execute `asadmin ping-connection-pool SQLServerPool`.

Em Linux/macOS, use o console do GlassFish em `http://localhost:4848`: Resources > JDBC > JDBC Connection Pools. Classe `com.microsoft.sqlserver.jdbc.SQLServerDataSource`, tipo `javax.sql.DataSource`, propriedades `serverName`, `portNumber`, `databaseName=loja`, `user=loja`, `password`, `encrypt=true` e `trustServerCertificate=true`. Depois crie o recurso `jdbc/loja` apontando para `SQLServerPool`.

## 3. Compilação e implantação

Na raiz:

```text
ant clean dist
asadmin deploy --force=true dist/CadastroEE.ear
```

O Ant baixa somente a API de compilação do Maven Central. As implementações de Jakarta EE e JSTL vêm do GlassFish; não são empacotadas no WAR. O EJB vai no EAR, evitando duplicar a entidade no módulo Web.

No NetBeans, use Arquivo > Abrir Projeto e selecione a raiz. O projeto usa Ant em formato livre, com fontes EJB e WAR separadas, para evitar os arquivos gerados que não são necessários à aplicação. Build gera o EAR; Test executa os testes. Para Run/Deploy, configure `GLASSFISH_HOME` ou execute o alvo `deploy` com `-Dasadmin=C:/servidores/glassfish7/bin/asadmin.bat`. No Windows, também é possível executar o comando de implantação acima diretamente no terminal da IDE.

Endereços:

- `http://localhost:8080/CadastroEE-war/ServletProduto`: consulta inicial em lista HTML.
- `http://localhost:8080/CadastroEE-war/ServletProdutoFC?acao=listar`: cadastro completo.

Se a porta 8080 estiver ocupada, configure outra porta no listener HTTP do GlassFish e ajuste os endereços. Nesta validação foi usada a porta 18080; o SQL Server local usou 14330.

## 4. Testes

```text
ant test
python test/integracao.py http://localhost:18080/CadastroEE-war
```

O segundo comando exige Python 3 e a aplicação em execução. Usa apenas a biblioteca padrão, cria seu próprio produto e o remove ao terminar. Exercita persistência, telas, validações, escape de HTML, métodos HTTP e proteção contra reenvio de formulário.

As operações usam `RequestDispatcher.forward`, conforme o roteiro. Após cada gravação, o token do formulário muda para impedir que atualizar a página repita a operação. Um formulário antigo, inclusive de outra aba, precisa ser reaberto. O link Excluir abre a confirmação; a remoção acontece por POST.

`precoVenda` usa `Float`, como exigido. Isso implica aproximação binária; em um sistema financeiro real o tipo adequado seria `BigDecimal` junto de DECIMAL no banco.
