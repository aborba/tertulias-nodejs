/*
Use master;
GO

if db_id('Tertulias') is null
BEGIN
    CREATE DATABASE Tertulias;
    IF NOT EXISTS (SELECT loginname FROM master.dbo.syslogins WHERE name = TertuliasAdmin AND dbname = 'Tertulias')
		CREATE LOGIN TertuliasAdmin WITH PASSWORD = 'TertuliasAdminPassword@ISEL_PS'
	IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'TertuliasAdmin')
	BEGIN
	    CREATE USER [TertuliasAdmin] FOR LOGIN [TertuliasAdmin]
	    EXEC sp_addrolemember N'db_owner', N'TertuliasAdmin'
	END;
END
GO
*/

/*
Use Tertulias;
GO
*/

IF OBJECT_ID(N'dbo.sp_postMessage_byAlias') IS NOT NULL DROP PROCEDURE sp_postMessage_byAlias;
IF OBJECT_ID(N'dbo.sp_postMessage') IS NOT NULL DROP PROCEDURE sp_postMessage;
IF OBJECT_ID(N'dbo.sp_insertTertulia') IS NOT NULL DROP PROCEDURE sp_insertTertulia;
IF OBJECT_ID(N'dbo.sp_buildChecklist') IS NOT NULL DROP PROCEDURE sp_buildChecklist;
IF OBJECT_ID(N'dbo.sp_createEventDefaultLocation') IS NOT NULL DROP PROCEDURE sp_createEventDefaultLocation;
IF OBJECT_ID(N'dbo.sp_createEvent') IS NOT NULL DROP PROCEDURE sp_createEvent;
IF OBJECT_ID(N'dbo.sp_getEventIdTertuliaId') IS NOT NULL DROP PROCEDURE sp_getEventIdTertuliaId;
IF OBJECT_ID(N'dbo.sp_assignChecklistItems') IS NOT NULL DROP PROCEDURE sp_assignChecklistItems;
IF OBJECT_ID(N'dbo.sp_getId') IS NOT NULL DROP PROCEDURE sp_getId;
IF OBJECT_ID(N'dbo.FnGetCatalogItemId_byTertuliaId') IS NOT NULL DROP FUNCTION FnGetCatalogItemId_byTertuliaId;
IF OBJECT_ID(N'dbo.FnGetTemplateId_byTertuliaId') IS NOT NULL DROP FUNCTION FnGetTemplateId_byTertuliaId;
IF OBJECT_ID(N'dbo.FnGetUserId_byAlias') IS NOT NULL DROP FUNCTION FnGetUserId_byAlias;
IF OBJECT_ID(N'dbo.FnGetTertuliaLocation_byTertuliaId') IS NOT NULL DROP FUNCTION FnGetTertuliaLocation_byTertuliaId;
IF OBJECT_ID(N'dbo.FnGetTertuliaTemplateId_byTertuliaId') IS NOT NULL DROP FUNCTION FnGetTertuliaTemplateId_byTertuliaId;
IF OBJECT_ID(N'dbo.FnGetEventId_byTertuliaId') IS NOT NULL DROP FUNCTION FnGetEventId_byTertuliaId;
GO
IF OBJECT_ID(N'dbo.Messages') IS NOT NULL DROP TABLE Messages;
IF OBJECT_ID(N'dbo.Tags') IS NOT NULL DROP TABLE Tags;
IF OBJECT_ID(N'dbo.Contributions') IS NOT NULL DROP TABLE Contributions;
IF OBJECT_ID(N'dbo.EventsItems') IS NOT NULL DROP TABLE EventsItems;
IF OBJECT_ID(N'dbo.TemplatesCat') IS NOT NULL DROP TABLE TemplatesCat;
IF OBJECT_ID(N'dbo.ItemsCatalog') IS NOT NULL DROP TABLE ItemsCatalog;
IF OBJECT_ID(N'dbo.Templates') IS NOT NULL DROP TABLE Templates;
IF OBJECT_ID(N'dbo.Events') IS NOT NULL DROP TABLE Events;
IF OBJECT_ID(N'dbo.Members') IS NOT NULL DROP TABLE Members;
IF OBJECT_ID(N'dbo.Users') IS NOT NULL DROP TABLE Users;
IF OBJECT_ID(N'dbo.Tertulias') IS NOT NULL DROP TABLE Tertulias;
IF OBJECT_ID(N'dbo.Roles') IS NOT NULL DROP TABLE Roles;
IF OBJECT_ID(N'dbo.Locations') IS NOT NULL DROP TABLE Locations;
IF OBJECT_ID(N'dbo.Schedules') IS NOT NULL DROP TABLE Schedules;
IF OBJECT_ID(N'dbo.Recurrencies') IS NOT NULL DROP TABLE Recurrencies;
GO

