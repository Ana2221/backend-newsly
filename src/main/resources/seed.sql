-- ============================================================
-- SEED — newsly
-- Ordem: respeita todas as FKs (sem empresa ainda nos usuarios)
-- UUID armazenado como BINARY(16) via UNHEX(REPLACE(...,'-',''))
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Limpa e reseta auto_increment (TRUNCATE com FK_CHECKS=0)
TRUNCATE TABLE solicitacao;
TRUNCATE TABLE diretor_permissoes;
TRUNCATE TABLE diretor;
TRUNCATE TABLE diretoria;
TRUNCATE TABLE funcao;
TRUNCATE TABLE comentario;
TRUNCATE TABLE noticia;
TRUNCATE TABLE gestor_permissoes;
TRUNCATE TABLE responsavel_permissoes;
TRUNCATE TABLE colaborador;
TRUNCATE TABLE gestor;
TRUNCATE TABLE responsavel;
TRUNCATE TABLE empresa_jr;
TRUNCATE TABLE usuario;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 1. USUÁRIOS BASE (tabela usuario — herança JOINED)
-- ============================================================

-- Responsaveis (ids 1, 2)
INSERT INTO usuario (id, tipo, nome, senha, email, telefone) VALUES
  (1, 'RESPONSAVEL', 'Maria Costa',   'senha123', 'maria@newsly.com',   '11999990001'),
  (2, 'RESPONSAVEL', 'Pedro Alves',   'senha123', 'pedro@newsly.com',   '11999990002');

-- Gestores (ids 3, 4)
INSERT INTO usuario (id, tipo, nome, senha, email, telefone) VALUES
  (3, 'GESTOR', 'Carlos Lima',   'senha123', 'carlos@newsly.com',  '11999990003'),
  (4, 'GESTOR', 'Juliana Ferreira', 'senha123', 'juliana@newsly.com', '11999990004');

-- Colaboradores (ids 5, 6, 7, 8)
INSERT INTO usuario (id, tipo, nome, senha, email, telefone) VALUES
  (5, 'COLABORADOR', 'Ana Silva',      'senha123', 'ana@newsly.com',      '11999990005'),
  (6, 'COLABORADOR', 'Bruno Souza',    'senha123', 'bruno@newsly.com',    '11999990006'),
  (7, 'COLABORADOR', 'Carla Mendes',   'senha123', 'carla@newsly.com',    '11999990007'),
  (8, 'COLABORADOR', 'Diego Rocha',    'senha123', 'diego@newsly.com',    '11999990008');

-- ============================================================
-- 2. SUBCLASSES DE USUARIO
-- ============================================================

-- Responsaveis
INSERT INTO responsavel (usuario_id, matricula, empresa_id) VALUES
  (1, UNHEX(REPLACE('a0000001-0000-0000-0000-000000000001', '-', '')), NULL),
  (2, UNHEX(REPLACE('a0000002-0000-0000-0000-000000000002', '-', '')), NULL);

-- Gestores
INSERT INTO gestor (usuario_id, matricula) VALUES
  (3, UNHEX(REPLACE('b0000003-0000-0000-0000-000000000003', '-', ''))),
  (4, UNHEX(REPLACE('b0000004-0000-0000-0000-000000000004', '-', '')));

-- Colaboradores (empresa_id null por enquanto — será atualizado após criar empresas)
INSERT INTO colaborador (usuario_id, matricula, atividade, empresa_id) VALUES
  (5, UNHEX(REPLACE('c0000005-0000-0000-0000-000000000005', '-', '')), 1, NULL),
  (6, UNHEX(REPLACE('c0000006-0000-0000-0000-000000000006', '-', '')), 1, NULL),
  (7, UNHEX(REPLACE('c0000007-0000-0000-0000-000000000007', '-', '')), 1, NULL),
  (8, UNHEX(REPLACE('c0000008-0000-0000-0000-000000000008', '-', '')), 0, NULL);

