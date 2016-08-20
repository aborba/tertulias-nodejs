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
 	rb - RBAC0
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
IF OBJECT_ID(N'dbo.fnGetEnumByVal') IS NOT NULL DROP FUNCTION fnGetEnumByVal;
IF OBJECT_ID(N'dbo.spSetEnumI') IS NOT NULL DROP PROCEDURE spSetEnumI;
IF OBJECT_ID(N'dbo.spSetEnumS') IS NOT NULL DROP PROCEDURE spSetEnumS;
GO

IF OBJECT_ID(N'sp_subscribePublicTertulia') IS NOT NULL DROP PROCEDURE sp_subscribePublicTertulia;
IF OBJECT_ID(N'sp_unsubscribePublicTertulia') IS NOT NULL DROP PROCEDURE sp_unsubscribePublicTertulia;
IF OBJECT_ID(N'dbo.sp_acceptInvitationToTertulia') IS NOT NULL DROP PROCEDURE sp_acceptInvitationToTertulia;
IF OBJECT_ID(N'dbo.sp_createInvitationVouchers') IS NOT NULL DROP PROCEDURE sp_createInvitationVouchers;
IF OBJECT_ID(N'dbo.fnGetTemplate_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetTemplate_byTertuliaId;
IF OBJECT_ID(N'dbo.fnGetUserId_byAlias') IS NOT NULL DROP FUNCTION fnGetUserId_byAlias;
IF OBJECT_ID(N'dbo.fnGetUserSid_byAlias') IS NOT NULL DROP FUNCTION fnGetUserSid_byAlias;
IF OBJECT_ID(N'dbo.fnGetUserId_bySid') IS NOT NULL DROP FUNCTION fnGetUserId_bySid;
IF OBJECT_ID(N'dbo.fnGetTertuliaLocation_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetTertuliaLocation_byTertuliaId;
IF OBJECT_ID(N'dbo.fnGetItem_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetItem_byTertuliaId;
IF OBJECT_ID(N'dbo.fnGetEvent_byTertuliaId') IS NOT NULL DROP FUNCTION fnGetEvent_byTertuliaId;
IF OBJECT_ID(N'dbo.fnIsPublic') IS NOT NULL DROP FUNCTION fnIsPublic;
IF OBJECT_ID(N'dbo.sp_getId') IS NOT NULL DROP PROCEDURE sp_getId;
IF OBJECT_ID(N'dbo.sp_getEventIdTertuliaId') IS NOT NULL DROP PROCEDURE sp_getEventIdTertuliaId;
IF OBJECT_ID(N'dbo.sp_createEvent') IS NOT NULL DROP PROCEDURE sp_createEvent;
IF OBJECT_ID(N'dbo.sp_createEventDefaultLocation') IS NOT NULL DROP PROCEDURE sp_createEventDefaultLocation;
IF OBJECT_ID(N'dbo.sp_insertTertulia_Weekly_sid') IS NOT NULL DROP PROCEDURE sp_insertTertulia_Weekly_sid;
IF OBJECT_ID(N'dbo.sp_insertTertulia_Weekly') IS NOT NULL DROP PROCEDURE sp_insertTertulia_Weekly;
IF OBJECT_ID(N'dbo.sp_insertTertulia_Monthly') IS NOT NULL DROP PROCEDURE sp_insertTertulia_Monthly;
IF OBJECT_ID(N'dbo.sp_insertTertulia_Monthly_sid') IS NOT NULL DROP PROCEDURE sp_insertTertulia_Monthly_sid;
IF OBJECT_ID(N'dbo.sp_insertTertulia_MonthlyW') IS NOT NULL DROP PROCEDURE sp_insertTertulia_MonthlyW;
IF OBJECT_ID(N'dbo.sp_insertTertulia_MonthlyW_sid') IS NOT NULL DROP PROCEDURE sp_insertTertulia_MonthlyW_sid;
IF OBJECT_ID(N'dbo.sp_insertTertulia_Yearly') IS NOT NULL DROP PROCEDURE sp_insertTertulia_Yearly;
IF OBJECT_ID(N'dbo.sp_insertTertulia_Yearly_sid') IS NOT NULL DROP PROCEDURE sp_insertTertulia_Yearly_sid;
IF OBJECT_ID(N'dbo.sp_insertTertulia_YearlyW') IS NOT NULL DROP PROCEDURE sp_insertTertulia_YearlyW;
IF OBJECT_ID(N'dbo.sp_insertTertulia_YearlyW_sid') IS NOT NULL DROP PROCEDURE sp_insertTertulia_YearlyW_sid;
IF OBJECT_ID(N'dbo.sp_updateTertulia_Weekly_sid') IS NOT NULL DROP PROCEDURE sp_updateTertulia_Weekly_sid;
IF OBJECT_ID(N'dbo.sp_updateTertulia_Weekly') IS NOT NULL DROP PROCEDURE sp_updateTertulia_Weekly;
IF OBJECT_ID(N'dbo.sp_updateTertulia_Monthly') IS NOT NULL DROP PROCEDURE sp_updateTertulia_Monthly;
IF OBJECT_ID(N'dbo.sp_updateTertulia_Monthly_sid') IS NOT NULL DROP PROCEDURE sp_updateTertulia_Monthly_sid;
IF OBJECT_ID(N'dbo.sp_updateTertulia_MonthlyW') IS NOT NULL DROP PROCEDURE sp_updateTertulia_MonthlyW;
IF OBJECT_ID(N'dbo.sp_updateTertulia_MonthlyW_sid') IS NOT NULL DROP PROCEDURE sp_updateTertulia_MonthlyW_sid;
IF OBJECT_ID(N'dbo.sp_updateTertulia_Yearly') IS NOT NULL DROP PROCEDURE sp_updateTertulia_Yearly;
IF OBJECT_ID(N'dbo.sp_updateTertulia_Yearly_sid') IS NOT NULL DROP PROCEDURE sp_updateTertulia_Yearly_sid;
IF OBJECT_ID(N'dbo.sp_updateTertulia_YearlyW') IS NOT NULL DROP PROCEDURE sp_updateTertulia_YearlyW;
IF OBJECT_ID(N'dbo.sp_updateTertulia_YearlyW_sid') IS NOT NULL DROP PROCEDURE sp_updateTertulia_YearlyW_sid;

IF OBJECT_ID(N'dbo.sp_getTertuliaMembers') IS NOT NULL DROP PROCEDURE sp_getTertuliaMembers;
IF OBJECT_ID(N'dbo.sp_postNotification') IS NOT NULL DROP PROCEDURE sp_postNotification;
IF OBJECT_ID(N'dbo.sp_buildEventsItems') IS NOT NULL DROP PROCEDURE sp_buildEventsItems;
IF OBJECT_ID(N'dbo.sp_assignChecklistItems') IS NOT NULL DROP PROCEDURE sp_assignChecklistItems;
IF OBJECT_ID(N'dbo.fnGetAuthorizationForActionBySid') IS NOT NULL DROP FUNCTION fnGetAuthorizationForActionBySid;
IF OBJECT_ID(N'dbo.fnGetAuthorizationForAction') IS NOT NULL DROP FUNCTION fnGetAuthorizationForAction;
IF OBJECT_ID(N'dbo.fnGetAuthorization') IS NOT NULL DROP FUNCTION fnGetAuthorization;
IF OBJECT_ID(N'dbo.fnGetUserRole') IS NOT NULL DROP FUNCTION fnGetUserRole;
GO

IF OBJECT_ID(N'dbo.trLogUserInsert') IS NOT NULL DROP TRIGGER trLogUserInsert;

IF OBJECT_ID(N'dbo.RBAC0') IS NOT NULL DROP TABLE RBAC0;
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
IF OBJECT_ID(N'dbo.Logs') IS NOT NULL DROP TABLE Logs;
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

CREATE TABLE RBAC0(
	rb_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, rb_role INTEGER NOT NULL
	, rb_action INTEGER NOT NULL
	, CONSTRAINT un_rbac0_ra UNIQUE (rb_role, rb_action)
	, CONSTRAINT fk_rbac0_role FOREIGN KEY (rb_role) REFERENCES EnumValues(nv_id)
	, CONSTRAINT fk_rbac0_action FOREIGN KEY (rb_action) REFERENCES EnumValues(nv_id)
);
GO

CREATE TABLE Logs(
	lg_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, lg_type INTEGER NOT NULL
	, lg_refid INTEGER NOT NULL
	, lg_timestamp DATETIME NOT NULL DEFAULT GETDATE()
	, CONSTRAINT fk_reference_type FOREIGN KEY (lg_type) REFERENCES EnumTypes(nt_id)
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
	, md_is_fromstart BIT NOT NULL DEFAULT 1
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
	, yd_is_fromstart BIT NOT NULL DEFAULT 1
	, yd_skip INTEGER NOT NULL DEFAULT 0
	, CONSTRAINT fk_yearlyd_schedule FOREIGN KEY (yd_schedule) REFERENCES Schedules(sc_id)
);
GO

CREATE TABLE YearlyW(
	yw_id INTEGER IDENTITY(1,1) PRIMARY KEY
	, yw_schedule INTEGER NOT NULL
	, yw_dow INTEGER NOT NULL DEFAULT 1
	, yw_weeknr INTEGER NOT NULL DEFAULT 0
	, yw_is_fromstart BIT NOT NULL DEFAULT 1
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
	, ym_is_fromstart BIT NOT NULL DEFAULT 1
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
	, lo_latitude FLOAT
	, lo_longitude FLOAT
	, lo_geography AS geography::STGeomFromText('POINT(' + convert(varchar(12),lo_latitude) + ' ' + convert(varchar(12),lo_longitude) + ')', 4326)
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
	, tr_is_private BIT NOT NULL DEFAULT 1
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
	, in_batch VARCHAR(36) NOT NULL -- Ex: E5FD8BEF-94EB-4BF4-B85A-FAA4B1B5FE33
	, in_tertulia INTEGER
	, in_user INTEGER NOT NULL
	, in_is_acknowledged BIT NOT NULL DEFAULT 0
	, in_invitationDate DATETIME DEFAULT GETDATE()
	, CONSTRAINT un_invitations_key UNIQUE (in_key)
	, CONSTRAINT un_invitations_ke UNIQUE (in_key, in_user)
	, CONSTRAINT fk_invitations_tertulia FOREIGN KEY (in_tertulia) REFERENCES Tertulias(tr_id)
	, CONSTRAINT fk_invitations_user FOREIGN KEY (in_user) REFERENCES Users(us_id)
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
CREATE FUNCTION fnCountOpenInvitations(@tertulia INTEGER)
RETURNS INTEGER
AS 
BEGIN
	DECLARE @cnt INTEGER;
	SELECT @cnt = COUNT(in_id) FROM Invitations WHERE in_tertulia = @tertulia AND in_is_acknowledged = 0;
	RETURN @cnt;
END;
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

CREATE FUNCTION fnGetEnumByVal(@enumtype VARCHAR(20), @value INTEGER)
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = nv_id FROM EnumTypes INNER JOIN EnumValues ON nv_type = nt_id 
	WHERE nt_name = @enumtype AND nv_value = @value;
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

CREATE FUNCTION fnGetUserRole(@user INTEGER, @tertulia INTEGER)
RETURNS INTEGER
AS 
BEGIN
	DECLARE @role INTEGER;
	SELECT @role = mb_role
	FROM Members
	INNER JOIN Tertulias ON mb_tertulia = tr_id
	WHERE tr_is_cancelled = 0 AND mb_user = @user AND mb_tertulia = @tertulia;
	return @role;
END;
GO

CREATE FUNCTION fnGetAuthorization(@user INTEGER, @tertulia INTEGER, @action INTEGER)
RETURNS BIT
AS 
BEGIN
	DECLARE @result BIT
	SELECT @result = CASE WHEN EXISTS (
		SELECT rb_id FROM Rbac0
		INNER JOIN EnumValues ON rb_action = nv_id
		WHERE rb_role = (
			SELECT mb_role FROM tertulias
				INNER JOIN members ON mb_tertulia = tr_id
				INNER JOIN users ON mb_user = us_id
				WHERE tr_is_cancelled = 0
					AND tr_id = @tertulia
					AND us_id = @user
			)
		AND nv_id = @action
	) THEN CAST (1 AS BIT)
	ELSE CAST (0 AS BIT)
	END;
	return @result;
END;
GO

CREATE FUNCTION fnGetAuthorizationForAction(@user INTEGER, @tertulia INTEGER, @actionName VARCHAR(40))
RETURNS BIT
AS 
BEGIN
	DECLARE @action INTEGER;
	SET @action = dbo.fnGetEnum('Actions', @actionName);
	RETURN dbo.fnGetAuthorization(@user, @tertulia, @action);
END;
GO

CREATE FUNCTION fnGetAuthorizationForActionBySid(@userSid VARCHAR(40), @tertulia INTEGER, @actionName VARCHAR(40))
RETURNS BIT
AS 
BEGIN
	DECLARE @user INTEGER;
	SET @user = dbo.fnGetUserId_bySid(@userSid);
	RETURN dbo.fnGetAuthorizationForAction(@user, @tertulia, @actionName);
END;
GO

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

CREATE FUNCTION fnGetUserSid_byAlias(@alias VARCHAR(40))
RETURNS VARCHAR(40)
AS 
BEGIN
	DECLARE @sid VARCHAR(40);
	SELECT @sid = us_sid FROM Users WHERE us_alias = @alias;
	RETURN @sid;
END;
GO

CREATE FUNCTION fnGetUserId_bySid(@userSid VARCHAR(40))
RETURNS INTEGER
AS 
BEGIN
	DECLARE @id INTEGER;
	SELECT @id = us_id FROM Users WHERE us_sid = @userSid;
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

CREATE FUNCTION fnIsPublic(@tertulia INTEGER)
RETURNS INTEGER
AS 
BEGIN
	DECLARE @is_private BIT;
	SELECT @is_private = tr_is_private FROM Tertulias WHERE tr_id = @tertulia;
	RETURN @is_private;
END;
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
CREATE PROCEDURE sp_insertTertulia_Weekly
	@userId INTEGER, 
	@tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80),
	@locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleWeekDay INTEGER, @scheduleSkip INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_insertTertulia_Weekly
BEGIN TRY
	DECLARE @scheduleType INTEGER, @location INTEGER, @schedule INTEGER, @tertulia INTEGER, @owner INTEGER, @dow INTEGER;

	SET @scheduleType = dbo.fnGetEnum('Schedule', 'Weekly');
	EXEC @location = dbo.sp_getId 'lo', 'Locations', 'Dummy';

	SET @dow = dbo.fnGetEnumByVal('WeekDays', @scheduleWeekDay);

	INSERT INTO Schedules (sc_type) VALUES (@scheduleType);
	SET @schedule = SCOPE_IDENTITY();

	INSERT INTO Weekly (wk_schedule, wk_dow, wk_skip) 
	VALUES (@schedule, @dow, @scheduleSkip);

    INSERT INTO Tertulias (tr_name, tr_subject, tr_location, tr_schedule, tr_is_private) 
    VALUES (@tertuliaName, @tertuliaSubject, @location, @schedule, @tertuliaIsPrivate);
    SET @tertulia = SCOPE_IDENTITY();

    INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_city, lo_country, lo_latitude, lo_longitude, lo_tertulia)
    VALUES (@locationName, @locationAddress, @locationZip, @locationCity, @locationCountry, @locationLatitude, @locationLongitude, @tertulia);
    SET @location = SCOPE_IDENTITY();

    UPDATE Tertulias SET tr_location = @location WHERE tr_id = @tertulia;

    SET @owner = dbo.fnGetEnum('Roles', 'owner');
	INSERT INTO Members (mb_tertulia, mb_user, mb_role) VALUES (@tertulia, @userId, @owner);
	COMMIT TRANSACTION tran_sp_insertTertulia_Weekly
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_insertTertulia_Weekly
END CATCH
GO

CREATE PROCEDURE sp_insertTertulia_Weekly_sid
	@userSid VARCHAR(40), 
	@tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80),
	@locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleWeekDay INTEGER, @scheduleSkip INTEGER
AS
BEGIN
	DECLARE @userId INTEGER;
	SET @userId = dbo.fnGetUserId_bySid(@userSid);

	EXEC sp_insertTertulia_Weekly @userId,
		@tertuliaName, @tertuliaSubject, @tertuliaIsPrivate,
		@locationName, @locationAddress, @locationZip, @locationCity, @locationCountry,
		@locationLatitude, @locationLongitude,
		@scheduleWeekDay, @scheduleSkip
END
GO

CREATE PROCEDURE sp_insertTertulia_Monthly
	@userId INTEGER, 
	@tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80),
	@locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleDayNr INTEGER, @scheduleIsFromStart BIT, @scheduleSkip INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_insertTertulia_Monthly
