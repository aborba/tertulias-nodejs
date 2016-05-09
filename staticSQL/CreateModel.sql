/*
if db_id('Tertulias') is null
    create database Tertulias;
GO
USE Tertulias;
*/

/*
Use Tertulias;
GO
*/

/*
CREATE LOGIN TertuliasAdmin WITH PASSWORD = 'TertuliasAdminPassword@ISEL_PS'
--GO*/

/*
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'TertuliasAdmin')
BEGIN
    CREATE USER [TertuliasAdmin] FOR LOGIN [TertuliasAdmin]
    EXEC sp_addrolemember N'db_owner', N'TertuliasAdmin'
END;
--GO
*/

IF object_id(N'dbo.Members_Vw') IS NOT NULL DROP VIEW Members_Vw;
IF object_id(N'dbo.Tertulias_Vw') IS NOT NULL DROP VIEW Tertulias_Vw;
IF object_id(N'dbo.Members') IS NOT NULL DROP TABLE Members;
IF object_id(N'dbo.Users') IS NOT NULL DROP TABLE Users;
IF object_id(N'dbo.Tertulias') IS NOT NULL DROP TABLE Tertulias;
IF object_id(N'dbo.Roles') IS NOT NULL DROP TABLE Roles;
IF object_id(N'dbo.Roles') IS NOT NULL DROP TABLE Roles;
GO

CREATE TABLE Roles(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	roleName VARCHAR(20) NOT NULL,
	CONSTRAINT un_roles_name UNIQUE (roleName)
);
GO

CREATE TABLE Users(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	sid VARCHAR(40) NOT NULL,
	alias VARCHAR(20),
	firstName VARCHAR(40),
	lastName VARCHAR(40),
	email VARCHAR(40),
	picture VARCHAR(250),
	CONSTRAINT un_users_alias UNIQUE (alias),
	CONSTRAINT un_users_email UNIQUE (email)
);
GO

CREATE TABLE Tertulias(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	title VARCHAR(40) NOT NULL,
	subject VARCHAR(80),
	location INTEGER NOT NULL DEFAULT 0,
	schedule INTEGER NOT NULL DEFAULT 0,
	private INTEGER NOT NULL DEFAULT 0,
	CONSTRAINT un_tertulia_title UNIQUE (title),
	CONSTRAINT fk_tertulia_location FOREIGN KEY (location) REFERENCES Locations(id),
	CONSTRAINT fk_tertulia_schedule FOREIGN KEY (schedule) REFERENCES Schedule(id)
);
GO

CREATE TABLE Members(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	tertulia INTEGER NOT NULL,
	usr INTEGER NOT NULL,
	role INTEGER NOT NULL,
	CONSTRAINT fk_members_tertulia FOREIGN KEY (tertulia) REFERENCES Tertulias(id),
	CONSTRAINT fk_members_user FOREIGN KEY (usr) REFERENCES Users(id),
	CONSTRAINT fk_members_role FOREIGN KEY (role) REFERENCES Roles(id)
);
GO

CREATE TABLE Locations(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	name VARCHAR(40) NOT NULL,
	address VARCHAR(80),
	zip VARCHAR(40),
	country VARCHAR(40),
	latitude VARCHAR(12),
	longitude VARCHAR(12),
	CONSTRAINT un_location_name UNIQUE (name)
);
GO

CREATE TABLE RecurrencyTypes(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	name VARCHAR(40) NOT NULL,
	CONSTRAINT un_recurrency_name UNIQUE (name)
);
GO

CREATE TABLE Schedules(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	recurrencyType INTEGER NOT NULL,
	fromStart BOOLEAN NOT NULL DEFAULT true,
	skip INTEGER NOT NULL,
	param1 VARCHAR(10),
	param2 VARCHAR(10),
	CONSTRAINT un_schedule_name UNIQUE (name),
	CONSTRAINT fk_schedule_recurrency FOREIGN KEY (recurrencyType) REFERENCES RecurrencyTypes(id)
);
GO

