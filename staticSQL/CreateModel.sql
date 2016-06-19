-------------------------------------------------------------------------------
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
IF OBJECT_ID(N'dbo.spSetEnumI') IS NOT NULL DROP PROCEDURE spSetEnumI;
IF OBJECT_ID(N'dbo.spSetEnumS') IS NOT NULL DROP PROCEDURE spSetEnumS;
GO

IF OBJECT_ID(N'dbo.spAcceptInvitation') IS NOT NULL DROP PROCEDURE spAcceptInvitation;
IF OBJECT_ID(N'dbo.spInvite') IS NOT NULL DROP PROCEDURE spInvite;
IF OBJECT_ID(N'dbo.fnGetTemplate_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetTemplate_byTertuliaId;
IF OBJECT_ID(N'dbo.fnGetUserId_byAlias') IS NOT NULL DROP FUNCTION fnGetUserId_byAlias;
IF OBJECT_ID(N'dbo.fnGetUserId_bySid') IS NOT NULL DROP FUNCTION fnGetUserId_bySid;
IF OBJECT_ID(N'dbo.fnGetTertuliaLocation_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetTertuliaLocation_byTertuliaId;
IF OBJECT_ID(N'dbo.fnGetItem_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetItem_byTertuliaId;
IF OBJECT_ID(N'dbo.fnGetEvent_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetEvent_byTertuliaId;
IF OBJECT_ID(N'dbo.sp_getId') IS NOT NULL DROP PROCEDURE sp_getId;
IF OBJECT_ID(N'dbo.sp_getEventIdTertuliaId') IS NOT NULL DROP PROCEDURE sp_getEventIdTertuliaId;
IF OBJECT_ID(N'dbo.sp_createEvent') IS NOT NULL DROP PROCEDURE sp_createEvent;
IF OBJECT_ID(N'dbo.sp_createEventDefaultLocation') IS NOT NULL DROP PROCEDURE sp_createEventDefaultLocation;
IF OBJECT_ID(N'dbo.sp_insertTertulia_MonthlyW_sid') IS NOT NULL DROP PROCEDURE sp_insertTertulia_MonthlyW_sid;
IF OBJECT_ID(N'dbo.sp_insertTertulia_MonthlyW') IS NOT NULL DROP PROCEDURE sp_insertTertulia_MonthlyW;

IF OBJECT_ID(N'dbo.sp_postNotification_byAlias') IS NOT NULL DROP PROCEDURE sp_postNotification_byAlias;
IF OBJECT_ID(N'dbo.sp_postNotification') IS NOT NULL DROP PROCEDURE sp_postNotification;
IF OBJECT_ID(N'dbo.sp_buildEventsItems') IS NOT NULL DROP PROCEDURE sp_buildEventsItems;
IF OBJECT_ID(N'dbo.sp_assignChecklistItems') IS NOT NULL DROP PROCEDURE sp_assignChecklistItems;
GO

IF OBJECT_ID(N'dbo.Invitations') IS NOT NULL DROP TABLE Invitations;
IF OBJECT_ID(N'dbo.fnCountOpenInvitations') IS NOT NULL DROP FUNCTION fnCountOpenInvitations;
IF OBJECT_ID(N'dbo.ReadNotifications') IS NOT NULL DROP TABLE ReadNotifications;
IF OBJECT_ID(N'dbo.Notifications') IS NOT NULL DROP TABLE Notifications;
IF OBJECT_ID(N'dbo.Contributions') IS NOT NULL DROP TABLE Contributions;
IF OBJECT_ID(N'dbo.EventsItems') IS NOT NULL DROP TABLE EventsItems;
IF OBJECT_ID(N'dbo.QuantifiedItems') IS NOT NULL DROP TABLE QuantifiedItems;
IF OBJECT_ID(N'dbo.Templates') IS NOT NULL DROP TABLE Templates;
IF OBJECT_ID(N'dbo.Items') IS NOT NULL DROP TABLE Items;
IF OBJECT_ID(N'dbo.Events') IS NOT NULL DROP TABLE Events;
IF OBJECT_ID(N'dbo.Members') IS NOT NULL DROP TABLE Members;
IF OBJECT_ID(N'dbo.Users') IS NOT NULL DROP TABLE Users;
DECLARE @constName VARCHAR(255);
SELECT @constName = CONSTRAINT_NAME FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS WHERE CONSTRAINT_NAME='fk_location_tertulia';
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
	, nv_description VARCHAR(80)
	, nv_value INTEGER DEFAULT 0
	, CONSTRAINT un_enumvalue_name UNIQUE (nv_type, nv_name)
	, CONSTRAINT fk_enumvalue_type FOREIGN KEY (nv_type) REFERENCES EnumTypes(nt_id)
);
GO

CREATE TABLE Schedules(
	sc_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, sc_type INTEGER NOT NULL
	, CONSTRAINT fk_schedule_type FOREIGN KEY (sc_type) REFERENCES EnumValues(nv_id)
);
GO

CREATE TABLE Weekly(
	wk_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, wk_schedule INTEGER NOT NULL
	, wk_dow INTEGER NOT NULL
	, wk_skip INTEGER NOT NULL DEFAULT 0
	, CONSTRAINT fk_weekly_schedule FOREIGN KEY (wk_schedule) REFERENCES Schedules(sc_id)
	, CONSTRAINT fk_weekly_dow FOREIGN KEY (wk_dow) REFERENCES EnumValues(nv_id)
);
GO

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

CREATE TABLE YearlyD(
	yd_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, yd_schedule INTEGER NOT NULL
	, yd_doy INTEGER NOT NULL DEFAULT 1
	, yd_is_fromend BIT NOT NULL DEFAULT 0
	, yd_skip INTEGER NOT NULL DEFAULT 0
	, CONSTRAINT fk_yearlyd_schedule FOREIGN KEY (yd_schedule) REFERENCES Schedules(sc_id)
);
GO

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

CREATE TABLE Locations(
	lo_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, lo_name VARCHAR(40) NOT NULL
	, lo_address VARCHAR(80)
	, lo_zip VARCHAR(10)
	, lo_city VARCHAR(40)
	, lo_country VARCHAR(20)
	, lo_latitude VARCHAR(12)
	, lo_longitude VARCHAR(12)
	, lo_tertulia INTEGER NOT NULL
 	, CONSTRAINT un_location_nt UNIQUE (lo_name, lo_tertulia)
	, CONSTRAINT un_location_ntll UNIQUE (lo_name, lo_tertulia, lo_latitude, lo_longitude)
--	, CONSTRAINT fk_location_tertulia FOREIGN KEY (lo_tertulia) REFERENCES Tertulias(tr_id)
);
GO

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

CREATE TABLE Items(
	it_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, it_name VARCHAR(40) NOT NULL
	, it_tertulia INTEGER NOT NULL
	, CONSTRAINT un_items_tnsu UNIQUE (it_tertulia, it_name)
	, CONSTRAINT fk_items_tertulia FOREIGN KEY (it_tertulia) REFERENCES Tertulias(tr_id)
);
GO

CREATE TABLE Templates(
	tp_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, tp_name VARCHAR(40) NOT NULL
	, tp_tertulia INTEGER NOT NULL
	, CONSTRAINT un_templates_name UNIQUE (tp_tertulia, tp_name)
	, CONSTRAINT fk_templates_tertulia FOREIGN KEY (tp_tertulia) REFERENCES Tertulias(tr_id)
);
GO

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

CREATE TABLE ReadNotifications(
	rn_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, rn_user INTEGER NOT NULL
	, rn_notification INTEGER NOT NULL
	, CONSTRAINT un_readnotifications_un UNIQUE (rn_user, rn_notification)
	, CONSTRAINT fk_readnotifications_user FOREIGN KEY (rn_user) REFERENCES Users(us_id)
	, CONSTRAINT fk_readnotifications_notification FOREIGN KEY (rn_notification) REFERENCES Notifications(no_id)
);
GO

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

CREATE PROCEDURE spSetEnumI @enumtype VARCHAR(20), @name VARCHAR(20), @value INTEGER
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

CREATE PROCEDURE spSetEnumS @enumtype VARCHAR(20), @name VARCHAR(20), @description VARCHAR(80)
AS 
BEGIN
	DECLARE @id INTEGER;
	IF NOT EXISTS (SELECT nt_id FROM EnumTypes WHERE nt_name = @enumtype)
	BEGIN
		INSERT INTO EnumTypes (nt_name) VALUES (@enumtype);
		SET @id = SCOPE_IDENTITY();
	END
	ELSE SELECT @id = nt_id FROM EnumTypes WHERE nt_name = @enumtype;
	INSERT INTO EnumValues (nv_type, nv_name, nv_description) VALUES (@id, @name, @description);
END;
GO

-- TODO: CHECK TERTULIAS
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

CREATE FUNCTION fnGetUserId_byAlias(@alias VARCHAR(40))
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = us_id FROM Users WHERE us_alias = @alias;
	RETURN @id;
END;
GO

CREATE FUNCTION fnGetUserId_bySid(@sid VARCHAR(40))
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = us_id FROM Users WHERE us_sid = @sid;
	RETURN @id;
END;
GO

CREATE FUNCTION fnGetTertuliaLocation_byTertuliaId(@tertuliaId INTEGER)
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = tr_location FROM Tertulias WHERE tr_id = @tertuliaId AND tr_is_cancelled = 0;
	RETURN @id;
END;
GO

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
CREATE PROCEDURE sp_insertTertulia_MonthlyW
	@name VARCHAR(40), @subject VARCHAR(80), 
	@userId INTEGER, 
	@weekDay VARCHAR(20), @weekNr INTEGER, 
	@fromStart BIT, @skip INTEGER, 
	@locationName VARCHAR(40),
	@locationAddress VARCHAR(80),
	@locationZip VARCHAR(40),
	@locationCity VARCHAR(40),
	@locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12),
	@locationLongitude VARCHAR(12),
	@isPrivate INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_insertTertulia_MonthlyW
BEGIN TRY
	DECLARE @scheduleType INTEGER, @location INTEGER, @schedule INTEGER, @tertulia INTEGER, @owner INTEGER, @dow INTEGER;

	SET @scheduleType = dbo.fnGetEnum('Schedule', 'MonthlyW');
	EXEC @location = dbo.sp_getId 'lo', 'Locations', 'Dummy';

	SET @dow = dbo.fnGetEnum('WeekDays', @weekDay);

	INSERT INTO Schedules (sc_type) VALUES (@scheduleType);
	SET @schedule = SCOPE_IDENTITY();

	INSERT INTO MonthlyW (mw_schedule, mw_dow, mw_weeknr, mw_is_fromstart, mw_skip) 
	VALUES (@schedule, @dow, @weekNr, @fromStart, @skip);

    INSERT INTO Tertulias (tr_name, tr_subject, tr_location, tr_schedule, tr_is_private) 
    VALUES (@name, @subject, @location, @schedule, @isPrivate);
    SET @tertulia = SCOPE_IDENTITY();

    INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_city, lo_country, lo_latitude, lo_longitude, lo_tertulia)
    VALUES (@locationName, @locationAddress, @locationZip, @locationCity, @locationCountry, @locationLatitude, @locationLongitude, @tertulia);
    SET @location = SCOPE_IDENTITY();

    UPDATE Tertulias SET tr_location = @location WHERE tr_id = @tertulia;

    SET @owner = dbo.fnGetEnum('Roles', 'owner');
	INSERT INTO Members (mb_tertulia, mb_user, mb_role) VALUES (@tertulia, @userId, @owner);
	COMMIT TRANSACTION tran_sp_insertTertulia_MonthlyW
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_insertTertulia_MonthlyW
END CATCH
GO

CREATE PROCEDURE sp_insertTertulia_MonthlyW_sid
	@name VARCHAR(40), @subject VARCHAR(80), 
	@userSid INTEGER, 
	@weekDay VARCHAR(20), @weekNr INTEGER, 
	@fromStart BIT, @skip INTEGER, 
	@locationName VARCHAR(40),
	@locationAddress VARCHAR(80),
	@locationZip VARCHAR(40),
	@locationCity VARCHAR(40),
	@locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12),
	@locationLongitude VARCHAR(12),
	@isPrivate INTEGER
