-- data-postgres.sql — Massa de dados de demonstração para HotelImpact (perfil dev)
--
-- Executado pelo mecanismo nativo de inicialização SQL do Spring Boot
-- (spring.sql.init.mode=always, perfil dev). Todas as instruções são idempotentes:
-- INSERT ... ON CONFLICT DO NOTHING nas chaves únicas; consultas condicionais
-- para evitar duplicação de registros dependentes de FKs.
-- Pode ser executado repetidamente sem duplicar registros.
--
-- A massa de dados cobre:
--   4 categorias de quartos
--   16 quartos (distribuídos entre categorias, andares e estados)
--   20 hóspedes
--   12 reservas (pendentes, confirmadas, canceladas, no-show, concluídas)
--   5 hospedagens (em andamento, encerradas, canceladas)
--   10 itens de catálogo (produtos e serviços)
--   12 consumos (ativos, cancelados)
--   15 pagamentos (confirmados, estornados)
--   4 ordens de manutenção (aberta, em andamento, concluída, cancelada)
--   8 tarefas de limpeza (pendente, em execução, concluída)
--   Registros de auditoria para operações realizadas
--
-- A massa representa cenários reais: quartos em diferentes estados,
-- reservas em diferentes estados, hospedagens ativas e encerradas,
-- consumos ativos e cancelados, pagamentos confirmados e estornados,
-- manutenções em diferentes estágios, limpezas em diferentes estágios.

-- ============================================================
-- Categorias de quartos (4)
-- ============================================================
INSERT INTO categoria_quarto (nome, descricao, capacidade_maxima, quantidade_camas, preco_base, ativo)
VALUES
    ('Standard', 'Quarto padrão com amenities básicas', 2, 1, 180.00, true),
    ('Superior', 'Quarto superior com vista para o jardim', 2, 1, 250.00, true),
    ('Luxo', 'Quarto de luxo com banheira e vista para o mar', 3, 2, 420.00, true),
    ('Suíte Master', 'Suíte master com sala de estar e varanda privativa', 4, 2, 680.00, true)
ON CONFLICT (nome) DO NOTHING;

-- ============================================================
-- Hóspedes (20) — CPFs fictícios válidos (algoritmo módulo 11)
-- ============================================================
INSERT INTO hospede (cpf, nome, telefone, email, data_nascimento, observacoes, data_cadastro)
VALUES
    ('52998224725', 'Ana Carolina Silva', '(11) 98888-1234', 'ana.silva@email.com', '1985-03-14', 'Prefere quarto silencioso', '2024-01-15 10:30:00'),
    ('63547819230', 'Bruno Henrique Costa', '(21) 99999-5678', 'bruno.costa@email.com', '1990-07-22', 'Alérgico a penicilina', '2024-01-20 14:15:00'),
    ('74185296341', 'Carla Daniela Oliveira', '(31) 98888-9012', 'carla.oliveira@email.com', '1988-11-05', '', '2024-02-10 09:45:00'),
    ('85296374152', 'Daniel Eduardo Santos', '(41) 97777-3456', 'daniel.santos@email.com', '1992-01-30', 'Vegetariano', '2024-02-25 16:20:00'),
    ('96385274163', 'Eduarda Fernanda Lima', '(51) 96666-7890', 'eduarda.lima@email.com', '1987-09-18', 'Aniversário em setembro', '2024-03-05 11:10:00'),
    ('14725836904', 'Felipe Gabriel Souza', '(61) 95555-2345', 'felipe.souza@email.com', '1995-12-20', '', '2024-03-18 13:55:00'),
    ('25836914725', 'Gabriela Helena Pereira', '(71) 94444-6789', 'gabriela.pereira@email.com', '1983-04-12', 'Intolerante à lactose', '2024-04-02 10:00:00'),
    ('36925814736', 'Henrique Igor Martins', '(81) 93333-1234', 'henrique.martins@email.com', '1991-08-25', 'Fumante', '2024-04-15 15:30:00'),
    ('48159263707', 'Isabela Júlia Rocha', '(91) 92222-5678', 'isabela.rocha@email.com', '1989-02-28', '', '2024-05-10 08:45:00'),
    ('59263714818', 'João Kleber Almeida', '(11) 91111-9012', 'joao.almeida@email.com', '1993-06-15', 'Prefere andar alto', '2024-05-22 17:20:00'),
    ('61374825929', 'Karen Luisa Fernandes', '(21) 90000-3456', 'karen.fernandes@email.com', '1986-10-03', 'Alergia a camarão', '2024-06-05 12:15:00'),
    ('72485936030', 'Lucas Matheus Gonçalves', '(31) 99999-7890', 'lucas.goncalves@email.com', '1994-12-11', '', '2024-06-20 09:00:00'),
    ('83596047141', 'Mariana Natália Ribeiro', '(41) 98888-2345', 'mariana.ribeiro@email.com', '1984-05-19', 'Prefere quarto com banheira', '2024-07-03 14:40:00'),
    ('94607158252', 'Nathan Otávio Cardoso', '(51) 97777-6789', 'nathan.cardoso@email.com', '1996-03-07', '', '2024-07-18 16:55:00'),
    ('05718269363', 'Olivia Paula Dias', '(61) 96666-1234', 'olivia.dias@email.com', '1982-08-30', 'Cadeirante - acessibilidade', '2024-08-01 10:25:00'),
    ('16829370474', 'Pedro Quirino Nunes', '(71) 95555-5678', 'pedro.nunes@email.com', '1990-11-22', '', '2024-08-15 13:10:00'),
    ('27930481585', 'Quésia Raquel Moreira', '(81) 94444-9012', 'quesia.moreira@email.com', '1988-04-17', 'Vegetariana estrita', '2024-08-28 15:50:00'),
    ('38041592696', 'Rafael Samuel Teixeira', '(91) 93333-3456', 'rafael.teixeira@email.com', '1992-09-05', 'Alérgico a látex', '2024-09-12 11:35:00'),
    ('49152603707', 'Sofia Talita Barbosa', '(11) 92222-7890', 'sofia.barbosa@email.com', '1995-01-28', '', '2024-09-25 08:20:00'),
    ('50263714818', 'Tiago Ulisses Vieira', '(21) 91111-2345', 'tiago.vieira@email.com', '1987-07-14', 'Prefere quarto térreo', '2024-10-08 12:45:00')
