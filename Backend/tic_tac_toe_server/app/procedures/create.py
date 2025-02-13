get_all_user = """
CREATE PROCEDURE GetAllUser
AS
BEGIN
IF EXISTS (
    SELECT 1
    FROM [user]
)
BEGIN
    SELECT * FROM [user];
END
ELSE
BEGIN
PRINT 'Table user is empty';
END
END
"""

get_all_address = """
CREATE PROCEDURE GetAllAddress
AS
BEGIN
IF EXISTS (
    SELECT 1
    FROM [address]
)
BEGIN
    SELECT * FROM [address];
END
ELSE
BEGIN
PRINT 'Table address is empty';
END
END
"""

get_all_tournament = """
CREATE PROCEDURE GetAllTournament
AS
BEGIN
IF EXISTS (
    SELECT 1
    FROM [tournament]
)
BEGIN
    SELECT * FROM [tournament];
END
ELSE
BEGIN
PRINT 'Table tournament is empty';
END
END
"""

get_all_game = """
CREATE PROCEDURE GetAllGame
AS
BEGIN
IF EXISTS (
    SELECT 1
    FROM [game]
)
BEGIN
    SELECT * FROM [game];
END
ELSE
BEGIN
PRINT 'Table game is empty';
END
END

"""

get_all_photo_report = """
CREATE PROCEDURE GetAllPhotoReport
AS
BEGIN
IF EXISTS (
    SELECT 1
    FROM [photo_report]
)
BEGIN
    SELECT * FROM [photo_report];
END
ELSE
BEGIN
PRINT 'Table photo_report is empty';
END
END
"""

get_ranking = """
CREATE PROCEDURE GetRanking
AS
BEGIN
IF EXISTS (
    SELECT 1
    FROM [ranking]
)
BEGIN
    SELECT * FROM [ranking];
END
ELSE
BEGIN
PRINT 'Table ranking is empty';
END
END
"""

search_by_username = """
CREATE PROCEDURE SearchByUsername
    @Username NVARCHAR(50)
AS
BEGIN
IF EXISTS (
    SELECT 1 
    FROM [user] u
    INNER JOIN [apartment] a ON u.apartment_id = a.id
    INNER JOIN [house] h ON a.house_id = h.id
    INNER JOIN [street] s ON h.street_id = s.id
    INNER JOIN [city] c ON s.city_id = c.id
    WHERE u.username LIKE '%' + @Username + '%'
    )
BEGIN
    SELECT 
    u.username AS [User],
    c.name AS [City],
    s.name AS [Street],
    h.number AS [House],
        COALESCE(a.number_apartment, 'No Apartment') AS [Apartment]
    FROM [user] u
    INNER JOIN [apartment] a ON u.apartment_id = a.id
    INNER JOIN [house] h ON a.house_id = h.id
    INNER JOIN [street] s ON h.street_id = s.id
    INNER JOIN [city] c ON s.city_id = c.id
    WHERE u.username LIKE '%' + @Username + '%';
END
ELSE
BEGIN
    PRINT 'Not found user by username';
END
END
"""

search_by_street = """
CREATE PROCEDURE SearchByStreet
    @Street NVARCHAR(100)
AS
BEGIN
IF EXISTS (
    SELECT 1
    FROM [street] s
    INNER JOIN [house] h ON s.id = h.street_id
    LEFT JOIN [apartment] a ON h.id = a.house_id
    LEFT JOIN [user] u ON a.id = u.apartment_id
    WHERE s.name LIKE '%' + @Street + '%'
    )
BEGIN
    SELECT 
    u.username AS [Username],
    c.name AS [City],
    s.name AS [Street],
    h.number AS [House],
        COALESCE(a.number_apartment, 'No Apartment') AS [Apartment]
    FROM [street] s
    INNER JOIN [house] h ON s.id = h.street_id
    LEFT JOIN [apartment] a ON h.id = a.house_id
    LEFT JOIN [user] u ON a.id = u.apartment_id
    INNER JOIN [city] c ON s.city_id = c.id
    WHERE s.name LIKE '%' + @Street + '%';
END
ELSE
BEGIN
    PRINT 'Not found users by this street';
END
END
"""

search_by_fullname_apartament_num = """
CREATE PROCEDURE SearchByFullnameApartamentNum
    @Fullname NVARCHAR(50),
    @ApartmentNum NVARCHAR(20)
AS
BEGIN
IF EXISTS (
    SELECT 1 
    FROM [user_requisite] r
    INNER JOIN [user] u ON r.id = u.requisite_id
    LEFT JOIN [apartment] a ON u.apartment_id = a.id
    WHERE r.full_name LIKE '%' + @Fullname + '%'
    AND (
    (a.number_apartment = @ApartmentNum AND @ApartmentNum != 'No Apartment') 
    OR 
    (@ApartmentNum = 'No Apartment' AND u.apartment_id IS NULL)
        )
    )
BEGIN
    SELECT 
    u.username AS [Username],
    r.full_name AS [Name],
    r.phone_number AS [Phone],
    r.email AS [Email],
    c.name AS [City],
    s.name AS [Street],
    h.number AS [House],
        COALESCE(a.number_apartment, 'No Apartment') AS [Apartment]
    FROM [user_requisite] r
    INNER JOIN [user] u ON r.id = u.requisite_id
    LEFT JOIN [apartment] a ON u.apartment_id = a.id
    INNER JOIN [house] h ON a.house_id = h.id
    INNER JOIN [street] s ON h.street_id = s.id
    INNER JOIN [city] c ON s.city_id = c.id
    WHERE r.full_name LIKE '%' + @Fullname + '%'
    AND (
    (a.number_apartment = @ApartmentNum AND @ApartmentNum != 'No Apartment') 
    OR 
    (@ApartmentNum = 'No Apartment' AND u.apartment_id IS NULL)
        )  
END
ELSE
BEGIN
    PRINT 'No user with these parameters was found';
END
END
"""

search_all_tournament_for_fullname = """
CREATE PROCEDURE SearchAllTournamentFullname
    @Fullname NVARCHAR(50)
AS
BEGIN
IF EXISTS (
    SELECT 1 
    FROM [user_requisite] r
    INNER JOIN [user] u ON r.id = u.requisite_id
    LEFT JOIN [tournament] t ON u.id = t.player_id
    WHERE r.full_name LIKE '%' + @Fullname + '%'
    )
BEGIN
    SELECT
    r.full_name AS [Name],
        COALESCE(CAST(t.id AS NVARCHAR), 'No Tournament') AS [TournamentID]
    FROM [user_requisite] r
    INNER JOIN [user] u ON r.id = u.requisite_id
    LEFT JOIN [tournament] t ON u.id = t.player_id
    WHERE r.full_name LIKE '%' + @Fullname + '%';
END
ELSE
BEGIN 
    PRINT 'The user either does not exist or there were no tournaments for him';
END
END
"""

procedures = {
    "GetAllUser": get_all_user,
    "GetAllAddress": get_all_address,
    "GetAllTournament": get_all_tournament,
    "GetAllGame": get_all_game,
    "GetAllPhotoReport": get_all_photo_report,
    "GetRanking": get_ranking,
    "SearchByUsername": search_by_username,
    "SearchByStreet": search_by_street,
    "SearchByFullnameApartamentNum": search_by_fullname_apartament_num,
    "SearchAllTournamentFullname": search_all_tournament_for_fullname,
}