AS
BEGIN
	DECLARE @userId INTEGER;
	SET @userId = dbo.fnGetUserId_bySid(@userSid);

	EXEC sp_insertTertulia_MonthlyW @name, @subject,
		@userSid,
		@weekDay, @weekNr, @fromStart, @skip,
		@locationName, @locationAddress, @locationZip, @locationCity, @locationCountry,
		@locationLatitude, @locationLongitude,
		@isPrivate
END
GO

-- TODO: CHECK TERTULIAS
CREATE PROCEDURE sp_createEvent
	@tertuliaName VARCHAR(40), 
	@eventLocation VARCHAR(40),
	@eventDate DATETIME
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_createEvent
BEGIN TRY
	DECLARE @tertulia INTEGER, @location INTEGER;
	EXEC @tertulia = dbo.sp_getId 'tr', 'Tertulias', @tertuliaName;
	EXEC @location = dbo.sp_getId 'lo', 'Locations', @eventLocation;
	INSERT INTO Events (ev_tertulia, ev_location, ev_targetDate) VALUES (@tertulia, @location, @eventDate);
	COMMIT TRANSACTION tran_sp_createEvent
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_createEvent
END CATCH
GO

CREATE PROCEDURE sp_createEventDefaultLocation
	@tertuliaName VARCHAR(40), 
	@eventDate DATETIME
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_createEventDfltLoc
BEGIN TRY
	DECLARE @tertuliaId INTEGER, @locationId INTEGER;
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', @tertuliaName;
	SET @locationId = dbo.fnGetTertuliaLocation_byTertuliaId(@tertuliaId);
	INSERT INTO Events (ev_tertulia, ev_location, ev_targetDate) VALUES (@tertuliaId, @locationId, @eventDate);
	COMMIT TRANSACTION tran_sp_createEventDfltLoc
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_createEventDfltLoc
END CATCH
GO