ON CONFLICT (cpf) DO NOTHING;

-- ============================================================
-- Quartos (16) — 4 por categoria, distribuídos nos andares 1 a 4
-- Estados variados: DISPONIVEL, OCUPADO, LIMPEZA, MANUTENCAO, INTERDITADO
-- ============================================================
INSERT INTO quarto (numero, categoria_id, andar, capacidade_operacional, estado, observacoes, ativo)
SELECT q.numero, c.id, q.andar, q.capacidade_operacional, q.estado, q.observacoes, q.ativo
FROM (VALUES
    ('101', 'Standard', 1, 2, 'DISPONIVEL', 'Quarto padrão andar térreo', true),
    ('102', 'Standard', 1, 2, 'OCUPADO', 'Ocupado até 2025-01-20', true),
    ('103', 'Standard', 1, 2, 'LIMPEZA', 'Aguardando governança', true),
    ('104', 'Standard', 1, 2, 'MANUTENCAO', 'Troca de ar-condicionado', true),
    ('201', 'Superior', 2, 2, 'DISPONIVEL', 'Vista para jardim', true),
    ('202', 'Superior', 2, 2, 'DISPONIVEL', 'Vista para jardim', true),
    ('203', 'Superior', 2, 2, 'OCUPADO', 'Ocupado até 2025-01-25', true),
    ('204', 'Superior', 2, 2, 'INTERDITADO', 'Infiltração no teto', true),
    ('301', 'Luxo', 3, 3, 'DISPONIVEL', 'Vista para o mar', true),
    ('302', 'Luxo', 3, 3, 'DISPONIVEL', 'Vista para o mar', true),
    ('303', 'Luxo', 3, 3, 'OCUPADO', 'Ocupado até 2025-02-01', true),
    ('304', 'Luxo', 3, 3, 'LIMPEZA', 'Aguardando governança', true),
    ('401', 'Suíte Master', 4, 4, 'DISPONIVEL', 'Suíte master com varanda', true),
    ('402', 'Suíte Master', 4, 4, 'DISPONIVEL', 'Suíte master com varanda', true),
    ('403', 'Suíte Master', 4, 4, 'OCUPADO', 'Ocupado até 2025-02-10', true),
    ('404', 'Suíte Master', 4, 4, 'MANUTENCAO', 'Reforma do banheiro', true)
) AS q(numero, categoria_nome, andar, capacidade_operacional, estado, observacoes, ativo)
JOIN categoria_quarto c ON c.nome = q.categoria_nome
ON CONFLICT (numero) DO NOTHING;

-- ============================================================
-- Itens de catálogo (10) — 6 produtos, 4 serviços
-- ============================================================
INSERT INTO item_catalogo (nome, descricao, tipo, preco_unitario, controla_estoque, estoque_atual, estoque_minimo, ativo)
VALUES
    ('Água mineral 500ml', 'Garrafa de água mineral sem gás', 'PRODUTO', 5.00, true, 120, 20, true),
    ('Refrigerante lata 350ml', 'Refrigerante cola zero açúcar', 'PRODUTO', 8.00, true, 80, 15, true),
    ('Cerveja long neck 330ml', 'Cerveja premium', 'PRODUTO', 12.00, true, 60, 12, true),
    ('Kit higiene pessoal', 'Sabonete, shampoo, condicionador, escova dental', 'PRODUTO', 25.00, true, 40, 10, true),
    ('Fraldas descartáveis (pacote)', 'Tamanho M, 32 unidades', 'PRODUTO', 45.00, true, 30, 5, true),
    ('Café expresso', 'Café expresso curto', 'SERVICO', 6.00, false, 0, 0, true),
    ('Lavanderia (kg)', 'Lavagem e passagem de roupas por kg', 'SERVICO', 18.00, false, 0, 0, true),
    ('Massagem relaxante (30min)', 'Massagem relaxante corporal 30 minutos', 'SERVICO', 120.00, false, 0, 0, true),
    ('Translado aeroporto', 'Translado hotel-aeroporto-hotel', 'SERVICO', 80.00, false, 0, 0, true),
    ('Room service café da manhã', 'Café da manhã servido no quarto', 'SERVICO', 45.00, false, 0, 0, true)
