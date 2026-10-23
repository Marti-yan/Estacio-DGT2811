# Sistema cadastral com Java, JPA, EJB, Servlets e JSP

## Identificação

Universidade Estácio de Sá

Campus: Cabo Frio - Braga

Curso: Desenvolvimento Full Stack

Disciplina: DGT2811 - Desenvolvimento Back-End Corporativo com Java e Cloud

Aluno: Yan Martins Lourenço Fraga

Matrícula: 202211246051

Turma: não se aplica

Semestre: 4/5, conforme informado pelo aluno

Data da execução: 23 de outubro de 2026

Repositório: https://github.com/Marti-yan/Estacio-DGT2811

## 1. Objetivo da prática

Implementar um cadastro Web de produtos com persistência no SQL Server, regras de negócio em EJB e apresentação em JSP. O trabalho reúne os três procedimentos do roteiro: persistência e controle, interface cadastral e melhoria visual com Bootstrap.

O sistema permite consultar, incluir, alterar e excluir produtos. Cada produto possui identificador, nome, quantidade e preço de venda. A implementação usa orientação a objetos e mantém as responsabilidades separadas sem criar camadas que apenas repassariam chamadas.

## 2. Ambiente e decisões de implementação

O ambiente validado utilizou JDK 17, Ant 1.10.15, GlassFish 7.1.1, Jakarta EE 10, SQL Server 2022 e o driver Microsoft JDBC 12.10.2.jre11. As telas utilizam Bootstrap 5.3.8 por CDN. O SQL Server foi executado localmente em contêiner e o GlassFish, no Windows.

O roteiro menciona Jakarta EE 8 e, ao mesmo tempo, pede a troca de imports javax por jakarta. Essa combinação é inconsistente. Foi usada uma versão posterior do GlassFish, permitida no próprio enunciado, com a API Jakarta EE 10. O descritor de persistência usa o esquema XML 3.0, também utilizado pela especificação Jakarta Persistence 3.1. O tipo javax.sql.DataSource permanece assim porque pertence ao Java SE.

O banco da prática anterior não foi disponibilizado. Por isso, o projeto inclui um script com a tabela Produto, única tabela necessária aos fluxos solicitados. Não foram inventadas entidades de outros exercícios. O campo precoVenda é Float no Java e REAL no SQL Server, conforme o tipo exigido pelo roteiro. Para aplicações financeiras, BigDecimal e DECIMAL seriam mais adequados devido à precisão decimal.

O projeto usa Ant e mantém os módulos CadastroEE, CadastroEE-ejb e CadastroEE-war. A configuração do NetBeans é um projeto Ant de formato livre, com as duas áreas de fontes separadas. Essa é uma adaptação ao assistente de criação do roteiro: preserva a organização e os artefatos EAR, EJB e WAR, mas evita arquivos gerados desnecessários. A compilação e a implantação foram executadas por linha de comando; não se afirma uma execução do assistente gráfico do NetBeans.

A porta 8080 já estava ocupada no computador de validação. O GlassFish foi configurado na porta 18080 e o SQL Server na porta 14330. O caminho da aplicação permaneceu CadastroEE-war. O guia de execução explica a configuração padrão e essa variação.

## 3. Procedimento 1 - Persistência e controle

O arquivo database/loja.sql cria a base loja, a tabela dbo.Produto e três produtos de exemplo quando a tabela está vazia. O identificador é gerado pelo SQL Server. O script não apaga objetos já existentes. O usuário loja recebeu apenas SELECT, INSERT, UPDATE e DELETE sobre a tabela.

A classe Produto representa os dados e valida nome, quantidade e preço. A atualização valida todos os valores antes de modificar o objeto, evitando alterações parciais quando um campo é inválido. As anotações JPA mapeiam os atributos para a tabela existente, sem geração automática de esquema.

ProdutoFacade é um EJB sem estado, exposto pela interface local ProdutoFacadeLocal. O contêiner injeta o EntityManager e gerencia as transações JTA. A inclusão usa persist, a exclusão usa remove e a alteração modifica uma entidade gerenciada. O JPA sincroniza essa alteração ao concluir a transação; uma chamada adicional a merge não é necessária nesse caso.

O recurso JNDI jdbc/loja aponta para SQLServerPool. O script de configuração gera um descritor temporário a partir da senha informada na sessão, registra os recursos e testa a conexão. A senha não faz parte do código nem do repositório.

ServletProduto recebe o EJB com @EJB, consulta os produtos e encaminha a lista para ProdutoTeste.jsp. O resultado é uma lista HTML com nome, quantidade e preço. A montagem da apresentação fica no JSP para não misturar HTML ao código Java do servlet.

## 4. Procedimento 2 - Interface cadastral

ServletProdutoFC implementa o Front Controller. Seu método processRequest recebe o parâmetro acao e trata listar, formIncluir, formAlterar, incluir, alterar e excluir. O servlet interpreta a requisição, chama a interface local e define os atributos que serão lidos pela página.

