<%@ page pageEncoding="UTF-8" %>
<%@ include file="cabecalho.jspf" %>
<c:if test="${not empty exclusao}">
    <section class="alert alert-warning" aria-label="Confirmar exclusão">
        <p>Excluir o produto <strong><c:out value="${exclusao.nome}"/></strong>?</p>
        <form method="post" action="${controller}">
            <input type="hidden" name="acao" value="excluir">
            <input type="hidden" name="id" value="${exclusao.idProduto}">
            <input type="hidden" name="csrf" value="${sessionScope.csrf}">
            <button class="btn btn-danger btn-sm" type="submit">Confirmar exclusão</button>
            <a class="btn btn-outline-secondary btn-sm" href="${controller}?acao=listar">Cancelar</a>
        </form>
    </section>
</c:if>
<a class="btn btn-primary m-2" href="${controller}?acao=formIncluir">Novo produto</a>
<div class="table-responsive">
    <table class="table table-striped align-middle">
        <caption>Produtos cadastrados na loja</caption>
        <thead class="table-dark">
            <tr><th scope="col">ID</th><th scope="col">Nome</th><th scope="col">Quantidade</th>
                <th scope="col">Preço de venda</th><th scope="col">Ações</th></tr>
        </thead>
        <tbody>
        <c:forEach var="produto" items="${produtos}">
            <tr>
                <td>${produto.idProduto}</td>
                <td><c:out value="${produto.nome}"/></td>
                <td>${produto.quantidade}</td>
                <td class="text-nowrap"><fmt:formatNumber value="${produto.precoVenda}" type="currency" currencySymbol="R$"/></td>
                <td class="text-nowrap">
                    <a class="btn btn-primary btn-sm" href="${controller}?acao=formAlterar&amp;id=${produto.idProduto}">Alterar</a>
                    <a class="btn btn-danger btn-sm" href="${controller}?acao=excluir&amp;id=${produto.idProduto}">Excluir</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty produtos}">
            <tr><td colspan="5" class="text-center text-secondary py-4">Nenhum produto cadastrado.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
<%@ include file="rodape.jspf" %>