-- See <TEST 01>
CREATE TABLE Roles(
	ro_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	ro_name VARCHAR(20) NOT NULL,
	CONSTRAINT un_roles_name UNIQUE (ro_name)
);
GO

-- See <TEST 04>
CREATE TABLE Users(
	us_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	us_sid VARCHAR(40) NOT NULL,
	us_alias VARCHAR(20),
	us_firstName VARCHAR(40),
	us_lastName VARCHAR(40),
	us_email VARCHAR(40),
	us_picture VARCHAR(255),
	CONSTRAINT un_users_alias UNIQUE (us_alias),
	CONSTRAINT un_users_email UNIQUE (us_email)
);
GO

-- See <TEST 02>
CREATE TABLE Recurrencies(
	rc_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	rc_name VARCHAR(40) NOT NULL,
	rc_description VARCHAR(255) NOT NULL,
	CONSTRAINT un_recurrency_name UNIQUE (rc_name)
);
GO

-- See <TEST 06>
CREATE TABLE Schedules(
	sc_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	sc_recurrency INTEGER NOT NULL,
	sc_fromStart BIT NOT NULL DEFAULT '1',
	sc_skip INTEGER NOT NULL,
	sc_param1 VARCHAR(10),
	sc_param2 VARCHAR(10),
	CONSTRAINT fk_schedule_recurrency FOREIGN KEY (sc_recurrency) REFERENCES Recurrencies(rc_id)
);
GO

-- See <TEST 05> <TEST 07>
CREATE TABLE Locations(
	lo_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	lo_name VARCHAR(40) NOT NULL,
	lo_address VARCHAR(80),
	lo_zip VARCHAR(40),
	lo_country VARCHAR(40),
	lo_latitude VARCHAR(12),
	lo_longitude VARCHAR(12),
	CONSTRAINT un_location_name UNIQUE (lo_name),
	CONSTRAINT un_location_nll UNIQUE (lo_name, lo_latitude, lo_longitude)
);
GO

-- See <TEST 06> <TEST 07>
CREATE TABLE Tertulias(
	tr_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	tr_name VARCHAR(40) NOT NULL,
	tr_subject VARCHAR(80),
	tr_location INTEGER NOT NULL DEFAULT 0,
	tr_schedule INTEGER NOT NULL DEFAULT 0,
	tr_private BIT NOT NULL DEFAULT 0,
	CONSTRAINT un_tertulia_name UNIQUE (tr_name),
	CONSTRAINT fk_tertulia_location FOREIGN KEY (tr_location) REFERENCES Locations(lo_id),
	CONSTRAINT fk_tertulia_schedule FOREIGN KEY (tr_schedule) REFERENCES Schedules(sc_id)
);
GO

-- See <TEST 06>
CREATE TABLE Members(
	mb_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	mb_tertulia INTEGER NOT NULL,
	mb_user INTEGER NOT NULL,
	mb_role INTEGER NOT NULL,
	CONSTRAINT un_members_tu UNIQUE (mb_tertulia, mb_user),
	CONSTRAINT fk_members_tertulia FOREIGN KEY (mb_tertulia) REFERENCES Tertulias(tr_id),
	CONSTRAINT fk_members_user FOREIGN KEY (mb_user) REFERENCES Users(us_id),
	CONSTRAINT fk_members_role FOREIGN KEY (mb_role) REFERENCES Roles(ro_id)
);
GO

-- See <TEST 07>
CREATE TABLE Events(
	ev_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	ev_tertulia INTEGER NOT NULL,
	ev_location INTEGER NOT NULL DEFAULT 0,
	ev_targetdate DATETIME NOT NULL,
	ev_note VARCHAR(120),
	CONSTRAINT un_event_tlt UNIQUE (ev_tertulia, ev_location, ev_targetdate),
	CONSTRAINT fk_events_tertulia FOREIGN KEY (ev_tertulia) REFERENCES Tertulias(tr_id),
	CONSTRAINT fk_events_location FOREIGN KEY (ev_location) REFERENCES Locations(lo_id)
);
GO

