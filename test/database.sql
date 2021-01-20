create schema app;

create table app.user (
    id serial primary key,
    name varchar(20) not null,
    initials char(3) not null,
    admin boolean not null,
    description text,
    reputation bigint not null,
    veracity real not null,
    height double precision,
    hats smallint
);

insert into app.user (name, initials, admin, reputation, veracity) values
    ('foo', 'f', true, 1, 99.5),
    ('bar', 'bf', false, 9001, 0.11);
