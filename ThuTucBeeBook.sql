--AUTHOR
--Get All Author
ALTER PROC getAllAuthor
AS
BEGIN
	SELECT * FROM author
END
EXEC getAllAuthor

--Get Author By Id
ALTER PROC getAuthorById @authorid BIGINT
AS
BEGIN
    IF EXISTS (SELECT 1 FROM author WHERE author_id = @authorid)
    BEGIN
        SELECT *
        FROM author
        WHERE author_id = @authorid;
    END
    ELSE
    BEGIN
        PRINT 'Xảy ra lỗi: Không có tác giả nào có id tương ứng.';
    END
END
EXEC getAuthorById 18

--Insert Author
ALTER PROC addNewAuthor
    @authorName NVARCHAR(255),
    @dob DATE,
    @bio NVARCHAR(MAX)
AS
BEGIN
    INSERT INTO author (author_name, dob, bio)
    VALUES (@authorName, @dob, @bio);
END
EXEC addNewAuthor 
    @authorName = N'John Green', 
    @dob = '1977-08-24', 
    @bio = N'John Green is an American author and YouTube content creator. He is well known for his young adult fiction, including the bestselling novels The Fault in Our Stars and Paper Towns.'

--Update Author
ALTER PROC updateAuthor
    @authorId BIGINT,
    @authorName NVARCHAR(255),
    @dob DATE,
    @bio NVARCHAR(MAX)
AS
BEGIN
    IF EXISTS (SELECT 1 FROM author WHERE author_id = @authorId)
    BEGIN
        UPDATE author
        SET
            author_name = @authorName,
            dob = @dob,
            bio = @bio
        WHERE
            author_id = @authorId;
        PRINT 'Cập nhật thông tin tác giả thành công.';
    END
    ELSE
    BEGIN
        PRINT 'ID tác giả không tồn tại trong cơ sở dữ liệu. Không thể cập nhật.';
    END
END

EXEC updateAuthor
	@authorId = 22,
    @authorName = N'John Greennnn', 
    @dob = '1977-08-24', 
    @bio = N'John Green is an American author and YouTube content creator. He is well known for his young adult fiction, including the bestselling novels The Fault in Our Stars and Paper Towns.'

--Delete Author
CREATE PROC deleteAuthor
    @authorId BIGINT
AS
BEGIN
        -- Xóa liên kết trong bảng trung gian
        DELETE FROM author_book WHERE author_id = @authorId;
        -- Xóa thông tin tác giả từ bảng author
        DELETE FROM author WHERE author_id = @authorId;
END
EXEC deleteAuthor 15


--TYPE
--Get All Type
ALTER PROC getAllType
AS
BEGIN
	SELECT * FROM "type"
END
EXEC getAllType

--Get Type By Id
ALTER PROC getTypeById @typeid BIGINT
AS
BEGIN
    IF EXISTS (SELECT 1 FROM type WHERE "type_id" = @typeid)
    BEGIN
        SELECT *
        FROM "type"
        WHERE "type_id" = @typeid;
    END
    ELSE
    BEGIN
        PRINT 'Xảy ra lỗi: Không có thể loại sách nào có id tương ứng.';
    END
END
EXEC getTypeById 5

--Insert Type
ALTER PROC addNewType
    @typeName NVARCHAR(255),
    @description NVARCHAR(MAX)
AS
BEGIN
    INSERT INTO "type"("type_name", "description")
    VALUES (@typeName, @description);
END
EXEC addNewType 
    @typeName = N'Funny', 
    @description = 'Books that involve solving a crime or puzzle'

--Update Type
ALTER PROC updateType
    @typeId BIGINT,
    @typeName NVARCHAR(255),
    @description NVARCHAR(MAX)
AS
BEGIN
    IF EXISTS (SELECT 1 FROM "type" WHERE "type_id" = @typeId)
    BEGIN
        UPDATE "type"
        SET
            "type_name" = @typeName,
            "description" = @description
        WHERE
            "type_id" = @typeId;
        PRINT 'Cập nhật thông tin thể loại sách thành công.';
    END
    ELSE
    BEGIN
        PRINT 'ID thể loại sách không tồn tại trong cơ sở dữ liệu. Không thể cập nhật.';
    END
