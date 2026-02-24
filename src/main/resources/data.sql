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
