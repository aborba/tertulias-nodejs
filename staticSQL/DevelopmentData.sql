----------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------
--                                                                                                                              --
--  ######  ####### #     # ####### #       ####### ######  #     # ####### #     # #######    ######     #    #######    #     --
--  #     # #       #     # #       #       #     # #     # ##   ## #       ##    #    #       #     #   # #      #      # #    --
--  #     # #       #     # #       #       #     # #     # # # # # #       # #   #    #       #     #  #   #     #     #   #   --
--  #     # #####   #     # #####   #       #     # ######  #  #  # #####   #  #  #    #       #     # #     #    #    #     #  --
--  #     # #        #   #  #       #       #     # #       #     # #       #   # #    #       #     # #######    #    #######  --
--  #     # #         # #   #       #       #     # #       #     # #       #    ##    #       #     # #     #    #    #     #  --
--  ######  #######    #    ####### ####### ####### #       #     # ####### #     #    #       ######  #     #    #    #     #  --
--                                                                                                                              --
----------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------

-- Create a MonthlyW Schedule
DECLARE @dow INTEGER, @scheduleType INTEGER, @schedule INTEGER;
SET @dow = dbo.fnGetEnum('WeekDays', 'Monday');
SET @scheduleType = dbo.fnGetEnum('Schedule', 'MonthlyW');
INSERT INTO Schedules (sc_type) VALUES (@scheduleType);
SET @schedule = SCOPE_IDENTITY();
INSERT INTO MonthlyW (mw_schedule, mw_dow, mw_weeknr, mw_is_fromstart, mw_skip) VALUES (@schedule, @dow, 0, 1, 0);
GO

-- Create application users
INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
	('sid:fadae567db0f67c6fe69d25ee8ffc0b5', N'aborba', N'António', N'Borba da Silva', 'antonio.borba@gmail.com', ''),
	('sid:357a070bdaf6a373efaf9ab34c8ae5b9', N'GGLabs', N'António', N'Borba da Silva', 'abs@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
GO

-- Create a set of tertulia locations
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
	, N'1050'						   -- [locationZip]
	, N'Lisboa'                   	   -- [locationCity]
	, N'Portugal'                      -- [locationCountry]
	, N'38.733541'                     -- [locationLatitude]
	, N'-9.147056'                     -- [locationLongitude]
	, 1;                               -- [isPrivate]
SELECT @tertulia = tr_id FROM Tertulias WHERE tr_name = N'Tertulia do Tejo';
INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_city, lo_country, lo_latitude, lo_longitude, lo_tertulia) VALUES
	  (N'Pastelaria Mexicana',              N'Avenida Guerra Junqueiro 30C',                      N'1000-167', N'Lisboa',  'Portugal', '38.740117', '-9.136394', @tertulia)
