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
	picture VARCHAR(80),
	CONSTRAINT un_users_alias UNIQUE (alias),
	CONSTRAINT un_users_email UNIQUE (email)
);
GO

CREATE TABLE Tertulias(
	id INTEGER IDENTITY(1,1) PRIMARY KEY,
	title VARCHAR(40) NOT NULL,
	subject VARCHAR(80),
	schedule INTEGER NOT NULL DEFAULT 0,
	private INTEGER NOT NULL DEFAULT 0,
	CONSTRAINT un_tertulia_title UNIQUE (title)
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

INSERT INTO Users (sid, alias) VALUES ('sid:fadae567db0f67c6fe69d25ee8ffc0b5', N'aborba');
GO

DECLARE @userId INTEGER; SELECT @UserId = id FROM Users WHERE alias = 'aborba';
EXEC SpInsertTertulia @UserId, N'Tertulia do Tejo', N'O que seria do Mundo sem nós!', 0, 1;
EXEC SpInsertTertulia @UserId, N'Tertúlia dos primos', N'Só Celoricos', 0, 1;
EXEC SpInsertTertulia @UserId, N'Escolinha 72-77', N'Sempre em contato', 0, 1;
EXEC SpInsertTertulia @UserId, N'Natais BS', N'Mais um...', 0, 1;
EXEC SpInsertTertulia @UserId, N'Gulbenkian Música', N'', 0, 0;
EXEC SpInsertTertulia @UserId, N'CALM', N'Ex MAC - Sempre só nós 8', 0, 1;
EXEC SpInsertTertulia @UserId, N'AtHere', N'Tipo RoBoTo', 0, 1;
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