BEGIN TRY
	DECLARE @scheduleType INTEGER, @location INTEGER, @schedule INTEGER, @tertulia INTEGER, @owner INTEGER;

	SET @scheduleType = dbo.fnGetEnum('Schedule', 'MonthlyD');
	EXEC @location = dbo.sp_getId 'lo', 'Locations', 'Dummy';

	INSERT INTO Schedules (sc_type) VALUES (@scheduleType);
	SET @schedule = SCOPE_IDENTITY();

	INSERT INTO MonthlyD (md_schedule, md_dom, md_is_fromstart, md_skip) 
	VALUES (@schedule, @scheduleDayNr, @scheduleIsFromStart, @scheduleSkip);

    INSERT INTO Tertulias (tr_name, tr_subject, tr_location, tr_schedule, tr_is_private) 
    VALUES (@tertuliaName, @tertuliaSubject, @location, @schedule, @tertuliaIsPrivate);
    SET @tertulia = SCOPE_IDENTITY();

    INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_city, lo_country, lo_latitude, lo_longitude, lo_tertulia)
    VALUES (@locationName, @locationAddress, @locationZip, @locationCity, @locationCountry, @locationLatitude, @locationLongitude, @tertulia);
    SET @location = SCOPE_IDENTITY();

    UPDATE Tertulias SET tr_location = @location WHERE tr_id = @tertulia;

    SET @owner = dbo.fnGetEnum('Roles', 'owner');
	INSERT INTO Members (mb_tertulia, mb_user, mb_role) VALUES (@tertulia, @userId, @owner);
	COMMIT TRANSACTION tran_sp_insertTertulia_Monthly
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_insertTertulia_Monthly
END CATCH
GO