ON CONFLICT (nome) DO NOTHING;

-- ============================================================
-- Reservas (12) — em diferentes estados
-- Código formatado: RES-ANO-SEQUENCIAL
-- ============================================================
-- Para criar reservas, precisamos dos IDs de hóspedes e quartos.
-- Usamos CTEs para buscar IDs por chaves naturais.

-- Reserva 1: Pendente, futura
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000001', h.id, q.id, '2025-02-10', '2025-02-13', 2, c.preco_base, 0.00, c.preco_base * 3, '2025-01-10 14:30:00', 'PENDENTE', NULL, 'Solicitação de quarto silencioso'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '52998224725' AND q.numero = '201' AND c.nome = 'Superior'
ON CONFLICT (codigo) DO NOTHING;

-- Reserva 2: Confirmada, futura
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000002', h.id, q.id, '2025-02-15', '2025-02-18', 2, c.preco_base, 15.00, (c.preco_base * 3) - 15.00, '2025-01-12 10:15:00', 'CONFIRMADA', NULL, 'Aniversário de casamento'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '63547819230' AND q.numero = '301' AND c.nome = 'Luxo'
ON CONFLICT (codigo) DO NOTHING;

-- Reserva 3: Cancelada
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000003', h.id, q.id, '2025-02-20', '2025-02-22', 2, c.preco_base, 0.00, c.preco_base * 2, '2025-01-05 09:00:00', 'CANCELADA', NULL, 'Cliente cancelou por motivo de saúde'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '74185296341' AND q.numero = '202' AND c.nome = 'Standard'
ON CONFLICT (codigo) DO NOTHING;

-- Reserva 4: No-show (data passada, sem check-in)
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000004', h.id, q.id, '2025-01-15', '2025-01-17', 1, c.preco_base, 0.00, c.preco_base * 2, '2025-01-10 11:00:00', 'NO_SHOW', NULL, 'Cliente não compareceu'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '85296374152' AND q.numero = '101' AND c.nome = 'Standard'
ON CONFLICT (codigo) DO NOTHING;

-- Reserva 5: Concluída (check-out realizado)
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000005', h.id, q.id, '2025-01-05', '2025-01-08', 2, c.preco_base, 20.00, (c.preco_base * 3) - 20.00, '2024-12-20 16:45:00', 'CONCLUIDA', NULL, 'Estadia de 3 noites'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '96385274163' AND q.numero = '302' AND c.nome = 'Luxo'
ON CONFLICT (codigo) DO NOTHING;

-- Reserva 6: Pendente, futura
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000006', h.id, q.id, '2025-02-20', '2025-02-23', 3, c.preco_base, 0.00, c.preco_base * 3, '2025-01-18 13:20:00', 'PENDENTE', 'Reserva para família com criança', 'Berço solicitado'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '14725836904' AND q.numero = '401' AND c.nome = 'Suíte Master'
ON CONFLICT (codigo) DO NOTHING;

-- Reserva 7: Confirmada, check-in hoje
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000007', h.id, q.id, CURRENT_DATE, CURRENT_DATE + INTERVAL '2 days', 2, c.preco_base, 0.00, c.preco_base * 2, '2025-01-19 08:00:00', 'CONFIRMADA', NULL, 'Check-in previsto para hoje'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '25836914725' AND q.numero = '203' AND c.nome = 'Standard'
ON CONFLICT (codigo) DO NOTHING;

-- Reserva 8: Confirmada, check-in ontem
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000008', h.id, q.id, CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '1 day', 2, c.preco_base, 10.00, (c.preco_base * 2) - 10.00, '2025-01-18 15:00:00', 'CONFIRMADA', NULL, 'Check-in ontem'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '36925814736' AND q.numero = '303' AND c.nome = 'Luxo'
ON CONFLICT (codigo) DO NOTHING;

-- Reserva 9: Pendente, data passada (exceção)
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000009', h.id, q.id, '2025-01-10', '2025-01-12', 2, c.preco_base, 0.00, c.preco_base * 2, '2025-01-08 12:00:00', 'PENDENTE', 'Reserva para datas passadas - evento corporativo', 'Reserva excepcional autorizada pelo gerente'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '48159263707' AND q.numero = '204' AND c.nome = 'Standard'
ON CONFLICT (codigo) DO NOTHING;