--	, (N'Restaurante Cave Real',            N'Avenida 5 de Outubro 13',                           N'1050',     N'Lisboa',  'Portugal', '38.733541', '-9.147056', @tertulia)
	, (N'Restaurante Picanha',              N'Rua das Janelas Verdes 96',                         N'1200',     N'Lisboa',  'Portugal', '38.705678', '-9.160624', @tertulia)
	, (N'Restaurante EntreCopos',           N'Rua de Entrecampos, nº11',                          N'1000-151', N'Lisboa',  'Portugal', '38.744912', '-9.145291', @tertulia)
	, (N'Lisboa Racket Center',             N'Rua Alferes Malheiro',                              N'1700',     N'Lisboa',  'Portugal', '38.758372', '-9.134471', @tertulia)
	, (N'Restaurante O Jacinto',            N'Avenida Ventura Terra 2',                           N'1600-781', N'Lisboa',  'Portugal', '38.758563', '-9.167007', @tertulia)
	, (N'Restaurante Taberna Gourmet',      N'Rua Padre Américo 28',                              N'1600-548', N'Lisboa',  'Portugal', '38.763603', '-9.180278', @tertulia)
	, (N'Café A Luz Ideal',                 N'Rua Gen. Schiappa Monteiro 2A',                     N'1600-155', N'Lisboa',  'Portugal', '38.754401', '-9.174995', @tertulia)
	, (N'Restaurante Honorato - Telheiras', N'Rua Professor Francisco Gentil, Lote A, Telheiras', N'1600',     N'Lisboa',  'Portugal', '38.760363', '-9.166720', @tertulia)
	, (N'Restaurante Gardens',              N'Rua Principal, S/N, Urbanização Quinta Alcoutins',  N'1600-263', N'Lisboa',  'Portugal', '38.776200', '-9.171391', @tertulia)
	, (N'Pastelaria Arcadas',               N'Rua Cidade de Lobito 282',                          N'1800-071', N'Lisboa',  'Portugal', '38.764007', '-9.112470', @tertulia)
	, (N'Varsailles - Técnico',             N'Avenida Rovisco Pais 1',                            N'1049-001', N'Lisboa',  'Portugal', '38.737674', '-9.138564', @tertulia)
	, (N'Pastelaria Zineira',               N'Rua Principal, 444, Livramento',                    N'2765-383', N'Estoril', 'Portugal', '38.713092', '-9.371864', @tertulia)
	, (N'Avó Fernanda',                     N'Avenida Nações Unidas, 33, 2.ºDtº',                 N'1600-531', N'Lisboa',  'Portugal', '38.764288', '-9.180429', @tertulia);
GO

-- Create a set of tertulias
-- [name], [subject], [userId], [weekDay], [weekNr], [fromStart], [skip], [locationName], [isPrivate]
DECLARE @userId INTEGER, @tertulia INTEGER;
SET @UserId = dbo.fnGetUserId_byAlias('GGLabs');
EXEC sp_insertTertulia_MonthlyW 
	N'Test with Tertúlia dos primos', N'Só Celoricos' -- [name], [subject]
	, @UserId                               -- [userId]
	, N'friday', 0 , 1 , 3                  -- [weekDay], [weekNr], [fromStart], [skip]
	, N'Restaurante O Jacinto', N'Avenida Ventura Terra 2', N'1600-781', N'Lisboa', N'Portugal' -- [locationName], [locationAddress], [locationZip], [locationCountry]                     --
	, N'38.758563', N'-9.167007'            -- [locationLatitude], [locationLongitude]
	, 1;                                    -- [isPrivate]
EXEC sp_insertTertulia_MonthlyW 
	N'Test with Test with Escolinha 72-77', N'Sempre em contato'            
	, @UserId
	, 'saturday', 0, 1, 10
	, 'Restaurante EntreCopos', N'Rua de Entrecampos, nº11', N'1000-151', N'Lisboa', 'Portugal'
	, '38.744912', '-9.145291'
	, 1;
EXEC sp_insertTertulia_MonthlyW 
	N'Test with Natais BS' , N'Mais um...'
	, @UserId
	, 'sunday', 0, 0, 51
	, 'Avó Fernanda', N'Avenida Nações Unidas, 33, 2.ºDtº', N'1600-531', N'Lisboa', 'Portugal'
	, '38.764288', '-9.180429'
	, 1;
SET @UserId = dbo.fnGetUserId_byAlias('aborba')
EXEC sp_insertTertulia_MonthlyW 
	N'Test with Gulbenkian Música' , N''
	, @UserId
	, 'thursday', 1, 1, 3
	, 'Restaurante Gardens', N'Rua Principal, S/N, Urbanização Quinta Alcoutins', N'1600-263', N'Lisboa', 'Portugal'
	, '38.776200', '-9.171391'
	, 0;
EXEC sp_insertTertulia_MonthlyW 
	N'Test with CALM', N'Ex MAC - Sempre só nós 8'
	, @UserId
	, 'friday' , 0, 0, 3
	, 'Restaurante Taberna Gourmet', N'Rua Padre Américo 28', N'1600-548', N'Lisboa', 'Portugal'
	, '38.763603', '-9.180278'
	, 1;