CREATE PROCEDURE sp_buildEventsItems
	@tertuliaName VARCHAR(40), 
	@eventDate DATETIME, 
	@templateName VARCHAR(40)
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_buildEventsItems
BEGIN TRY
	DECLARE @event INTEGER, @tertulia INTEGER, @template INTEGER;
	EXEC dbo.sp_getEventIdTertuliaId @tertulianame, @eventDate, @event OUTPUT, @tertulia OUTPUT;
	SET @template = dbo.fnGetTemplate_byTertuliaId(@tertulia, @templateName);
	DECLARE _cursor CURSOR FOR SELECT qi_item, qi_quantity FROM QuantifiedItems WHERE qi_template = @template;
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
	COMMIT TRANSACTION tran_sp_buildEventsItems
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_buildEventsItems
END CATCH
GO

-- Commit Event Checklist item to user
CREATE PROCEDURE sp_assignChecklistItems
	@userAlias VARCHAR(40), 
	@tertulianame VARCHAR(40), 
	@eventDate DATETIME, 
	@itemName VARCHAR(40), 
	@itemQuantity INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_assignChecklistItems
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
	COMMIT TRANSACTION tran_sp_assignChecklistItems;
	RETURN @itemQuantity;
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_assignChecklistItems
END CATCH
GO

