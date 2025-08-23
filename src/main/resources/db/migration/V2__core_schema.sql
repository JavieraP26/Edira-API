-- V2: Tablas núcleo mínimas para Edira-API (MySQL 8)
-- Convenciones: snake_case, InnoDB, utf8mb4_0900_ai_ci, timestamps con milisegundos.

-- Asegura orden de creación/llaves foráneas
SET FOREIGN_KEY_CHECKS = 0;

-- 1) Tenants
CREATE TABLE tenant (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  code         VARCHAR(50)  NOT NULL,
  name         VARCHAR(120) NOT NULL,
  status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
  created_at   TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at   TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  created_by   VARCHAR(100) NULL,
  updated_by   VARCHAR(100) NULL,
  CONSTRAINT uk_tenant_code UNIQUE (code),
  CONSTRAINT chk_tenant_status CHECK (status IN ('ACTIVE','INACTIVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2) Usuarios de aplicación (por tenant)
CREATE TABLE user_account (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id     BIGINT       NOT NULL,
  email         VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  display_name  VARCHAR(120) NULL,
  status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
  created_at    TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at    TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  created_by    VARCHAR(100) NULL,
  updated_by    VARCHAR(100) NULL,
  CONSTRAINT uk_user_email_per_tenant UNIQUE (tenant_id, email),
  CONSTRAINT fk_user_tenant FOREIGN KEY (tenant_id)
      REFERENCES tenant (id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE','INACTIVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3) Roles por tenant
CREATE TABLE role (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id   BIGINT      NOT NULL,
  name        VARCHAR(60) NOT NULL,
  description VARCHAR(255) NULL,
  created_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  created_by  VARCHAR(100) NULL,
  updated_by  VARCHAR(100) NULL,
  CONSTRAINT uk_role_name_per_tenant UNIQUE (tenant_id, name),
  CONSTRAINT fk_role_tenant FOREIGN KEY (tenant_id)
      REFERENCES tenant (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4) Asignación N:M usuario <-> rol
CREATE TABLE user_role (
  user_id     BIGINT NOT NULL,
  role_id     BIGINT NOT NULL,
  assigned_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id)
      REFERENCES user_account (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id)
      REFERENCES role (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5) (Infra) Outbox para eventos de dominio (opcional pero útil)
CREATE TABLE domain_event_outbox (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  tenant_id      BIGINT       NULL,
  aggregate_type VARCHAR(100) NOT NULL,
  aggregate_id   VARCHAR(100) NOT NULL,
  event_type     VARCHAR(100) NOT NULL,
  payload        JSON         NOT NULL,
  occurred_at    TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  processed      TINYINT(1)   NOT NULL DEFAULT 0,
  processed_at   TIMESTAMP(3) NULL,
  INDEX idx_outbox_unprocessed (processed, occurred_at),
  INDEX idx_outbox_tenant (tenant_id),
  CONSTRAINT fk_outbox_tenant FOREIGN KEY (tenant_id)
      REFERENCES tenant (id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
