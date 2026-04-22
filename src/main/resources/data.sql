-- outbox_events 테이블 마이그레이션
--
-- 순서: (1) 새 컬럼 추가(NULL 허용) → (2) 기존 값으로 backfill
--       → (3) NOT NULL 제약 적용 → (4) 기존 컬럼 DROP
--
-- backfill 규칙:
--   processed = true  → discord_status = 'SUCCESS', email_status = 'SUCCESS'
--   discord_sent = true  (processed != true) → discord_status = 'SUCCESS'
--   email_sent   = true  (processed != true) → email_status   = 'SUCCESS'
--   그 외 PENDING 채널 → 'PENDING' 유지
-- ============================================================

-- ── 1: 새 컬럼 추가 (NULL 허용으로 먼저 추가하여 기존 row 보호) ──

SET @add_discord_status = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'discord_status') = 0,
    'ALTER TABLE outbox_events ADD COLUMN discord_status VARCHAR(20) NULL COMMENT ''Discord 전송 상태 (PENDING/SUCCESS/FAILED)''',
    'SELECT ''discord_status already exists'' AS status'
);
PREPARE stmt FROM @add_discord_status; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @add_email_status = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'email_status') = 0,
    'ALTER TABLE outbox_events ADD COLUMN email_status VARCHAR(20) NULL COMMENT ''Email 전송 상태 (PENDING/SUCCESS/FAILED)''',
    'SELECT ''email_status already exists'' AS status'
);
PREPARE stmt FROM @add_email_status; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @add_retry_count = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'retry_count') = 0,
    'ALTER TABLE outbox_events ADD COLUMN retry_count INT NULL COMMENT ''Discord+Email 공통 재시도 횟수''',
    'SELECT ''retry_count already exists'' AS status'
);
PREPARE stmt FROM @add_retry_count; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ── 2: 기존 컬럼 값으로 backfill ──

-- 2-a) processed = true 인 row → 두 채널 모두 SUCCESS
SET @backfill_processed = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'processed') > 0,
    'UPDATE outbox_events
        SET discord_status = CASE WHEN processed = TRUE THEN ''SUCCESS'' ELSE COALESCE(discord_status, ''PENDING'') END,
            email_status   = CASE WHEN processed = TRUE THEN ''SUCCESS'' ELSE COALESCE(email_status,   ''PENDING'') END
      WHERE discord_status IS NULL OR email_status IS NULL',
    'SELECT ''processed column not found, skip backfill'' AS status'
);
PREPARE stmt FROM @backfill_processed; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-b) discord_sent 컬럼이 있는 경우 → discord_status 보정
--      (processed = false 이지만 discord_sent = true 인 부분 처리)
SET @backfill_discord = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'discord_sent') > 0,
    'UPDATE outbox_events
        SET discord_status = CASE
              WHEN discord_sent = TRUE  THEN ''SUCCESS''
              ELSE COALESCE(discord_status, ''PENDING'')
            END
      WHERE discord_status IS NULL OR discord_status = ''PENDING''',
    'SELECT ''discord_sent column not found, skip backfill'' AS status'
);
PREPARE stmt FROM @backfill_discord; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-c) email_sent 컬럼이 있는 경우 → email_status 보정
SET @backfill_email = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'email_sent') > 0,
    'UPDATE outbox_events
        SET email_status = CASE
              WHEN email_sent = TRUE  THEN ''SUCCESS''
              ELSE COALESCE(email_status, ''PENDING'')
            END
      WHERE email_status IS NULL OR email_status = ''PENDING''',
    'SELECT ''email_sent column not found, skip backfill'' AS status'
);
PREPARE stmt FROM @backfill_email; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2-d) backfill 후에도 NULL이 남아있는 row는 PENDING 으로 초기화
--      (기존 컬럼이 아예 없었던 신규 환경 또는 누락 데이터 보호)
UPDATE outbox_events
   SET discord_status = COALESCE(discord_status, 'PENDING'),
       email_status   = COALESCE(email_status,   'PENDING'),
       retry_count    = COALESCE(retry_count,     0)
 WHERE discord_status IS NULL OR email_status IS NULL OR retry_count IS NULL;