CREATE PROCEDURE sp_insertTertulia_Monthly_sid
	@userSid VARCHAR(40), 
	@tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80),
	@locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleDayNr INTEGER, @scheduleIsFromStart BIT, @scheduleSkip INTEGER
AS
BEGIN
	DECLARE @userId INTEGER;
	SET @userId = dbo.fnGetUserId_bySid(@userSid);

	EXEC sp_insertTertulia_Monthly @userId,
		@tertuliaName, @tertuliaSubject, @tertuliaIsPrivate,
		@locationName, @locationAddress, @locationZip, @locationCity, @locationCountry,
		@locationLatitude, @locationLongitude,
		@scheduleDayNr, @scheduleIsFromStart, @scheduleSkip
END
GO

CREATE PROCEDURE sp_insertTertulia_MonthlyW
	@userId INTEGER, 
	@tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80),
	@locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleWeekDay INTEGER, @scheduleWeekNr INTEGER, @scheduleIsFromStart BIT, @scheduleSkip INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_insertTertulia_MonthlyW
BEGIN TRY
	DECLARE @scheduleType INTEGER, @location INTEGER, @schedule INTEGER, @tertulia INTEGER, @owner INTEGER, @dow INTEGER;

	SET @scheduleType = dbo.fnGetEnum('Schedule', 'MonthlyW');
	EXEC @location = dbo.sp_getId 'lo', 'Locations', 'Dummy';

	SET @dow = dbo.fnGetEnumByVal('WeekDays', @scheduleWeekDay);

	INSERT INTO Schedules (sc_type) VALUES (@scheduleType);
	SET @schedule = SCOPE_IDENTITY();

	INSERT INTO MonthlyW (mw_schedule, mw_dow, mw_weeknr, mw_is_fromstart, mw_skip) 
	VALUES (@schedule, @dow, @scheduleWeekNr, @scheduleIsFromStart, @scheduleSkip);

    INSERT INTO Tertulias (tr_name, tr_subject, tr_location, tr_schedule, tr_is_private) 
    VALUES (@tertuliaName, @tertuliaSubject, @location, @schedule, @tertuliaIsPrivate);
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
	@userSid VARCHAR(40), 
	@tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80),
	@locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleWeekDay VARCHAR(20), @scheduleWeekNr INTEGER, @scheduleIsFromStart BIT, @scheduleSkip INTEGER
