-------------------------------------------------------------------------------
--                                                                           --
--    ####### ####### ######  ####### #     # #       ###    #     #####     --
--       #    #       #     #    #    #     # #        #    # #   #     #    --
--       #    #       #     #    #    #     # #        #   #   #  #          --
--       #    #####   ######     #    #     # #        #  #     #  #####     --
--       #    #       #   #      #    #     # #        #  #######       #    --
--       #    #       #    #     #    #     # #        #  #     # #     #    --
--       #    ####### #     #    #     #####  ####### ### #     #  #####     --
--                                                                           --
-------------------------------------------------------------------------------
                                                                     
/*
 PREFIXES
	co - Contributions
	ei - EventsItems
	ev - Events
	in - Invitations
	it - Items
 	lo - Locations
 	mb - Members
 	md - MonthlyD
 	mw - MonthlyW
 	nt - EnumTypes
 	no - Notifications
 	nv - EnumValues
 	qi - QuantifiedItems
 	sc - Schedules
 	tp - Templates
 	tr - Tertulias
	us - Users
	wk - Weekly
	yd - YearlyD
	ym - YearlyM
	yw - YearlyW
 */

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

----------------------------------------------------------------------
--                                                                  --
--     #####  #       #######    #    #     #    #     # ######     --
--    #     # #       #         # #   ##    #    #     # #     #    --
--    #       #       #        #   #  # #   #    #     # #     #    --
--    #       #       #####   #     # #  #  #    #     # ######     --
--    #       #       #       ####### #   # #    #     # #          --
--    #     # #       #       #     # #    ##    #     # #          --
--     #####  ####### ####### #     # #     #     #####  #          --
--                                                                  --
----------------------------------------------------------------------
                                                            
IF OBJECT_ID(N'dbo.fnGetEnum') IS NOT NULL DROP FUNCTION fnGetEnum;
IF OBJECT_ID(N'dbo.spSetEnum') IS NOT NULL DROP PROCEDURE spSetEnum;
GO