CREATE PROCEDURE sp_postNotification
	@userId INTEGER,
	@tertuliaName VARCHAR(40), 
	@typeName VARCHAR(40), 
	@message VARCHAR(40)
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_postNotification
BEGIN TRY
	DECLARE @tertulia INTEGER, @tag INTEGER;
	EXEC @tertulia = dbo.sp_getId 'tr', 'Tertulias', @tertuliaName;
	SET @tag = dbo.fnGetEnum('Tags', 'announcements');
	INSERT INTO Notifications (no_tertulia, no_user, no_tag, no_message) 
	VALUES (@tertulia, @userId, @tag, @message);
	COMMIT TRANSACTION tran_sp_postNotification
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_postNotification
END CATCH
GO

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
BEGIN TRANSACTION tran_spAcceptInvitation
BEGIN TRY
	DECLARE @tertulia INTEGER, @role INTEGER, @email_i VARCHAR(40), @email_u VARCHAR(40);
	SELECT @tertulia = in_tertulia, @email_i = in_email FROM Invitations 
		WHERE in_key = @token AND in_is_acknowledged = 0;
	SELECT @email_u = us_email FROM Users WHERE us_id = @userId;
	IF @email_i <> @email_u
	BEGIN
		ROLLBACK TRANSACTION tran_spAcceptInvitation
		RETURN -1;
	END
	SET @role = dbo.fnGetEnum('Roles', 'owner');
	INSERT INTO Members (mb_tertulia, mb_user, mb_role) VALUES (@tertulia, @userId, @role);
	UPDATE Invitations SET in_is_acknowledged = 1 WHERE in_key = @token;
	COMMIT TRANSACTION tran_spAcceptInvitation
	RETURN @token;
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_spAcceptInvitation
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
EXEC spSetEnumI N'Dummy', N'dummy', 0;