ProdutoLista.jsp apresenta identificador, nome, quantidade e preço em uma tabela, além dos links de inclusão, alteração e exclusão. ProdutoDados.jsp atende tanto a inclusão quanto a alteração, com campos ocultos para acao e, quando necessário, id. Os dados são enviados por POST.

O destino dos formulários é ProdutoDados.jsp e o destino das listagens é ProdutoLista.jsp. A navegação usa RequestDispatcher.forward, como solicitado. As páginas ficam dentro de WEB-INF para que o acesso passe pelo servlet.

O link Excluir abre uma confirmação; somente o POST confirmado remove o produto. Os formulários usam um token de sessão contra requisições forjadas. Após uma gravação o token é renovado para impedir repetição ao atualizar a página. Como consequência, um formulário aberto anteriormente em outra aba também precisa ser reaberto.

O sistema rejeita nome vazio ou longo demais, quantidades negativas ou não inteiras e preços negativos, não numéricos, infinitos ou NaN. IDs inválidos retornam 400 e produtos inexistentes retornam 404. Os valores exibidos pelo usuário passam por c:out, evitando interpretação de HTML. Dados de conexão e detalhes de exceções não são exibidos na página de erro geral.

## 5. Procedimento 3 - Bootstrap

O cabeçalho compartilhado inclui o CSS e o JavaScript do Bootstrap por CDN, com verificação de integridade, além da metatag viewport. As duas páginas cadastrais incluem esse cabeçalho, evitando repetir os mesmos links.

A listagem usa container, table, table-striped e table-dark. Os links usam btn-primary ou btn-danger e os tamanhos pedidos. O formulário usa form, mb-3, form-label e form-control. A tabela fica em table-responsive para permitir rolagem horizontal em telas estreitas. Rótulos associados aos campos, cabeçalhos de tabela e mensagens com role ajudam na acessibilidade.

A interface usa somente recursos necessários ao cadastro. Não foram adicionados frameworks de front-end, APIs paralelas ou classes genéricas sem uso.

## 6. Resultados da execução

A compilação gerou CadastroEE-ejb.jar, CadastroEE-war.war e CadastroEE.ear. A implantação do EAR no GlassFish e o ping de SQLServerPool foram concluídos com sucesso. A base permaneceu com os três exemplos após a limpeza dos produtos de teste.

Foram aprovadas 14 verificações do domínio e 29 verificações HTTP. Os testes HTTP passaram pela aplicação implantada, usando EJB, JPA e SQL Server reais. Eles verificaram listagem, inclusão, recuperação, alteração, exclusão, validações, acentuação UTF-8, escape de HTML, ações desconhecidas, IDs inválidos e proteção dos formulários. O script cria apenas seus próprios dados de teste e os remove ao terminar.

Na demonstração visual foi incluído um Estojo com 12 unidades e preço de R$ 24,90. Em seguida, a quantidade foi alterada para 15 e o preço para R$ 26,90. Por fim, o produto foi excluído após a confirmação. As capturas mostram esses resultados e a consulta pelo servlet inicial. Os identificadores não são reiniciados após a exclusão, comportamento normal de uma coluna IDENTITY.

As evidências estão na pasta docs/evidencias. As figuras e os registros de execução são apresentados a seguir no PDF. Não foram usados resultados simulados nem imagens de interfaces que não correspondam à aplicação.

## 7. Análise e conclusão - Procedimento 1

### Como é organizado um projeto corporativo no NetBeans?

Uma aplicação corporativa pode reunir módulos EJB e Web em um EAR. O módulo EJB contém entidades e serviços de negócio; o módulo Web contém servlets, JSPs e recursos de apresentação. O projeto principal descreve como esses módulos são empacotados. Neste trabalho, o build Ant realiza essa composição e a configuração da IDE aponta para as fontes de cada módulo.

### Qual é o papel de JPA e EJB em uma aplicação Web Java?

JPA faz o mapeamento entre objetos e dados relacionais, oferecendo operações de persistência e consultas. EJB administra componentes de negócio no servidor, incluindo transações, ciclo de vida e concorrência das instâncias. Aqui, ProdutoFacade usa JPA dentro de transações controladas pelo GlassFish; o servlet não precisa abrir conexões nem controlar commit e rollback.

### Como o NetBeans melhora a produtividade com JPA e EJB?

Os assistentes podem gerar entidades a partir de tabelas, interfaces e session beans, além de ajudar com descritores, bibliotecas, conclusão de código e implantação. Esse código inicial precisa ser revisado: nomes, tipos, regras de validação e versões das APIs devem corresponder à aplicação. O trabalho mantém uma implementação pequena, organizada para edição na IDE e também compilável fora dela.

### O que são Servlets e qual é o suporte oferecido pela IDE?

Servlets são componentes executados pelo contêiner Web para receber requisições e produzir respostas. HttpServlet fornece métodos como doGet e doPost. O NetBeans ajuda a criar classes, mapear URLs, editar web.xml e executar o projeto no servidor. Neste cadastro, os mapeamentos estão explicitamente no descritor Web.

