<%@ page pageEncoding="UTF-8" %>
<%@ include file="cabecalho.jspf" %>
<c:set var="alterando" value="${not empty produto or editando}"/>
<c:set var="acao" value="${alterando ? 'alterar' : 'incluir'}"/>
<h2 class="h4 mb-3">${alterando ? 'Alterar produto' : 'Novo produto'}</h2>
<form class="form" method="post" action="${controller}">
    <input type="hidden" name="acao" value="${acao}">
    <input type="hidden" name="csrf" value="${sessionScope.csrf}">
    <c:if test="${alterando}">
        <input type="hidden" name="id" value="<c:out value='${reexibir ? param.id : produto.idProduto}'/>">
    </c:if>
    <div class="mb-3">
        <label class="form-label" for="nome">Nome</label>
        <input class="form-control" id="nome" name="nome" required maxlength="100"
               value="<c:out value='${reexibir ? param.nome : produto.nome}'/>">
    </div>
    <div class="mb-3">
        <label class="form-label" for="quantidade">Quantidade</label>
        <input class="form-control" id="quantidade" name="quantidade" type="number" min="0" max="2147483647" step="1" required
               value="<c:out value='${reexibir ? param.quantidade : produto.quantidade}'/>">
    </div>
    <div class="mb-3">
        <label class="form-label" for="precoVenda">Preço de venda (R$)</label>
        <input class="form-control" id="precoVenda" name="precoVenda" type="number" min="0" step="0.01" required
               value="<c:out value='${reexibir ? param.precoVenda : produto.precoVenda}'/>">
    </div>
    <button class="btn btn-primary" type="submit">${alterando ? 'Salvar alterações' : 'Incluir produto'}</button>
    <a class="btn btn-outline-secondary" href="${controller}?acao=listar">Voltar</a>
</form>
<%@ include file="rodape.jspf" %>