END

EXEC updateType
	@typeId = 6,
    @typeName = N'Funny', 
    @description = 'Genre containing works characterized by humor, entertainment, and laughter for readers'

--Delete Type
CREATE PROC deleteType
    @typeId BIGINT
AS
BEGIN
        -- Xóa liên kết trong bảng trung gian
        DELETE FROM book_type WHERE "type_id" = @typeId;
        -- Xóa thông tin loại sách từ bảng type
        DELETE FROM "type" WHERE "type_id" = @typeId;
END
EXEC deleteType 2


--BOOK
--Get All Book
ALTER PROC getAllBook
AS
BEGIN
	SELECT * FROM book
END
EXEC getAllBook

--Get Book By Id
ALTER PROC getBookById @bookid BIGINT
AS
BEGIN
    IF EXISTS (SELECT 1 FROM book WHERE book_id = @bookid)
    BEGIN
        SELECT *
        FROM book
        WHERE book_id = @bookid;
    END
    ELSE
    BEGIN
        PRINT 'Xảy ra lỗi: Không có sách nào có tương ứng.';
    END
END
EXEC getBookById 100

--Insert Book
ALTER PROC addNewBook
    @bookName NVARCHAR(255),
    @introduce bigint,
	@IBSN bigint,
	@publicationYear bigint,
	@publisher NVARCHAR(255),
	@totalPages bigint,
	@price bigint,
	@fileSource bigint,
	@isFree bigint
AS
BEGIN
    INSERT INTO book (book_name, introduce, IBSN, publication_year, publisher, total_pages, price, file_source, is_free)
    VALUES (@bookName, @introduce, @IBSN, @publicationYear, @publisher, @totalPages, @price, @fileSource, @isFree);
END
EXEC addNewBook 
    @bookName = 'The Hitchhiker Guide to the Galaxy', 
    @introduce = 21,
	@IBSN = 9780345391803,
	@publicationYear = 1992,
	@publisher = 'Del Rey Books',
	@totalPages =  224,
	@price = 10.99,
	@fileSource = 21,
	@isFree = 0

--Update Book
CREATE PROC updateBook
	@bookId bigint,
    @bookName NVARCHAR(255),
    @introduce bigint,
	@IBSN bigint,
	@publicationYear bigint,
	@publisher NVARCHAR(255),
	@totalPages bigint,
	@price bigint,
	@fileSource bigint,
	@isFree bigint
AS
BEGIN
    IF EXISTS (SELECT 1 FROM book WHERE book_id = @bookId)
    BEGIN
        UPDATE book
        SET
            book_name = @bookName,
			introduce = @introduce,
			IBSN = @IBSN,
			publication_year = @publicationYear,
			publisher = @publisher,
			total_pages = @totalPages,
			price = @price,
			file_source = @fileSource,
			is_free = @isFree
        WHERE
            book_id = @bookId;
        PRINT 'Cập nhật thông tin sách thành công.';
    END
    ELSE
    BEGIN
        PRINT 'ID sách không tồn tại trong cơ sở dữ liệu. Không thể cập nhật.';
    END
END

EXEC updateBook
	@bookId = 21,
    @bookName = 'The Hitchhiker Guide to the Galaxy and The Moon', 
    @introduce = 21,
	@IBSN = 9780345391803,
	@publicationYear = 1992,
	@publisher = 'Del Rey Books',
	@totalPages =  224,
	@price = 10.99,
	@fileSource = 21,
	@isFree = 0

--Delete Book
ALTER PROC deleteBook
    @bookId BIGINT
AS
BEGIN
		-- Xóa liên kết trong bảng trung gian author
        DELETE FROM author_book WHERE book_id = @bookId;
        -- Xóa liên kết trong bảng trung gian type
        DELETE FROM book_type WHERE book_id = @bookId;
		-- Xóa liên kết trong bảng trung gian raiting
        DELETE FROM rating WHERE book_id = @bookId;
		-- Xóa liên kết trong bảng trung gian comment
        DELETE FROM comment WHERE book_id = @bookId;
		-- Xóa liên kết trong bảng trung gian rental_receipt
        DELETE FROM rental_receipt WHERE book_id = @bookId;
		-- Xóa liên kết trong bảng trung gian report
        DELETE FROM report WHERE book_id = @bookId;
		-- Xóa liên kết trong bảng trung gian bookmark
        DELETE FROM bookmark WHERE book_id = @bookId;
        -- Xóa thông tin sách từ bảng book
        DELETE FROM book WHERE book_id = @bookId;
