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
BEGIN TRANSACTION tran_test
BEGIN TRY
	DECLARE @dow INTEGER, @schedule INTEGER;
	SET @dow = dbo.fnGetEnum('WeekDays', 'Monday');
	DECLARE @scheduleType INTEGER; SET @scheduleType = dbo.fnGetEnum('Schedule', 'MonthlyW');
	INSERT INTO Schedules (sc_type) VALUES (@scheduleType);
	SET @schedule = SCOPE_IDENTITY();
	INSERT INTO MonthlyW (mw_schedule, mw_dow, mw_weeknr, mw_is_fromstart, mw_skip) VALUES (@schedule, @dow, 0, 1, 0);
	ROLLBACK TRANSACTION tran_test
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION tran_test
END CATCH
GO

-- Create application users
-- TEST 01
BEGIN TRANSACTION tran_test
BEGIN TRY
	INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
		('sid:73efaf9ab34c8ae5b9357a070bdaf6a3', N'TestUser', N'TestAntónio', N'Test Borba da Silva', 'abstest@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
	ROLLBACK TRANSACTION tran_test
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION tran_test
END CATCH
GO

-- Create a set of tertulia locations
-- TEST 02
BEGIN TRANSACTION tran_test
BEGIN TRY
	INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
		('sid:73efaf9ab34c8ae5b9357a070bdaf6a3', N'TestUser', N'TestAntónio', N'Test Borba da Silva', 'abstest@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
	DECLARE @userId INTEGER, @tertulia INTEGER;
	SET @UserId = dbo.fnGetUserId_byAlias('TestUser');
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Tertulia do Tejo'      -- [name]
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
	SELECT @tertulia = tr_id FROM Tertulias WHERE tr_name = N'Test with Tertulia do Tejo';
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
	ROLLBACK TRANSACTION tran_test
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION tran_test
END CATCH
GO

-- Create a set of tertulias
-- TEST 03
-- [name], [subject], [userId], [weekDay], [weekNr], [fromStart], [skip], [locationName], [isPrivate]
BEGIN TRANSACTION tran_test
BEGIN TRY
	INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
		('sid:73efaf9ab34c8ae5b9357a070bdaf6a3', N'TestUser', N'TestAntónio', N'Test Borba da Silva', 'abstest@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
	DECLARE @userId INTEGER, @tertulia INTEGER;
	SET @UserId = dbo.fnGetUserId_byAlias('TestUser');
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Tertúlia dos primos', N'Só Celoricos' -- [name], [subject]
		, @UserId                               -- [userId]
		, N'friday', 0 , 1 , 3                  -- [weekDay], [weekNr], [fromStart], [skip]
		, N'Restaurante O Jacinto', N'Avenida Ventura Terra 2', N'1600-781 Lisboa', N'Portugal' -- [locationName], [locationAddress], [locationZip], [locationCountry]                     --
		, N'38.758563', N'-9.167007'            -- [locationLatitude], [locationLongitude]
		, 1;                                    -- [isPrivate]
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Test with Escolinha 72-77', N'Sempre em contato'            
		, @UserId
		, 'saturday', 0, 1, 10
		, 'Restaurante EntreCopos', N'Rua de Entrecampos, nº11', N'1000-151 Lisboa', 'Portugal'
		, '38.744912', '-9.145291'
		, 1;
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Natais BS' , N'Mais um...'
		, @UserId
		, 'sunday', 0, 0, 51
		, 'Avó Fernanda', N'Avenida Nações Unidas, 33, 2.ºDtº', N'1600-531 Lisboa', 'Portugal'
		, '38.764288', '-9.180429'
		, 1;
	SET @UserId = dbo.fnGetUserId_byAlias('TestUser')
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Gulbenkian Música' , N''
		, @UserId
		, 'thursday', 1, 1, 3
		, 'Restaurante Gardens', N'Rua Principal, S/N, Urbanização Quinta Alcoutins', N'1600-263 Lisboa', 'Portugal'
		, '38.776200', '-9.171391'
		, 0;
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with CALM', N'Ex MAC - Sempre só nós 8'
		, @UserId
		, 'friday' , 0, 0, 3
		, 'Restaurante Taberna Gourmet', N'Rua Padre Américo 28', N'1600-548 Lisboa', 'Portugal'
		, '38.763603', '-9.180278'
		, 1;
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with AtHere', N'Tipo RoBoTo'
		, @UserId
		, 'thursday', 0, 0, 5
		, 'Pastelaria Zineira', N'Rua Principal, 444, Livramento', N'2765-383 Estoril', 'Portugal'
		, '38.713092', '-9.371864'
		, 1;
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Test with Terças Ggl', N''
		, @UserId
		, 'tuesday', 0, 0, 0
		, 'Varsailles - Técnico', N'Avenida Rovisco Pais 1', N'1049-001 Lisboa', 'Portugal'
		, '38.737674', '-9.138564'
		, 1;
	ROLLBACK TRANSACTION tran_test
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION tran_test
END CATCH
GO

-- Create Tertulia Events
-- TEST 007
BEGIN TRANSACTION tran_test
BEGIN TRY
	INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
		('sid:73efaf9ab34c8ae5b9357a070bdaf6a3', N'TestUser', N'TestAntónio', N'Test Borba da Silva', 'abstest@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
	DECLARE @userId INTEGER, @tertulia INTEGER;
	SET @UserId = dbo.fnGetUserId_byAlias('TestUser');
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Terças Ggl', N''         -- [name], [subject]
		, @UserId                  -- [userId]
		, N'tuesday', 0 , 0 , 0    -- [weekDay], [weekNr], [fromStart], [skip]
		, 'Varsailles - Técnico', N'Avenida Rovisco Pais 1', N'1049-001 Lisboa', 'Portugal' -- [locationName], [locationAddress], [locationZip], [locationCountry]                     --
		, '38.737674', '-9.138564' -- [locationLatitude], [locationLongitude]
		, 1;                       -- [isPrivate]
	SELECT @tertulia = tr_id FROM Tertulias WHERE tr_name = N'Test with Terças Ggl';
	INSERT INTO Locations (lo_name, lo_address, lo_zip, lo_country, lo_latitude, lo_longitude, lo_tertulia) VALUES
		(N'Lisboa Racket Center', N'Rua Alferes Malheiro', N'1700 Lisboa', 'Portugal', '38.758372', '-9.134471', @tertulia);
	EXEC dbo.sp_createEvent 'Test with Terças Ggl', 'Lisboa Racket Center', '20160523 13:00:00';
	EXEC dbo.sp_createEventDefaultLocation 'Test with Terças Ggl', '20160904 13:00:00';
	ROLLBACK TRANSACTION tran_test
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION tran_test
END CATCH
GO

-- Create Tertulia Items inventory
-- Test 008
BEGIN TRANSACTION tran_test
BEGIN TRY
	INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
		('sid:73efaf9ab34c8ae5b9357a070bdaf6a3', N'TestUser', N'TestAntónio', N'Test Borba da Silva', 'abstest@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
	DECLARE @userId INTEGER;
	SET @UserId = dbo.fnGetUserId_byAlias('TestUser');
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Escolinha 72-77', N'Sempre em contato'            
		, @UserId
		, N'saturday', 0, 1, 10
		, 'Restaurante EntreCopos', N'Rua de Entrecampos, nº11', N'1000-151 Lisboa', 'Portugal'
		, '38.744912', '-9.145291'
		, 1;
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Tertulia do Tejo', N'O que seria do Mundo sem nós!'
		, @UserId
		, N'friday', 2, 1, 0
		, N'Restaurante Cave Real', N'Avenida 5 de Outubro 13', N'1050 Lisboa', N'Portugal'
		, N'38.733541', N'-9.147056'
		, 1;
	DECLARE @tertuliaId INTEGER;
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Escolinha 72-77';
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
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Tertulia do Tejo';
	INSERT INTO Items (it_name, it_tertulia) VALUES  
		('Cerveja (1lt)', @tertuliaId),
		('Vinho Verde Branco (75cl)', @tertuliaId),
		('Vinho Tinto Frutado (75cl)', @tertuliaId),
		('Água Tónica (1lt)', @tertuliaId),
		('Gin (75cl)', @tertuliaId),
		('Copos de vidro', @tertuliaId),
		('Guardanapos de papel (200g)', @tertuliaId);
	ROLLBACK TRANSACTION tran_test
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION tran_test
END CATCH
GO

-- Create Tertulia Items Templates
-- TEST 009
BEGIN TRANSACTION tran_test
BEGIN TRY
	INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
		('sid:73efaf9ab34c8ae5b9357a070bdaf6a3', N'TestUser', N'TestAntónio', N'Test Borba da Silva', 'abstest@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
	DECLARE @userId INTEGER;
	SET @UserId = dbo.fnGetUserId_byAlias('TestUser');
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Escolinha 72-77', N'Sempre em contato'            
		, @UserId
		, N'saturday', 0, 1, 10
		, 'Restaurante EntreCopos', N'Rua de Entrecampos, nº11', N'1000-151 Lisboa', 'Portugal'
		, '38.744912', '-9.145291'
		, 1;
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Tertulia do Tejo', N'O que seria do Mundo sem nós!'
		, @UserId
		, N'friday', 2, 1, 0
		, N'Restaurante Cave Real', N'Avenida 5 de Outubro 13', N'1050 Lisboa', N'Portugal'
		, N'38.733541', N'-9.147056'
		, 1;
	DECLARE @tertuliaId INTEGER;
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Escolinha 72-77';
	INSERT INTO Templates (tp_name, tp_tertulia) VALUES
		('Drinks', @tertuliaId),
		('Snacks', @tertuliaId);
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Tertulia do Tejo';
	INSERT INTO Templates (tp_name, tp_tertulia) VALUES
		('Drinks', @tertuliaId);
	ROLLBACK TRANSACTION tran_test
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION tran_test
END CATCH
GO

-- Fill Tertulia Items Templates with items from Tertulia Items inventory
-- TEST 010
BEGIN TRANSACTION tran_test
BEGIN TRY
	INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
		('sid:73efaf9ab34c8ae5b9357a070bdaf6a3', N'TestUser', N'TestAntónio', N'Test Borba da Silva', 'abstest@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');

	DECLARE @userId INTEGER, @tertuliaId INTEGER;

	SET @UserId = dbo.fnGetUserId_byAlias('TestUser');
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Escolinha 72-77', N'Sempre em contato'            
		, @UserId
		, N'saturday', 0, 1, 10
		, 'Restaurante EntreCopos', N'Rua de Entrecampos, nº11', N'1000-151 Lisboa', 'Portugal'
		, '38.744912', '-9.145291'
		, 1;
	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Tertulia do Tejo', N'O que seria do Mundo sem nós!'
		, @UserId
		, N'friday', 2, 1, 0
		, N'Restaurante Cave Real', N'Avenida 5 de Outubro 13', N'1050 Lisboa', N'Portugal'
		, N'38.733541', N'-9.147056'
		, 1;
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Escolinha 72-77';

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
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Tertulia do Tejo';
	INSERT INTO Items (it_name, it_tertulia) VALUES  
		('Cerveja (1lt)', @tertuliaId),
		('Vinho Verde Branco (75cl)', @tertuliaId),
		('Vinho Tinto Frutado (75cl)', @tertuliaId),
		('Água Tónica (1lt)', @tertuliaId),
		('Gin (75cl)', @tertuliaId),
		('Copos de vidro', @tertuliaId),
		('Guardanapos de papel (200g)', @tertuliaId);

	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Escolinha 72-77';
	INSERT INTO Templates (tp_name, tp_tertulia) VALUES
		('Drinks', @tertuliaId),
		('Snacks', @tertuliaId);
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Tertulia do Tejo';
	INSERT INTO Templates (tp_name, tp_tertulia) VALUES
		('Drinks', @tertuliaId);

	DECLARE @templateId INTEGER;
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Escolinha 72-77';
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
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Tertulia do Tejo';
	SET @templateId = dbo.fnGetTemplate_byTertuliaId(@tertuliaId, 'Drinks');
	INSERT INTO QuantifiedItems (qi_template, qi_item, qi_quantity) VALUES 
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Cerveja (1lt)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Vinho Verde Branco (75cl)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Vinho Tinto Frutado (75cl)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Água Tónica (1lt)'), 2),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Gin (75cl)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Copos de vidro'), 4),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Guardanapos de papel (200g)'), 1);
	ROLLBACK TRANSACTION tran_test
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION tran_test
END CATCH
GO

-- Fill Event Checklist with Tertulia Template
-- TEST 011
BEGIN TRANSACTION tran_test
BEGIN TRY
	INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
		('sid:73efaf9ab34c8ae5b9357a070bdaf6a3', N'TestUser', N'TestAntónio', N'Test Borba da Silva', 'abstest@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
	DECLARE @userId INTEGER, @tertuliaId INTEGER;

	SET @UserId = dbo.fnGetUserId_byAlias('TestUser');

	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Tertulia do Tejo', N'O que seria do Mundo sem nós!'
		, @UserId
		, N'friday', 2, 1, 0
		, N'Restaurante Cave Real', N'Avenida 5 de Outubro 13', N'1050 Lisboa', N'Portugal'
		, N'38.733541', N'-9.147056'
		, 1;

	EXEC dbo.sp_createEventDefaultLocation 'Test with Tertulia do Tejo', '2016-09-04 13:00:00';

	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Tertulia do Tejo';
	INSERT INTO Items (it_name, it_tertulia) VALUES  
		('Cerveja (1lt)', @tertuliaId),
		('Vinho Verde Branco (75cl)', @tertuliaId),
		('Vinho Tinto Frutado (75cl)', @tertuliaId),
		('Água Tónica (1lt)', @tertuliaId),
		('Gin (75cl)', @tertuliaId),
		('Copos de vidro', @tertuliaId),
		('Guardanapos de papel (200g)', @tertuliaId);

	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Tertulia do Tejo';
	INSERT INTO Templates (tp_name, tp_tertulia) VALUES
		('Drinks', @tertuliaId);

	DECLARE @templateId INTEGER;
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Tertulia do Tejo';
	SET @templateId = dbo.fnGetTemplate_byTertuliaId(@tertuliaId, 'Drinks');
	INSERT INTO QuantifiedItems (qi_template, qi_item, qi_quantity) VALUES 
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Cerveja (1lt)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Vinho Verde Branco (75cl)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Vinho Tinto Frutado (75cl)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Água Tónica (1lt)'), 2),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Gin (75cl)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Copos de vidro'), 4),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Guardanapos de papel (200g)'), 1);

	EXEC sp_buildEventsItems 'Test with Tertulia do Tejo', '2016-09-04 13:00:00', 'Drinks';

	ROLLBACK TRANSACTION tran_test
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION tran_test
END CATCH
GO

-- Commit to handle items to a Tertulia event
-- TEST 012
BEGIN TRANSACTION tran_test
BEGIN TRY
	INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
		('sid:73efaf9ab34c8ae5b9357a070bdaf6a3', N'TestUser', N'TestAntónio', N'Test Borba da Silva', 'abstest@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
	DECLARE @userId INTEGER, @tertuliaId INTEGER;

	SET @UserId = dbo.fnGetUserId_byAlias('TestUser');

	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Tertulia do Tejo', N'O que seria do Mundo sem nós!'
		, @UserId
		, N'friday', 2, 1, 0
		, N'Restaurante Cave Real', N'Avenida 5 de Outubro 13', N'1050 Lisboa', N'Portugal'
		, N'38.733541', N'-9.147056'
		, 1;

	EXEC dbo.sp_createEventDefaultLocation 'Test with Tertulia do Tejo', '2016-09-04 13:00:00';

	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Tertulia do Tejo';
	INSERT INTO Items (it_name, it_tertulia) VALUES  
		('Cerveja (1lt)', @tertuliaId),
		('Vinho Verde Branco (75cl)', @tertuliaId),
		('Vinho Tinto Frutado (75cl)', @tertuliaId),
		('Água Tónica (1lt)', @tertuliaId),
		('Gin (75cl)', @tertuliaId),
		('Copos de vidro', @tertuliaId),
		('Guardanapos de papel (200g)', @tertuliaId);

	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Tertulia do Tejo';
	INSERT INTO Templates (tp_name, tp_tertulia) VALUES
		('Drinks', @tertuliaId);

	DECLARE @templateId INTEGER;
	EXEC @tertuliaId = dbo.sp_getId 'tr', 'Tertulias', 'Test with Tertulia do Tejo';
	SET @templateId = dbo.fnGetTemplate_byTertuliaId(@tertuliaId, 'Drinks');
	INSERT INTO QuantifiedItems (qi_template, qi_item, qi_quantity) VALUES 
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Cerveja (1lt)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Vinho Verde Branco (75cl)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Vinho Tinto Frutado (75cl)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Água Tónica (1lt)'), 2),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Gin (75cl)'), 1),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Copos de vidro'), 4),
		(@templateId, dbo.fnGetItem_byTertuliaId(@tertuliaId, 'Guardanapos de papel (200g)'), 1);

	EXEC sp_buildEventsItems 'Test with Tertulia do Tejo', '2016-09-04 13:00:00', 'Drinks';

	DECLARE @commitment INTEGER, @itemName VARCHAR(40);
	SET @itemName = 'Copos de vidro';
	EXEC @commitment = sp_assignChecklistItems 'TestUser', 'Test with Tertulia do Tejo', '2016-09-04 13:00:00', @itemName, 2;
	PRINT 'Commitment for ' + @itemName + ': ' + CAST(@commitment AS VARCHAR);
	SET @itemName = 'Cerveja (1lt)';
	EXEC @commitment = sp_assignChecklistItems 'TestUser', 'Test with Tertulia do Tejo', '2016-09-04 13:00:00', @itemName, 2;
	PRINT 'Commitment for ' + @itemName + ': ' + CAST(@commitment AS VARCHAR);
	ROLLBACK TRANSACTION tran_test
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION tran_test
END CATCH
GO

-- Post a message in a Tertulia
-- TEST 013
BEGIN TRANSACTION tran_test
BEGIN TRY
	INSERT INTO Users (us_sid, us_alias, us_firstname, us_lastname, us_email, us_picture) VALUES 
		('sid:73efaf9ab34c8ae5b9357a070bdaf6a3', N'TestUser', N'TestAntónio', N'Test Borba da Silva', 'abstest@ggl.pt', 'https://lh4.googleusercontent.com/-l5aXbFF6eI8/AAAAAAAAAAI/AAAAAAAAAik/bjXsvC1iVHY/s96-c/photo.jpg');
	DECLARE @userId INTEGER, @tertuliaId INTEGER;

	SET @UserId = dbo.fnGetUserId_byAlias('TestUser');

	EXEC sp_insertTertulia_MonthlyW 
		N'Test with Tertulia do Tejo', N'O que seria do Mundo sem nós!'
		, @UserId
		, N'friday', 2, 1, 0
		, N'Restaurante Cave Real', N'Avenida 5 de Outubro 13', N'1050 Lisboa', N'Portugal'
		, N'38.733541', N'-9.147056'
		, 1;

	EXEC sp_postNotification_byAlias 'TestUser', 'Test with Tertulia do Tejo', 'Announcement', 'My test post to a tertulia';
	
	ROLLBACK TRANSACTION tran_test
END TRY
BEGIN CATCH
	DECLARE @errno INTEGER, @errmsg VARCHAR(255);
	SELECT @errno = ERROR_NUMBER(), @errmsg = ERROR_MESSAGE();
	RAISERROR(N'Error number: %i, Error message: %s' -- Message text
		, 10, 1 -- Severity, State
		, @errno, @errmsg) WITH NOWAIT
	ROLLBACK TRANSACTION tran_test
END CATCH
GO