-- WeekDays
EXEC spSetEnumI N'WeekDays', N'sunday',    1;
EXEC spSetEnumI N'WeekDays', N'monday',    2;
EXEC spSetEnumI N'WeekDays', N'tuesday',   3; 
EXEC spSetEnumI N'WeekDays', N'wednesday', 4;
EXEC spSetEnumI N'WeekDays', N'thursday',  5;
EXEC spSetEnumI N'WeekDays', N'friday',    6;
EXEC spSetEnumI N'WeekDays', N'saturday',  7;
GO

-- MonthsNames
EXEC spSetEnumI N'MonthNames', N'january',    1;
EXEC spSetEnumI N'MonthNames', N'february',   2;
EXEC spSetEnumI N'MonthNames', N'march',      3;
EXEC spSetEnumI N'MonthNames', N'april',      4;
EXEC spSetEnumI N'MonthNames', N'may',        5;
EXEC spSetEnumI N'MonthNames', N'june',       6;
EXEC spSetEnumI N'MonthNames', N'july',       7;
EXEC spSetEnumI N'MonthNames', N'august',     8;
EXEC spSetEnumI N'MonthNames', N'september',  9;
EXEC spSetEnumI N'MonthNames', N'october',   10;
EXEC spSetEnumI N'MonthNames', N'november',  11;
EXEC spSetEnumI N'MonthNames', N'december',  12;
GO

-- ScheduleTypes
EXEC spSetEnumS N'Schedule', N'Weekly',   N'Scheduled weekly on a day of the week.'; -- Events with a weekly scheduling on a week day.
EXEC spSetEnumS N'Schedule', N'MonthlyD', N'Scheduled monthly on a day of the month.'; -- Events with a monthly scheduling on a month day.
EXEC spSetEnumS N'Schedule', N'MonthlyW', N'Scheduled monthly on a day of the week of a week of the month.'; -- Events with a monthly scheduling on a week day of a week of the month.
EXEC spSetEnumS N'Schedule', N'YearlyD',  N'Scheduled yearly on a day of the year.'; -- Events with a yearly scheduling on a year day.
EXEC spSetEnumS N'Schedule', N'YearlyW',  N'Scheduled yearly on a day of the week of a week of the year.'; -- Events with a yearly scheduling on a week day of a week of the year.
EXEC spSetEnumS N'Schedule', N'YearlyM',  N'Scheduled yearly on a day of the month of a month of the year.'; -- Events with a yearly scheduling on a month day of a month of the year.
GO

-- Roles
EXEC spSetEnumI N'Roles', N'owner',   0;
EXEC spSetEnumI N'Roles', N'manager', 0;
EXEC spSetEnumI N'Roles', N'member',  0;
GO

-- Tags
EXEC spSetEnumI N'Tags', N'announcements', 0;
GO

-- Dummy Data
DECLARE @dummy INTEGER, @schedule INTEGER, @location INTEGER, @tertulia INTEGER;

SET @dummy = dbo.fnGetEnum('Dummy', 'dummy');
INSERT INTO Schedules (sc_type) VALUES (@dummy);
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