AS
BEGIN
	DECLARE @userId INTEGER;
	SET @userId = dbo.fnGetUserId_bySid(@userSid);

	EXEC sp_insertTertulia_MonthlyW @userId,
		@tertuliaName, @tertuliaSubject, @tertuliaIsPrivate,
		@locationName, @locationAddress, @locationZip, @locationCity, @locationCountry,
		@locationLatitude, @locationLongitude,
		@scheduleWeekDay, @scheduleWeekNr, @scheduleIsFromStart, @scheduleSkip
END
GO

-- UPDATE

CREATE PROCEDURE sp_updateTertulia_Weekly
	@userId INTEGER, 
	@tertuliaId INTEGER, @tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80), @locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleWeekDay INTEGER, @scheduleSkip INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_updateTertulia_Weekly
BEGIN TRY

	IF (dbo.fnGetAuthorizationForAction(@userId, @tertuliaId, 'UPDATE_TERTULIA') <> 1)
	BEGIN
		ROLLBACK TRANSACTION tran_sp_updateTertulia_Weekly;
		RETURN 1;
	END

	DECLARE @weekly INTEGER, @schedule INTEGER, @newSchedule INTEGER, @checkTertuliaId INTEGER, @dow INTEGER, @locationId INTEGER;

	SELECT @checkTertuliaId = tr_id
	FROM Tertulias INNER JOIN Members ON mb_tertulia = tr_id
	WHERE tr_is_cancelled = 0 AND mb_user = @userId AND tr_id = @tertuliaId;

	IF @checkTertuliaId IS NULL
	BEGIN
		ROLLBACK TRANSACTION tran_sp_updateTertulia_Weekly;
		RETURN 1;
	END

	SELECT @weekly = wk_id
	FROM Tertulias
	INNER JOIN Schedules ON tr_schedule = sc_id
	INNER JOIN Weekly ON wk_schedule = sc_id
	WHERE tr_id = @tertuliaId;

	SET @dow = dbo.fnGetEnumByVal('WeekDays', @scheduleWeekDay);

	IF (@weekly IS NOT NULL)
	BEGIN
		UPDATE Weekly SET wk_dow = @dow, wk_skip = @scheduleSkip WHERE wk_id = @weekly;
	END
	ELSE
	BEGIN
		SELECT @schedule = tr_schedule FROM Tertulias WHERE tr_id = @tertuliaId;
		DELETE FROM MonthlyD WHERE md_schedule = @schedule;
		DELETE FROM MonthlyW WHERE mw_schedule = @schedule;
		-- DELETE FROM YearlyD WHERE yd_schedule = @schedule;
		-- DELETE FROM YearlyW WHERE yw_schedule = @schedule;
		INSERT INTO Weekly (wk_schedule, wk_dow, wk_skip) VALUES (@schedule, @dow, @scheduleSkip);
		SET @newSchedule = dbo.fnGetEnum('Schedule', 'Weekly');
		UPDATE Schedules SET sc_type = @newSchedule WHERE sc_id = @schedule;
	END

	SELECT @locationId = tr_location FROM Tertulias WHERE tr_id = @tertuliaId;

	UPDATE Locations SET
		lo_name = @locationName, 
		lo_address = @locationAddress, lo_zip = @locationZip, lo_city = @locationCity, lo_country = @locationCountry,
		lo_latitude = @locationLatitude, lo_longitude = @locationLongitude
	WHERE lo_id = @locationId;

	UPDATE Tertulias SET tr_name = @tertuliaName, tr_subject = @tertuliaSubject, tr_is_private = @tertuliaIsPrivate
	WHERE tr_id = @tertuliaId;

	COMMIT TRANSACTION tran_sp_updateTertulia_Weekly

