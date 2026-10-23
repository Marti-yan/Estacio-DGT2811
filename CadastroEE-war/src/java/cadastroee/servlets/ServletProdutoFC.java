package cadastroee.servlets;

import cadastroee.controller.ProdutoFacadeLocal;
import cadastroee.controller.ProdutoNaoEncontradoException;
import cadastroee.model.Produto;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class ServletProdutoFC extends HttpServlet {

    private static final Set<String> ACOES = Set.of(
            "listar", "formIncluir", "formAlterar", "incluir", "alterar", "excluir");

    @EJB
    private ProdutoFacadeLocal facade;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        String acao = request.getParameter("acao");
        if (acao == null) {
            acao = "listar";
        }
        if (!ACOES.contains(acao)) {
            response.sendError(400, "Ação inválida.");
            return;
        }
        boolean post = "POST".equals(request.getMethod());
        if (!post && ("incluir".equals(acao) || "alterar".equals(acao))) {
            response.setHeader("Allow", "POST");
            response.sendError(405);
            return;
        }
        var session = request.getSession();
        if (session.getAttribute("csrf") == null) {
            session.setAttribute("csrf", UUID.randomUUID().toString());
        }
        if (post && !session.getAttribute("csrf").equals(request.getParameter("csrf"))) {
            response.sendError(403, "Formulário expirado. Abra a listagem e tente novamente.");
            return;
        }

        String destino = "ProdutoLista.jsp";
        try {
            switch (acao) {
                case "formIncluir" -> destino = "ProdutoDados.jsp";
                case "formAlterar" -> {
                    request.setAttribute("produto", facade.find(id(request)));
                    destino = "ProdutoDados.jsp";
                }
                case "incluir", "alterar" -> {
                    Produto dados = dados(request);
                    if ("incluir".equals(acao)) {
                        facade.create(dados);
                    } else {
                        facade.edit(id(request), dados.getNome(), dados.getQuantidade(), dados.getPrecoVenda());
                    }
                    request.setAttribute("mensagem", "incluir".equals(acao)
                            ? "Produto incluído." : "Produto atualizado.");
                    session.setAttribute("csrf", UUID.randomUUID().toString());
                }
                case "excluir" -> {
                    if (post) {
                        facade.remove(id(request));
                        request.setAttribute("mensagem", "Produto excluído.");
                        session.setAttribute("csrf", UUID.randomUUID().toString());
                    } else {
                        request.setAttribute("exclusao", facade.find(id(request)));
                    }
                }
                default -> { }
            }
        } catch (ProdutoNaoEncontradoException e) {
            response.setStatus(404);
            request.setAttribute("erro", e.getMessage());
        } catch (IllegalArgumentException e) {
            response.setStatus(400);
            request.setAttribute("erro", e.getMessage());
            if ("incluir".equals(acao) || "alterar".equals(acao)) {
                request.setAttribute("reexibir", true);
                request.setAttribute("editando", "alterar".equals(acao));
                destino = "ProdutoDados.jsp";
            }
        }
        if ("ProdutoLista.jsp".equals(destino)) {
            request.setAttribute("produtos", facade.findAll());
        }
        request.getRequestDispatcher("/WEB-INF/views/" + destino).forward(request, response);
    }

    private int id(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException ignored) {
            // A mesma mensagem atende valores ausentes ou inválidos.
        }
        throw new IllegalArgumentException("Identificador de produto inválido.");
    }

    private Produto dados(HttpServletRequest request) {
        try {
            int quantidade = Integer.parseInt(request.getParameter("quantidade"));
            String preco = request.getParameter("precoVenda");
            float precoVenda = Float.parseFloat(preco == null ? "" : preco.replace(',', '.'));
            return new Produto(request.getParameter("nome"), quantidade, precoVenda);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe uma quantidade inteira e um preço numérico válidos.");
        }
    }
}