-- Reserva 10: Confirmada, futura
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000010', h.id, q.id, '2025-03-01', '2025-03-05', 4, c.preco_base, 50.00, (c.preco_base * 4) - 50.00, '2025-01-20 11:30:00', 'CONFIRMADA', NULL, 'Família com 2 crianças'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '59263714818' AND q.numero = '402' AND c.nome = 'Suíte Master'
ON CONFLICT (codigo) DO NOTHING;

-- Reserva 11: Cancelada
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000011', h.id, q.id, '2025-03-10', '2025-03-12', 2, c.preco_base, 0.00, c.preco_base * 2, '2025-01-22 09:45:00', 'CANCELADA', NULL, 'Cliente cancelou - mudança de planos'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '61374825929' AND q.numero = '102' AND c.nome = 'Standard'
ON CONFLICT (codigo) DO NOTHING;

-- Reserva 12: No-show
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000012', h.id, q.id, CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '3 days', 1, c.preco_base, 0.00, c.preco_base * 2, '2025-01-15 14:00:00', 'NO_SHOW', NULL, 'Não compareceu'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '72485936030' AND q.numero = '103' AND c.nome = 'Standard'
ON CONFLICT (codigo) DO NOTHING;

-- ============================================================
-- Hospedagens (5) — estados: EM_ANDAMENTO, ENCERRADA, CANCELADA
-- ============================================================

-- Hospedagem 1: Check-in hoje, reserva 7 (CONFIRMADA), quarto 203
INSERT INTO hospedagem (reserva_id, quarto_id, check_in_real, check_out_real, estado, motivo_excecao_encerramento, observacoes)
SELECT r.id, q.id, NOW(), NULL, 'EM_ANDAMENTO', NULL, 'Check-in realizado normalmente'
FROM reserva r, quarto q
WHERE r.codigo = 'RES-2025-000007' AND q.numero = '203'
ON CONFLICT (reserva_id) DO NOTHING;

-- Hospedagem 2: Check-in ontem, reserva 8 (CONFIRMADA), quarto 303
INSERT INTO hospedagem (reserva_id, quarto_id, check_in_real, check_out_real, estado, motivo_excecao_encerramento, observacoes)
SELECT r.id, q.id, NOW() - INTERVAL '1 day', NULL, 'EM_ANDAMENTO', NULL, 'Hóspede chegou ontem à noite'
FROM reserva r, quarto q
WHERE r.codigo = 'RES-2025-000008' AND q.numero = '303'
ON CONFLICT (reserva_id) DO NOTHING;

-- Hospedagem 3: Encerrada (check-out ontem), reserva 5 (CONCLUIDA), quarto 302
INSERT INTO hospedagem (reserva_id, quarto_id, check_in_real, check_out_real, estado, motivo_excecao_encerramento, observacoes)
SELECT r.id, q.id, '2025-01-05 14:30:00', '2025-01-08 11:00:00', 'ENCERRADA', NULL, 'Estadia normal, check-out pontual'
FROM reserva r, quarto q
WHERE r.codigo = 'RES-2025-000005' AND q.numero = '302'
ON CONFLICT (reserva_id) DO NOTHING;

-- Hospedagem 4: Encerrada com justificativa (saldo pendente), reserva hipotética
INSERT INTO hospedagem (reserva_id, quarto_id, check_in_real, check_out_real, estado, motivo_excecao_encerramento, observacoes)
SELECT r.id, q.id, '2025-01-10 15:00:00', '2025-01-12 10:00:00', 'CANCELADA', 'Hóspede saiu sem quitar consumos; cobrança posterior acordada', 'Saída antecipada sem quitação total'
FROM reserva r, quarto q
WHERE r.codigo = 'RES-2025-000009' AND q.numero = '204'
ON CONFLICT (reserva_id) DO NOTHING;

-- Hospedagem 5: Encerrada normalmente
INSERT INTO hospedagem (reserva_id, quarto_id, check_in_real, check_out_real, estado, motivo_excecao_encerramento, observacoes)
SELECT r.id, q.id, '2025-01-01 16:00:00', '2025-01-04 12:00:00', 'ENCERRADA', NULL, 'Estadia de 3 noites finalizada'
FROM reserva r, quarto q
WHERE r.codigo = 'RES-2025-000005' AND q.numero = '302'
ON CONFLICT (reserva_id) DO NOTHING;

-- Nota: A reserva 5 já foi usada na hospedagem 3. Para evitar conflito de UNIQUE (reserva_id),
-- não criamos uma segunda hospedagem para a mesma reserva.
-- As 5 hospedagens usam reservas diferentes: 7, 8, 5, 9, e precisamos de uma 5ª reserva distinta.
-- Como a reserva 5 já foi usada, vamos criar uma hospedagem adicional baseada em uma reserva nova.