END
EXEC deleteBook 100


--COMMENT
--Get Comment By Id
ALTER PROC getCommentById
    @userId INT,
    @bookId INT
AS
BEGIN
	IF EXISTS (SELECT 1 FROM book WHERE book_id = @bookid)
    BEGIN
        SELECT *
        FROM comment
        WHERE "user_id" = @userId AND book_id = @bookId;
    END
	ELSE
    BEGIN
        PRINT 'Xảy ra lỗi khi lấy comment.';
    END
END

EXEC getCommentById
	 @userId = 9, 
	 @bookId = 90;


--Insert new comment
CREATE PROC addNewComment
    @userId BIGINT,
    @bookId BIGINT,
    @comment NVARCHAR(255),
    @createdAt DATETIME
AS
BEGIN
    BEGIN TRY
        INSERT INTO comment ("user_id", book_id, comment, created_at)
        VALUES (@userId, @bookId, @comment, @createdAt);

        PRINT 'Thêm bình luận thành công.';
    END TRY
    BEGIN CATCH
        PRINT 'Xảy ra lỗi khi thêm bình luận.';
    END CATCH
END 
EXEC addNewComment 
    @userId = 21, 
    @bookId = 21, 
    @comment = N'This book is amazing!', 
    @createdAt = '2023-12-25 08:00:00';

--Update Comment
ALTER PROC updateComment
    @userId BIGINT,
	@bookId BIGINT,
    @comment NVARCHAR(255),
    @createdAt DATETIME
AS
BEGIN
    BEGIN TRY
        UPDATE comment
        SET comment = @comment,
            created_at = @createdAt
        WHERE "user_id" = @userId AND book_id = @bookId;

        PRINT 'Cập nhật comment thành công.';
    END TRY
    BEGIN CATCH
        PRINT 'Xảy ra lỗi khi cập nhật comment.';
    END CATCH
END
EXEC updateComment 
    @userId = 21,
	@bookId = 21,
    @comment = N'This book is amazinggggggggggggggg!', 
    @createdAt = '2023-12-25 08:00:00';

--Delete Comment
ALTER PROC deleteComment
    @commentId BIGINT
AS
BEGIN
     DELETE FROM comment
     WHERE "comment_id" = @commentId
END
EXEC deleteComment
    @commentId = 20

--Check Comment Exists
ALTER PROC CheckCommentExists
    @commentId BIGINT,
    @Exists BIT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM comment WHERE comment_id = @commentId)
        SET @Exists = 1;
    ELSE
        SET @Exists = 0;
END
EXEC CheckCommentExists @commentId = 123;


EXEC CheckCommentExists 200
--USER
--Get All User
CREATE PROC getAllUser
AS
BEGIN
	SELECT * FROM "user"
END
EXEC getAllUser

--Get User By Id
CREATE PROC getUserById @userid BIGINT
AS
BEGIN
    IF EXISTS (SELECT 1 FROM "user" WHERE "user_id" = @userid)
    BEGIN
        SELECT *
        FROM "user"
        WHERE "user_id" = @userid;
    END
    ELSE
    BEGIN
        PRINT 'Xảy ra lỗi: Không có người dùng nào tương ứng.';
    END
END
EXEC getUserById 10

--Insert User
CREATE PROC addNewUser
    @firtsName NVARCHAR(255),
    @lastName NVARCHAR(255),
	@DOB date,
	@userName NVARCHAR(255),
	@password NVARCHAR(255),
	@point bigint,
	@gender NVARCHAR(255)
AS
BEGIN
    INSERT INTO "user"(first_name, last_name, DOB, username, "password", point, gender)
    VALUES (@firtsName, @lastName, @DOB, @userName, @password, @point, @gender);
