INSERT INTO caminhao (id, placa, tara) VALUES
(1, 'ABC1D23', 7000.0),
(2, 'XYZ9E88', 6500.0),
(3, 'JHK4F55', 8000.0),
(4, 'BRA2A19', 7200.0);

INSERT INTO tipo_grao (id, nome, preco_ton) VALUES
(1, 'Soja', 100.85),
(2, 'Milho', 158.20),
(3, 'Trigo', 179.65),
(4, 'Cevada', 209.10);

INSERT INTO filial (id, nome) VALUES
(1, 'Filial Matriz'),
(2, 'Filial Norte'),
(3, 'Filial Sul');

INSERT INTO balanca (id, codigo_hardware, filial_id) VALUES
(1, 'BAL001', 1),
(2, 'BAL002', 1),
(3, 'BAL003', 2),
(4, 'BAL004', 2),
(5, 'BAL005', 3),
(6, 'BAL006', 3);

INSERT INTO transacao_transporte (
    id, caminhao_id, tipo_grao_id, balanca_id, filial_id, peso_bruto, tara, peso_liquido, custo_carga, inicio, fim) VALUES
    (1, 1, 1, 1, 1, 10500.0, 7000.0, 3500.0, 352975, '2025-11-22 18:45:05.123', '2025-11-22 18:48:08.351');