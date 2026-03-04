-- outbox_events 테이블 마이그레이션
-- processed 컬럼 제거 (채널 상태로 대체)
SET @drop_processed = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'processed') > 0,
    'ALTER TABLE outbox_events DROP COLUMN processed',
    'SELECT "processed already dropped" AS status'
);
PREPARE stmt FROM @drop_processed; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @drop_processed_at = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'processed_at') > 0,
    'ALTER TABLE outbox_events DROP COLUMN processed_at',
    'SELECT "processed_at already dropped" AS status'
);
PREPARE stmt FROM @drop_processed_at; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @drop_discord_sent = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'discord_sent') > 0,
    'ALTER TABLE outbox_events DROP COLUMN discord_sent',
    'SELECT "discord_sent already dropped" AS status'
);
PREPARE stmt FROM @drop_discord_sent; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @drop_email_sent = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'email_sent') > 0,
    'ALTER TABLE outbox_events DROP COLUMN email_sent',
    'SELECT "email_sent already dropped" AS status'
);
PREPARE stmt FROM @drop_email_sent; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 채널별 상태 및 공통 재시도 카운트 컬럼 추가
SET @add_discord_status = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'discord_status') = 0,
    'ALTER TABLE outbox_events ADD COLUMN discord_status VARCHAR(20) NOT NULL DEFAULT ''PENDING'' COMMENT ''Discord 전송 상태 (PENDING/SUCCESS/FAILED)''',
    'SELECT "discord_status already exists" AS status'
);
PREPARE stmt FROM @add_discord_status; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @add_email_status = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'email_status') = 0,
    'ALTER TABLE outbox_events ADD COLUMN email_status VARCHAR(20) NOT NULL DEFAULT ''PENDING'' COMMENT ''Email 전송 상태 (PENDING/SUCCESS/FAILED)''',
    'SELECT "email_status already exists" AS status'
);
PREPARE stmt FROM @add_email_status; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @add_retry_count = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'outbox_events' AND COLUMN_NAME = 'retry_count') = 0,
    'ALTER TABLE outbox_events ADD COLUMN retry_count INT NOT NULL DEFAULT 0 COMMENT ''Discord+Email 공통 재시도 횟수''',
    'SELECT "retry_count already exists" AS status'
);
PREPARE stmt FROM @add_retry_count; EXECUTE stmt; DEALLOCATE PREPARE stmt;

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
    'SELECT "snapshot_id already nullable" AS status'
);

PREPARE stmt FROM @alter_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