-- Permissoes dos gestores
INSERT INTO gestor_permissoes (gestor_id, permissao) VALUES
  (3, 'LEITOR'),
  (3, 'ESCRITOR'),
  (4, 'LEITOR');

-- Permissoes dos responsaveis
INSERT INTO responsavel_permissoes (responsavel_id, permissao) VALUES
  (1, 'ESCRITOR'),
  (2, 'LEITOR'),
  (2, 'ESCRITOR');

-- ============================================================
-- 3. EMPRESAS
-- ============================================================

INSERT INTO empresa_jr (id, nome, cnpj, descricao, logo_url, email, telefone, color) VALUES
  (1, 'Nite Jr',       '12.345.678/0001-90', 'Empresa júnior de tecnologia',        NULL, 'contato@nitejr.com',    '1133330001', '#1A73E8'),
  (2, 'Far Consultoria', '98.765.432/0001-11', 'Empresa júnior de consultoria',     NULL, 'contato@farconsult.com', '1133330002', '#34A853');

-- Vincula responsaveis às empresas
UPDATE responsavel SET empresa_id = 1 WHERE usuario_id = 1;
UPDATE responsavel SET empresa_id = 2 WHERE usuario_id = 2;

-- Vincula colaboradores às empresas
UPDATE colaborador SET empresa_id = 1 WHERE usuario_id IN (5, 6);
UPDATE colaborador SET empresa_id = 2 WHERE usuario_id IN (7, 8);

-- ============================================================
-- 4. FUNÇÕES
-- ============================================================

INSERT INTO funcao (id, nome, descricao) VALUES
  (1, 'Presidente',    'Responsável pela gestão geral da empresa'),
  (2, 'Vice-Presidente', 'Apoia o presidente e assume em sua ausência'),
  (3, 'Diretor Financeiro', 'Responsável pelas finanças'),
  (4, 'Diretor de Marketing', 'Responsável pela comunicação e marketing');

-- ============================================================
-- 5. DIRETORIAS
-- ============================================================

INSERT INTO diretoria (id, criada_em, empresa_id) VALUES
  (1, '2024-01-15', 1),
  (2, '2024-02-01', 2);

-- ============================================================
-- 6. DIRETORES
-- ============================================================

-- Empresa 1: Ana (5) = Presidente, Bruno (6) = Diretor Financeiro
INSERT INTO diretor (colaborador_id, funcao_id, diretoria_id) VALUES
  (5, 1, 1),
  (6, 3, 1);

-- Empresa 2: Carla (7) = Presidente, Diego (8) = Diretor de Marketing
INSERT INTO diretor (colaborador_id, funcao_id, diretoria_id) VALUES
  (7, 1, 2),
  (8, 4, 2);

-- Permissoes dos diretores
INSERT INTO diretor_permissoes (diretor_id, permissao) VALUES
  (5, 'ESCRITOR'),
  (6, 'LEITOR'),
  (7, 'ESCRITOR'),
  (8, 'LEITOR');

-- ============================================================
-- 7. NOTICIAS
-- ============================================================

INSERT INTO noticia (id, titulo, conteudo, publicada_em, imagem_url, empresa_id) VALUES
  (1,
   'Nite Jr firma parceria estratégica com empresa de inovação',
   'A Nite Jr anunciou hoje uma parceria com a startup InnTech para desenvolvimento de soluções de automação para o mercado local. A parceria prevê troca de conhecimentos e projetos conjuntos ao longo de 2026.',
   '2026-08-10 09:00:00', NULL, 1),

  (2,
   'Nite Jr lança novo programa de capacitação para membros',
   'O programa CapacitaNite 2026 oferta trilhas de aprendizado em desenvolvimento web, gestão de projetos e empreendedorismo. As inscrições estão abertas até o final do mês.',
   '2026-09-01 10:30:00', NULL, 1),

  (3,
   'Far Consultoria apresenta resultados do primeiro semestre',
   'Os resultados do primeiro semestre de 2026 mostram crescimento de 35% no número de clientes atendidos. O CEO da empresa atribui o resultado à expansão da equipe e à nova metodologia de diagnóstico.',
   '2026-09-15 14:00:00', NULL, 2),

  (4,
   'Far Consultoria abre processo seletivo para novos membros',
   'Estão abertas as inscrições para o processo seletivo do segundo semestre de 2026. São oferecidas vagas nas áreas de consultoria empresarial, financeira e de marketing.',
   '2026-10-01 08:00:00', NULL, 2);

