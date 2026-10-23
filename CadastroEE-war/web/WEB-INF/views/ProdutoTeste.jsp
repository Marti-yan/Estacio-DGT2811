<%@ page pageEncoding="UTF-8" %>
<%@ include file="cabecalho.jspf" %>
<h2 class="h4">Consulta pelo ServletProduto</h2>
<ul>
    <c:forEach var="produto" items="${produtos}">
        <li><c:out value="${produto.nome}"/> · ${produto.quantidade} unidade(s) ·
            <fmt:formatNumber value="${produto.precoVenda}" type="currency" currencySymbol="R$"/></li>
    </c:forEach>
</ul>
<a href="${controller}?acao=listar">Abrir cadastro</a>
<%@ include file="rodape.jspf" %>
