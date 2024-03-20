CREATE TABLE IF NOT EXISTS `Device` (
    id Long primary key,
    name VARCHAR(255) DEFAULT NULL,
    cpu_frequency Long,
    sram Long,
    flash Long,
    floating_point Integer DEFAULT 0,
    cpu_arch VARCHAR(255)
);


CREATE TABLE IF NOT EXISTS `Model` (
    id Long primary key,
    name VARCHAR(255) DEFAULT NULL
);

insert into Device(id,name,cpu_frequency,sram,flash,cpu_arch,floating_point) values (10001,'Apollo3 Blue',48,384,1024,'Arm Cortex-M4',0);
insert into Device(id,name,cpu_frequency,sram,flash,cpu_arch,floating_point) values (10002,'DISCO-F746NG',216,320,1024,'Arm Cortex-M7',0);
insert into Device(id,name,cpu_frequency,sram,flash,cpu_arch,floating_point) values (10003,'Arduino Nano 33 BLE Sense',64,256,1024,'Arm Cortex-M4F',0);
-- insert into Device(id,name) values (1,'Apollo3 Blue');
-- insert into Device(id,name) values (2,'DISCO-F746NG');
-- insert into Device(id,name) values (3,'Arduino Nano 33 BLE Sense');

insert into Model(id,name) values (1,'CNN');
insert into Model(id,name) values (2,'RNN');
insert into Model(id,name) values (3,'Perceptron');