EXEC sp_insertTertulia_MonthlyW 
	N'Test with AtHere', N'Tipo RoBoTo'
	, @UserId
	, 'thursday', 0, 0, 5
	, 'Pastelaria Zineira', N'Rua Principal, 444, Livramento', N'2765-383', N'Estoril', 'Portugal'
	, '38.713092', '-9.371864'
	, 1;
EXEC sp_insertTertulia_MonthlyW 
	N'Test with Test with Terças Ggl', N''
	, @UserId
	, 'tuesday', 0, 0, 0
	, 'Varsailles - Técnico', N'Avenida Rovisco Pais 1', N'1049-001', N'Lisboa', 'Portugal'
	, '38.737674', '-9.138564'
	, 1;
GO

-- Set a user member of other tertulias
DECLARE @tertulia INTEGER, @user INTEGER, @role INTEGER;
SET @user = dbo.fnGetUserId_byAlias('aborba');
SET @role = dbo.fnGetEnum('Roles', 'member');
EXEC @tertulia = dbo.sp_getId 'tr', 'Tertulias', 'Tertúlia dos primos';
INSERT INTO Members (mb_tertulia, mb_user, mb_role) VALUES (@tertulia, @user, @role);
EXEC @tertulia = dbo.sp_getId 'tr', 'Tertulias', 'Escolinha 72-77';
INSERT INTO Members (mb_tertulia, mb_user, mb_role) VALUES (@tertulia, @user, @role);
GO

-- Create Tertulia Events
DECLARE @userId INTEGER, @tertulia INTEGER;
SET @UserId = dbo.fnGetUserId_byAlias('GGLabs');
SELECT @tertulia = tr_id FROM Tertulias WHERE tr_name = N'Terças Ggl';
EXEC dbo.sp_createEvent 'Terças Ggl', 'Lisboa Racket Center', '20160523 13:00:00';
EXEC dbo.sp_createEventDefaultLocation 'Terças Ggl', '20160904 13:00:00';
EXEC dbo.sp_createEventDefaultLocation 'Tertulia do Tejo', '2016-09-04 13:00:00';
GO

-- Create Tertulia Items inventory
DECLARE @userId INTEGER, @tertuliaId INTEGER;
SET @UserId = dbo.fnGetUserId_byAlias('GGLabs');
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Escolinha 72-77';
INSERT INTO Items (it_name, it_tertulia) VALUES 
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
INSERT INTO Items (it_name, it_tertulia) VALUES  
	('Cerveja (1lt)', @tertuliaId),
	('Vinho Verde Branco (75cl)', @tertuliaId),
	('Vinho Tinto Frutado (75cl)', @tertuliaId),
	('Água Tónica (1lt)', @tertuliaId),
	('Gin (75cl)', @tertuliaId),
	('Copos de vidro', @tertuliaId),
	('Guardanapos de papel (200g)', @tertuliaId);
GO

-- Create Tertulia Items Templates
DECLARE @userId INTEGER, @tertuliaId INTEGER;
SET @UserId = dbo.fnGetUserId_byAlias('GGLabs');
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Escolinha 72-77';
INSERT INTO Templates (tp_name, tp_tertulia) VALUES
	('Drinks', @tertuliaId),
	('Snacks', @tertuliaId);
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Tertulia do Tejo';
INSERT INTO Templates (tp_name, tp_tertulia) VALUES
	('Drinks', @tertuliaId);
GO