-- See <TEST 08>
CREATE TABLE ItemsCatalog(
	ic_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	ic_name VARCHAR(40) NOT NULL,
	ic_tertulia INTEGER NOT NULL,
	CONSTRAINT un_checklistsitems_tnsu UNIQUE (ic_tertulia, ic_name),
	CONSTRAINT fk_checklistsitems_tertulia FOREIGN KEY (ic_tertulia) REFERENCES Tertulias(tr_id)
);
GO

-- See <TEST 09>
CREATE TABLE Templates(
	tp_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	tp_name VARCHAR(40) NOT NULL,
	tp_tertulia INTEGER NOT NULL,
	CONSTRAINT un_checklistsTemplates_name UNIQUE (tp_tertulia, tp_name),
	CONSTRAINT fk_checklistsTemplates_tertulia FOREIGN KEY (tp_tertulia) REFERENCES Tertulias(tr_id)
);
GO

-- See <TEST 10> <TEST 11>
CREATE TABLE TemplatesCat(
	tc_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	tc_template INTEGER NOT NULL,
	tc_item INTEGER NOT NULL,
	tc_quantity INTEGER NOT NULL,
	CONSTRAINT un_checkliststemplatesItems_tni UNIQUE (tc_template, tc_item),
	CONSTRAINT fk_checkliststemplatesItems_checklist FOREIGN KEY (tc_template) REFERENCES Templates(tp_id),
	CONSTRAINT fk_checkliststemplatesItems_item FOREIGN KEY (tc_item) REFERENCES ItemsCatalog(ic_id)
);
GO

-- See <TEST 11> <TEST 12>
CREATE TABLE EventsItems(
	ei_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	ei_event INTEGER NOT NULL,
	ei_item INTEGER NOT NULL,
	ei_quantity INTEGER NOT NULL DEFAULT 1,
	CONSTRAINT un_eventschecklists_ei UNIQUE (ei_event, ei_item),
	CONSTRAINT fk_eventschecklists_event FOREIGN KEY (ei_event) REFERENCES Events(ev_id),
	CONSTRAINT fk_eventschecklists_item FOREIGN KEY (ei_item) REFERENCES ItemsCatalog(ic_id)	
);
GO

-- See <TEST 12>
CREATE TABLE Contributions(
	ct_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	ct_user INTEGER NOT NULL,
	ct_event INTEGER NOT NULL,
	ct_item INTEGER NOT NULL,
	ct_quantity INTEGER NOT NULL DEFAULT 1,
	CONSTRAINT un_checklistsitemsassignments_iu UNIQUE (ct_user, ct_item),
	CONSTRAINT fk_checklistsitemsassignments_user FOREIGN KEY (ct_user) REFERENCES Users(us_id),
	CONSTRAINT fk_checklistsitemsassignments_event FOREIGN KEY (ct_event) REFERENCES Events(ev_id),
	CONSTRAINT fk_checklistsitemsassignments_item FOREIGN KEY (ct_item) REFERENCES ItemsCatalog(ic_id)	
);
GO

-- See <TEST 03>
CREATE TABLE Tags(
	tg_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	tg_name VARCHAR(20) NOT NULL,
	tg_description VARCHAR(40),
	CONSTRAINT un_messagetypes_name UNIQUE (tg_name)
);
GO

-- See <TEST 13>
CREATE TABLE Messages(
	ms_id INTEGER IDENTITY(1,1) PRIMARY KEY,
	ms_tertulia INTEGER NOT NULL,
	ms_user INTEGER NOT NULL,
	ms_timestamp DATETIME NOT NULL,
	ms_tag INTEGER NOT NULL,
	ms_message VARCHAR(40),
	CONSTRAINT un_messages_tuttm UNIQUE (ms_tertulia, ms_user, ms_timestamp, ms_tag, ms_message),
	CONSTRAINT fk_messages_tertulia FOREIGN KEY (ms_tertulia) REFERENCES Tertulias(tr_id),
	CONSTRAINT fk_messages_usr FOREIGN KEY (ms_user) REFERENCES Users(us_id),
	CONSTRAINT fk_messages_type FOREIGN KEY (ms_tag) REFERENCES Tags(tg_id)
);
GO

-- VIEWS

-- FUNCTIONS AND STORED PROCEDURES