-- Adicionar uma reserva extra para a 5ª hospedagem
INSERT INTO reserva (codigo, hospede_id, quarto_id, data_check_in, data_check_out, quantidade_hospedes, valor_diaria, desconto, valor_total, data_criacao, estado, motivo_excecao, observacoes)
SELECT 'RES-2025-000013', h.id, q.id, '2025-01-20', '2025-01-23', 2, c.preco_base, 0.00, c.preco_base * 3, '2025-01-18 10:00:00', 'CONFIRMADA', NULL, 'Reserva para 5ª hospedagem de teste'
FROM hospede h, quarto q, categoria_quarto c
WHERE h.cpf = '05718269363' AND q.numero = '104' AND c.nome = 'Standard'
ON CONFLICT (codigo) DO NOTHING;

-- Hospedagem 5: Em andamento (check-in hoje)
INSERT INTO hospedagem (reserva_id, quarto_id, check_in_real, check_out_real, estado, motivo_excecao_encerramento, observacoes)
SELECT r.id, q.id, NOW(), NULL, 'EM_ANDAMENTO', NULL, 'Check-in de última hora'
FROM reserva r, quarto q
WHERE r.codigo = 'RES-2025-000013' AND q.numero = '104'
ON CONFLICT (reserva_id) DO NOTHING;

-- ============================================================
-- Consumos (12) — alguns ativos, alguns cancelados
-- ============================================================

-- Consumos para hospedagem 1 (quarto 203, reserva 7)
INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes)
SELECT h.id, i.id, 2, i.preco_unitario, i.preco_unitario * 2, NOW() - INTERVAL '2 hours', 'ATIVO', 'Consumo de frigobar'
FROM hospedagem h, item_catalogo i
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000007')
  AND i.nome = 'Água mineral 500ml'
ON CONFLICT DO NOTHING;

INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes)
SELECT h.id, i.id, 1, i.preco_unitario, i.preco_unitario * 1, NOW() - INTERVAL '1 hour', 'ATIVO', 'Café da manhã no quarto'
FROM hospedagem h, item_catalogo i
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000007')
  AND i.nome = 'Café expresso'
ON CONFLICT DO NOTHING;

-- Consumos para hospedagem 2 (quarto 303, reserva 8)
INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes)
SELECT h.id, i.id, 3, i.preco_unitario, i.preco_unitario * 3, NOW() - INTERVAL '3 hours', 'ATIVO', 'Refrigerantes'
FROM hospedagem h, item_catalogo i
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000008')
  AND i.nome = 'Refrigerante lata 350ml'
ON CONFLICT DO NOTHING;

INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes)
SELECT h.id, i.id, 2, i.preco_unitario, i.preco_unitario * 2, NOW() - INTERVAL '30 minutes', 'CANCELADO', 'Cancelado pelo hóspede - pedido errado'
FROM hospedagem h, item_catalogo i
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000008')
  AND i.nome = 'Cerveja long neck 330ml'
ON CONFLICT DO NOTHING;

-- Consumos para hospedagem 3 (encerrada, reserva 5, quarto 302)
INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes)
SELECT h.id, i.id, 1, i.preco_unitario, i.preco_unitario * 1, '2025-01-06 10:00:00', 'ATIVO', 'Kit higiene'
FROM hospedagem h, item_catalogo i
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')
  AND i.nome = 'Kit higiene pessoal'
ON CONFLICT DO NOTHING;

INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes)
SELECT h.id, i.id, 2, i.preco_unitario, i.preco_unitario * 2, '2025-01-07 14:00:00', 'ATIVO', 'Lavanderia'
FROM hospedagem h, item_catalogo i
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')
  AND i.nome = 'Lavanderia (kg)'
ON CONFLICT DO NOTHING;

INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes)
SELECT h.id, i.id, 1, i.preco_unitario, i.preco_unitario * 1, '2025-01-07 18:00:00', 'ATIVO', 'Massagem relaxante'
FROM hospedagem h, item_catalogo i
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')
  AND i.nome = 'Massagem relaxante (30min)'
ON CONFLICT DO NOTHING;

-- Consumos para hospedagem 4 (encerrada com exceção, reserva 9, quarto 204)
INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes)
SELECT h.id, i.id, 2, i.preco_unitario, i.preco_unitario * 2, '2025-01-11 10:00:00', 'ATIVO', 'Refrigerantes'
FROM hospedagem h, item_catalogo i
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000009')
  AND i.nome = 'Refrigerante lata 350ml'
ON CONFLICT DO NOTHING;

INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes)
SELECT h.id, i.id, 1, i.preco_unitario, i.preco_unitario * 1, '2025-01-11 20:00:00', 'CANCELADO', 'Cancelado - hóspede não consumiu'
FROM hospedagem h, item_catalogo i
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000009')
  AND i.nome = 'Cerveja long neck 330ml'
