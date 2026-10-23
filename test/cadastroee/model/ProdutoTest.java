package cadastroee.model;

public class ProdutoTest {
    private static int verificacoes;

    public static void main(String[] args) {
        Produto produto = new Produto("  Caderno  ", 10, 18.90f);
        conferir("Caderno".equals(produto.getNome()));
        conferir(produto.getIdProduto() == null);
        produto.atualizar("Caneta", 0, 0f);
        conferir(produto.getQuantidade() == 0 && produto.getPrecoVenda() == 0f);
        rejeitar(() -> produto.atualizar("", 1, 1f));
        rejeitar(() -> produto.atualizar(" ", 1, 1f));
        rejeitar(() -> produto.atualizar(null, 1, 1f));
        rejeitar(() -> produto.atualizar("a".repeat(101), 1, 1f));
        rejeitar(() -> produto.atualizar("Produto", -1, 1f));
        rejeitar(() -> produto.atualizar("Produto", null, 1f));
        rejeitar(() -> produto.atualizar("Produto", 1, -1f));
        rejeitar(() -> produto.atualizar("Produto", 1, null));
        rejeitar(() -> produto.atualizar("Produto", 1, Float.NaN));
        rejeitar(() -> produto.atualizar("Produto", 1, Float.POSITIVE_INFINITY));
        conferir("Caneta".equals(produto.getNome()) && produto.getQuantidade() == 0);
        System.out.println(verificacoes + " verificações do domínio passaram.");
    }

    private static void rejeitar(Runnable operacao) {
        try {
            operacao.run();
        } catch (IllegalArgumentException expected) {
            verificacoes++;
            return;
        }
        throw new AssertionError("A operação deveria ter sido rejeitada.");
    }

    private static void conferir(boolean condicao) {
        if (!condicao) {
            throw new AssertionError("Resultado diferente do esperado.");
        }
        verificacoes++;
    }
}