END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_updateTertulia_Weekly
END CATCH
GO

CREATE PROCEDURE sp_updateTertulia_Weekly_sid
	@userSid VARCHAR(40), 
	@tertuliaId INTEGER, @tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80), @locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleWeekDay INTEGER, @scheduleSkip INTEGER
AS
BEGIN
	DECLARE @userId INTEGER;
	SET @userId = dbo.fnGetUserId_bySid(@userSid);

	EXEC sp_updateTertulia_Weekly
		@userId,
		@tertuliaId, @tertuliaName, @tertuliaSubject, @tertuliaIsPrivate,
		@locationName, @locationAddress, @locationZip, @locationCity, @locationCountry,
		@locationLatitude, @locationLongitude,
		@scheduleWeekDay, @scheduleSkip
END
GO

CREATE PROCEDURE sp_updateTertulia_Monthly
	@userId INTEGER, 
	@tertuliaId INTEGER, @tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80), @locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleDayNr INTEGER, @scheduleIsFromStart BIT, @scheduleSkip INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_updateTertulia_Monthly
BEGIN TRY

	DECLARE @authorization BIT;
	SET @authorization = dbo.fnGetAuthorizationForAction(@userId, @tertuliaId, 'UPDATE_TERTULIA');
	IF (@authorization <> 1)
	BEGIN
		ROLLBACK TRANSACTION tran_sp_updateTertulia_Monthly;
		RETURN 1;
	END

	DECLARE @monthly INTEGER, @schedule INTEGER, @newSchedule INTEGER, @checkTertuliaId INTEGER, @locationId INTEGER;

	SELECT @checkTertuliaId = tr_id
	FROM Tertulias INNER JOIN Members ON mb_tertulia = tr_id
	WHERE tr_is_cancelled = 0 AND mb_user = @userId AND tr_id = @tertuliaId;

	IF @checkTertuliaId IS NULL
	BEGIN
		ROLLBACK TRANSACTION tran_sp_updateTertulia_Monthly;
		RETURN 1;
	END

	SELECT @monthly = md_id
	FROM Tertulias
	INNER JOIN Schedules ON tr_schedule = sc_id
	INNER JOIN MonthlyD ON md_schedule = sc_id
	WHERE tr_id = @tertuliaId;

	IF (@monthly IS NOT NULL)
	BEGIN
		UPDATE MonthlyD SET md_dom = @scheduleDayNr, md_is_fromstart = @scheduleIsFromStart, md_skip = @scheduleSkip WHERE md_id = @monthly;
	END
	ELSE
	BEGIN
		SELECT @schedule = tr_schedule FROM Tertulias WHERE tr_id = @tertuliaId;
		DELETE FROM Weekly WHERE wk_schedule = @schedule;
		DELETE FROM MonthlyW WHERE mw_schedule = @schedule;
		-- DELETE FROM YearlyD WHERE yd_schedule = @schedule;
		-- DELETE FROM YearlyW WHERE yw_schedule = @schedule;
		INSERT INTO MonthlyD (md_schedule, md_dom, md_is_fromstart, md_skip) VALUES (@schedule, @scheduleDayNr, @scheduleIsFromStart, @scheduleSkip);
		SET @newSchedule = dbo.fnGetEnum('Schedule', 'MonthlyD');
		UPDATE Schedules SET sc_type = @newSchedule WHERE sc_id = @schedule;
	END

	SELECT @locationId = tr_location FROM Tertulias WHERE tr_id = @tertuliaId;

	UPDATE Locations SET
		lo_name = @locationName, 
		lo_address = @locationAddress, lo_zip = @locationZip, lo_city = @locationCity, lo_country = @locationCountry,
		lo_latitude = @locationLatitude, lo_longitude = @locationLongitude
	WHERE lo_id = @locationId;

	UPDATE Tertulias SET tr_name = @tertuliaName, tr_subject = @tertuliaSubject, tr_is_private = @tertuliaIsPrivate
	WHERE tr_id = @tertuliaId;

	COMMIT TRANSACTION tran_sp_updateTertulia_Monthly

END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_updateTertulia_Monthly
END CATCH
GO

CREATE PROCEDURE sp_updateTertulia_Monthly_sid
	@userSid VARCHAR(40), 
	@tertuliaId INTEGER, @tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80), @locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleDayNr INTEGER, @scheduleIsFromStart BIT, @scheduleSkip INTEGER
AS
BEGIN
	DECLARE @userId INTEGER;
	SET @userId = dbo.fnGetUserId_bySid(@userSid);

	EXEC sp_updateTertulia_Monthly
		@userId,
		@tertuliaId, @tertuliaName, @tertuliaSubject, @tertuliaIsPrivate,
		@locationName, @locationAddress, @locationZip, @locationCity, @locationCountry,
		@locationLatitude, @locationLongitude,
		@scheduleDayNr, @scheduleIsFromStart, @scheduleSkip
END
GO

CREATE PROCEDURE sp_updateTertulia_MonthlyW
	@userId INTEGER, 
	@tertuliaId INTEGER, @tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80), @locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleWeekDay INTEGER, @scheduleWeekNr INTEGER, @scheduleIsFromStart BIT, @scheduleSkip INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_updateTertulia_MonthlyW