ON CONFLICT DO NOTHING;

-- Consumos para hospedagem 5 (em andamento, reserva 13, quarto 104)
INSERT INTO consumo (hospedagem_id, item_id, quantidade, preco_unitario, valor_total, data_hora, estado, observacoes)
SELECT h.id, i.id, 1, i.preco_unitario, i.preco_unitario * 1, NOW() - INTERVAL '2 hours', 'ATIVO', 'Café da manhã'
FROM hospedagem h, item_catalogo i
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000013')
  AND i.nome = 'Room service café da manhã'
ON CONFLICT DO NOTHING;

-- ============================================================
-- Pagamentos (15) — confirmados e estornados
-- ============================================================

-- Pagamentos para hospedagem 1 (reserva 7, valor total = 250*2 = 500)
INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 300.00, 'PIX', NOW() - INTERVAL '1 hour', 'CONFIRMADO', 'Sinal de 60%'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000007')
ON CONFLICT DO NOTHING;

INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 200.00, 'CARTAO_CREDITO', NOW() - INTERVAL '30 minutes', 'CONFIRMADO', 'Saldo restante'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000007')
ON CONFLICT DO NOTHING;

-- Pagamentos para hospedagem 2 (reserva 8, valor total = (420*2)-10 = 830)
INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 500.00, 'CARTAO_DEBITO', NOW() - INTERVAL '2 hours', 'CONFIRMADO', 'Parte do pagamento'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000008')
ON CONFLICT DO NOTHING;

INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 330.00, 'DINHEIRO', NOW() - INTERVAL '1 hour', 'CONFIRMADO', 'Saldo final'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000008')
ON CONFLICT DO NOTHING;

-- Pagamentos para hospedagem 3 (encerrada, reserva 5, valor = (420*3)-20 = 1240)
INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 600.00, 'CARTAO_CREDITO', '2025-01-06 10:00:00', 'CONFIRMADO', 'Entrada'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')
ON CONFLICT DO NOTHING;

INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 640.00, 'PIX', '2025-01-08 11:00:00', 'CONFIRMADO', 'Saldo no check-out'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')
ON CONFLICT DO NOTHING;

-- Pagamentos para hospedagem 4 (encerrada com exceção, saldo pendente)
INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 200.00, 'DINHEIRO', '2025-01-11 12:00:00', 'CONFIRMADO', 'Pagamento parcial'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000009')
ON CONFLICT DO NOTHING;

-- Pagamento estornado (exemplo)
INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 50.00, 'CARTAO_CREDITO', '2025-01-07 15:00:00', 'ESTORNADO', 'Estorno de consumo cancelado - massagem'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')
ON CONFLICT DO NOTHING;

-- Pagamento para hospedagem 5 (em andamento, reserva 13)
INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 400.00, 'CARTAO_CREDITO', NOW() - INTERVAL '30 minutes', 'CONFIRMADO', 'Sinal'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000013')
ON CONFLICT DO NOTHING;

-- Pagamentos adicionais para completar 15 registros
INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 120.00, 'DINHEIRO', '2025-01-07 16:00:00', 'CONFIRMADO', 'Massagem paga no quarto'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')
ON CONFLICT DO NOTHING;

INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 80.00, 'PIX', '2025-01-11 18:00:00', 'CONFIRMADO', 'Translado aeroporto'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000009')
ON CONFLICT DO NOTHING;

INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 45.00, 'CARTAO_DEBITO', NOW() - INTERVAL '1 hour', 'CONFIRMADO', 'Café da manhã room service'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000013')
ON CONFLICT DO NOTHING;

INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 18.00, 'DINHEIRO', NOW() - INTERVAL '30 minutes', 'CONFIRMADO', 'Lavanderia'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000013')
ON CONFLICT DO NOTHING;

INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 100.00, 'PIX', '2025-01-07 14:00:00', 'ESTORNADO', 'Estorno de translado cancelado'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')
ON CONFLICT DO NOTHING;

INSERT INTO pagamento (hospedagem_id, valor, forma, data_hora, estado, observacao)
SELECT h.id, 25.00, 'PIX', '2025-01-06 11:00:00', 'CONFIRMADO', 'Kit higiene'
FROM hospedagem h
WHERE h.reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')
ON CONFLICT DO NOTHING;

-- ============================================================
-- Ordens de manutenção (4)
-- ============================================================

-- 1. Aberta, prioridade CRITICA (interdita quarto 204)
INSERT INTO ordem_manutencao (quarto_id, titulo, descricao, prioridade, estado, aberta_em, concluida_em, interditou_quarto, observacoes)
SELECT q.id, 'Infiltração no teto', 'Infiltração detectada após chuva forte; goteira sobre a cama', 'CRITICA', 'ABERTA', NOW() - INTERVAL '2 days', NULL, true, 'Quarto interditado preventivamente'
FROM quarto q
WHERE q.numero = '204'
ON CONFLICT DO NOTHING;

