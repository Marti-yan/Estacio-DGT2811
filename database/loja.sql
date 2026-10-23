IF DB_ID(N'loja') IS NULL
    CREATE DATABASE loja;
GO
USE loja;
GO
IF OBJECT_ID(N'dbo.Produto', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Produto (
        idProduto INT IDENTITY(1,1) PRIMARY KEY,
        nome NVARCHAR(100) NOT NULL,
        quantidade INT NOT NULL CHECK (quantidade >= 0),
        precoVenda REAL NOT NULL CHECK (precoVenda >= 0)
    );
END;
GO
IF NOT EXISTS (SELECT 1 FROM dbo.Produto)
BEGIN
    INSERT INTO dbo.Produto (nome, quantidade, precoVenda) VALUES
        (N'Caneta', 50, 2.50),
        (N'Caderno', 20, 18.90),
        (N'Mochila', 10, 89.90);
END;
GO
