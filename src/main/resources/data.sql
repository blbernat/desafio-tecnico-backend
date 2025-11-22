INSERT INTO caminhao (placa, tara) VALUES
('ABC1D23', 7000.0),
('XYZ9E88', 6500.0),
('JHK4F55', 8000.0),
('BRA2A19', 7200.0);

INSERT INTO tipo_grao (nome, preco_ton) VALUES
('Soja', 100.85),
('Milho', 158.20),
('Trigo', 179.65),
('Cevada', 209.10);

INSERT INTO filial (nome) VALUES
('Filial Matriz'),
('Filial Norte'),
('Filial Sul');

INSERT INTO balanca (codigo_hardware, filial_id) VALUES
('BAL001', 1),
('BAL002', 1),
('BAL003', 2),
('BAL004', 2),
('BAL005', 3),
('BAL006', 3);

INSERT INTO transacao_transporte (
    caminhao_id, tipo_grao_id, balanca_id, peso_bruto, tara, peso_liquido, custo_carga, inicio, fim) VALUES
    (1, 1, 1, 10500.0, 7000.0, 3500.0, 352975, '2025-11-22 18:45:05.123', '2025-11-22 18:48:08.351');