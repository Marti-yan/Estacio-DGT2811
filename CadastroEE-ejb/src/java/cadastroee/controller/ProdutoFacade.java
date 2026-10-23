package cadastroee.controller;

import cadastroee.model.Produto;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ProdutoFacade implements ProdutoFacadeLocal {

    @PersistenceContext(unitName = "CadastroEE-ejbPU")
    private EntityManager em;

    @Override
    public void create(Produto produto) {
        em.persist(produto);
    }

    @Override
    public void edit(int id, String nome, int quantidade, float precoVenda) {
        find(id).atualizar(nome, quantidade, precoVenda);
    }

    @Override
    public void remove(int id) {
        em.remove(find(id));
    }

    @Override
    public Produto find(int id) {
        Produto produto = em.find(Produto.class, id);
        if (produto == null) {
            throw new ProdutoNaoEncontradoException();
        }
        return produto;
    }

    @Override
    public List<Produto> findAll() {
        return em.createQuery("SELECT p FROM Produto p ORDER BY p.idProduto", Produto.class)
                .getResultList();
    }
}
