package cadastroee.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Produto", schema = "dbo")
public class Produto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProduto")
    private Integer idProduto;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Float precoVenda;

    protected Produto() {
        // Exigido pelo JPA.
    }

    public Produto(String nome, Integer quantidade, Float precoVenda) {
        atualizar(nome, quantidade, precoVenda);
    }

    public void atualizar(String nome, Integer quantidade, Float precoVenda) {
        if (nome == null || nome.isBlank() || nome.strip().length() > 100) {
            throw new IllegalArgumentException("Informe um nome de até 100 caracteres.");
        }
        if (quantidade == null || quantidade < 0) {
            throw new IllegalArgumentException("A quantidade deve ser um inteiro maior ou igual a zero.");
        }
        if (precoVenda == null || !Float.isFinite(precoVenda) || precoVenda < 0) {
            throw new IllegalArgumentException("O preço deve ser um número maior ou igual a zero.");
        }
        this.nome = nome.strip();
        this.quantidade = quantidade;
        this.precoVenda = precoVenda;
    }

    public Integer getIdProduto() {
        return idProduto;
    }

    public String getNome() {
        return nome;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Float getPrecoVenda() {
        return precoVenda;
    }
}