CREATE VIEW Members_Vw AS
	SELECT Members.id AS memberId, 
		Tertulias.id AS tertuliaId, Tertulias.title AS tertuliaTitle, 
		Users.id AS userId, Users.alias AS userAlias, 
		Roles.id AS roleId, Roles.roleName AS roleName
	FROM ((Members 
		INNER JOIN Tertulias ON Members.tertulia = Tertulias.id) 
			INNER JOIN Users ON Members.usr = Users.id) 
				INNER JOIN Roles ON Members.role = Roles.id;
GO

CREATE VIEW Tertulias_Vw AS
	SELECT
		Tertulias.id AS tertuliaId,
		Tertulias.title AS tertuliaTitle,
		Tertulias.subject AS tertuliaSubject,
		Tertulias.schedule AS tertuliaSchedule,
		Tertulias.private AS tertuliaPrivate,
		Users.sid AS userId,
		Users.alias AS userAlias 
	FROM (Tertulias 
		INNER JOIN Members ON Members.tertulia = Tertulias.id) 
			INNER JOIN Users ON Members.usr = Users.id;
GO

IF object_id(N'dbo.SpInsertTertulia') IS NOT NULL DROP PROCEDURE SpInsertTertulia;
GO

CREATE PROCEDURE dbo.SpInsertTertulia
	@userId INTEGER, 
	@title VARCHAR(40), @subject VARCHAR(80), @schedule INTEGER, @private INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	INSERT INTO Tertulias (title, subject, schedule, private) VALUES (@title, @subject, @schedule, @private);
    DECLARE @tertuliaId INTEGER; SET @tertuliaId = SCOPE_IDENTITY();
    DECLARE @adminId INTEGER; SELECT @AdminId = id FROM Roles WHERE roleName='Administrator';
	INSERT INTO Members (tertulia, usr, role) VALUES (@tertuliaId, @userId, @adminId);
	COMMIT
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK
END CATCH
GO

/*
IF object_id(N'dbo.trInsertTertulia') IS NOT NULL DROP TRIGGER trInsertTertulia;
GO

CREATE TRIGGER trInsertTertulia ON Tertulias AFTER INSERT
AS
BEGIN
	DECLARE @tertuliaId INTEGER; SET @tertuliaId = @@IDENTITY;
	print @tertuliaId;
	DECLARE @UserId INTEGER; SELECT @UserId = id FROM Users WHERE alias = 'aborba';
    DECLARE @adminId INTEGER; SELECT @AdminId = id FROM Roles WHERE roleName='Administrator';
	INSERT INTO Members (tertulia, userId, role) VALUES (@tertuliaId, @userId, @adminId);
END
GO
*/

INSERT INTO Roles (roleName) VALUES (N'administrator'), (N'manager'), (N'member');
GO

