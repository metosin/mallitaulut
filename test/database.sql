create schema app;

create table app.user (
    id serial primary key,
    name text not null
);

insert into app.user (name) values ('foo'), ('bar');
