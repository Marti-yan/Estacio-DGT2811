package cadastroee.controller;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException() {
        super("Produto não encontrado. Ele pode ter sido excluído em outra aba.");
    }
}
