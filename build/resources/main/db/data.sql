INSERT INTO admin_user (username, password, role, is_active)
VALUES ('rapidadmin', '{noop}rapid1234', 'ADMIN', TRUE);

INSERT INTO admin_log (username, action, message)
VALUES ('rapidadmin', 'INIT_SYSTEM', '초기 설정 및 테스트 데이터 삽입');