-- See <TEST 11>
CREATE FUNCTION FnGetTemplateId_byTertuliaId(@tertuliaId INTEGER, @templateName VARCHAR(40))
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = tp_id FROM Templates 
	WHERE tp_tertulia = @tertuliaId AND tp_name = @templateName;
	RETURN @id;
END;
GO

-- See <TEST 06> <TEST 12> <TEST 13>
CREATE FUNCTION FnGetUserId_byAlias(@alias VARCHAR(40))
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = us_id FROM Users WHERE us_alias = @alias;
	RETURN @id;
END;
GO

-- See <TEST 07>
CREATE FUNCTION FnGetTertuliaLocation_byTertuliaId(@tertuliaId INTEGER)
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = tr_location FROM Tertulias WHERE tr_id = @tertuliaId;
	RETURN @id;
END;
GO

-- See <TEST 10> <TEST 12>
CREATE FUNCTION FnGetCatalogItemId_byTertuliaId(@tertuliaId INTEGER, @itemName VARCHAR(40))
RETURNS INTEGER
AS 
BEGIN
    DECLARE @id INTEGER;
    SELECT @id = ic_id FROM ItemsCatalog WHERE ic_tertulia = @tertuliaId AND ic_name = @itemName;
    RETURN @id;
END;
GO

-- See <TEST 10>
CREATE FUNCTION FnGetTertuliaTemplateId_byTertuliaId(@tertuliaId INTEGER, @templateName VARCHAR(40))
RETURNS INTEGER
AS 
BEGIN
    DECLARE @id INTEGER;
    SELECT @id = tp_id FROM Templates
	WHERE tp_name = @templateName AND tp_tertulia = @tertuliaId;
    RETURN @id;
END;
GO

-- See <TEST 12>
CREATE FUNCTION FnGetEventId_byTertuliaId(@tertuliaId INTEGER, @eventDate DATETIME)
RETURNS INTEGER
AS
BEGIN
    DECLARE @id INTEGER;
    SELECT @id = ev_id FROM Events
	WHERE ev_targetdate = @eventDate AND ev_tertulia = @tertuliaId;
    RETURN @id;
END
GO

-- See <TEST 08> <TEST 09> <TEST 10> <TEST 12> <TEST 13>
CREATE PROCEDURE sp_getId
	@starter VARCHAR(10),
    @tableName SYSNAME,
	@name VARCHAR(40)
AS
BEGIN
	SET NOCOUNT ON;
	DECLARE @SQL NVARCHAR(MAX);
	SET @SQL =  N' SELECT @id = ' + @starter + '_id FROM ' + QUOTENAME(@tableName) + N' WHERE ' + @starter + '_name = @name;';
	DECLARE @id INTEGER;
	EXECUTE sp_executesql @SQL, N'@name VARCHAR(40), @id INTEGER OUTPUT', @name, @id OUTPUT
	RETURN @id;
END
GO

-- See <TEST 11>
CREATE PROCEDURE sp_getEventIdTertuliaId
	@tertuliaName VARCHAR(40), @eventDate DATETIME,
	@eventId INTEGER OUTPUT, @tertuliaId INTEGER OUTPUT
AS
BEGIN
	EXEC @tertuliaId = sp_getId 'tr', 'Tertulias', @tertulianame;
	SELECT @eventId = ev_id FROM Events WHERE ev_tertulia = @tertuliaId AND ev_targetdate = @eventDate;
END
GO

-- See <TEST 06>
CREATE PROCEDURE sp_insertTertulia
	@userId INTEGER, 
	@name VARCHAR(40), @subject VARCHAR(80), 
	@recurrencyTypeName NVARCHAR(40), @fromStart BIT, @skip INTEGER, @param1 VARCHAR(10), @param2 VARCHAR(10), 
	@locationName VARCHAR(40),
	@private INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @recurrencyTypeId INTEGER, @locationId INTEGER, @scheduleId INTEGER, @tertuliaId INTEGER, @ownerId INTEGER;
	EXEC @recurrencyTypeId = dbo.sp_getId 'rc', 'Recurrencies', @recurrencyTypeName;
	EXEC @locationId = dbo.sp_getId 'lo', 'Locations', @locationName;
	INSERT INTO Schedules (sc_recurrency, sc_fromstart, sc_skip, sc_param1, sc_param2) VALUES (@recurrencyTypeId, @fromStart, @skip, @param1, @param2);
	SET @scheduleId = SCOPE_IDENTITY();
	INSERT INTO Tertulias (tr_name, tr_subject, tr_location, tr_schedule, tr_private) VALUES (@name, @subject, @locationId, @scheduleId, @private);
    SET @tertuliaId = SCOPE_IDENTITY();
    EXEC @ownerId = dbo.sp_getId 'ro', 'Roles', 'Owner';
	INSERT INTO Members (mb_tertulia, mb_user, mb_role) VALUES (@tertuliaId, @userId, @ownerId);
	COMMIT
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK
END CATCH
GO

