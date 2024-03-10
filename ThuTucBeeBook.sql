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

--Insert Rating
CREATE PROC insertRating(@userId bigint,@bookId bigint, @rating bigint)
AS	
BEGIN
	INSERT INTO rating values(@userId, @bookId, @rating, GETDATE())
END

--Update Rating
CREATE PROC updateRating(@userId bigint,@bookId bigint, @rating bigint)
AS
BEGIN
	update rating set rating = @rating, created_at = GETDATE() where user_id = @userId and book_id = @bookId
END
--Raiting Book
CREATE PROC ratingBook(@userId bigint,@bookId bigint, @rating bigint)
AS
BEGIN
	if exists (select * from rating where user_id = @userId and book_id = @bookId)
		BEGIN
			EXEC updateRating @userId, @bookId, @rating
		END
	ELSE
		BEGIN
			EXEC insertRating @userId, @bookId, @rating
		END
END

--Insert Comment
CREATE PROC insertComment(@userId bigint, @bookId bigint, @comment nvarchar(250))
AS
BEGIN
	insert into comment values(@userId, @bookId, @comment, GETDATE())
END

--Get Comment
CREATE PROC getComment(@bookId bigint, @offset bigint, @fetch bigint)
AS
BEGIN
		select * from comment
		where book_id = @bookId
		order by created_at desc
		OFFSET @offset ROWS FETCH NEXT @fetch ROWS ONLY
END


--Report Book
CREATE PROC reportBook(@userId bigint, @bookId bigint, @reportContent nvarchar(250))
AS
BEGIN
	insert into report values(@userId, @bookId, @reportContent, GETDATE())
END

--Point Transaction
CREATE PROC getAllPointTransaction(@userId bigint, @offset bigint, @fetch bigint)
AS
BEGIN
		select id_transaction, transaction_date, points_added,type_name from point_transaction p
		join transaction_type t on p.transaction_type = t.type_id
		where user_id = @userId
		order by transaction_date desc
		OFFSET @offset ROWS FETCH NEXT @fetch ROWS ONLY
END

--Statistical
CREATE PROC getAllRentedBook(@userId bigint, @offset bigint, @fetch bigint)
AS
BEGIN
	select book.book_id ,book_name, rental_date, point_price 
		from book join rental_receipt on book.book_id = rental_receipt.book_id where user_id = @userId
		order by rental_date desc
		OFFSET @offset ROWS FETCH NEXT @fetch ROWS ONLY
END

--Statistical By Month
CREATE PROC getRentedBookByMonth(@userId bigint, @month bigint, @year bigint, @offset bigint, @fetch bigint)
AS
BEGIN
	select book.book_id, book_name, rental_date, point_price 
		from book join rental_receipt on book.book_id = rental_receipt.book_id
		where user_id = @userId and MONTH(rental_date) = @month and YEAR(rental_date) = @year
		order by rental_date desc
		OFFSET @offset ROWS FETCH NEXT @fetch ROWS ONLY
END

--Statistical By Year
CREATE PROC getRentedBookByYear(@userId bigint, @year bigint, @offset bigint, @fetch bigint)
AS
BEGIN
	select book.book_id,book_name, rental_date, point_price 
		from book join rental_receipt on book.book_id = rental_receipt.book_id 
		where user_id = @userId and YEAR(rental_date) = @year
		order by rental_date desc
		OFFSET @offset ROWS FETCH NEXT @fetch ROWS ONLY
END


--Get Rented Book
CREATE PROC getRentedBook(@userId bigint, @month bigint = null, @year bigint = null, @offset bigint, @fetch bigint)
AS
BEGIN
	IF @month IS NULL AND @year IS NULL
	BEGIN
		EXEC getAllRentedBook @userId, @offset, @fetch
	END
	ELSE IF @month IS NULL AND @year IS NOT NULL
	BEGIN
		EXEC getRentedBookByYear @userId, @year, @offset, @fetch
	END
	ELSE
	BEGIN
		EXEC getRentedBookByMonth @userId, @month, @year, @offset, @fetch
	END
END