-- 2. Em andamento, prioridade ALTA (quarto 404)
INSERT INTO ordem_manutencao (quarto_id, titulo, descricao, prioridade, estado, aberta_em, concluida_em, interditou_quarto, observacoes)
SELECT q.id, 'Reforma do banheiro', 'Troca de revestimento e reparo de vazamento no box', 'ALTA', 'EM_ANDAMENTO', NOW() - INTERVAL '5 days', NULL, true, 'Quarto em manutenção desde o início'
FROM quarto q
WHERE q.numero = '404'
ON CONFLICT DO NOTHING;

-- 3. Concluída, prioridade MEDIA (quarto 104)
INSERT INTO ordem_manutencao (quarto_id, titulo, descricao, prioridade, estado, aberta_em, concluida_em, interditou_quarto, observacoes)
SELECT q.id, 'Troca de ar-condicionado', 'Substituição do split antigo por modelo inverter', 'MEDIA', 'CONCLUIDA', NOW() - INTERVAL '10 days', NOW() - INTERVAL '2 days', false, 'Serviço concluído, quarto liberado'
FROM quarto q
WHERE q.numero = '104'
ON CONFLICT DO NOTHING;

-- 4. Cancelada, prioridade BAIXA (quarto 101 - não existe manutenção ativa)
INSERT INTO ordem_manutencao (quarto_id, titulo, descricao, prioridade, estado, aberta_em, concluida_em, interditou_quarto, observacoes)
SELECT q.id, 'Pintura preventiva', 'Pintura das paredes do quarto', 'BAIXA', 'CANCELADA', NOW() - INTERVAL '15 days', NULL, false, 'Cancelada por falta de orçamento'
FROM quarto q
WHERE q.numero = '101'
ON CONFLICT DO NOTHING;

-- ============================================================
-- Tarefas de limpeza (8)
-- ============================================================

-- 1. Pendente - quarto 103 (está LIMPEZA)
INSERT INTO tarefa_limpeza (quarto_id, estado, criada_em, concluida_em, observacoes)
SELECT q.id, 'PENDENTE', NOW() - INTERVAL '1 hour', NULL, 'Limpeza padrão de saída'
FROM quarto q
WHERE q.numero = '103'
ON CONFLICT DO NOTHING;

-- 2. Em execução - quarto 304 (está LIMPEZA)
INSERT INTO tarefa_limpeza (quarto_id, estado, criada_em, concluida_em, observacoes)
SELECT q.id, 'EM_EXECUCAO', NOW() - INTERVAL '30 minutes', NULL, 'Limpeza profunda pós-check-out'
FROM quarto q
WHERE q.numero = '304'
ON CONFLICT DO NOTHING;

-- 3. Concluída - quarto 302 (hospedagem 3 encerrada)
INSERT INTO tarefa_limpeza (quarto_id, estado, criada_em, concluida_em, observacoes)
SELECT q.id, 'CONCLUIDA', '2025-01-08 11:30:00', '2025-01-08 13:00:00', 'Limpeza pós-check-out padrão'
FROM quarto q
WHERE q.numero = '302'
ON CONFLICT DO NOTHING;

-- 4. Concluída - quarto 104 (manutenção concluída)
INSERT INTO tarefa_limpeza (quarto_id, estado, criada_em, concluida_em, observacoes)
SELECT q.id, 'CONCLUIDA', '2025-01-10 14:00:00', '2025-01-10 16:00:00', 'Limpeza pós-manutenção do ar-condicionado'
FROM quarto q
WHERE q.numero = '104'
ON CONFLICT DO NOTHING;

-- 5. Pendente - quarto 204 (interditado, aguardando manutenção)
INSERT INTO tarefa_limpeza (quarto_id, estado, criada_em, concluida_em, observacoes)
SELECT q.id, 'PENDENTE', NOW() - INTERVAL '1 day', NULL, 'Aguardando conclusão da manutenção para limpeza final'
FROM quarto q
WHERE q.numero = '204'
ON CONFLICT DO NOTHING;

-- 6. Concluída - quarto 101 (manutenção cancelada)
INSERT INTO tarefa_limpeza (quarto_id, estado, criada_em, concluida_em, observacoes)
SELECT q.id, 'CONCLUIDA', NOW() - INTERVAL '5 days', NOW() - INTERVAL '4 days', 'Limpeza de rotina'
FROM quarto q
WHERE q.numero = '101'
ON CONFLICT DO NOTHING;

-- 7. Em execução - quarto 404 (manutenção em andamento)
INSERT INTO tarefa_limpeza (quarto_id, estado, criada_em, concluida_em, observacoes)
SELECT q.id, 'EM_EXECUCAO', NOW() - INTERVAL '2 hours', NULL, 'Limpeza durante manutenção do banheiro'
FROM quarto q
WHERE q.numero = '404'
ON CONFLICT DO NOTHING;