-- See <TEST 07>
CREATE PROCEDURE sp_createEvent
	@tertuliaName VARCHAR(40), 
	@eventLocation VARCHAR(40),
	@eventDate DATETIME
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @tertuliaId INTEGER, @locationId INTEGER;
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', @tertuliaName;
	EXEC @locationId = dbo.sp_getId 'lo', 'Locations', @eventLocation;
	INSERT INTO Events (ev_tertulia, ev_location, ev_targetDate) VALUES (@tertuliaId, @locationId, @eventDate);
	COMMIT
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK
END CATCH
GO

-- See <TEST 07>
CREATE PROCEDURE sp_createEventDefaultLocation
	@tertuliaName VARCHAR(40), 
	@eventDate DATETIME
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @tertuliaId INTEGER, @locationId INTEGER;
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', @tertuliaName;
	SET @locationId = dbo.FnGetTertuliaLocation_byTertuliaId(@tertuliaId);
	INSERT INTO Events (ev_tertulia, ev_location, ev_targetDate) VALUES (@tertuliaId, @locationId, @eventDate);
	COMMIT
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK
END CATCH
GO

-- See <TEST 11>
CREATE PROCEDURE sp_buildChecklist
	@tertuliaName VARCHAR(40), 
	@eventDate DATETIME, 
	@templateName VARCHAR(40)
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @eventId INTEGER, @tertuliaId INTEGER, @templateId INTEGER;
	EXEC dbo.sp_getEventIdTertuliaId @tertulianame, @eventDate, @eventId OUTPUT, @tertuliaId OUTPUT;
	SET @templateId = dbo.FnGetTemplateId_byTertuliaId(@tertuliaId, @templateName);
	DECLARE _cursor CURSOR FOR SELECT tc_item, tc_quantity FROM TemplatesCat WHERE tc_template = @templateId;
	OPEN _cursor;
	DECLARE @itemId INTEGER, @baseQty INTEGER;
	FETCH NEXT FROM _cursor INTO @itemId, @baseQty;
	WHILE @@FETCH_STATUS = 0
	BEGIN
		INSERT INTO EventsItems (ei_event, ei_item, ei_quantity) VALUES (@eventId, @itemId, @baseQty);
		FETCH NEXT FROM _cursor INTO @itemId, @baseQty;
	END
	CLOSE _cursor;
	DEALLOCATE _cursor;
	COMMIT
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK
END CATCH
GO

-- Commit Event Checklist item to user
-- See <TEST 12>
CREATE PROCEDURE sp_assignChecklistItems
	@userAlias VARCHAR(40), 
	@tertulianame VARCHAR(40), 
	@eventDate DATETIME, 
	@itemName VARCHAR(40), 
	@itemQuantity INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @userId INTEGER, @tertuliaId INTEGER, @eventId INTEGER, @itemId INTEGER, @totalQuantity INTEGER, @committedQuantity INTEGER;
	SET @userId = dbo.FnGetUserId_byAlias(@userAlias);
	EXEC @tertuliaId = sp_getId 'tr', 'Tertulias', @tertulianame;
	SET @eventId = dbo.FnGetEventId_byTertuliaId(@tertuliaId, @eventDate);
	SET @itemId = dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, @itemName);

	SELECT @totalQuantity = SUM(ei_quantity) FROM EventsItems 
	WHERE ei_event = @eventId AND ei_item = @itemId;
	if (@totalQuantity = NULL) SET @totalQuantity = 0

	SELECT @committedQuantity = SUM(ct_quantity) FROM Contributions 
	WHERE ct_event = @eventId AND ct_item = @itemId AND ct_user <> @userId;
	if (@committedQuantity IS NULL) SET @committedQuantity = 0

	DECLARE @availableQuantity INTEGER;
	SET @availableQuantity = @totalQuantity - @committedQuantity;
	IF (@availableQuantity < 0) SET @availableQuantity = 0;
	IF (@itemQuantity > @availableQuantity) SET @itemQuantity = @availableQuantity;
	IF ((SELECT COUNT(ct_id) FROM Contributions WHERE ct_user = @userId AND ct_event = @eventId AND ct_item = @itemId) = 0)
	BEGIN
		INSERT INTO Contributions (ct_user, ct_event, ct_item, ct_quantity) VALUES (@userId, @eventId, @itemId, @itemQuantity);
	END ELSE BEGIN
		UPDATE Contributions SET ct_quantity = @itemQuantity WHERE ct_user = @userId AND ct_event = @eventId AND ct_item = @itemId;
	END
	COMMIT;
	RETURN @itemQuantity;
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK
END CATCH
GO

