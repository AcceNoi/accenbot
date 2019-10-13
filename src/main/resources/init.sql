create database if not exists dmzjbot default character set utf8 ;
use dmzjbot;
create table cfg_listen_status (
id bigint not null primary key auto_increment,
apply_type int not null,
apply_target int not null,
listener_code varchar(50),
listener_status varchar(2),
listener_status2 varchar(200),
create_user_id varchar(20),
create_time datetime,
update_user_id varchar(20),
update_time datetime
);

create table cfg_quick_reply(
	id bigint not null primary key auto_increment,
    match_type smallint not null,
    pattern varchar(500),
    apply_type smallint not null,
    apply_target varchar(20) ,
    need_at smallint not null default 2,
    apply varchar(2000),
    create_user_id varchar(20) ,
    create_time datetime,
    `status` smallint not null
);

create table q_message(
	id bigint not null primary key auto_increment,
    message_type varchar(20),
    sub_type varchar(20),
    message_id varchar(20) not null,
    group_id varchar(20),
    user_id varchar(20),
    message varchar(2000),
    raw_message varchar(2000),
    send_time datetime,
    font varchar(10)
);

create table sys_qnum(
	qnum varchar(20) not null primary key,
    secret varchar(100) 
);

create table sys_record_count(
id bigint not null primary key auto_increment,
record_type varchar(20) not null,
record_target varchar(20),
record_value varchar(50),
attr1 varchar(500),
attr2 varchar(500),
create_time datetime,
create_user_id varchar(20),
update_time datetime,
update_user_id varchar(20)
);

create table cmd_bu_sub(
	id bigint not null primary key auto_increment,
    `type` varchar(10) not null,
    target_id varchar(20) not null,
    subscriber varchar(20) not null,
    sub_target varchar(20) ,
    sub_type varchar(20),
    sub_obj varchar(20),
	sub_obj_mark varchar(200),
    sub_time datetime,
    `status` varchar(2) not null
);