-- ============================================================
-- 8. COMENTARIOS
-- ============================================================

-- Comentarios raiz
INSERT INTO comentario (id, conteudo, publicado_em, noticia_id, comentario_pai_id) VALUES
  (1, 'Ótima notícia! Essa parceria vai trazer muitos benefícios.',   '2026-08-10 10:00:00', 1, NULL),
  (2, 'Parabéns à equipe da Nite Jr pelo resultado!',                 '2026-08-10 11:30:00', 1, NULL),
  (3, 'Muito bom! Quero saber mais sobre o programa de capacitação.', '2026-09-01 12:00:00', 2, NULL),
  (4, 'Crescimento impressionante da Far Consultoria!',               '2026-09-15 15:00:00', 3, NULL);

-- Respostas (comentarios filhos)
INSERT INTO comentario (id, conteudo, publicado_em, noticia_id, comentario_pai_id) VALUES
  (5, 'Concordo! A InnTech é referência no setor.',       '2026-08-10 10:45:00', 1, 1),
  (6, 'As trilhas de aprendizado estão incríveis!',       '2026-09-01 13:00:00', 2, 3),
  (7, 'A metodologia nova realmente fez a diferença.',    '2026-09-15 16:00:00', 3, 4);

-- ============================================================
-- 9. SOLICITACOES
-- ============================================================

-- Maria (1) solicita ao Carlos (3) para a noticia 1 → ACEITA
INSERT INTO solicitacao (id, responsavel_id, gestor_id, noticia_id, status, criada_em, respondida_em, motivo_recusa) VALUES
  (1, 1, 3, 1, 'ACEITA', '2026-08-09 08:00:00', '2026-08-09 09:30:00', NULL);

-- Maria (1) solicita ao Carlos (3) para a noticia 2 → PENDENTE
INSERT INTO solicitacao (id, responsavel_id, gestor_id, noticia_id, status, criada_em, respondida_em, motivo_recusa) VALUES
  (2, 1, 3, 2, 'PENDENTE', '2026-08-31 17:00:00', NULL, NULL);

-- Pedro (2) solicita ao Carlos (3) para a noticia 3 → RECUSADA
INSERT INTO solicitacao (id, responsavel_id, gestor_id, noticia_id, status, criada_em, respondida_em, motivo_recusa) VALUES
  (3, 2, 3, 3, 'RECUSADA', '2026-09-14 10:00:00', '2026-09-14 14:00:00', 'Conteúdo precisa de revisão antes da publicação.');

-- Pedro (2) solicita à Juliana (4) para a noticia 4 → PENDENTE
INSERT INTO solicitacao (id, responsavel_id, gestor_id, noticia_id, status, criada_em, respondida_em, motivo_recusa) VALUES
  (4, 2, 4, 4, 'PENDENTE', '2026-09-30 09:00:00', NULL, NULL);

-- Maria (1) solicita ao Carlos (3) para a noticia 1 → CANCELADA (teste de cancelamento)
INSERT INTO solicitacao (id, responsavel_id, gestor_id, noticia_id, status, criada_em, respondida_em, motivo_recusa) VALUES
  (5, 1, 4, 2, 'CANCELADA', '2026-09-05 11:00:00', NULL, NULL);