END
EXEC addNewUser 
    @firtsName = 'John',
	@lastName = 'Doe',
	@DOB =  '1990-05-15',
	@userName = 'johndoe',
	@password = 'password123',
	@point = 100,
	@gender =  'Male'

--Update User
CREATE PROC updateUser
    @userId BIGINT,
    @firtsName NVARCHAR(255),
    @lastName NVARCHAR(255),
	@DOB date,
	@userName NVARCHAR(255),
	@password NVARCHAR(255),
	@point bigint,
	@gender NVARCHAR(255)
AS
BEGIN
    IF EXISTS (SELECT 1 FROM "user" WHERE "user_id" = @userId)
    BEGIN
        UPDATE "user"
        SET
            first_name = @firtsName,
            last_name = @lastName,
			DOB = @DOB,
			username = @userName,
			"password" = @password,
			point = @point,
			gender = @gender
        WHERE
            "user_id" = @userId;
        PRINT 'Cập nhật thông tin người dùng thành công.';
    END
    ELSE
    BEGIN
        PRINT 'ID người dùng không tồn tại trong cơ sở dữ liệu. Không thể cập nhật.';
    END
END

EXEC updateUser
	@userId = 21,
    @firtsName = 'John',
	@lastName = 'Doeeeeeee',
	@DOB =  '1990-05-15',
	@userName = 'johndoe',
	@password = 'password123',
	@point = 100,
	@gender =  'Male'

--Delete User
CREATE PROC deleteUser
    @userId BIGINT
AS
BEGIN
		-- Xóa liên kết trong bảng trung gian rental_receipt
        DELETE FROM role_user WHERE "user_id" = @userId;
		-- Xóa liên kết trong bảng trung gian bookmark
        DELETE FROM bookmark WHERE "user_id" = @userId;
		-- Xóa liên kết trong bảng trung gian report
        DELETE FROM report WHERE "user_id" = @userId;
		-- Xóa liên kết trong bảng trung gian comment
        DELETE FROM comment WHERE "user_id" = @userId;
		-- Xóa liên kết trong bảng trung gian raiting
        DELETE FROM rating WHERE "user_id" = @userId;
        -- Xóa liên kết trong bảng trung gian rental_receipt
        DELETE FROM rental_receipt WHERE "user_id" = @userId;
		-- Xóa các point_transaction liên quan
        DELETE FROM point_transaction WHERE "user_id"=@userId;
        -- Xóa thông tin người dùng từ bảng user
        DELETE FROM "user" WHERE "user_id" = @userId;
END
EXEC deleteUser 10

--Top 3 Book 
CREATE PROC getTop3BestSellingBooks
AS
BEGIN
    SELECT TOP 3 
        r.book_id,
        COUNT(r.book_id) AS total_rentals,
        b.book_name,
		b.introduce,
		b.IBSN,
		b.publication_year,
		b.publisher,
		b.total_pages,
		b.price,
		b.file_source,
		b.is_free
    FROM
        rental_receipt r
    INNER JOIN
        book b ON r.book_id = b.book_id
    GROUP BY
        r.book_id,
		b.book_name,
		b.introduce,
		b.IBSN,
		b.publication_year,
		b.publisher,
		b.total_pages,
		b.price,
		b.file_source,
		b.is_free
    ORDER BY
        total_rentals DESC;
END;
EXEC getTop3BestSellingBooks

--Get All Comment
ALTER PROC getAllComment(@offset BIGINT = null, @fetch BIGINT = null)
AS
BEGIN
	IF @offset is null and @fetch is null
	BEGIN
		SELECT comment_id, book_id, "user_id", comment, created_at 
		FROM comment
		ORDER BY created_at DESC
	END
	ELSE
	BEGIN
		SELECT comment_id, book_id, "user_id", comment, created_at 
		FROM comment
		ORDER BY created_at DESC
		OFFSET @offset ROWS FETCH NEXT @fetch ROWS ONLY
	END