-- Fill Tertulia Items Templates with items from Tertulia Items inventory
DECLARE @userId INTEGER, @tertuliaId INTEGER, @templateId INTEGER;
SET @UserId = dbo.fnGetUserId_byAlias('GGLabs');
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Escolinha 72-77';
SET @templateId = dbo.fnGetTemplate_byTertuliaId(@tertuliaId, 'Snacks');
INSERT INTO QuantifiedItems (qi_template, qi_item, qi_quantity) VALUES 
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Pão cereais (500g)'), 2),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Fiambre (500g)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Queijo Flamengo (500g)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Pacotes Batata Frita (500g)'), 4),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Sortido de frutos secos (200g)'), 6),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Pacote de bolacha maria'), 2);
SET @templateId = dbo.fnGetTemplate_byTertuliaId(@tertuliaId, 'Drinks');
INSERT INTO QuantifiedItems (qi_template, qi_item, qi_quantity) VALUES 
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Coca-Cola (1lt)'), 2),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Sumol laranja (1lt)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Cerveja (1lt)'), 4),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Coca-Cola em lata (1)'), 6),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Água Tónica (1lt)'), 2);
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Tertulia do Tejo';
SET @templateId = dbo.fnGetTemplate_byTertuliaId(@tertuliaId, 'Drinks');
INSERT INTO QuantifiedItems (qi_template, qi_item, qi_quantity) VALUES 
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Cerveja (1lt)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Vinho Verde Branco (75cl)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Vinho Tinto Frutado (75cl)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Água Tónica (1lt)'), 2),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Gin (75cl)'), 1),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Copos de vidro'), 4),
	(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Guardanapos de papel (200g)'), 1);
GO

-- Fill Event Checklist with Tertulia Template
DECLARE @userId INTEGER, @tertuliaId INTEGER, @templateId INTEGER;
SET @UserId = dbo.fnGetUserId_byAlias('GGLabs');
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Tertulia do Tejo';
SET @templateId = dbo.fnGetTemplate_byTertuliaId(@tertuliaId, 'Drinks');
EXEC sp_buildEventsItems 'Tertulia do Tejo', '2016-09-04 13:00:00', 'Drinks';
GO

-- Commit to handle items to a Tertulia event
DECLARE @userId INTEGER, @tertuliaId INTEGER, @commitment INTEGER, @itemName VARCHAR(40);
SET @UserId = dbo.fnGetUserId_byAlias('GGLabs');
EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Tertulia do Tejo';
SET @itemName = 'Copos de vidro';
EXEC @commitment = sp_assignChecklistItems 'GGLabs', 'Tertulia do Tejo', '2016-09-04 13:00:00', @itemName, 2;
PRINT 'Commitment for ' + @itemName + ': ' + CAST(@commitment AS VARCHAR);
SET @itemName = 'Cerveja (1lt)';
EXEC @commitment = sp_assignChecklistItems 'GGLabs', 'Tertulia do Tejo', '2016-09-04 13:00:00', @itemName, 2;
PRINT 'Commitment for ' + @itemName + ': ' + CAST(@commitment AS VARCHAR);
GO

-- Post a message in a Tertulia
EXEC sp_postNotification_byAlias 'GGLabs', 'Tertulia do Tejo', 'Announcement', 'My test post to a tertulia';
EXEC sp_postNotification_byAlias 'GGLabs', 'Terças Ggl', 'Announcement', 'Another test post to a tertulia';
EXEC sp_postNotification_byAlias 'GGLabs', 'Terças Ggl', 'Announcement', 'And another test post to a tertulia';
EXEC sp_postNotification_byAlias 'GGLabs', 'Terças Ggl', 'Announcement', 'Yet another test post to a tertulia';
GO

-- Mark Tertulia messages as read
DECLARE @user INTEGER, @tertulia INTEGER, @notification INTEGER;
SET @user = dbo.fnGetUserId_byAlias('GGLabs');
EXEC @tertulia = sp_getId 'tr', 'Tertulias', 'Terças Ggl';
SELECT @notification = no_id FROM Notifications WHERE no_tertulia = @tertulia AND no_message = 'Another test post to a tertulia';
insert into readnotifications(rn_user, rn_notification) values (@user, @notification);
SELECT @notification = no_id FROM Notifications WHERE no_tertulia = @tertulia AND no_message = 'And another test post to a tertulia';
insert into readnotifications(rn_user, rn_notification) values (@user, @notification);
SET @user = dbo.fnGetUserId_byAlias('aborba');
insert into readnotifications(rn_user, rn_notification) values (@user, @notification);