-- ── STEP 3: NOT NULL 제약 및 DEFAULT 값 적용 ──

SET @notnull_discord = IF(
    (SELECT IS_NULLABLE FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'discord_status') = 'YES',
    'ALTER TABLE outbox_events MODIFY COLUMN discord_status VARCHAR(20) NOT NULL DEFAULT ''PENDING'' COMMENT ''Discord 전송 상태 (PENDING/SUCCESS/FAILED)''',
    'SELECT ''discord_status already NOT NULL'' AS status'
);
PREPARE stmt FROM @notnull_discord; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @notnull_email = IF(
    (SELECT IS_NULLABLE FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'email_status') = 'YES',
    'ALTER TABLE outbox_events MODIFY COLUMN email_status VARCHAR(20) NOT NULL DEFAULT ''PENDING'' COMMENT ''Email 전송 상태 (PENDING/SUCCESS/FAILED)''',
    'SELECT ''email_status already NOT NULL'' AS status'
);
PREPARE stmt FROM @notnull_email; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @notnull_retry = IF(
    (SELECT IS_NULLABLE FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'retry_count') = 'YES',
    'ALTER TABLE outbox_events MODIFY COLUMN retry_count INT NOT NULL DEFAULT 0 COMMENT ''Discord+Email 공통 재시도 횟수''',
    'SELECT ''retry_count already NOT NULL'' AS status'
);
PREPARE stmt FROM @notnull_retry; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ── STEP 4: 기존 컬럼 DROP (backfill 완료 후 안전하게 제거) ──

SET @drop_processed = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'processed') > 0,
    'ALTER TABLE outbox_events DROP COLUMN processed',
    'SELECT ''processed already dropped'' AS status'
);
PREPARE stmt FROM @drop_processed; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @drop_processed_at = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'processed_at') > 0,
    'ALTER TABLE outbox_events DROP COLUMN processed_at',
    'SELECT ''processed_at already dropped'' AS status'
);
PREPARE stmt FROM @drop_processed_at; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @drop_discord_sent = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'discord_sent') > 0,
    'ALTER TABLE outbox_events DROP COLUMN discord_sent',
    'SELECT ''discord_sent already dropped'' AS status'
);
PREPARE stmt FROM @drop_discord_sent; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @drop_email_sent = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'email_sent') > 0,
    'ALTER TABLE outbox_events DROP COLUMN email_sent',
    'SELECT ''email_sent already dropped'' AS status'
);
PREPARE stmt FROM @drop_email_sent; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- ShedLock 분산 스케줄링 락 테이블
CREATE TABLE IF NOT EXISTS shedlock (
    name VARCHAR(64) NOT NULL COMMENT '락 이름 (예: KeypairSyncTask)',
    lock_until TIMESTAMP(3) NOT NULL COMMENT '락 해제 시각',
    locked_at TIMESTAMP(3) NOT NULL COMMENT '락 획득 시각',
    locked_by VARCHAR(255) NOT NULL COMMENT '락을 획득한 서버/인스턴스 식별자',
    PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ShedLock 분산 스케줄링 락';

-- Snapshot tasks 테이블의 snapshot_id를 NULL 허용으로 변경
-- (스냅샷 생성 전에는 NULL이고, 성공 시 업데이트됨)
-- 테이블이 존재할 때만 실행 (에러 무시)
SET @alter_sql = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
     AND TABLE_NAME = 'snapshot_tasks'
     AND COLUMN_NAME = 'snapshot_id'
     AND IS_NULLABLE = 'NO') > 0,
    'ALTER TABLE snapshot_tasks MODIFY COLUMN snapshot_id VARCHAR(64) NULL',
    'SELECT ''snapshot_id already nullable'' AS status'
);

PREPARE stmt FROM @alter_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
