CREATE TABLE IF NOT EXISTS currency (
	id INT PRIMARY KEY AUTO_INCREMENT,
	currency_name VARCHAR(30),
	currency_chi_name VARCHAR(30),
	description VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS record_rate (
	id INT PRIMARY KEY AUTO_INCREMENT,
	currency_name VARCHAR(30),
	rate DECIMAL(10,4),
	update_time TIMESTAMP
);