BEGIN TRY

	IF (dbo.fnGetAuthorizationForAction(@userId, @tertuliaId, 'UPDATE_TERTULIA') <> 1)
	BEGIN
		ROLLBACK TRANSACTION tran_sp_updateTertulia_MonthlyW;
		RETURN 1;
	END

	DECLARE @monthlyw INTEGER, @schedule INTEGER, @newSchedule INTEGER, @checkTertuliaId INTEGER, @dow INTEGER, @locationId INTEGER;

	SELECT @checkTertuliaId = tr_id
	FROM Tertulias INNER JOIN Members ON mb_tertulia = tr_id
	WHERE tr_is_cancelled = 0 AND mb_user = @userId AND tr_id = @tertuliaId;

	IF @checkTertuliaId IS NULL
	BEGIN
		ROLLBACK TRANSACTION tran_sp_updateTertulia_MonthlyW;
		RETURN 1;
	END

	SELECT @monthlyw = mw_id
	FROM Tertulias
	INNER JOIN Schedules ON tr_schedule = sc_id
	INNER JOIN MonthlyW ON mw_schedule = sc_id
	WHERE tr_id = @tertuliaId;

	SET @dow = dbo.fnGetEnumByVal('WeekDays', @scheduleWeekDay);

	IF (@monthlyw IS NOT NULL)
	BEGIN
		UPDATE MonthlyW SET mw_dow = @dow, mw_weeknr = @scheduleWeekNr, mw_is_fromstart = @scheduleIsFromStart, mw_skip = @scheduleSkip WHERE mw_id = @monthlyw;
	END
	ELSE
	BEGIN
		SELECT @schedule = tr_schedule FROM Tertulias WHERE tr_id = @tertuliaId;
		DELETE FROM Weekly WHERE wk_schedule = @schedule;
		DELETE FROM MonthlyD WHERE md_schedule = @schedule;
		-- DELETE FROM YearlyD WHERE yd_schedule = @schedule;
		-- DELETE FROM YearlyW WHERE yw_schedule = @schedule;
		INSERT INTO MonthlyW (mw_schedule, mw_dow, mw_weeknr, mw_is_fromstart, mw_skip) VALUES (@schedule, @dow, @scheduleWeekNr, @scheduleIsFromStart, @scheduleSkip);
		SET @newSchedule = dbo.fnGetEnum('Schedule', 'MonthlyW');
		UPDATE Schedules SET sc_type = @newSchedule WHERE sc_id = @schedule;
	END

	SELECT @locationId = tr_location FROM Tertulias WHERE tr_id = @tertuliaId;

	UPDATE Locations SET
		lo_name = @locationName, 
		lo_address = @locationAddress, lo_zip = @locationZip, lo_city = @locationCity, lo_country = @locationCountry,
		lo_latitude = @locationLatitude, lo_longitude = @locationLongitude
	WHERE lo_id = @locationId;

	UPDATE Tertulias SET tr_name = @tertuliaName, tr_subject = @tertuliaSubject, tr_is_private = @tertuliaIsPrivate
	WHERE tr_id = @tertuliaId;

	COMMIT TRANSACTION tran_sp_updateTertulia_MonthlyW

END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_updateTertulia_MonthlyW
END CATCH
GO

CREATE PROCEDURE sp_updateTertulia_MonthlyW_sid
	@userSid VARCHAR(40), 
	@tertuliaId INTEGER, @tertuliaName VARCHAR(40), @tertuliaSubject VARCHAR(80), @tertuliaIsPrivate BIT,
	@locationName VARCHAR(40), @locationAddress VARCHAR(80), @locationZip VARCHAR(40), @locationCity VARCHAR(40), @locationCountry VARCHAR(40),
	@locationLatitude VARCHAR(12), @locationLongitude VARCHAR(12),
	@scheduleWeekDay INTEGER, @scheduleWeekNr INTEGER, @scheduleIsFromStart BIT, @scheduleSkip INTEGER
AS
BEGIN
	DECLARE @userId INTEGER;
	SET @userId = dbo.fnGetUserId_bySid(@userSid);

	EXEC sp_updateTertulia_MonthlyW
		@userId,
		@tertuliaId, @tertuliaName, @tertuliaSubject, @tertuliaIsPrivate,
		@locationName, @locationAddress, @locationZip, @locationCity, @locationCountry,
		@locationLatitude, @locationLongitude,
		@scheduleWeekDay, @scheduleWeekNr, @scheduleIsFromStart, @scheduleSkip
END
GO

-- TODO: CHECK TERTULIAS
CREATE PROCEDURE sp_createEvent
	@userSid VARCHAR(40),
	@tertulia INTEGER, 
	@eventLocation VARCHAR(40),
	@eventDate DATETIME
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_createEvent
BEGIN TRY

	IF (dbo.fnGetAuthorizationForActionBySid(@userSid, @tertulia, 'CREATE_EVENT') <> 1)
	BEGIN
		ROLLBACK TRANSACTION tran_sp_createEvent;
		RETURN 1;
	END

	DECLARE @location INTEGER;
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
	@userSid VARCHAR(40),
	@tertulia INTEGER, 
	@eventDate DATETIME
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_createEventDfltLoc
BEGIN TRY

	IF (dbo.fnGetAuthorizationForActionBySid(@userSid, @tertulia, 'CREATE_EVENT') <> 1)
	BEGIN
		ROLLBACK TRANSACTION tran_sp_createEventDfltLoc;
		RETURN 1;
	END

	DECLARE @location INTEGER;
	SET @location = dbo.fnGetTertuliaLocation_byTertuliaId(@tertulia);
	INSERT INTO Events (ev_tertulia, ev_location, ev_targetDate) VALUES (@tertulia, @location, @eventDate);
	COMMIT TRANSACTION tran_sp_createEventDfltLoc
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_sp_createEventDfltLoc
END CATCH
GO

CREATE PROCEDURE sp_buildEventsItems
	@userSid VARCHAR(40),
	@tertulia INTEGER, 
	@eventDate DATETIME, 
	@templateName VARCHAR(40)
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_buildEventsItems
BEGIN TRY

	IF (dbo.fnGetAuthorizationForActionBySid(@userSid, @tertulia, 'MANAGE_EVENT') <> 1)
	BEGIN
		ROLLBACK TRANSACTION tran_sp_buildEventsItems;
		RETURN 1;
	END

	DECLARE @event INTEGER, @template INTEGER;
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
	CLOSE _cursor;
	DEALLOCATE _cursor;
	ROLLBACK TRANSACTION tran_sp_buildEventsItems
END CATCH
GO