### Como os Servlets se comunicam com os Session Beans?

A anotação @EJB solicita a injeção de uma referência à interface local. O servlet chama seus métodos e o contêiner encaminha a chamada a uma instância gerenciada do bean. O código não deve instanciar ProdutoFacade com new, pois isso dispensaria o gerenciamento de transações e a injeção do EntityManager.

## 8. Análise e conclusão - Procedimento 2

### Como funciona o Front Controller na arquitetura MVC?

Um controlador central recebe as requisições de um conjunto de funcionalidades, escolhe a operação e encaminha os dados para a apresentação. No projeto, ServletProdutoFC é o controlador, Produto e o serviço EJB representam o modelo, e os JSPs são as visões. Essa divisão concentra a navegação e evita regras de negócio espalhadas pelas páginas.

### Quais são as diferenças e semelhanças entre Servlets e JSPs?

Ambos são executados no servidor e participam do processamento de requisições Web. Um servlet é escrito principalmente em Java e é adequado ao controle do fluxo. Um JSP é escrito principalmente como apresentação, combinando HTML, Expression Language e bibliotecas de tags. O contêiner traduz e compila o JSP em um servlet; portanto, eles compartilham a infraestrutura Web, mas têm papéis diferentes no código-fonte.

### Qual é a diferença entre redirecionamento e forward? Para que servem parâmetros e atributos?

Um redirecionamento envia ao navegador uma resposta que provoca uma nova requisição e normalmente altera a URL exibida. Os atributos da requisição anterior não acompanham essa nova chamada. O forward transfere o processamento internamente, preservando a mesma requisição e seus atributos, sem mudar a URL no navegador. Parâmetros chegam como texto da URL ou do formulário; atributos são objetos colocados pelo servidor, como a lista de produtos e a entidade a editar.

## 9. Análise e conclusão - Procedimento 3

### Como o Bootstrap é utilizado?

Os arquivos CSS e JavaScript são incluídos na página, e as classes do framework são aplicadas aos elementos HTML. Neste trabalho, a maior parte do resultado vem das classes CSS de tabelas, formulários, espaçamento, alertas e botões. O JavaScript foi incluído conforme o roteiro, embora as operações do cadastro sejam processadas no servidor.

### Por que o Bootstrap favorece a independência estrutural do HTML?

O HTML descreve conteúdo e semântica; as classes CSS concentram regras de apresentação em folhas externas. Isso permite ajustar cores, espaços e comportamento visual sem reescrever a lógica de negócio. Essa independência não é absoluta: alguns componentes exigem marcação e classes específicas. O benefício principal é separar melhor apresentação, conteúdo e processamento.

### Qual é a relação entre Bootstrap e responsividade?

O framework oferece estilos mobile first, pontos de quebra, contêineres e utilitários que se adaptam à largura disponível. A metatag viewport permite o dimensionamento correto em dispositivos móveis. No cadastro, os campos ocupam a largura do contêiner e a tabela usa table-responsive; quando o espaço é insuficiente, a rolagem fica na tabela em vez de forçar toda a página a uma largura fixa.

## 10. Conclusão geral

O cadastro demonstrou a integração entre a camada Web, os serviços EJB e a persistência JPA no SQL Server. A separação de responsabilidades manteve o código pequeno e permitiu verificar regras do produto sem servidor, complementando essa verificação com testes HTTP da aplicação completa.

Os três procedimentos foram reunidos em uma aplicação executável e documentada. As adaptações de versão, de criação do projeto Ant e de esquema inicial do banco foram explicitadas. O ambiente é destinado à prática local; uma publicação de uso real exigiria autenticação, autorização, certificado adequado e tratamento monetário com precisão decimal.

## 11. Referências

Roteiro da disciplina DGT2811: https://sway.cloud.microsoft/xgFrBfFns0eB9W2U?ref=Link

GlassFish 7.1.1 e Jakarta EE 10: https://glassfish.org/download_gf7.html

Jakarta Persistence 3.1 e seus esquemas XML: https://jakarta.ee/specifications/persistence/3.1/

Bootstrap 5.3 - introdução e CDN: https://getbootstrap.com/docs/5.3/getting-started/introduction/

Microsoft JDBC Driver para SQL Server: https://learn.microsoft.com/pt-br/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server

Logotipo institucional extraído da capa do Relatório de Autoavaliação Institucional 2024 da Estácio: https://cdn.portal.estacio.br/Relatorio_de_Autoavaliacao_Institucional_de_2024_e_2023_998242a87e.pdf

Consulta às fontes e execução: 23/10/2026.

## 12. Código-fonte

O apêndice do PDF contém os códigos da aplicação, os descritores, o script do banco, a configuração do ambiente e os testes. Os arquivos originais, com a estrutura de diretórios e o histórico de versões, estão no repositório indicado na capa.
