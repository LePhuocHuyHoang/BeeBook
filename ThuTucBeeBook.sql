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
CREATE PROC deleteBook
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
CREATE PROC deleteComment
    @commentId BIGINT
AS
BEGIN
     DELETE FROM comment
     WHERE "comment_id" = @commentId
END
EXEC deleteComment
    @commentId = 20

--Check Comment Exists
CREATE PROC CheckCommentExists
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
		b.point_price,
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
		b.point_price,
		b.file_source,
		b.is_free
    ORDER BY
        total_rentals DESC;
END;
EXEC getTop3BestSellingBooks

--Get All Comment
CREATE PROC getAllComment(@offset BIGINT = null, @fetch BIGINT = null)
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
 CREATE PROC filterBook(@typeName NVARCHAR(255) = null, @authorName NVARCHAR(255) = null, @minPointPrice BIGINT = null, @maxPointPrice BIGINT = null)
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
			AND (@minPointPrice IS NULL OR b.point_price >= @minPointPrice)
			AND (@maxPointPrice IS NULL OR b.point_price <= @maxPointPrice)
	END

EXEC filterBook 

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
CREATE PROC filterAuthor(@birthYear INT = null, @typeName NVARCHAR(255) = null)
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
CREATE PROC filterUser(
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
Alter PROC insertComment(@userName nvarchar(250), @bookId bigint, @comment nvarchar(250))
AS
BEGIN
	DECLARE @userId BIGINT
	SELECT @userId = user_id from [user] where username = @userName
	insert into comment values(@userId, @bookId, @comment, GETDATE())
END

--Get Comment
ALTER PROC getComment(@bookId bigint, @offset bigint, @fetch bigint)
AS
BEGIN
		select username, comment, created_at from comment
		join [user] on [user].user_id = comment.user_id
		where book_id = @bookId
		order by created_at desc
		OFFSET @offset ROWS FETCH NEXT @fetch ROWS ONLY
END

exec getComment 1,0,5


--Report Book
CREATE PROC reportBook(@userId bigint, @bookId bigint, @reportContent nvarchar(250))
AS
BEGIN
	insert into report values(@userId, @bookId, @reportContent, GETDATE())
END

--Point Transaction
ALTER PROC getAllPointTransaction(@userId bigint, @offset bigint, @fetch bigint)
AS
BEGIN
		select transaction_date, points_added,type_name from point_transaction p
		join transaction_type t on p.transaction_type = t.type_id
		where user_id = @userId
		order by transaction_date desc
		OFFSET @offset ROWS FETCH NEXT @fetch ROWS ONLY
END

exec getAllPointTransaction 22,0,5

--Statistical
CREATE PROC getAllRentedBook(@userId bigint, @offset bigint, @fetch bigint)
AS
BEGIN
	select book.book_id ,book_name, rental_date, rental_receipt.point_price 
		from book join rental_receipt on book.book_id = rental_receipt.book_id where user_id = @userId
		order by rental_date desc
		OFFSET @offset ROWS FETCH NEXT @fetch ROWS ONLY
END

--Statistical By Month
CREATE PROC getRentedBookByMonth(@userId bigint, @month bigint, @year bigint, @offset bigint, @fetch bigint)
AS
BEGIN
	select book.book_id, book_name, rental_date, rental_receipt.point_price 
		from book join rental_receipt on book.book_id = rental_receipt.book_id
		where user_id = @userId and MONTH(rental_date) = @month and YEAR(rental_date) = @year
		order by rental_date desc
		OFFSET @offset ROWS FETCH NEXT @fetch ROWS ONLY
END

--Statistical By Year
CREATE PROC getRentedBookByYear(@userId bigint, @year bigint, @offset bigint, @fetch bigint)
AS
BEGIN
	select book.book_id,book_name, rental_date, rental_receipt.point_price 
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

--Sách mới: sách trong năm hiện tại 
Create PROC GETNEWBOOKS
AS
BEGIN
SELECT b.*,
    (SELECT t.type_name AS name FROM book_type bt 
	JOIN type t ON bt.type_id = t.type_id 
	WHERE bt.book_id = b.book_id FOR JSON PATH) AS types,
    (SELECT a.* FROM author a JOIN author_book ab 
	ON a.author_id = ab.author_id 
	WHERE ab.book_id = b.book_id FOR JSON PATH) AS authors
FROM book b
where year(publication_year) = year(getDate())
end;

--Gọi Proc
EXEC GETNEWBOOKS

--- Sách nổi bật: Sách top lượt mua và sách trong năm 2023 - 2024
Create PROCEDURE GetFeaturedBooks
    @NumberOfBooks INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP (@NumberOfBooks)
        b.book_id,
        b.book_name,
        b.introduce,
        b.IBSN,
        b.publication_year,
        b.publisher,
        b.total_pages,
        b.point_price,
        b.file_source,
        b.is_free,
        (
            SELECT t.type_name AS name 
            FROM book_type bt 
            JOIN type t ON bt.type_id = t.type_id 
            WHERE bt.book_id = b.book_id FOR JSON PATH
        ) AS types,
        JSON_QUERY((
            SELECT 
                a.author_id,
                a.author_name,
                a.DOB,
                a.bio
            FROM 
                author a 
            JOIN 
                author_book ab ON a.author_id = ab.author_id
            WHERE 
                ab.book_id = b.book_id
            FOR JSON PATH)
        ) AS authors,
        COUNT(rr.book_id) AS rental_count
    FROM 
        book b
    LEFT JOIN 
        rental_receipt rr ON b.book_id = rr.book_id
    WHERE 
        YEAR(b.publication_year) BETWEEN YEAR(GETDATE()) - 1 AND YEAR(GETDATE())
    GROUP BY 
        b.book_id,
        b.book_name,
        b.introduce,
        b.IBSN,
        b.publication_year,
        b.publisher,
        b.total_pages,
        b.point_price,
        b.file_source,
        b.is_free
    HAVING 
        COUNT(rr.book_id) > 0
    ORDER BY 
        rental_count DESC;
END;
--Goi Proc:
EXEC GetFeaturedBooks 1


--Trigger ADD and DEDUCT Amount when creat new Point Transaction

CREATE TRIGGER trg_update_user_points
ON point_transaction
AFTER INSERT
AS
BEGIN
    -- Kiểm tra xem có dữ liệu được chèn hay không
    IF EXISTS (SELECT 1 FROM inserted)
    BEGIN
        -- Cập nhật số điểm cho người dùng
        UPDATE u
        SET u.point = 
            CASE 
                WHEN i.transaction_type = 1 THEN u.point + i.points_added
                WHEN i.transaction_type = 2 THEN u.point - i.points_added
                ELSE u.point -- Nếu transaction_type không phải 1 hoặc 2, không thay đổi số điểm
            END
        FROM [user] u
        INNER JOIN inserted i ON u."user_id" = i."user_id";
    END
END;

--Tính trung bình Raiting
CREATE PROC averageRating
    @bookId BIGINT
AS
BEGIN
    -- Chọn AVG(rating) từ bảng rating r cho mỗi book_id
    SELECT AVG(r.rating) AS averageRating
    FROM rating r
    JOIN book b ON r.book_id = b.book_id
    WHERE b.book_id = @bookId;
END;

EXEC averageRating @bookId=1

--Get User Rating
ALTER PROC getUserRating
    @userId BIGINT,
	@bookId BIGINT
AS
BEGIN
    SELECT rating
    FROM rating r
    WHERE user_id=@userId and book_id=@bookId
END;

EXEC getUserRating 21, 1

--Get all bookmark by user
Create PROCEDURE getBookmark 
    @userId INT
AS
BEGIN
    SELECT b.*,
        (SELECT t.type_name AS name 
            FROM book_type bt 
            JOIN type t ON bt.type_id = t.type_id 
            WHERE bt.book_id = b.book_id 
            FOR JSON PATH, WITHOUT_ARRAY_WRAPPER) AS types,
        (SELECT a.author_id, a.author_name, a.DOB, a.bio 
            FROM author a 
            JOIN author_book ab ON a.author_id = ab.author_id 
            WHERE ab.book_id = b.book_id 
            FOR JSON PATH, WITHOUT_ARRAY_WRAPPER) AS authors
    FROM bookmark bm
    LEFT JOIN book b ON bm.book_id = b.book_id
    WHERE bm.user_id = @userId;
END;


