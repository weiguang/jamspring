-- ============================================
-- SQLite 版本
-- ============================================
CREATE TABLE tb_test
(
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    key          TEXT    DEFAULT NULL,
    value        TEXT    DEFAULT NULL,
    amt          REAL    NOT NULL DEFAULT 0,
    status       INTEGER NOT NULL DEFAULT 1,
    create_time   TEXT    NOT NULL DEFAULT (datetime('now', 'localtime')),
    update_time   TEXT    NOT NULL DEFAULT (datetime('now', 'localtime'))
);

-- SQLite 自动更新 updated_at 的触发器
CREATE TRIGGER update_tb_test_timestamp
    AFTER UPDATE ON tb_test
    FOR EACH ROW
BEGIN
    UPDATE tb_test SET update_time = datetime('now', 'localtime') WHERE id = NEW.id;
END;
