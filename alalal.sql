Create table Usuario (
UUID_Usuario varchar2(150) not null,
nombre_usuario varchar2(25) not null,
contrasena_usuario varchar2(20) not null
);

Create table Ticket (
UUID_ticket varchar2(150) not null,
titulo varchar2(75) not null,
descripcion varchar2(200) not null,
autor varchar2(50) not null,
email_autor varchar2(150) not null,
fecha_ticket varchar2(20) not null
);
drop table Ticket

SELECT * FROM Usuario