END
 
 EXEC getAllComment

 --Book Filter.
 ALTER PROC filterBook(@typeName NVARCHAR(255) = null, @authorName NVARCHAR(255) = null, @minPrice BIGINT = null, @maxPrice BIGINT = null)
 AS
	BEGIN
		SELECT b.*
		FROM book b
		JOIN book_type bt ON b.book_id = bt.book_id
		JOIN "type" t ON t."type_id" = bt."type_id"
		JOIN author_book ab ON ab.book_id = b.book_id
		JOIN author a ON a.author_id = ab.author_id
		WHERE (@typeName IS NULL OR t."type_name" = @typeName)
			AND (@authorName IS NULL OR a.author_name = @authorName)
			AND (@minPrice IS NULL OR b.price >= @minPrice)
			AND (@maxPrice IS NULL OR b.price <= @maxPrice)
	END

EXEC filterBook null, "J.D. Salinger", 15, 100

--Top 3 Author 
CREATE PROC getTop3BestAuthors
AS
BEGIN
    SELECT TOP 3 
        COUNT(r.book_id) AS total_rentals,
		a.author_id,
        a.author_name,
		a.DOB,
		a.bio
    FROM
        rental_receipt r
    INNER JOIN
        book b ON r.book_id = b.book_id
	INNER JOIN
		author_book ab ON b.book_id = ab.book_id
	INNER JOIN
		author a ON ab.author_id = a.author_id
    GROUP BY
        a.author_id,
        a.author_name,
		a.DOB,
		a.bio
    ORDER BY
        total_rentals DESC;
END;
EXEC getTop3BestAuthors

--Author Filter.
ALTER PROC filterAuthor(@birthYear INT = null, @typeName NVARCHAR(255) = null)
AS
	BEGIN
		SELECT DISTINCT a.*
		FROM author a
		JOIN author_book ab ON ab.author_id = a.author_id
		JOIN book b ON b.book_id = ab.book_id
		JOIN book_type bt ON b.book_id = bt.book_id
		JOIN "type" t ON t."type_id" = bt."type_id"
		WHERE (@birthYear IS NULL OR YEAR(a.DOB) = @birthYear)
			AND (@typeName IS NULL OR t."type_name" = @typeName)
	END
EXEC filterAuthor 1903, "Science Fiction"

--Top 3 Type
CREATE PROC getTop3BestTypes
AS
BEGIN
    SELECT TOP 3 
        COUNT(r.book_id) AS total_rentals,
		t."type_id",
        t."type_name",
		t."description"
    FROM
        rental_receipt r
    INNER JOIN
        book b ON r.book_id = b.book_id
	INNER JOIN
		book_type bt ON b.book_id = bt.book_id
	INNER JOIN
		type t ON bt."type_id" = t."type_id"
    GROUP BY
        t."type_id",
        t."type_name",
		t."description"
    ORDER BY
        total_rentals DESC;
END;
EXEC getTop3BestTypes

 --User Filter.
ALTER PROC filterUser(
    @gender NVARCHAR(255) = null,
    @DOB INT = null,
    @minPoint BIGINT = null,
    @maxPoint BIGINT = null
)
AS
BEGIN
    DECLARE @validGender NVARCHAR(255);

    -- Chỉ chấp nhận giá trị Male hoặc Female
    IF (@gender IS NOT NULL AND @gender IN ('Male', 'Female'))
        SET @validGender = @gender;
    ELSE
        SET @validGender = NULL;

    SELECT *
    FROM "user"
    WHERE (@validGender IS NULL OR gender = @validGender)
        AND (@DOB IS NULL OR YEAR(DOB) = @DOB)
        AND (@minPoint IS NULL OR point >= @minPoint)
        AND (@maxPoint IS NULL OR point <= @maxPoint)
END

EXEC filterUser 

--Top 3 User
CREATE PROC getTop3BestUsers
AS
BEGIN
    SELECT TOP 3 
        COUNT(r."user_id") AS total_purchases,
        u."user_id",
        u.first_name,
		u.last_name,
        u.DOB,
		u.username,
		u."password",
		u.point,
        u.gender
    FROM
        rental_receipt r
    INNER JOIN
        "user" u ON r."user_id" = u."user_id"
    GROUP BY
        u."user_id",
        u.first_name,
		u.last_name,
        u.DOB,
		u.username,
		u."password",
		u.point,
        u.gender
    ORDER BY
        total_purchases DESC;
END;

EXEC getTop3BestUsers