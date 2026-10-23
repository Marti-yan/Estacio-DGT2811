# CadastroEE

Trabalho prático de Desenvolvimento Back-End Corporativo com Java e Cloud (DGT2811).
É um cadastro de produtos: dá para listar, incluir, alterar e excluir. Usei Java com JPA e EJB no back-end, Servlets e JSP nas telas e Bootstrap no visual. O banco é SQL Server.

## Como rodar

Precisa de JDK 17, Ant, GlassFish 7.1.1 e SQL Server. O projeto também abre no NetBeans.

1. Execute `database/loja.sql` no SQL Server.
2. Configure o driver JDBC e o recurso `jdbc/loja` no GlassFish. O passo a passo está em [docs/execucao.md](docs/execucao.md).
3. Na pasta do projeto, execute `ant dist` e implante `dist/CadastroEE.ear` no GlassFish.
4. Abra [o cadastro](http://localhost:8080/CadastroEE-war/ServletProdutoFC?acao=listar).

`CadastroEE-ejb` contém a entidade e o serviço. `CadastroEE-war` contém os servlets e as páginas. `CadastroEE` reúne os dois no EAR.

Os testes do domínio rodam com `ant test`. Com o sistema no ar, execute `python test/integracao.py` para testar o cadastro completo.

O [relatório da prática](docs/relatorio.pdf) reúne a implementação, as telas e as respostas do roteiro.

Yan Martins Lourenço Fraga