IF OBJECT_ID(N'dbo.spAcceptInvitation') IS NOT NULL DROP PROCEDURE spAcceptInvitation;
IF OBJECT_ID(N'dbo.spInvite') IS NOT NULL DROP PROCEDURE spInvite;
IF OBJECT_ID(N'dbo.fnGetTemplate_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetTemplate_byTertuliaId;
IF OBJECT_ID(N'dbo.fnGetUserId_byAlias') IS NOT NULL DROP FUNCTION fnGetUserId_byAlias;
IF OBJECT_ID(N'dbo.fnGetTertuliaLocation_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetTertuliaLocation_byTertuliaId;
IF OBJECT_ID(N'dbo.fnGetItem_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetItem_byTertuliaId;
IF OBJECT_ID(N'dbo.fnGetEvent_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetEvent_byTertuliaId;
IF OBJECT_ID(N'dbo.sp_getId') IS NOT NULL DROP PROCEDURE sp_getId;
IF OBJECT_ID(N'dbo.sp_getEventIdTertuliaId') IS NOT NULL DROP PROCEDURE sp_getEventIdTertuliaId;
IF OBJECT_ID(N'dbo.sp_createEvent') IS NOT NULL DROP PROCEDURE sp_createEvent;
IF OBJECT_ID(N'dbo.sp_createEventDefaultLocation') IS NOT NULL DROP PROCEDURE sp_createEventDefaultLocation;
IF OBJECT_ID(N'dbo.sp_insertTertulia_MonthlyW') IS NOT NULL DROP PROCEDURE sp_insertTertulia_MonthlyW;

IF OBJECT_ID(N'dbo.sp_postNotification_byAlias') IS NOT NULL DROP PROCEDURE sp_postNotification_byAlias;
IF OBJECT_ID(N'dbo.sp_postNotification') IS NOT NULL DROP PROCEDURE sp_postNotification;
IF OBJECT_ID(N'dbo.sp_buildEventsItems') IS NOT NULL DROP PROCEDURE sp_buildEventsItems;
IF OBJECT_ID(N'dbo.sp_assignChecklistItems') IS NOT NULL DROP PROCEDURE sp_assignChecklistItems;
GO

IF OBJECT_ID(N'dbo.Invitations') IS NOT NULL DROP TABLE Invitations;
IF OBJECT_ID(N'dbo.fnCountOpenInvitations') IS NOT NULL DROP FUNCTION fnCountOpenInvitations;
IF OBJECT_ID(N'dbo.Notifications') IS NOT NULL DROP TABLE Notifications;
IF OBJECT_ID(N'dbo.Contributions') IS NOT NULL DROP TABLE Contributions;
IF OBJECT_ID(N'dbo.EventsItems') IS NOT NULL DROP TABLE EventsItems;
IF OBJECT_ID(N'dbo.QuantifiedItems') IS NOT NULL DROP TABLE QuantifiedItems;
IF OBJECT_ID(N'dbo.Templates') IS NOT NULL DROP TABLE Templates;
IF OBJECT_ID(N'dbo.Items') IS NOT NULL DROP TABLE Items;
IF OBJECT_ID(N'dbo.Events') IS NOT NULL DROP TABLE Events;
IF OBJECT_ID(N'dbo.Members') IS NOT NULL DROP TABLE Members;
IF OBJECT_ID(N'dbo.Users') IS NOT NULL DROP TABLE Users;
DECLARE @constName VARCHAR(255); SELECT @constName = CONSTRAINT_NAME FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS WHERE CONSTRAINT_NAME='fk_location_tertulia';
IF @constName IS NOT NULL ALTER TABLE Locations DROP CONSTRAINT fk_location_tertulia;
IF OBJECT_ID(N'dbo.Tertulias') IS NOT NULL DROP TABLE Tertulias;
IF OBJECT_ID(N'dbo.Locations') IS NOT NULL DROP TABLE Locations;
IF OBJECT_ID(N'dbo.YearlyM') IS NOT NULL DROP TABLE YearlyM;
IF OBJECT_ID(N'dbo.YearlyW') IS NOT NULL DROP TABLE YearlyW;
IF OBJECT_ID(N'dbo.YearlyD') IS NOT NULL DROP TABLE YearlyD;
IF OBJECT_ID(N'dbo.MonthlyW') IS NOT NULL DROP TABLE MonthlyW;
IF OBJECT_ID(N'dbo.MonthlyD') IS NOT NULL DROP TABLE MonthlyD;
IF OBJECT_ID(N'dbo.Weekly') IS NOT NULL DROP TABLE Weekly;
IF OBJECT_ID(N'dbo.Schedules') IS NOT NULL DROP TABLE Schedules;
IF OBJECT_ID(N'dbo.EnumValues') IS NOT NULL DROP TABLE EnumValues;
IF OBJECT_ID(N'dbo.EnumTypes') IS NOT NULL DROP TABLE EnumTypes;
GO

-----------------------------------------------------------
--                                                       --
--    #######    #    ######  #       #######  #####     --
--       #      # #   #     # #       #       #     #    --
--       #     #   #  #     # #       #       #          --
--       #    #     # ######  #       #####    #####     --
--       #    ####### #     # #       #             #    --
--       #    #     # #     # #       #       #     #    --
--       #    #     # ######  ####### #######  #####     --
--                                                       --
-----------------------------------------------------------

CREATE TABLE EnumTypes(
	nt_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, nt_name VARCHAR(20) NOT NULL
	, CONSTRAINT un_enumtype_name UNIQUE (nt_name)
);
GO

CREATE TABLE EnumValues(
	nv_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, nv_type INTEGER NOT NULL
	, nv_name VARCHAR(20) NOT NULL
	, nv_value INTEGER DEFAULT 0
	, CONSTRAINT un_enumvalue_name UNIQUE (nv_type, nv_name)
	, CONSTRAINT fk_enumvalue_type FOREIGN KEY (nv_type) REFERENCES EnumTypes(nt_id)
);
GO

-- See <TEST 03>
CREATE TABLE Schedules(
	sc_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, sc_recurrency INTEGER NOT NULL
	, CONSTRAINT fk_schedule_recurrency FOREIGN KEY (sc_recurrency) REFERENCES EnumValues(nv_id)
);
GO

-- See <TEST 0XX>
CREATE TABLE Weekly(
	wk_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, wk_schedule INTEGER NOT NULL
	, wk_dow INTEGER NOT NULL
	, wk_skip INTEGER NOT NULL DEFAULT 0
	, CONSTRAINT fk_weekly_schedule FOREIGN KEY (wk_schedule) REFERENCES Schedules(sc_id)
	, CONSTRAINT fk_weekly_dow FOREIGN KEY (wk_dow) REFERENCES EnumValues(nv_id)
);
GO

-- See <TEST 0XX>
CREATE TABLE MonthlyD(
	md_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, md_schedule INTEGER NOT NULL
	, md_dom INTEGER NOT NULL
	, md_is_fromend BIT NOT NULL DEFAULT 0
	, md_skip INTEGER NOT NULL DEFAULT 0
	, CONSTRAINT fk_monthlyd_schedule FOREIGN KEY (md_schedule) REFERENCES Schedules(sc_id)
	, CONSTRAINT fk_monthlyd_dom FOREIGN KEY (md_dom) REFERENCES EnumValues(nv_id)
);
GO

-- See <TEST 0XX>
CREATE TABLE MonthlyW(
	mw_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, mw_schedule INTEGER NOT NULL
	, mw_dow INTEGER NOT NULL DEFAULT 1
	, mw_weeknr INTEGER NOT NULL DEFAULT 0
	, mw_is_fromstart BIT NOT NULL DEFAULT 1
	, mw_skip INTEGER NOT NULL DEFAULT 0
	, CONSTRAINT fk_monthlyw_schedule FOREIGN KEY (mw_schedule) REFERENCES Schedules(sc_id)
	, CONSTRAINT fk_monthlyw_dow FOREIGN KEY (mw_dow) REFERENCES EnumValues(nv_id)
);
GO

-- See <TEST 0XX>
CREATE TABLE YearlyD(
	yd_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, yd_schedule INTEGER NOT NULL
	, yd_doy INTEGER NOT NULL DEFAULT 1
	, yd_is_fromend BIT NOT NULL DEFAULT 0
	, yd_skip INTEGER NOT NULL DEFAULT 0
	, CONSTRAINT fk_yearlyd_schedule FOREIGN KEY (yd_schedule) REFERENCES Schedules(sc_id)
);
GO

-- See <TEST 0XX>
CREATE TABLE YearlyW(
	yw_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, yw_schedule INTEGER NOT NULL
	, yw_dow INTEGER NOT NULL DEFAULT 1
	, yw_weeknr INTEGER NOT NULL DEFAULT 0
	, yw_is_fromend BIT NOT NULL DEFAULT 0
	, yw_skip INTEGER NOT NULL DEFAULT 0
	, CONSTRAINT fk_yearlyw_schedule FOREIGN KEY (yw_schedule) REFERENCES Schedules(sc_id)
	, CONSTRAINT fk_yearlyw_dow FOREIGN KEY (yw_dow) REFERENCES EnumValues(nv_id)
);
GO

-- See <TEST 0XX>
CREATE TABLE YearlyM(
	ym_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, ym_schedule INTEGER NOT NULL
	, ym_dom INTEGER NOT NULL DEFAULT 1
	, ym_month INTEGER NOT NULL DEFAULT 0
	, ym_is_fromend BIT NOT NULL DEFAULT 0
	, ym_skip INTEGER NOT NULL DEFAULT 0
	, CONSTRAINT fk_yearlym_schedule FOREIGN KEY (ym_schedule) REFERENCES Schedules(sc_id)
	, CONSTRAINT fk_yearlym_month FOREIGN KEY (ym_month) REFERENCES EnumValues(nv_id)
);
GO

-- See <TEST 02> <TEST 007>
CREATE TABLE Locations(
	lo_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, lo_name VARCHAR(40) NOT NULL
	, lo_address VARCHAR(80)
	, lo_zip VARCHAR(40)
	, lo_country VARCHAR(40)
	, lo_latitude VARCHAR(12)
	, lo_longitude VARCHAR(12)
	, lo_tertulia INTEGER NOT NULL
 	, CONSTRAINT un_location_nt UNIQUE (lo_name, lo_tertulia)
	, CONSTRAINT un_location_ntll UNIQUE (lo_name, lo_tertulia, lo_latitude, lo_longitude)
--	, CONSTRAINT fk_location_tertulia FOREIGN KEY (lo_tertulia) REFERENCES Tertulias(tr_id)
);
GO

-- See <TEST 03> <TEST 007>
CREATE TABLE Tertulias(
	tr_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, tr_name VARCHAR(40) NOT NULL
	, tr_subject VARCHAR(80)
	, tr_location INTEGER NOT NULL
	, tr_schedule INTEGER NOT NULL
	, tr_is_private BIT NOT NULL DEFAULT 0
	, tr_is_cancelled BIT NOT NULL DEFAULT 0
	, CONSTRAINT un_tertulia_name UNIQUE (tr_name)
	, CONSTRAINT fk_tertulia_location FOREIGN KEY (tr_location) REFERENCES Locations(lo_id)
	, CONSTRAINT fk_tertulia_schedule FOREIGN KEY (tr_schedule) REFERENCES Schedules(sc_id)
);
GO

-- See <TEST 01>
CREATE TABLE Users(
	us_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, us_sid VARCHAR(40) NOT NULL
	, us_alias VARCHAR(20)
	, us_firstName VARCHAR(40)
	, us_lastName VARCHAR(40)
	, us_email VARCHAR(40)
	, us_picture VARCHAR(255)
	, CONSTRAINT un_users_sid UNIQUE (us_sid)
	, CONSTRAINT un_users_alias UNIQUE (us_alias)
	, CONSTRAINT un_users_email UNIQUE (us_email)
);
GO

-- See <TEST 03>
CREATE TABLE Members(
	mb_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, mb_tertulia INTEGER NOT NULL
	, mb_user INTEGER NOT NULL
	, mb_role INTEGER NOT NULL
	, CONSTRAINT un_members_tu UNIQUE (mb_tertulia, mb_user)
	, CONSTRAINT fk_members_tertulia FOREIGN KEY (mb_tertulia) REFERENCES Tertulias(tr_id)
	, CONSTRAINT fk_members_user FOREIGN KEY (mb_user) REFERENCES Users(us_id)
);
GO

-- See <TEST 007>
-- TODO: URI
-- TODO: Criar localização 0
CREATE TABLE Events(
	ev_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, ev_tertulia INTEGER NOT NULL
	, ev_location INTEGER NOT NULL DEFAULT 0
	, ev_targetdate DATETIME NOT NULL
	, ev_note VARCHAR(120)
	, CONSTRAINT un_event_tlt UNIQUE (ev_tertulia, ev_location, ev_targetdate)
	, CONSTRAINT fk_events_tertulia FOREIGN KEY (ev_tertulia) REFERENCES Tertulias(tr_id)
	, CONSTRAINT fk_events_location FOREIGN KEY (ev_location) REFERENCES Locations(lo_id)
);
GO

-- See <TEST 008>
CREATE TABLE Items(
	it_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, it_name VARCHAR(40) NOT NULL
	, it_tertulia INTEGER NOT NULL
	, CONSTRAINT un_items_tnsu UNIQUE (it_tertulia, it_name)
	, CONSTRAINT fk_items_tertulia FOREIGN KEY (it_tertulia) REFERENCES Tertulias(tr_id)
);
GO

-- See <TEST 009>
CREATE TABLE Templates(
	tp_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, tp_name VARCHAR(40) NOT NULL
	, tp_tertulia INTEGER NOT NULL
	, CONSTRAINT un_templates_name UNIQUE (tp_tertulia, tp_name)
	, CONSTRAINT fk_templates_tertulia FOREIGN KEY (tp_tertulia) REFERENCES Tertulias(tr_id)
);
GO

-- See <TEST 010> <TEST 011>
CREATE TABLE QuantifiedItems(
	qi_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, qi_template INTEGER NOT NULL
	, qi_item INTEGER NOT NULL
	, qi_quantity INTEGER NOT NULL
	, CONSTRAINT un_quantifieditems_tni UNIQUE (qi_template, qi_item)
	, CONSTRAINT fk_quantifieditems_template FOREIGN KEY (qi_template) REFERENCES Templates(tp_id)
	, CONSTRAINT fk_quantifieditems_item FOREIGN KEY (qi_item) REFERENCES Items(it_id)
);
GO

-- See <TEST 011> <TEST 012>
CREATE TABLE EventsItems(
	ei_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, ei_event INTEGER NOT NULL
	, ei_item INTEGER NOT NULL
	, ei_quantity INTEGER NOT NULL DEFAULT 1
	, CONSTRAINT un_eventsitems_ei UNIQUE (ei_event, ei_item)
	, CONSTRAINT fk_eventsitems_event FOREIGN KEY (ei_event) REFERENCES Events(ev_id)
	, CONSTRAINT fk_eventsitems_item FOREIGN KEY (ei_item) REFERENCES Items(it_id)	
);
GO

-- See <TEST 012>
-- TODO: fk eventitem ; remover event, item
CREATE TABLE Contributions(
	ct_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, ct_user INTEGER NOT NULL
	, ct_event INTEGER NOT NULL
	, ct_item INTEGER NOT NULL
	, ct_quantity INTEGER NOT NULL DEFAULT 1
	, CONSTRAINT un_contributions_iu UNIQUE (ct_user, ct_event, ct_item)
	, CONSTRAINT fk_contributions_user FOREIGN KEY (ct_user) REFERENCES Users(us_id)
	, CONSTRAINT fk_contributions_event FOREIGN KEY (ct_event) REFERENCES Events(ev_id)
	, CONSTRAINT fk_contributions_item FOREIGN KEY (ct_item) REFERENCES Items(it_id)	
);
GO

-- See <TEST 013>
CREATE TABLE Notifications(
	no_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, no_tertulia INTEGER NOT NULL
	, no_user INTEGER NOT NULL
	, no_timestamp DATETIME NOT NULL DEFAULT GETDATE()
	, no_tag INTEGER NOT NULL
	, no_message VARCHAR(40)
	, CONSTRAINT un_notifications_tuttm UNIQUE (no_tertulia, no_user, no_timestamp, no_tag, no_message)
	, CONSTRAINT fk_notifications_tertulia FOREIGN KEY (no_tertulia) REFERENCES Tertulias(tr_id)
	, CONSTRAINT fk_notifications_usr FOREIGN KEY (no_user) REFERENCES Users(us_id)
	, CONSTRAINT fk_notifications_type FOREIGN KEY (no_tag) REFERENCES EnumValues(nv_id)
);
GO

-- prever cancelamento
CREATE TABLE Invitations(
	in_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, in_key VARCHAR(36) NOT NULL -- Ex: E5FD8BEF-94EB-4BF4-B85A-FAA4B1B5FE33
	, in_tertulia INTEGER
	, in_email VARCHAR(40) NOT NULL
	, in_is_acknowledged BIT NOT NULL DEFAULT 0
	, in_invitationDate DATETIME DEFAULT GETDATE()
	, CONSTRAINT un_invitations_key UNIQUE (in_key)
	, CONSTRAINT un_invitations_ke UNIQUE (in_key, in_email)
	, CONSTRAINT fk_invitations_tertulia FOREIGN KEY (in_tertulia) REFERENCES Tertulias(tr_id)
);
GO

-----------------------------------------------
--                                           --
--    #     # ### ####### #     #  #####     --
--    #     #  #  #       #  #  # #     #    --
--    #     #  #  #       #  #  # #          --
--    #     #  #  #####   #  #  #  #####     --
--     #   #   #  #       #  #  #       #    --
--      # #    #  #       #  #  # #     #    --
--       #    ### #######  ## ##   #####     --
--                                           --
-----------------------------------------------
                                     

---------------------------------------------------------------------------------------------------------------------------
--                                                                                                                       --
--    ######  ######  #######  #####  ######     #    #     # #     #    #    ######  ### #       ### ####### #     #    --
--    #     # #     # #     # #     # #     #   # #   ##   ## ##   ##   # #   #     #  #  #        #     #     #   #     --
--    #     # #     # #     # #       #     #  #   #  # # # # # # # #  #   #  #     #  #  #        #     #      # #      --
--    ######  ######  #     # #  #### ######  #     # #  #  # #  #  # #     # ######   #  #        #     #       #       --
--    #       #   #   #     # #     # #   #   ####### #     # #     # ####### #     #  #  #        #     #       #       --
--    #       #    #  #     # #     # #    #  #     # #     # #     # #     # #     #  #  #        #     #       #       --
--    #       #     # #######  #####  #     # #     # #     # #     # #     # ######  ### ####### ###    #       #       --
--                                                                                                                       --
---------------------------------------------------------------------------------------------------------------------------

-- Invitations Check
CREATE FUNCTION fnCountOpenInvitations(@email VARCHAR(40))
RETURNS INTEGER
AS 
BEGIN
	DECLARE @cnt INTEGER;
	SELECT @cnt = COUNT(in_id) FROM Invitations WHERE in_email = @email AND in_is_acknowledged = 0;
	RETURN @cnt;
END;
GO

ALTER TABLE Invitations ADD CONSTRAINT ck_invitations_1 CHECK (dbo.fnCountOpenInvitations(in_email) = 1);
GO


CREATE FUNCTION fnGetEnum(@enumtype VARCHAR(20), @name VARCHAR(20))
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = nv_id FROM EnumTypes INNER JOIN EnumValues ON nv_type = nt_id 
	WHERE nt_name = @enumtype AND nv_name = @name;
	RETURN @id;
END;
GO

CREATE PROCEDURE spSetEnum @enumtype VARCHAR(20), @name VARCHAR(20), @value INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	IF NOT EXISTS (SELECT nt_id FROM EnumTypes WHERE nt_name = @enumtype)
	BEGIN
		INSERT INTO EnumTypes (nt_name) VALUES (@enumtype);
		SET @id = SCOPE_IDENTITY();
	END
	ELSE SELECT @id = nt_id FROM EnumTypes WHERE nt_name = @enumtype;
	INSERT INTO EnumValues (nv_type, nv_name, nv_value) VALUES (@id, @name, @value);
END;
GO

-- TODO: CHECK TERTULIAS
-- See <TEST 008> <TEST 009> <TEST 010> <TEST 012> <TEST 013>
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

---------------------------------------------------------------------------------------
--                                                                                   --
--    #       ####### #     # #     #  #####  ####### ### ####### #     #  #####     --
--     #      #       #     # ##    # #     #    #     #  #     # ##    # #     #    --
--      #     #       #     # # #   # #          #     #  #     # # #   # #          --
--       #    #####   #     # #  #  # #          #     #  #     # #  #  #  #####     --
--      #     #       #     # #   # # #          #     #  #     # #   # #       #    --
--     #      #       #     # #    ## #     #    #     #  #     # #    ## #     #    --
--    #       #        #####  #     #  #####     #    ### ####### #     #  #####     --
--                                                                                   --
---------------------------------------------------------------------------------------
                                                                             
-- See <TEST 011>
CREATE FUNCTION fnGetTemplate_byTertuliaId(@tertuliaId INTEGER, @templateName VARCHAR(40))
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = tp_id FROM Templates INNER JOIN Tertulias ON tp_tertulia = tr_id 
	WHERE tp_tertulia = @tertuliaId AND tp_name = @templateName AND tr_is_cancelled = 0;
	RETURN @id;
END;
GO

-- See <TEST 03> <TEST 012> <TEST 013>
CREATE FUNCTION fnGetUserId_byAlias(@alias VARCHAR(40))
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = us_id FROM Users WHERE us_alias = @alias;
	RETURN @id;
END;
GO

-- See <TEST 007>
CREATE FUNCTION fnGetTertuliaLocation_byTertuliaId(@tertuliaId INTEGER)
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = tr_location FROM Tertulias WHERE tr_id = @tertuliaId AND tr_is_cancelled = 0;
	RETURN @id;
END;
GO

-- See <TEST 010> <TEST 012>
CREATE FUNCTION fnGetItem_byTertuliaId(@tertuliaId INTEGER, @itemName VARCHAR(40))
RETURNS INTEGER
AS 
BEGIN
    DECLARE @id INTEGER;
    SELECT @id = it_id FROM Items INNER JOIN Tertulias ON it_tertulia = tr_id
    WHERE it_tertulia = @tertuliaId AND it_name = @itemName AND tr_is_cancelled = 0;
    RETURN @id;
END;
GO

-- See <TEST 012>
CREATE FUNCTION fnGetEvent_byTertuliaId(@tertuliaId INTEGER, @eventDate DATETIME)
RETURNS INTEGER
AS
BEGIN
    DECLARE @id INTEGER;
    SELECT @id = ev_id FROM Events INNER JOIN Tertulias ON ev_tertulia = tr_id
	WHERE ev_targetdate = @eventDate AND ev_tertulia = @tertuliaId AND tr_is_cancelled = 0;
    RETURN @id;
END
GO

------------------------------------------------------------------------------------------------------------------------------------------------------
--                                                                                                                                                  --
--    #        #####  ####### ####### ######  ####### ######     ######  ######  #######  #####  ####### ######  #     # ######  #######  #####     --
--     #      #     #    #    #     # #     # #       #     #    #     # #     # #     # #     # #       #     # #     # #     # #       #     #    --
--      #     #          #    #     # #     # #       #     #    #     # #     # #     # #       #       #     # #     # #     # #       #          --
--       #     #####     #    #     # ######  #####   #     #    ######  ######  #     # #       #####   #     # #     # ######  #####    #####     --
--      #           #    #    #     # #   #   #       #     #    #       #   #   #     # #       #       #     # #     # #   #   #             #    --
--     #      #     #    #    #     # #    #  #       #     #    #       #    #  #     # #     # #       #     # #     # #    #  #       #     #    --
--    #        #####     #    ####### #     # ####### ######     #       #     # #######  #####  ####### ######   #####  #     # #######  #####     --
--                                                                                                                                                  --
------------------------------------------------------------------------------------------------------------------------------------------------------

-- See <TEST 011>
CREATE PROCEDURE sp_getEventIdTertuliaId
	@tertuliaName VARCHAR(40), @eventDate DATETIME,
	@eventId INTEGER OUTPUT, @tertuliaId INTEGER OUTPUT
AS
BEGIN
	EXEC @tertuliaId = sp_getId 'tr', 'Tertulias', @tertulianame;
	SELECT @eventId = ev_id FROM Events INNER JOIN Tertulias ON ev_tertulia = tr_id
	WHERE ev_tertulia = @tertuliaId AND ev_targetdate = @eventDate AND tr_is_cancelled = 0;
END
GO

-- TODO: CHECK TERTULIAS
-- See <TEST 03>
CREATE PROCEDURE sp_insertTertulia_MonthlyW
	@name VARCHAR(40), @subject VARCHAR(80), 
	@userId INTEGER, 
	@weekDay VARCHAR(20), @weekNr INTEGER, 
	@fromStart BIT, @skip INTEGER, 
	@locationName VARCHAR(40),
	@locationAddress VARCHAR(80),
	@locationZip VARCHAR(40),
	@locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12),
	@locationLongitude VARCHAR(12),
	@isPrivate INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @recurrency INTEGER, @location INTEGER, @schedule INTEGER, @tertulia INTEGER, @owner INTEGER, @dow INTEGER;

	SET @recurrency = dbo.fnGetEnum('Recurrency', 'MonthlyW');
	EXEC @location = dbo.sp_getId 'lo', 'Locations', 'Dummy';

	SET @dow = dbo.fnGetEnum('WeekDays', @weekDay);

	INSERT INTO Schedules (sc_recurrency) VALUES (@recurrency);
	SET @schedule = SCOPE_IDENTITY();

	INSERT INTO MonthlyW (mw_schedule, mw_dow, mw_weeknr, mw_is_fromstart, mw_skip) 
	VALUES (@schedule, @dow, @weekNr, @fromStart, @skip);

    INSERT INTO Tertulias (tr_name, tr_subject, tr_location, tr_schedule, tr_is_private) 
    VALUES (@name, @subject, @location, @schedule, @isPrivate);
    SET @tertulia = SCOPE_IDENTITY();

    INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, lo_tertulia)
    VALUES (@locationName, @locationAddress, @locationZip, @locationCountry, @locationLatitude, @locationLongitude, @tertulia);
    SET @location = SCOPE_IDENTITY();

    UPDATE Tertulias SET tr_location = @location WHERE tr_id = @tertulia;

    SET @owner = dbo.fnGetEnum('Roles', 'owner');
	INSERT INTO Members (mb_tertulia, mb_user, mb_role) VALUES (@tertulia, @userId, @owner);
	COMMIT TRANSACTION
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION
END CATCH
GO

-- TODO: CHECK TERTULIAS
-- See <TEST 007>
CREATE PROCEDURE sp_createEvent
	@tertuliaName VARCHAR(40), 
	@eventLocation VARCHAR(40),
	@eventDate DATETIME
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @tertulia INTEGER, @location INTEGER;
	EXEC @tertulia = dbo.sp_getId 'tr', 'Tertulias', @tertuliaName;
	EXEC @location = dbo.sp_getId 'lo', 'Locations', @eventLocation;
	INSERT INTO Events (ev_tertulia, ev_location, ev_targetDate) VALUES (@tertulia, @location, @eventDate);
	COMMIT TRANSACTION
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION
END CATCH
GO

-- See <TEST 007>
CREATE PROCEDURE sp_createEventDefaultLocation
	@tertuliaName VARCHAR(40), 
	@eventDate DATETIME
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @tertuliaId INTEGER, @locationId INTEGER;
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', @tertuliaName;
	SET @locationId = dbo.fnGetTertuliaLocation_byTertuliaId(@tertuliaId);
	INSERT INTO Events (ev_tertulia, ev_location, ev_targetDate) VALUES (@tertuliaId, @locationId, @eventDate);
	COMMIT TRANSACTION
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION
END CATCH
GO

-- See <TEST 011>
CREATE PROCEDURE sp_buildEventsItems
	@tertuliaName VARCHAR(40), 
	@eventDate DATETIME, 
	@templateName VARCHAR(40)
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @event INTEGER, @tertulia INTEGER, @template INTEGER;
	EXEC dbo.sp_getEventIdTertuliaId @tertulianame, @eventDate, @event OUTPUT, @tertulia OUTPUT;
	SET @template = dbo.fnGetTemplate_byTertuliaId(@tertulia, @templateName);
	DECLARE _cursor CURSOR FOR SELECT tc_item, tc_quantity FROM TemplatesCat WHERE tc_template = @template; -- VAMOS AQUI
	OPEN _cursor;
	DECLARE @itemId INTEGER, @baseQty INTEGER;
	FETCH NEXT FROM _cursor INTO @itemId, @baseQty;
	WHILE @@FETCH_STATUS = 0
	BEGIN
		INSERT INTO EventsItems (ei_event, ei_item, ei_quantity) VALUES (@event, @itemId, @baseQty);
		FETCH NEXT FROM _cursor INTO @itemId, @baseQty;
	END
	CLOSE _cursor;
	DEALLOCATE _cursor;
	COMMIT TRANSACTION
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION
END CATCH
GO

-- Commit Event Checklist item to user
-- See <TEST 012>
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
	SET @userId = dbo.fnGetUserId_byAlias(@userAlias);
	EXEC @tertuliaId = sp_getId 'tr', 'Tertulias', @tertulianame;
	SET @eventId = dbo.fnGetEvent_byTertuliaId(@tertuliaId, @eventDate);
	SET @itemId = dbo.fnGetItem_byTertuliaId(@tertuliaId, @itemName);

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
	ROLLBACK TRANSACTION
END CATCH
GO

-- See <TEST 013>
CREATE PROCEDURE sp_postNotification
	@userId INTEGER,
	@tertuliaName VARCHAR(40), 
	@typeName VARCHAR(40), 
	@message VARCHAR(40)
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @tertulia INTEGER, @tag INTEGER;
	EXEC @tertulia = dbo.sp_getId 'tr', 'Tertulias', @tertuliaName;
	EXEC @tag = dbo.sp_getId 'tg', 'Tags', @typeName;
	INSERT INTO Notifications (no_tertulia, no_user, no_timestamp, no_message) 
	VALUES (@tertulia, @userId, @tag, @message);
	COMMIT TRANSACTION
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION
END CATCH
GO

-- See <TEST 013>
CREATE PROCEDURE sp_postNotification_byAlias
	@userAlias VARCHAR(40), 
	@tertuliaName VARCHAR(40), 
	@typeName VARCHAR(40), 
	@message VARCHAR(40)
AS
BEGIN
	DECLARE @userId INTEGER;
	SET @userId = dbo.fnGetUserId_byAlias(@userAlias);
	EXEC dbo.sp_postNotification @userId, @tertulianame, @typeName, @message;
END
GO

CREATE PROCEDURE spInvite @tertuliaName VARCHAR(40), @email VARCHAR(40)
AS 
BEGIN
	DECLARE @tertulia INTEGER; EXEC @tertulia = dbo.sp_getId 'tr', 'Tertulias', @tertulianame;
	DECLARE @token VARCHAR(36); SET @token = newid();
	INSERT INTO Invitations (in_key, in_tertulia, in_email) VALUES (@token, @tertulia, @email);
	RETURN @token;
END;
GO

CREATE PROCEDURE spAcceptInvitation @userId INTEGER, @token VARCHAR(36)
AS 
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @tertulia INTEGER, @role INTEGER, @email_i VARCHAR(40), @email_u VARCHAR(40);
	SELECT @tertulia = in_tertulia, @email_i = in_email FROM Invitations 
		WHERE in_key = @token AND in_is_acknowledged = 0;
	SELECT @email_u = us_email FROM Users WHERE us_id = @userId;
	IF @email_i <> @email_u
	BEGIN
		ROLLBACK; TRANSACTION
		RETURN -1;
	END
	SET @role = dbo.fnGetEnum('Roles', 'owner');
	INSERT INTO Members (mb_tertulia, mb_user, mb_role) VALUES (@tertulia, @userId, @role);
	UPDATE Invitations SET in_is_acknowledged = 1 WHERE in_key = @token;
	RETURN @token;
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION
END CATCH
GO

------------------------------------------------------------------------------
------------------------------------------------------------------------------
--                                                                          --
--    ######     #     #####  #######    ######     #    #######    #       --
--    #     #   # #   #     # #          #     #   # #      #      # #      --
--    #     #  #   #  #       #          #     #  #   #     #     #   #     --
--    ######  #     #  #####  #####      #     # #     #    #    #     #    --
--    #     # #######       # #          #     # #######    #    #######    --
--    #     # #     # #     # #          #     # #     #    #    #     #    --
--    ######  #     #  #####  #######    ######  #     #    #    #     #    --
--                                                                          --
------------------------------------------------------------------------------
------------------------------------------------------------------------------

-- DummyStuff
EXEC spSetEnum N'Dummy', N'dummy', 0;

-- WeekDays
EXEC spSetEnum N'WeekDays', N'sunday',    1;
EXEC spSetEnum N'WeekDays', N'monday',    2;
EXEC spSetEnum N'WeekDays', N'tuesday',   3; 
EXEC spSetEnum N'WeekDays', N'wednesday', 4;
EXEC spSetEnum N'WeekDays', N'thursday',  5;
EXEC spSetEnum N'WeekDays', N'friday',    6;
EXEC spSetEnum N'WeekDays', N'saturday',  7;
GO

-- MonthsNames
EXEC spSetEnum N'MonthNames', N'january',    1;
EXEC spSetEnum N'MonthNames', N'february',   2;
EXEC spSetEnum N'MonthNames', N'march',      3;
EXEC spSetEnum N'MonthNames', N'april',      4;
EXEC spSetEnum N'MonthNames', N'may',        5;
EXEC spSetEnum N'MonthNames', N'june',       6;
EXEC spSetEnum N'MonthNames', N'july',       7;
EXEC spSetEnum N'MonthNames', N'august',     8;
EXEC spSetEnum N'MonthNames', N'september',  9;
EXEC spSetEnum N'MonthNames', N'october',   10;
EXEC spSetEnum N'MonthNames', N'november',  11;
EXEC spSetEnum N'MonthNames', N'december',  12;
GO

-- Recurrencies
EXEC spSetEnum N'Recurrency', N'Weekly',   0; -- Events with a weekly recurrency on a week day.
EXEC spSetEnum N'Recurrency', N'MonthlyD', 0; -- Events with a monthly recurrency on a month day.
EXEC spSetEnum N'Recurrency', N'MonthlyW', 0; -- Events with a monthly recurrency on a week day of a week of the month.
EXEC spSetEnum N'Recurrency', N'YearlyD',  0; -- Events with a yearly recurrency on a year day.
EXEC spSetEnum N'Recurrency', N'YearlyW',  0; -- Events with a yearly recurrency on a week day of a week of the year.
EXEC spSetEnum N'Recurrency', N'YearlyM',  0; -- Events with a yearly recurrency on a month day of a month of the year
GO

-- Roles
EXEC spSetEnum N'Roles', N'owner',   0;
EXEC spSetEnum N'Roles', N'manager', 0;
EXEC spSetEnum N'Roles', N'member',  0;
GO

-- Tags
EXEC spSetEnum N'Tags', N'announcements', 0;
GO

-- Dummy Data
DECLARE @dummy INTEGER, @schedule INTEGER, @location INTEGER, @tertulia INTEGER;

SET @dummy = dbo.fnGetEnum('Dummy', 'dummy');
INSERT INTO Schedules (sc_recurrency) VALUES (@dummy);
SET @schedule = SCOPE_IDENTITY();

INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, lo_tertulia)
VALUES ('Dummy', 'Dummy', 'Dummy', 'Dummy', 'Dummy', 'Dummy', @dummy);
SET @location = SCOPE_IDENTITY();

INSERT INTO Tertulias (tr_name, tr_subject, tr_location, tr_schedule, tr_is_private, tr_is_cancelled)
VALUES ('Dummy', 'Dummy', @location, @schedule, 1, 1);
SET @tertulia = SCOPE_IDENTITY();

UPDATE Locations SET lo_tertulia = @tertulia;

ALTER TABLE Locations ADD CONSTRAINT fk_location_tertulia FOREIGN KEY (lo_tertulia) REFERENCES Tertulias(tr_id);
GO

------------------------------------------------------------------------------
------------------------------------------------------------------------------
--                                                                          --
--    ####### #######  #####  #######    ######     #    #######    #       --
--       #    #       #     #    #       #     #   # #      #      # #      --
--       #    #       #          #       #     #  #   #     #     #   #     --
--       #    #####    #####     #       #     # #     #    #    #     #    --
--       #    #             #    #       #     # #######    #    #######    --
--       #    #       #     #    #       #     # #     #    #    #     #    --
--       #    #######  #####     #       ######  #     #    #    #     #    --
--                                                                          --
------------------------------------------------------------------------------
------------------------------------------------------------------------------

-- Create a Schedule
-- TEST 00
-- To reposition
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @dow INTEGER, @schedule INTEGER;
	SET @dow = dbo.fnGetEnum('WeekDays', 'Monday');
	DECLARE @recurrency INTEGER; SET @recurrency = dbo.fnGetEnum('Recurrency', 'MonthlyW');
	INSERT INTO Schedules (sc_recurrency) VALUES (@recurrency);
	SET @schedule = SCOPE_IDENTITY();
	INSERT INTO MonthlyW (mw_schedule, mw_dow, mw_weeknr, mw_is_fromstart, mw_skip) VALUES (@schedule, @dow, 0, 1, 0);
	ROLLBACK TRANSACTION
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION
END CATCH
GO

-- Create application users
-- TEST 01
INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
	-- ('sid:fadae567db0f67c6fe69d25ee8ffc0b5', N'aborba', N'António', N'Borba da Silva', 'antonio.borba@gmail.com', ''),
	('sid:357a070bdaf6a373efaf9ab34c8ae5b9', N'GGLabs', N'António', N'Borba da Silva', 'abs@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
GO

-- Create a set of tertulia locations
-- TEST 02
DECLARE @userId INTEGER, @tertulia INTEGER;
SET @UserId = dbo.fnGetUserId_byAlias('GGLabs');
EXEC sp_insertTertulia_MonthlyW 
	N'Tertulia do Tejo'                -- [name]
	, N'O que seria do Mundo sem nós!' -- [subject]
	, @UserId                          -- [userId]
	, N'friday'                        -- [weekDay]
	, 2                                -- [weekNr]
	, 1                                -- [fromStart]
	, 0                                -- [skip]
	, N'Restaurante Cave Real'         -- [locationName]
	, N'Avenida 5 de Outubro 13'       -- [locationAddress]
	, N'1050 Lisboa'                   -- [locationZip]
	, N'Portugal'                      -- [locationCountry]
	, N'38.733541'                     -- [locationLatitude]
	, N'-9.147056'                     -- [locationLongitude]
	, 1;                               -- [isPrivate]
SELECT @tertulia = tr_id FROM Tertulias WHERE tr_name = N'Tertulia do Tejo';
INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, lo_tertulia) VALUES
	  (N'Pastelaria Mexicana',              N'Avenida Guerra Junqueiro 30C',                      N'1000-167 Lisboa',  'Portugal', '38.740117', '-9.136394', @tertulia)
--	, (N'Restaurante Cave Real',            N'Avenida 5 de Outubro 13',                           N'1050 Lisboa',      'Portugal', '38.733541', '-9.147056', @tertulia)
	, (N'Restaurante Picanha',              N'Rua das Janelas Verdes 96',                         N'1200 Lisboa',      'Portugal', '38.705678', '-9.160624', @tertulia)
	, (N'Restaurante EntreCopos',           N'Rua de Entrecampos, nº11',                          N'1000-151 Lisboa',  'Portugal', '38.744912', '-9.145291', @tertulia)
	, (N'Lisboa Racket Center',             N'Rua Alferes Malheiro',                              N'1700 Lisboa',      'Portugal', '38.758372', '-9.134471', @tertulia)
	, (N'Restaurante O Jacinto',            N'Avenida Ventura Terra 2',                           N'1600-781 Lisboa',  'Portugal', '38.758563', '-9.167007', @tertulia)
	, (N'Restaurante Taberna Gourmet',      N'Rua Padre Américo 28',                              N'1600-548 Lisboa',  'Portugal', '38.763603', '-9.180278', @tertulia)
	, (N'Café A Luz Ideal',                 N'Rua Gen. Schiappa Monteiro 2A',                     N'1600-155 Lisboa',  'Portugal', '38.754401', '-9.174995', @tertulia)
	, (N'Restaurante Honorato - Telheiras', N'Rua Professor Francisco Gentil, Lote A, Telheiras', N'1600 Lisboa',      'Portugal', '38.760363', '-9.166720', @tertulia)
	, (N'Restaurante Gardens',              N'Rua Principal, S/N, Urbanização Quinta Alcoutins',  N'1600-263 Lisboa',  'Portugal', '38.776200', '-9.171391', @tertulia)
	, (N'Pastelaria Arcadas',               N'Rua Cidade de Lobito 282',                          N'1800-071 Lisboa',  'Portugal', '38.764007', '-9.112470', @tertulia)
	, (N'Varsailles - Técnico',             N'Avenida Rovisco Pais 1',                            N'1049-001 Lisboa',  'Portugal', '38.737674', '-9.138564', @tertulia)
	, (N'Pastelaria Zineira',               N'Rua Principal, 444, Livramento',                    N'2765-383 Estoril', 'Portugal', '38.713092', '-9.371864', @tertulia)
	, (N'Avó Fernanda',                     N'Avenida Nações Unidas, 33, 2.ºDtº',                 N'1600-531 Lisboa',  'Portugal', '38.764288', '-9.180429', @tertulia);
GO

-- Create a set of tertulias
-- TEST 03
-- [name], [subject], [userId], [weekDay], [weekNr], [fromStart], [skip], [locationName], [isPrivate]
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @userId INTEGER, @tertulia INTEGER;
	SET @UserId = dbo.fnGetUserId_byAlias('GGLabs');
	EXEC sp_insertTertulia_MonthlyW 
		N'Tertúlia dos primos', N'Só Celoricos' -- [name], [subject]
		, @UserId                               -- [userId]
		, N'friday', 0 , 1 , 3                  -- [weekDay], [weekNr], [fromStart], [skip]
		, N'Restaurante O Jacinto', N'Avenida Ventura Terra 2', N'1600-781 Lisboa', N'Portugal' -- [locationName], [locationAddress], [locationZip], [locationCountry]                     --
		, N'38.758563', N'-9.167007'            -- [locationLatitude], [locationLongitude]
		, 1;                                    -- [isPrivate]
	EXEC sp_insertTertulia_MonthlyW 
		N'Escolinha 72-77', N'Sempre em contato'            
		, @UserId
		, 'saturday', 0, 1, 10
		, 'Restaurante EntreCopos', N'Rua de Entrecampos, nº11', N'1000-151 Lisboa', 'Portugal'
		, '38.744912', '-9.145291'
		, 1;
	EXEC sp_insertTertulia_MonthlyW 
		N'Natais BS' , N'Mais um...'
		, @UserId
		, 'sunday', 0, 0, 51
		, 'Avó Fernanda', N'Avenida Nações Unidas, 33, 2.ºDtº', N'1600-531 Lisboa', 'Portugal'
		, '38.764288', '-9.180429'
		, 1;
	SET @UserId = dbo.fnGetUserId_byAlias('GGLabs')
	EXEC sp_insertTertulia_MonthlyW 
		N'Gulbenkian Música' , N''
		, @UserId
		, 'thursday', 1, 1, 3
		, 'Restaurante Gardens', N'Rua Principal, S/N, Urbanização Quinta Alcoutins', N'1600-263 Lisboa', 'Portugal'
		, '38.776200', '-9.171391'
		, 0;
	EXEC sp_insertTertulia_MonthlyW 
		N'CALM', N'Ex MAC - Sempre só nós 8'
		, @UserId
		, 'friday' , 0, 0, 3
		, 'Restaurante Taberna Gourmet', N'Rua Padre Américo 28', N'1600-548 Lisboa', 'Portugal'
		, '38.763603', '-9.180278'
		, 1;
	EXEC sp_insertTertulia_MonthlyW 
		N'AtHere', N'Tipo RoBoTo'
		, @UserId
		, 'thursday', 0, 0, 5
		, 'Pastelaria Zineira', N'Rua Principal, 444, Livramento', N'2765-383 Estoril', 'Portugal'
		, '38.713092', '-9.371864'
		, 1;
	EXEC sp_insertTertulia_MonthlyW 
		N'Terças Ggl', N''
		, @UserId
		, 'tuesday', 0, 0, 0
		, 'Varsailles - Técnico', N'Avenida Rovisco Pais 1', N'1049-001 Lisboa', 'Portugal'
		, '38.737674', '-9.138564'
		, 1;
	ROLLBACK TRANSACTION
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION
END CATCH
GO

-- Create Tertulia Events
-- TEST 007
BEGIN TRANSACTION
BEGIN TRY
	DECLARE @userId INTEGER, @tertulia INTEGER;
	SET @UserId = dbo.fnGetUserId_byAlias('GGLabs');
	EXEC sp_insertTertulia_MonthlyW 
		N'Terças Ggl', N''         -- [name], [subject]
		, @UserId                  -- [userId]
		, N'tuesday', 0 , 0 , 0    -- [weekDay], [weekNr], [fromStart], [skip]
		, 'Varsailles - Técnico', N'Avenida Rovisco Pais 1', N'1049-001 Lisboa', 'Portugal' -- [locationName], [locationAddress], [locationZip], [locationCountry]                     --
		, '38.737674', '-9.138564' -- [locationLatitude], [locationLongitude]
		, 1;                       -- [isPrivate]
	SELECT @tertulia = tr_id FROM Tertulias WHERE tr_name = N'Terças Ggl';
	INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, lo_tertulia) VALUES
		(N'Lisboa Racket Center', N'Rua Alferes Malheiro', N'1700 Lisboa', 'Portugal', '38.758372', '-9.134471', @tertulia);
	EXEC dbo.sp_createEvent 'Terças Ggl', 'Lisboa Racket Center', '20160523 13:00:00';
	EXEC dbo.sp_createEventDefaultLocation 'Terças Ggl', '20160904 13:00:00';
	ROLLBACK TRANSACTION
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION
END CATCH
GO

