-- ShedLock 분산 스케줄링 락 테이블
CREATE TABLE IF NOT EXISTS shedlock (
    name VARCHAR(64) NOT NULL COMMENT '락 이름 (예: KeypairSyncTask)',
    lock_until TIMESTAMP(3) NOT NULL COMMENT '락 해제 시각',
    locked_at TIMESTAMP(3) NOT NULL COMMENT '락 획득 시각',
    locked_by VARCHAR(255) NOT NULL COMMENT '락을 획득한 서버/인스턴스 식별자',
    PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ShedLock 분산 스케줄링 락';