-- See <TEST 13>
CREATE PROCEDURE sp_postMessage
	@userId INTEGER,
	@tertuliaName VARCHAR(40), 
	@typeName VARCHAR(40), 
	@message VARCHAR(40)
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @tertuliaId INTEGER, @typeId INTEGER;
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', @tertuliaName;
	EXEC @typeId = dbo.sp_getId 'tg', 'Tags', @typeName;
	INSERT INTO Messages (ms_tertulia, ms_user, ms_timestamp, ms_tag, ms_message) 
	VALUES (@tertuliaId, @userId, GETDATE(), @typeId, @message);
	COMMIT
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK
END CATCH
GO

-- See <TEST 13>
CREATE PROCEDURE sp_postMessage_byAlias
	@userAlias VARCHAR(40), 
	@tertuliaName VARCHAR(40), 
	@typeName VARCHAR(40), 
	@message VARCHAR(40)
AS
BEGIN
	DECLARE @userId INTEGER;
	SET @userId = dbo.FnGetUserId_byAlias(@userAlias);
	EXEC dbo.sp_postMessage @userId, @tertulianame, @typeName, @message;
END
GO


-- BASE DATA


-- Setup supported tertulias member Roles
-- TEST 01
INSERT INTO Roles (ro_name) VALUES
	  (N'owner')
	, (N'manager')
	, (N'member');
GO

-- Setup supported types of recurrency for tertulias
-- TEST 02
INSERT INTO Recurrencies (rc_name, rc_description) VALUES
	  (N'No Repeat', 'No recurrency.')
	, (N'Daily', 'Occurs every [skip+1] days.')
	, (N'Weekly', 'Occurs every [skip+1] weeks on [param1] weekday.')
	, (N'Monthly', 'Occurs every [skip+1] months on fromStart?[param1]:[EOM-param1] day.')
	, (N'Yearly', 'Occurs every [skip+1] years on [param2] day of [param1] month.')
	, (N'Monthly - On a week day of a week', 'Occurs every [skip+1] months on [param2] weekday of fromStart?[param1]:[MW-param1] week.')
	, (N'Yearly - on a day', 'Occurs every [skip+1] years on fromStart?[param1]:[YD-param1] day of the year.');
GO

-- Setup Message Types
-- TEST 03
INSERT INTO Tags (tg_name, tg_description) VALUES 
	  ('Announcement', 'Announcements regarding for a Tertulia.')
	, ('Warning', 'Warnings for Tertulias.');
GO


-- TEST DATA