-- Create Tertulia Items inventory
-- Test 008
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
-- TEST 009
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
-- TEST 010
DECLARE @templateId INTEGER, @tertuliaId INTEGER;
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Escolinha 72-77';
SET @templateId = dbo.fnGetTemplate_byTertuliaId(@tertuliaId, 'Snacks');
INSERT INTO TemplatesCat (tc_template, tc_item, tc_quantity) VALUES 
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Pão cereais (500g)'), 2),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Fiambre (500g)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Queijo Flamengo (500g)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Pacotes Batata Frita (500g)'), 4),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Sortido de frutos secos (200g)'), 6),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Pacote de bolacha maria'), 2);
SET @templateId = dbo.fnGetTemplate_byTertuliaId(@tertuliaId, 'Drinks');
INSERT INTO TemplatesCat (tc_template, tc_item, tc_quantity) VALUES 
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Coca-Cola (1lt)'), 2),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Sumol laranja (1lt)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Cerveja (1lt)'), 4),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Coca-Cola em lata (1)'), 6),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Água Tónica (1lt)'), 2);
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Tertulia do Tejo';
SET @templateId = dbo.fnGetTemplate_byTertuliaId(@tertuliaId, 'Drinks');
INSERT INTO TemplatesCat (tc_template, tc_item, tc_quantity) VALUES 
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Cerveja (1lt)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Vinho Verde Branco (75cl)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Vinho Tinto Frutado (75cl)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Água Tónica (1lt)'), 2),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Gin (75cl)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Copos de vidro'), 4),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Guardanapos de papel (200g)'), 1);
GO

-- Fill Event Checklist with Tertulia Template
-- TEST 011
EXEC sp_buildEventsItems 'Tertulia do Tejo', '2016-09-04 13:00:00', 'Drinks';
GO

-- Commit to handle items to a Tertulia event
-- TEST 012
DECLARE @commitment INTEGER, @itemName VARCHAR(40);
SET @itemName = 'Copos de vidro';
EXEC @commitment = sp_assignChecklistItems 'GGLabs', 'Tertulia do Tejo', '2016-09-04 13:00:00', @itemName, 2;
PRINT 'Commitment for ' + @itemName + ': ' + CAST(@commitment AS VARCHAR);
SET @itemName = 'Cerveja (1lt)';
EXEC @commitment = sp_assignChecklistItems 'GGLabs', 'Tertulia do Tejo', '2016-09-04 13:00:00', @itemName, 2;
PRINT 'Commitment for ' + @itemName + ': ' + CAST(@commitment AS VARCHAR);
GO

-- Post a message in a Tertulia
-- TEST 013
EXEC sp_postNotification_byAlias 'GGLabs', 'Tertulia do Tejo', 'Announcement', 'My test 0post to a tertulia';
GO