INSERT INTO Users (sid, alias, firstName, lastName, email, picture) VALUES 
	('sid:fadae567db0f67c6fe69d25ee8ffc0b5', N'aborba', N'António', N'Borba da Silva', 'antonio.borba@gmail.com', ''),
	('sid:357a070bdaf6a373efaf9ab34c8ae5b9', N'GGlabs', N'António', N'Borba da Silva', 'abs@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
GO

INSERT INTO Locations (name, address, zip, country, latitude, longitude) VALUES
	(N'Pastelaria Mexicana', N'Av. Guerra Junqueiro 30C', N'1000-167 Lisboa', 'Portugal', '38.740117', '-9.136394'),
	(N'Restaurante Picanha', N'R. das Janelas Verdes 96', N'1200 Lisboa', 'Portugal', '38.705678', '-9.160624'),
	(N'Restaurante EntreCopos', N'Rua de Entrecampos, nº11', N'1000-151 Lisboa', 'Portugal', '38.744912', '-9.145291'),
	(N'Lisboa Racket Center', N'R. Alferes Malheiro', N'1700 Lisboa', 'Portugal', '38.758372', '-9.134471'),
	(N'Restaurante O Jaconto', N'Av. Ventura Terra 2', N'1600-781 Lisboa', 'Portugal', '38.758563', '-9.167007'),
	(N'Restaurante Taberna Gourmet', N'R. Padre Américo 28', N'1600-548 Lisboa', 'Portugal', '38.763603', '-9.180278'),
	(N'Café A Luz Ideal', N'R. Gen. Schiappa Monteiro 2A', N'1600-155', 'Portugal', '38.754401', '-9.174995'),
	(N'Restaurante Honorato - Telheiras', N'Rua Professor Francisco Gentil, Lote A, Telheiras', N'1600 Lisboa', 'Portugal', '38.760363', '-9.166720'),
	(N'Restaurante Gardens', N'Rua Principal, S/N, Urbanização Quinta Alcoutins', N'1600-263 Lisboa', 'Portugal', '38.776200', '-9.171391'),
	(N'Pastelaria Arcadas', N'R. Cidade de Lobito 282', N'1800-071 Lisboa', 'Portugal', '38.764007', '-9.112470')
GO

INSERT INTO RecurrencyTypes (name) VALUES
	(N'No Repeat'), (N'Daily'), (N'Weekly'), (N'Monthly'), (N'Yearly')
	--, (N'Monthly - On a week day of a week'), (N'Yearly - on a day');
GO

INSERT INTO Schedules (name, address, zip, country, latitude, longitude) VALUES
	(N'Pastelaria Mexicana', N'Av. Guerra Junqueiro 30C', N'1000-167 Lisboa', 'Portugal', '38.740117', '-9.136394'),
GO

DECLARE @userId1 INTEGER; SELECT @UserId1 = id FROM Users WHERE alias = 'aborba';
EXEC SpInsertTertulia @UserId1, N'Tertulia do Tejo', N'O que seria do Mundo sem nós!', 0, 1;
EXEC SpInsertTertulia @UserId1, N'Tertúlia dos primos', N'Só Celoricos', 0, 1;
EXEC SpInsertTertulia @UserId1, N'Escolinha 72-77', N'Sempre em contato', 0, 1;
EXEC SpInsertTertulia @UserId1, N'Natais BS', N'Mais um...', 0, 1;
DECLARE @userId2 INTEGER; SELECT @UserId2 = id FROM Users WHERE alias = 'GGlabs';
EXEC SpInsertTertulia @UserId2, N'Gulbenkian Música', N'', 0, 0;
EXEC SpInsertTertulia @UserId2, N'CALM', N'Ex MAC - Sempre só nós 8', 0, 1;
EXEC SpInsertTertulia @UserId2, N'AtHere', N'Tipo RoBoTo', 0, 1;
EXEC SpInsertTertulia @UserId2, N'Tertúlias às Quintas no Teatro Rápido', N'Tipo RoBoTo', 0, 1;
GO

/*
INSERT INTO Tertulias (title, subject, schedule, private) VALUES (N'Tertulia do Tejo', N'O que seria do Mundo sem nós!', 0, 1);
INSERT INTO Tertulias (title, subject, schedule, private) VALUES (N'Tertúlia dos primos', N'Só Celoricos', 0, 1);
INSERT INTO Tertulias (title, subject, schedule, private) VALUES (N'Escolinha 72-77', N'Sempre em contato', 0, 1);
INSERT INTO Tertulias (title, subject, schedule, private) VALUES (N'Natais BS', N'Mais um...', 0, 1);
INSERT INTO Tertulias (title, subject, schedule, private) VALUES (N'Gulbenkian Música', N'', 0, 1);
INSERT INTO Tertulias (title, subject, schedule, private) VALUES (N'CALM', N'Ex MAC - Sempre só nós 8', 0, 1);
INSERT INTO Tertulias (title, subject, schedule, private) VALUES (N'AtHere', N'Tipo RoBoTo', 0, 1);
GO
*/