-- Create application users
-- TEST 04
INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
	-- ('sid:fadae567db0f67c6fe69d25ee8ffc0b5', N'aborba', N'António', N'Borba da Silva', 'antonio.borba@gmail.com', ''),
	('sid:357a070bdaf6a373efaf9ab34c8ae5b9', N'GGLabs', N'António', N'Borba da Silva', 'abs@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
GO

-- Create a set of tertulia locations
-- TEST 05
INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude) VALUES
	  (N'Pastelaria Mexicana',              N'Avenida Guerra Junqueiro 30C',                      N'1000-167 Lisboa',  'Portugal', '38.740117', '-9.136394')
	, (N'Restaurante Picanha',              N'Rua das Janelas Verdes 96',                         N'1200 Lisboa',      'Portugal', '38.705678', '-9.160624')
	, (N'Restaurante EntreCopos',           N'Rua de Entrecampos, nº11',                          N'1000-151 Lisboa',  'Portugal', '38.744912', '-9.145291')
	, (N'Lisboa Racket Center',             N'Rua Alferes Malheiro',                              N'1700 Lisboa',      'Portugal', '38.758372', '-9.134471')
	, (N'Restaurante O Jacinto',            N'Avenida Ventura Terra 2',                           N'1600-781 Lisboa',  'Portugal', '38.758563', '-9.167007')
	, (N'Restaurante Taberna Gourmet',      N'Rua Padre Américo 28',                              N'1600-548 Lisboa',  'Portugal', '38.763603', '-9.180278')
	, (N'Café A Luz Ideal',                 N'Rua Gen. Schiappa Monteiro 2A',                     N'1600-155 Lisboa',  'Portugal', '38.754401', '-9.174995')
	, (N'Restaurante Honorato - Telheiras', N'Rua Professor Francisco Gentil, Lote A, Telheiras', N'1600 Lisboa',      'Portugal', '38.760363', '-9.166720')
	, (N'Restaurante Gardens',              N'Rua Principal, S/N, Urbanização Quinta Alcoutins',  N'1600-263 Lisboa',  'Portugal', '38.776200', '-9.171391')
	, (N'Pastelaria Arcadas',               N'Rua Cidade de Lobito 282',                          N'1800-071 Lisboa',  'Portugal', '38.764007', '-9.112470')
	, (N'Restaurante Cave Real',            N'Avenida 5 de Outubro 13',                           N'1050 Lisboa',      'Portugal', '38.733541', '-9.147056')
	, (N'Varsailles - Técnico',             N'Avenida Rovisco Pais 1',                            N'1049-001 Lisboa',  'Portugal', '38.737674', '-9.138564')
	, (N'Pastelaria Zineira',               N'Rua Principal, 444, Livramento',                    N'2765-383 Estoril', 'Portugal', '38.713092', '-9.371864')
	, (N'Avó Fernanda',                     N'Avenida Nações Unidas, 33, 2.ºDtº',                 N'1600-531 Lisboa',  'Portugal', '38.764288', '-9.180429');
GO

-- Create a set of tertulias
-- TEST 06
DECLARE @userId INTEGER;
SET @UserId = dbo.FnGetUserId_byAlias('GGLabs');
EXEC sp_insertTertulia @UserId, N'Tertulia do Tejo', N'O que seria do Mundo sem nós!', 'Monthly', 1, 1, '10', '', 'Restaurante Cave Real', 0;
EXEC sp_insertTertulia @UserId, N'Tertúlia dos primos', N'Só Celoricos', 'Monthly', 1, 3, '11', '', 'Restaurante O Jacinto', 0;
EXEC sp_insertTertulia @UserId, N'Escolinha 72-77', N'Sempre em contato', 'Yearly', 1, 3, '4/1', '', 'Restaurante EntreCopos', 0;
EXEC sp_insertTertulia @UserId, N'Natais BS', N'Mais um...', 'Yearly', '0', 0, '25/12', '', 'Avó Fernanda', 0;
SET @UserId = dbo.FnGetUserId_byAlias('GGLabs')
EXEC sp_insertTertulia @UserId, N'Gulbenkian Música', N'', 'Monthly', '1', 0, '12', '', 'Restaurante Gardens', 0;
EXEC sp_insertTertulia @UserId, N'CALM', N'Ex MAC - Sempre só nós 8', 'Monthly', '5', 0, '1', '', 'Restaurante Taberna Gourmet', 0;
EXEC sp_insertTertulia @UserId, N'AtHere', N'Tipo RoBoTo', 'Weekly', '1', 0, '5', '', 'Pastelaria Zineira', 0;
EXEC sp_insertTertulia @UserId, N'Terças Ggl', N'', 'Weekly', '1', 0, '3', '', 'Varsailles - Técnico', 0;
GO

-- Create Tertulia Events
-- TEST 07
EXEC dbo.sp_createEvent 'Terças Ggl', 'Lisboa Racket Center', '20160523 13:00:00';
EXEC dbo.sp_createEventDefaultLocation 'Tertulia do Tejo', '20160904 13:00:00';
EXEC dbo.sp_createEventDefaultLocation 'Escolinha 72-77', '20161022 20:00:00';
GO