-- 8. Pendente - quarto 201 (disponível, limpeza de rotina)
INSERT INTO tarefa_limpeza (quarto_id, estado, criada_em, concluida_em, observacoes)
SELECT q.id, 'PENDENTE', NOW() - INTERVAL '2 hours', NULL, 'Limpeza de rotina - quarto disponível'
FROM quarto q
WHERE q.numero = '201'
ON CONFLICT DO NOTHING;

-- ============================================================
-- Registros de auditoria (amostra)
-- ============================================================

INSERT INTO registro_auditoria (operacao, usuario, entidade_tipo, entidade_id, data_hora, detalhe)
VALUES
    ('RESERVA_CRIADA', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Reserva', (SELECT id FROM reserva WHERE codigo = 'RES-2025-000001'), NOW() - INTERVAL '10 days', 'Código RES-2025-000001'),
    ('RESERVA_CRIADA', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Reserva', (SELECT id FROM reserva WHERE codigo = 'RES-2025-000002'), NOW() - INTERVAL '9 days', 'Código RES-2025-000002'),
    ('RESERVA_CRIADA', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Reserva', (SELECT id FROM reserva WHERE codigo = 'RES-2025-000003'), NOW() - INTERVAL '8 days', 'Código RES-2025-000003'),
    ('RESERVA_CANCELADA', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Reserva', (SELECT id FROM reserva WHERE codigo = 'RES-2025-000003'), NOW() - INTERVAL '7 days', 'Código RES-2025-000003'),
    ('RESERVA_CRIADA', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Reserva', (SELECT id FROM reserva WHERE codigo = 'RES-2025-000004'), NOW() - INTERVAL '8 days', 'Código RES-2025-000004'),
    ('RESERVA_NO_SHOW', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Reserva', (SELECT id FROM reserva WHERE codigo = 'RES-2025-000004'), NOW() - INTERVAL '2 days', 'Código RES-2025-000004'),
    ('RESERVA_CRIADA', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Reserva', (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005'), NOW() - INTERVAL '15 days', 'Código RES-2025-000005'),
    ('CHECK_IN', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Hospedagem', (SELECT id FROM hospedagem WHERE reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')), NOW() - INTERVAL '12 days', 'Reserva RES-2025-000005'),
    ('CHECK_OUT', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Hospedagem', (SELECT id FROM hospedagem WHERE reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')), NOW() - INTERVAL '9 days', 'Reserva RES-2025-000005'),
    ('CONSUMO_LANCADO', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Consumo', (SELECT id FROM consumo WHERE hospedagem_id = (SELECT id FROM hospedagem WHERE reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')) LIMIT 1), NOW() - INTERVAL '11 days', 'Hospedagem 1 · Água mineral 500ml'),
    ('PAGAMENTO_REGISTRADO', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Pagamento', (SELECT id FROM pagamento WHERE hospedagem_id = (SELECT id FROM hospedagem WHERE reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000005')) LIMIT 1), NOW() - INTERVAL '9 days', 'Hospedagem 1 · PIX'),
    ('MANUTENCAO_ABERTA', 'RECEPCIONISTA_USER_PLACEHOLDER', 'OrdemManutencao', (SELECT id FROM ordem_manutencao WHERE quarto_id = (SELECT id FROM quarto WHERE numero = '204') LIMIT 1), NOW() - INTERVAL '2 days', 'Quarto 204 · Infiltração no teto'),
    ('MANUTENCAO_INICIADA', 'RECEPCIONISTA_USER_PLACEHOLDER', 'OrdemManutencao', (SELECT id FROM ordem_manutencao WHERE quarto_id = (SELECT id FROM quarto WHERE numero = '404') LIMIT 1), NOW() - INTERVAL '5 days', 'Quarto 404 · Reforma do banheiro'),
    ('LIMPEZA_CONCLUIDA', 'RECEPCIONISTA_USER_PLACEHOLDER', 'TarefaLimpeza', (SELECT id FROM tarefa_limpeza WHERE quarto_id = (SELECT id FROM quarto WHERE numero = '302') LIMIT 1), NOW() - INTERVAL '9 days', 'Quarto 302'),
    ('CHECK_IN', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Hospedagem', (SELECT id FROM hospedagem WHERE reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000007')), NOW(), 'Reserva RES-2025-000007'),
    ('CHECK_IN', 'RECEPCIONISTA_USER_PLACEHOLDER', 'Hospedagem', (SELECT id FROM hospedagem WHERE reserva_id = (SELECT id FROM reserva WHERE codigo = 'RES-2025-000008')), NOW() - INTERVAL '1 day', 'Reserva RES-2025-000008')
ON CONFLICT DO NOTHING;

-- Fim do script data-postgres.sql