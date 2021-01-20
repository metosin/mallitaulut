create schema app;

create table app.user (
    id serial primary key,
    username text not null
);

insert into app.user (username) values ('foo'), ('bar');
