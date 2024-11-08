-- 외부 연결을 허용하는 사용자 권한 설정
ALTER USER 'root'@'localhost' IDENTIFIED BY 'manager0';
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'manager0';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

