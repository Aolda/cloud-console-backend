INSERT IGNORE INTO projects (project_id)
VALUES ('0cc61cc8ed964714a06a42afa92c1dc6');

-- "kim" 키페어
INSERT IGNORE INTO keypairs (keypair_id, keypair_name, project_id) 
VALUES ('e5:28:80:4c:71:8a:ef:15:36:f8:d3:85:e4:46:89:07', 'api-test-keypair', '0cc61cc8ed964714a06a42afa92c1dc6');

-- "test" 키페어
INSERT IGNORE INTO keypairs (keypair_id, keypair_name, project_id) 
VALUES ('cb:82:05:a2:06:37:cb:da:c7:dd:be:35:e3:17:cb:6e', 'test', '0cc61cc8ed964714a06a42afa92c1dc6');
