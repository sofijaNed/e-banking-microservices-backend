-- Kreiraj bazu ako ne postoji
CREATE DATABASE IF NOT EXISTS loandb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE loandb;

-- Bezbedno brisanje (za slučaj postojećih FK-ova)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS loan_payment;
DROP TABLE IF EXISTS loan;
SET FOREIGN_KEY_CHECKS = 1;

-- Parent tabela
CREATE TABLE loan (
  id BIGINT NOT NULL AUTO_INCREMENT,
  principal_amount     DECIMAL(19,4),
  interest_rate        DECIMAL(6,4),
  term_months          INT,
  currency             VARCHAR(10),
  monthly_payment      DECIMAL(19,4),
  outstanding_balance  DECIMAL(19,4),
  date_issued          DATE,
  status ENUM('PENDING','APPROVED','REJECTED','DISBURSED') DEFAULT 'PENDING',
  approved_by BIGINT,         -- eksterni ID (user-service): nema FK
  approved_at DATE,
  note VARCHAR(500),
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  created_by VARCHAR(200),
  updated_by VARCHAR(200),
  account_id BIGINT,          -- eksterni ID (account-service): nema FK
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Child tabela sa FK na loan.id
CREATE TABLE loan_payment (
  id BIGINT NOT NULL AUTO_INCREMENT,
  loan_id BIGINT NOT NULL,
  due_date DATE,
  amount DECIMAL(19,4) NOT NULL,
  currency VARCHAR(10),
  paid TINYINT(1) DEFAULT 0,
  paid_at DATE,
  principal_amount DECIMAL(19,4),
  interest_amount DECIMAL(19,4),
  created_at TIMESTAMP NULL DEFAULT NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL,
  created_by VARCHAR(200),
  updated_by VARCHAR(200),
  payment_date DATE,
  note VARCHAR(255),
  PRIMARY KEY (id),
  KEY idx_loanpayment_loan (loan_id),
  CONSTRAINT fk_loanpayment_loan
    FOREIGN KEY (loan_id) REFERENCES loan(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed podaci: prvo LOAN, zatim LOAN_PAYMENT
INSERT INTO loan
  (id, principal_amount, interest_rate, term_months, currency, monthly_payment, outstanding_balance,
   date_issued, status, approved_by, approved_at, note, created_at, updated_at, created_by, updated_by, account_id)
VALUES
  (1, 100000.0000, 5.5000, 24, 'RSD', 4500.0000, 90000.0000, '2024-06-01', 'APPROVED', 1, '2024-06-05', 'Kredit za renoviranje stana', NULL, NULL, 'employee1', NULL, 1),
  (3, 500000.0000, 5.5000, 3,  'RSD', NULL,       500000.0000, '2025-08-11', 'APPROVED', 1, '2025-08-11', NULL, NULL, '2025-08-11 13:04:24', NULL, 'employee1', 1),
  (5, 60000.0000,  5.5000, 3,  'RSD', 20183.6100, 60000.0000,  '2025-08-12', 'APPROVED', 1, '2025-08-12', NULL, '2025-08-11 22:32:35', '2025-08-11 22:32:50', 'client1', 'employee1', 1),
  (10, 20000.0000, 5.5000, 2,  'RSD', 10068.8000, 20000.0000,  '2025-08-12', 'APPROVED', 1, '2025-08-12', NULL, '2025-08-11 23:57:09', '2025-08-12 00:21:59', 'client1', 'employee1', 1);

INSERT INTO loan_payment
  (id, loan_id, due_date, amount, currency, paid, paid_at, principal_amount, interest_amount,
   created_at, updated_at, created_by, updated_by, payment_date, note)
VALUES
  (1,  1,  '2024-07-01',  4500.0000, 'RSD', 1, '2024-07-01', 4050.0000, 450.0000, '2025-08-10 19:32:01', '2025-08-10 19:32:01', 'system',   'system',   '2024-07-01', 'Prva rata kredita'),
  (2,  1,  '2024-08-01',  4500.0000, 'RSD', 1, '2025-08-12', 4050.0000, 450.0000, '2025-08-10 19:32:01', '2025-08-12 00:17:19', 'system',   'client1',  '2024-08-01', 'Druga rata kredita'),
  (15, 5,  '2025-09-12', 20183.6100, 'RSD', 1, '2025-08-12', 19908.6100, 275.0000, '2025-08-11 22:32:50', '2025-08-12 00:09:10', 'employee1','client1',  NULL,        NULL),
  (16, 5,  '2025-10-12', 20183.6100, 'RSD', 1, '2025-08-12', 19999.8600, 183.7500, '2025-08-11 22:32:50', '2025-08-12 00:14:13', 'employee1','client1',  NULL,        NULL),
  (17, 5,  '2025-11-12', 20183.6100, 'RSD', 0, NULL,         20091.5200,  92.0900, '2025-08-11 22:32:50', '2025-08-11 22:32:50', 'employee1','employee1',NULL,        NULL),
  (20, 10, '2025-09-12', 10068.8000, 'RSD', 0, NULL,          9977.1300,  91.6700, '2025-08-12 00:21:59', '2025-08-12 00:21:59', 'employee1','employee1',NULL,        NULL),
  (21, 10, '2025-10-12', 10068.8000, 'RSD', 0, NULL,         10022.8600,  45.9400, '2025-08-12 00:21:59', '2025-08-12 00:21:59', 'employee1','employee1',NULL,        NULL);