-- Commit Event Checklist item to user
CREATE PROCEDURE sp_assignChecklistItems
	@userSid VARCHAR(40),
	@tertulia INTEGER, 
	@eventDate DATETIME, 
	@itemName VARCHAR(40), 
	@itemQuantity INTEGER
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_assignChecklistItems
BEGIN TRY
	DECLARE @userId INTEGER, @eventId INTEGER, @itemId INTEGER, @totalQuantity INTEGER, @committedQuantity INTEGER;
	SET @userId = dbo.fnGetUserId_bySid(@userSid);
	SET @eventId = dbo.fnGetEvent_byTertuliaId(@tertulia, @eventDate);
	SET @itemId = dbo.fnGetItem_byTertuliaId(@tertulia, @itemName);

	SELECT @totalQuantity = SUM(ei_quantity) FROM EventsItems 
	WHERE ei_event = @eventId AND ei_item = @itemId;
	IF (@totalQuantity = NULL) SET @totalQuantity = 0

	SELECT @committedQuantity = SUM(ct_quantity) FROM Contributions 
	WHERE ct_event = @eventId AND ct_item = @itemId AND ct_user <> @userId;
	IF (@committedQuantity IS NULL) SET @committedQuantity = 0

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
	@userSid VARCHAR(40),
	@tertulia INTEGER, 
	@typeName VARCHAR(40), 
	@message VARCHAR(40)
AS
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_postNotification
BEGIN TRY
	DECLARE @userId INTEGER, @tag INTEGER;
	SET @userId = dbo.fnGetUserId_bySid(@userSid);
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

CREATE PROCEDURE sp_getTertuliaMembers
	@userSid VARCHAR(40),
	@tertulia INTEGER
AS
BEGIN
	IF (dbo.fnGetAuthorizationForActionBySid(@userSid, @tertulia, 'READ_MEMBERS_DETAILS_PRIVATE_TERTULIA') <> 1)
		RETURN;

	DECLARE @isPrivate BIT;

	SELECT @isPrivate = tr_is_private
	FROM EnumValues
		INNER JOIN members ON mb_role = nv_id
		INNER JOIN tertulias ON mb_tertulia = tr_id
		INNER JOIN users ON mb_user = us_id
	WHERE tr_is_cancelled = 0
		AND us_sid = @userSid
		AND tr_id = @tertulia;

	IF @isPrivate = 1
		SELECT
			us_id AS id,
			us_sid AS sid,
			us_alias AS alias,
			us_firstName AS firstName,
			us_lastName AS lastName,
			us_email AS email,
			us_picture AS picture,
			nv_name AS role
		FROM Members
			INNER JOIN Users ON mb_user = us_id
			INNER JOIN Tertulias ON mb_tertulia = tr_id
			INNER JOIN EnumValues ON mb_role = nv_id
		WHERE tr_id = @tertulia;
	ELSE
		SELECT
			us_id AS id,
			us_alias AS alias,
			us_picture AS picture,
			nv_name AS role
		FROM Members
			INNER JOIN Users ON mb_user = us_id
			INNER JOIN Tertulias ON mb_tertulia = tr_id
			INNER JOIN EnumValues ON mb_role = nv_id
		WHERE tr_id = @tertulia;
END

CREATE PROCEDURE sp_createInvitationVouchers
	@userSid VARCHAR(40),
	@tertulia INTEGER,
	@vouchers_count INTEGER,
	@vouchers_batch VARCHAR(36) OUTPUT
AS 
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_inviteToTertulia
BEGIN TRY

	IF (dbo.fnGetAuthorizationForActionBySid(@userSid, @tertulia, 'INVITE_NEW_MEMBER') <> 1)
	BEGIN
		ROLLBACK TRANSACTION tran_sp_inviteToTertulia;
		RETURN 1;
	END

	DECLARE @userId INTEGER, @role VARCHAR(40);

	SET @userId = dbo.fnGetUserId_bySid(@userSid);

	SELECT @role = nv_name
		FROM Tertulias
		INNER JOIN Members ON mb_tertulia = tr_id
		INNER JOIN EnumValues ON mb_role = nv_id
		WHERE tr_is_cancelled = 0
			AND mb_user = @userId AND tr_id = @tertulia;

	IF (@role IS NULL OR @role NOT IN ('owner', 'member'))
	BEGIN
		ROLLBACK TRANSACTION tran_sp_inviteToTertulia
		RETURN 1;
	END

	SET @vouchers_batch = newid();

	DECLARE @voucher VARCHAR(36);

	WHILE @vouchers_count > 0
	BEGIN
		SET @voucher = newid();
		INSERT INTO Invitations (in_key, in_batch, in_tertulia, in_user) VALUES (@voucher, @vouchers_batch, @tertulia, @userId);
		SET @vouchers_count = @vouchers_count - 1;
	END
	COMMIT TRANSACTION tran_sp_inviteToTertulia
	RETURN 0;
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	--ROLLBACK TRANSACTION tran_sp_inviteToTertulia
END CATCH

CREATE PROCEDURE sp_acceptInvitationToTertulia
	@userSid VARCHAR(40),
	@token VARCHAR(36)
AS 
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_spAcceptInvitation
BEGIN TRY
	DECLARE @userId INTEGER, @tertulia INTEGER, @role INTEGER, @email_i VARCHAR(40), @email_u VARCHAR(40);
	SELECT @tertulia = in_tertulia
		FROM Invitations 
		INNER JOIN Tertulias ON in_tertulia = tr_id
		WHERE tr_is_cancelled = 0 AND in_key = @token AND in_is_acknowledged = 0;
	SET @userId = dbo.fnGetUserId_bySid(@userSid);
	IF (@userId IS NULL OR @tertulia IS NULL)
	BEGIN
		ROLLBACK TRANSACTION tran_spAcceptInvitation
		RETURN 1;
	END
	SET @role = dbo.fnGetEnum('Roles', 'member');
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

CREATE PROCEDURE sp_subscribePublicTertulia
	@userSid VARCHAR(40),
	@tertulia INTEGER
