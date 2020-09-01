create table authority (id bigint not null auto_increment, permission varchar(255), primary key (id)) engine=InnoDB;
create table role (id bigint not null auto_increment, name varchar(255), primary key (id)) engine=InnoDB;
create table role_authority (role_id bigint not null, authority_id bigint not null, primary key (role_id, authority_id)) engine=InnoDB;
create table user (id bigint not null auto_increment, account_non_expired bit, account_non_locked bit, credentials_non_expired bit, enabled bit, password varchar(255), username varchar(255), primary key (id)) engine=InnoDB;
create table user_role (user_id bigint not null, role_id bigint not null, primary key (user_id, role_id)) engine=InnoDB;
alter table role_authority add constraint FKqbri833f7xop13bvdje3xxtnw foreign key (authority_id) references authority (id);
alter table role_authority add constraint FK2052966dco7y9f97s1a824bj1 foreign key (role_id) references role (id);
alter table user_role add constraint FKa68196081fvovjhkek5m97n3y foreign key (role_id) references role (id);
alter table user_role add constraint FK859n2jvi8ivhui0rl0esws6o foreign key (user_id) references user (id);
