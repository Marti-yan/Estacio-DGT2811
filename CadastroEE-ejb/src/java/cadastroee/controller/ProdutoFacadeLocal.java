package cadastroee.controller;

import cadastroee.model.Produto;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface ProdutoFacadeLocal {
    void create(Produto produto);
    void edit(int id, String nome, int quantidade, float precoVenda);
    void remove(int id);
    Produto find(int id);
    List<Produto> findAll();
}