-- Create Tertulia Items inventory
-- Test 08
DECLARE @tertuliaId INTEGER;
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Escolinha 72-77';
INSERT INTO ItemsCatalog (ic_name, ic_tertulia) VALUES 
	('Coca-Cola (1lt)', @tertuliaId), 
	('Sumol laranja (1lt)', @tertuliaId), 
	('Cerveja (1lt)', @tertuliaId), 
	('Coca-Cola em lata (1)', @tertuliaId),
	('Água Tónica (1lt)', @tertuliaId),
	('Pão cereais (500g)', @tertuliaId),
	('Fiambre (500g)', @tertuliaId),
	('Queijo Flamengo (500g)', @tertuliaId),
	('Pacotes Batata Frita (500g)', @tertuliaId),
	('Sortido de frutos secos (200g)', @tertuliaId),
	('Pacote de bolacha maria', @tertuliaId);
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Tertulia do Tejo';
INSERT INTO ItemsCatalog (ic_name, ic_tertulia) VALUES  
	('Cerveja (1lt)', @tertuliaId),
	('Vinho Verde Branco (75cl)', @tertuliaId),
	('Vinho Tinto Frutado (75cl)', @tertuliaId),
	('Água Tónica (1lt)', @tertuliaId),
	('Gin (75cl)', @tertuliaId),
	('Copos de vidro', @tertuliaId),
	('Guardanapos de papel (200g)', @tertuliaId);
GO

-- Create Tertulia Items Templates
-- TEST 09
DECLARE @tertuliaId INTEGER;
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Escolinha 72-77';
INSERT INTO Templates (tp_name, tp_tertulia) VALUES
	('Drinks', @tertuliaId),
	('Snacks', @tertuliaId);
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Tertulia do Tejo';
INSERT INTO Templates (tp_name, tp_tertulia) VALUES
	('Drinks', @tertuliaId);
GO

-- Fill Tertulia Items Templates with items from Tertulia Items inventory
-- TEST 10
DECLARE @templateId INTEGER, @tertuliaId INTEGER;
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Escolinha 72-77';
SET @templateId = dbo.FnGetTertuliaTemplateId_byTertuliaId(@tertuliaId, 'Snacks');
INSERT INTO TemplatesCat (tc_template, tc_item, tc_quantity) VALUES 
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Pão cereais (500g)'), 2),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Fiambre (500g)'), 1),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Queijo Flamengo (500g)'), 1),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Pacotes Batata Frita (500g)'), 4),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Sortido de frutos secos (200g)'), 6),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Pacote de bolacha maria'), 2);
SET @templateId = dbo.FnGetTertuliaTemplateId_byTertuliaId(@tertuliaId, 'Drinks');
INSERT INTO TemplatesCat (tc_template, tc_item, tc_quantity) VALUES 
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Coca-Cola (1lt)'), 2),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Sumol laranja (1lt)'), 1),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Cerveja (1lt)'), 4),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Coca-Cola em lata (1)'), 6),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Água Tónica (1lt)'), 2);
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Tertulia do Tejo';
SET @templateId = dbo.FnGetTertuliaTemplateId_byTertuliaId(@tertuliaId, 'Drinks');
INSERT INTO TemplatesCat (tc_template, tc_item, tc_quantity) VALUES 
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Cerveja (1lt)'), 1),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Vinho Verde Branco (75cl)'), 1),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Vinho Tinto Frutado (75cl)'), 1),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Água Tónica (1lt)'), 2),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Gin (75cl)'), 1),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Copos de vidro'), 4),
	(@templateId, dbo.FnGetCatalogItemId_byTertuliaId(@tertuliaId, 'Guardanapos de papel (200g)'), 1);
GO

-- Fill Event Checklist with Tertulia Template
-- TEST 11
EXEC sp_buildChecklist 'Tertulia do Tejo', '2016-09-04 13:00:00', 'Drinks';
GO

-- Commit to handle items to a Tertulia event
-- TEST 12
DECLARE @commitment INTEGER, @itemName VARCHAR(40);
SET @itemName = 'Copos de vidro';
EXEC @commitment = sp_assignChecklistItems 'GGLabs', 'Tertulia do Tejo', '2016-09-04 13:00:00', @itemName, 2;
PRINT 'Commitment for ' + @itemName + ': ' + CAST(@commitment AS VARCHAR);
SET @itemName = 'Cerveja (1lt)';
EXEC @commitment = sp_assignChecklistItems 'GGLabs', 'Tertulia do Tejo', '2016-09-04 13:00:00', @itemName, 2;
PRINT 'Commitment for ' + @itemName + ': ' + CAST(@commitment AS VARCHAR);
GO

-- Post a message in a Tertulia
-- TEST 13
EXEC sp_postMessage_byAlias 'GGLabs', 'Tertulia do Tejo', 'Announcement', 'My test post to a tertulia';
GO
