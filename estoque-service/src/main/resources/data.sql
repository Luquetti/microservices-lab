INSERT INTO produto (id, nome, quantidade) VALUES
    (1, 'Notebook', 10),
    (2, 'Mouse', 50),
    (3, 'Teclado', 20)
ON CONFLICT (id) DO NOTHING;