AS 
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_sp_subscribePublicTertulia
BEGIN TRY
	DECLARE @totals INTEGER, @user INTEGER, @member INTEGER;

	SELECT @totals = COUNT(*) FROM Tertulias WHERE tr_is_cancelled = 0 AND tr_is_private = 0;
	IF (@totals = 0)
	BEGIN
		ROLLBACK TRANSACTION tran_sp_subscribePublicTertulia
		RAISERROR ('Tertulia is either cancelled or private.', -1, -1);
		RETURN 1;
	END

	SET @user = dbo.fnGetUserId_bySid(@userSid);

	SELECT @totals = COUNT(*) FROM Tertulias
	INNER JOIN Members ON mb_tertulia = tr_id
	WHERE tr_is_cancelled = 0 AND tr_id = @tertulia AND mb_user = @user
	IF (@totals = 1)
	BEGIN
		ROLLBACK TRANSACTION tran_sp_subscribePublicTertulia
		RAISERROR ('Duplicate subscription.', -1, -1);
		RETURN 1;
	END

    SET @member = dbo.fnGetEnum('Roles', 'member');

    INSERT INTO Members (mb_tertulia, mb_user, mb_role) VALUES (@tertulia, @user, @member);
    COMMIT TRANSACTION tran_sp_subscribePublicTertulia;
	RETURN 1;

END TRY
BEGIN CATCH
	DECLARE @ErrorMessage NVARCHAR(4000);  
	SELECT @ErrorMessage = ERROR_MESSAGE();
	ROLLBACK TRANSACTION tran_sp_subscribePublicTertulia;
	RAISERROR (@ErrorMessage, -1, -1);
	RETURN 1;
END CATCH
GO

CREATE PROCEDURE sp_unsubscribePublicTertulia
	@userSid VARCHAR(40),
	@tertulia INTEGER
AS 
SET TRANSACTION ISOLATION LEVEL READ COMMITTED
BEGIN TRANSACTION tran_spUnsubscribe
BEGIN TRY

	DECLARE @role INTEGER, @user INTEGER;

    SET @role = dbo.fnGetEnum('Roles', 'owner');
	SET @user = dbo.fnGetUserId_bySid(@userSid);

	DELETE FROM Members WHERE mb_user = @user AND mb_tertulia = @tertulia AND mb_role <> @role;
	COMMIT TRANSACTION tran_spUnsubscribe;
	RETURN 1;

END TRY
BEGIN CATCH
	DECLARE @ErrorMessage NVARCHAR(4000);  
	SELECT @ErrorMessage = ERROR_MESSAGE();
	ROLLBACK TRANSACTION tran_spUnsubscribe;
	RAISERROR (@ErrorMessage, -1, -1);
	RETURN 1;
END CATCH
GO

CREATE TRIGGER trLogUserInsert
	ON Users
	AFTER INSERT
AS
BEGIN
BEGIN TRANSACTION tran_log_user_creation WITH MARK
BEGIN TRY
	DECLARE @type INTEGER, @user INTEGER;
	SELECT @user = us_id FROM INSERTED;
	SELECT @type = nt_id FROM EnumTypes WHERE nt_name = 'Users';
	INSERT INTO Logs (lg_type, lg_refid) VALUES (@type, @user);
	COMMIT TRANSACTION tran_log_user_creation
END TRY
BEGIN CATCH
	SELECT ERROR_NUMBER() AS ErrorNumber, ERROR_MESSAGE() AS ErrorMessage;
	ROLLBACK TRANSACTION tran_log_user_creation
END CATCH
END
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

-- For Logging
INSERT INTO EnumTypes (nt_name) values ('Users');

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

-- Actions
EXEC spSetEnumI N'Actions', N'UPDATE_TERTULIA', 0;
EXEC spSetEnumI N'Actions', N'INVITE_NEW_MEMBER', 0;
EXEC spSetEnumI N'Actions', N'CREATE_EVENT', 0;
EXEC spSetEnumI N'Actions', N'MANAGE_EVENT', 0;
EXEC spSetEnumI N'Actions', N'READ_MEMBERS_DETAILS_PRIVATE_TERTULIA', 0;
GO

-- RBAC0
DECLARE @owner INTEGER, @manager INTEGER;
SET @owner = dbo.fnGetEnum('Roles', 'owner');
SET @manager = dbo.fnGetEnum('Roles', 'manager');

DECLARE @action INTEGER;

SET @action = dbo.fnGetEnum('Actions', 'UPDATE_TERTULIA');
INSERT INTO RBAC0 (rb_role, rb_action) VALUES (@owner, @action);

SET @action = dbo.fnGetEnum('Actions', 'INVITE_NEW_MEMBER');
INSERT INTO RBAC0 (rb_role, rb_action) VALUES (@owner, @action), (@manager, @action);

SET @action = dbo.fnGetEnum('Actions', 'CREATE_EVENT');
INSERT INTO RBAC0 (rb_role, rb_action) VALUES (@owner, @action), (@manager, @action);

SET @action = dbo.fnGetEnum('Actions', 'MANAGE_EVENT');
INSERT INTO RBAC0 (rb_role, rb_action) VALUES (@owner, @action), (@manager, @action);

SET @action = dbo.fnGetEnum('Actions', 'READ_MEMBERS_DETAILS_PRIVATE_TERTULIA');
INSERT INTO RBAC0 (rb_role, rb_action) VALUES (@owner, @action), (@manager, @action);
GO

-- Tags
EXEC spSetEnumI N'Tags', N'announcements', 0;
GO

-- Dummy Data
DECLARE @dummy INTEGER, @schedule INTEGER, @location INTEGER, @tertulia INTEGER;

SET @dummy = dbo.fnGetEnum('Dummy', 'dummy');
INSERT INTO Schedules (sc_type) VALUES (@dummy);
SET @schedule = SCOPE_IDENTITY();

INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_city, lo_country, lo_latitude, lo_longitude, lo_tertulia)
VALUES ('Dummy', 'Dummy', 'Dummy', 'Dummy', 'Dummy', 0, 0, @dummy);
SET @location = SCOPE_IDENTITY();

INSERT INTO Tertulias (tr_name, tr_subject, tr_location, tr_schedule, tr_is_private, tr_is_cancelled)
VALUES ('Dummy', 'Dummy', @location, @schedule, 1, 1);
SET @tertulia = SCOPE_IDENTITY();

UPDATE Locations SET lo_tertulia = @tertulia;

ALTER TABLE Locations ADD CONSTRAINT fk_location_tertulia FOREIGN KEY (lo_tertulia) REFERENCES Tertulias(tr_id);
GO
