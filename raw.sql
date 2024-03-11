CREATE TABLE "comment"(
	"comment_id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "book_id" BIGINT NOT NULL,
    "comment" NVARCHAR(255) NOT NULL,
    "created_at" DATETIME NOT NULL
);
ALTER TABLE
    "comment" ADD CONSTRAINT "comment_comment_id_primary" PRIMARY KEY("comment_id");
CREATE TABLE "role_user"(
    "role_id" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL
);
CREATE TABLE "author"(
    "author_id" BIGINT NOT NULL,
    "author_name" NVARCHAR(100) NOT NULL,
    "DOB" DATE NOT NULL,
    "bio" NVARCHAR(255) NOT NULL
);
ALTER TABLE
    "author" ADD CONSTRAINT "author_author_id_primary" PRIMARY KEY("author_id");
CREATE TABLE "transaction_type"(
    "type_id" BIGINT NOT NULL,
    "type_name" NVARCHAR(255) NOT NULL
);
ALTER TABLE
    "transaction_type" ADD CONSTRAINT "transaction_type_type_id_primary" PRIMARY KEY("type_id");
CREATE TABLE "author_book"(
    "book_id" BIGINT NOT NULL,
    "author_id" BIGINT NOT NULL
);
CREATE TABLE "user"(
    "user_id" BIGINT NOT NULL,
    "first_name" NVARCHAR(255) NOT NULL,
    "last_name" NVARCHAR(255) NOT NULL,
    "DOB" DATE NOT NULL,
    "username" NVARCHAR(255) NOT NULL,
    "password" NVARCHAR(255) NOT NULL,
    "point" BIGINT NOT NULL,
    "gender" NVARCHAR(255) NOT NULL
);
ALTER TABLE
    "user" ADD CONSTRAINT "user_user_id_primary" PRIMARY KEY("user_id");
CREATE TABLE "point_transaction"(
    "id_transaction" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL,
    "transaction_date" DATETIME NOT NULL,
    "points_added" BIGINT NOT NULL,
    "transaction_type" BIGINT NOT NULL
);
ALTER TABLE
    "point_transaction" ADD CONSTRAINT "point_transaction_id_transaction_primary" PRIMARY KEY("id_transaction");
CREATE TABLE "book"(
    "book_id" BIGINT NOT NULL,
    "book_name" NVARCHAR(255) NOT NULL,
    "introduce" NVARCHAR(255) NOT NULL,
    "IBSN" BIGINT NOT NULL,
    "publication_year" DATE NOT NULL,
    "publisher" NVARCHAR(255) NOT NULL,
    "total_pages" BIGINT NOT NULL,
    "point_price" BIGINT NOT NULL,
    "file_source" NVARCHAR(255) NOT NULL,
    "is_free" BIGINT NOT NULL
);
ALTER TABLE
    "book" ADD CONSTRAINT "book_book_id_primary" PRIMARY KEY("book_id");
CREATE TABLE "book_type"(
    "book_id" BIGINT NOT NULL,
    "type_id" BIGINT NOT NULL
);
CREATE TABLE "type"(
    "type_id" BIGINT NOT NULL,
    "type_name" NVARCHAR(255) NOT NULL,
    "description" NVARCHAR(255) NOT NULL
);
ALTER TABLE
    "type" ADD CONSTRAINT "type_type_id_primary" PRIMARY KEY("type_id");
CREATE TABLE "rating"(
    "user_id" BIGINT NOT NULL,
    "book_id" BIGINT NOT NULL,
    "rating" INT NOT NULL,
    "created_at" DATETIME NOT NULL
);
CREATE TABLE "report"(
    "user_id" BIGINT NOT NULL,
    "book_id" BIGINT NOT NULL,
    "report_content" NVARCHAR(255) NOT NULL,
    "created_at" DATETIME NOT NULL
);
CREATE TABLE "rental_receipt"(
    "user_id" BIGINT NOT NULL,
    "book_id" BIGINT NOT NULL,
    "rental_date" DATETIME NOT NULL,
    "point_price" BIGINT NOT NULL
);
CREATE TABLE "role"(
    "role_id" BIGINT NOT NULL,
    "role_name" NVARCHAR(255) NOT NULL,
    "description" NVARCHAR(255) NOT NULL
);
ALTER TABLE
    "role" ADD CONSTRAINT "role_role_id_primary" PRIMARY KEY("role_id");
CREATE TABLE "bookmark"(
    "user_id" BIGINT NOT NULL,
    "book_id" BIGINT NOT NULL
);
ALTER TABLE
    "report" ADD CONSTRAINT "report_book_id_foreign" FOREIGN KEY("book_id") REFERENCES "book"("book_id");
ALTER TABLE
    "point_transaction" ADD CONSTRAINT "point_transaction_transaction_type_foreign" FOREIGN KEY("transaction_type") REFERENCES "transaction_type"("type_id");
ALTER TABLE
    "rental_receipt" ADD CONSTRAINT "rental_receipt_book_id_foreign" FOREIGN KEY("book_id") REFERENCES "book"("book_id");
ALTER TABLE
    "bookmark" ADD CONSTRAINT "bookmark_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("user_id");
ALTER TABLE
    "comment" ADD CONSTRAINT "comment_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("user_id");
ALTER TABLE
    "rental_receipt" ADD CONSTRAINT "rental_receipt_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("user_id");
ALTER TABLE
    "rating" ADD CONSTRAINT "rating_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("user_id");
ALTER TABLE
    "book_type" ADD CONSTRAINT "book_type_type_id_foreign" FOREIGN KEY("type_id") REFERENCES "type"("type_id");
ALTER TABLE
    "role_user" ADD CONSTRAINT "role_user_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("user_id");
ALTER TABLE
    "comment" ADD CONSTRAINT "comment_book_id_foreign" FOREIGN KEY("book_id") REFERENCES "book"("book_id");
ALTER TABLE
    "role_user" ADD CONSTRAINT "role_user_role_id_foreign" FOREIGN KEY("role_id") REFERENCES "role"("role_id");
ALTER TABLE
    "bookmark" ADD CONSTRAINT "bookmark_book_id_foreign" FOREIGN KEY("book_id") REFERENCES "book"("book_id");
ALTER TABLE
    "book_type" ADD CONSTRAINT "book_type_book_id_foreign" FOREIGN KEY("book_id") REFERENCES "book"("book_id");
ALTER TABLE
    "author_book" ADD CONSTRAINT "author_book_book_id_foreign" FOREIGN KEY("book_id") REFERENCES "book"("book_id");
ALTER TABLE
    "rating" ADD CONSTRAINT "rating_book_id_foreign" FOREIGN KEY("book_id") REFERENCES "book"("book_id");
ALTER TABLE
    "point_transaction" ADD CONSTRAINT "point_transaction_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("user_id");
ALTER TABLE
    "report" ADD CONSTRAINT "report_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "user"("user_id");
-- Chèn dữ liệu vào bảng role
INSERT INTO role (role_id, role_name, description) VALUES
(1, 'Admin', 'Administrator role with full access'),
(2, 'Moderator', 'Moderator role with limited access'),
(3, 'User', 'Regular user role with basic access');

-- Chèn dữ liệu vào bảng "user"
INSERT INTO "user"("user_id", "first_name", "last_name", "DOB", "username", "password", "point", "gender") VALUES
(1, 'John', 'Doe', '1990-01-01', 'john.doe', 'password1', 100, 'Male'),
(2, 'Jane', 'Doe', '1991-02-02', 'jane.doe', 'password2', 150, 'Female'),
(3, 'Alice', 'Smith', '1992-03-03', 'alice.smith', 'password3', 200, 'Female'),
(4, 'Bob', 'Johnson', '1993-04-04', 'bob.johnson', 'password4', 50, 'Male'),
(5, 'Charlie', 'Williams', '1994-05-05', 'charlie.williams', 'password5', 100, 'Male'),
(6, 'David', 'Brown', '1995-06-06', 'david.brown', 'password6', 250, 'Male'),
(7, 'Emily', 'Davis', '1996-07-07', 'emily.davis', 'password7', 100, 'Female'),
(8, 'Ethan', 'Miller', '1997-08-08', 'ethan.miller', 'password8', 150, 'Male'),
(9, 'Olivia', 'Wilson', '1998-09-09', 'olivia.wilson', 'password9', 300, 'Female'),
(10, 'James', 'Moore', '1999-10-10', 'james.moore', 'password10', 50, 'Male'),
(11, 'Sophia', 'Taylor', '2000-11-11', 'sophia.taylor', 'password11', 150, 'Female'),
(12, 'Logan', 'Anderson', '2001-12-12', 'logan.anderson', 'password12', 200, 'Male'),
(13, 'Ava', 'Thomas', '2002-01-13', 'ava.thomas', 'password13', 350, 'Female'),
(14, 'Mia', 'Jackson', '2003-02-14', 'mia.jackson', 'password14', 100, 'Female'),
(15, 'Noah', 'White', '2004-03-15', 'noah.white', 'password15', 150, 'Male'),
(16, 'Emma', 'Harris', '2005-04-16', 'emma.harris', 'password16', 400, 'Female'),
(17, 'Liam', 'Martin', '2006-05-17', 'liam.martin', 'password17', 100, 'Male'),
(18, 'Aiden', 'Thompson', '2007-06-18', 'aiden.thompson', 'password18', 50, 'Male'),
(19, 'Isabella', 'Garcia', '2008-07-19', 'isabella.garcia', 'password19', 200, 'Female'),
(20, 'Charlotte', 'Martinez', '2009-08-20', 'charlotte.martinez', 'password20', 250, 'Female');



-- Chèn dữ liệu vào bảng book
INSERT INTO book (book_id, book_name, introduce, IBSN, publication_year, publisher, total_pages, point_price, file_source, is_free) 
VALUES 
(1, N'The Great Gatsby', N'A novel by F. Scott Fitzgerald', 9780743273565, '2021-05-10', N'Scribner', 180, 150000, N'/books/great_gatsby.pdf', 0),
(2, N'To Kill a Mockingbird', N'A novel by Harper Lee', 9780061120084, '2019-09-15', N'Harper Perennial Modern Classics', 281, 125000, N'/books/to_kill_a_mockingbird.pdf', 0),
(3, N'1984', N'A novel by George Orwell', 9780451524935, '2020-11-03', N'Signet Classics', 328, 180000, N'/books/1984.pdf', 0),
(4, N'Pride and Prejudice', N'A novel by Jane Austen', 9780679783268, '2018-06-12', N'Modern Library', 279, 120000, N'/books/pride_and_prejudice.pdf', 0),
(5, N'The Catcher in the Rye', N'A novel by J.D. Salinger', 9780316769488, '2022-02-28', N'Back Bay Books', 277, 110000, N'/books/the_catcher_in_the_rye.pdf', 0),
(6, N'To Kill a Mockingbird', N'A novel by Harper Lee', 9780061120084, '2019-09-15', N'Harper Perennial Modern Classics', 281, 125000, N'/books/to_kill_a_mockingbird.pdf', 0),
(7, N'Animal Farm', N'A novel by George Orwell', 9780451526342, '2021-08-24', N'Signet Classics', 144, 90000, N'/books/animal_farm.pdf', 0),
(8, N'Jane Eyre', N'A novel by Charlotte Bronte', 9780141441146, '2017-03-07', N'Penguin Classics', 624, 210000, N'/books/jane_eyre.pdf', 0),
(9, N'The Great Gatsby', N'A novel by F. Scott Fitzgerald', 9780743273565, '2021-05-10', N'Scribner', 180, 150000, N'/books/great_gatsby.pdf', 0),
(10, N'The Lord of the Rings', N'A novel by J.R.R. Tolkien', 9780544003415, '2019-06-11', N'Houghton Mifflin Harcourt', 1178, 280000, N'/books/lord_of_the_rings.pdf', 0),
(11, N'To Kill a Mockingbird', N'A novel by Harper Lee', 9780061120084, '2019-09-15', N'Harper Perennial Modern Classics', 281, 125000, N'/books/to_kill_a_mockingbird.pdf', 0),
(12, N'1984', N'A novel by George Orwell', 9780451524935, '2020-11-03', N'Signet Classics', 328, 180000, N'/books/1984.pdf', 0),
(13, N'Pride and Prejudice', N'A novel by Jane Austen', 9780679783268, '2018-06-12', N'Modern Library', 279, 120000, N'/books/pride_and_prejudice.pdf', 0),
(14, N'The Catcher in the Rye', N'A novel by J.D. Salinger', 9780316769488, '2022-02-28', N'Back Bay Books', 277, 110000, N'/books/the_catcher_in_the_rye.pdf', 0),
(15, N'Animal Farm', N'A novel by George Orwell', 9780451526342, '2021-08-24', N'Signet Classics', 144, 90000, N'/books/animal_farm.pdf', 0),
(16, N'Jane Eyre', N'A novel by Charlotte Bronte', 9780141441146, '2017-03-07', N'Penguin Classics', 624, 210000, N'/books/jane_eyre.pdf', 0),
(17, N'The Great Gatsby', N'A novel by F. Scott Fitzgerald', 9780743273565, '2021-05-10', N'Scribner', 180, 150000, N'/books/great_gatsby.pdf', 0),
(18, N'The Lord of the Rings', N'A novel by J.R.R. Tolkien', 9780544003415, '2019-06-11', N'Houghton Mifflin Harcourt', 1178, 280000, N'/books/lord_of_the_rings.pdf', 0),
(19, N'1984', N'A novel by George Orwell', 9780451524935, '2020-11-03', N'Signet Classics', 328, 180000, N'/books/1984.pdf', 0),
(20, N'Pride and Prejudice', N'A novel by Jane Austen', 9780679783268, '2018-06-12', N'Modern Library', 279, 120000, N'/books/pride_and_prejudice.pdf', 0);


-- Chèn dữ liệu vào bảng comment
INSERT INTO comment(comment_id,user_id, book_id, comment, created_at) VALUES
(1,1, 1, 'Great book!', '2023-01-05'),
(2,2, 2, 'Enjoyed reading it.', '2023-02-10'),
(3,3, 3, 'Highly recommend!', '2023-03-15'),
(4,4, 4, 'Good but not great.', '2023-04-20'),
(5,5, 5, 'One of my favorites!', '2023-05-25'),
(6,6, 6, 'Interesting story.', '2023-06-30'),
(7,7, 7, 'Must-read for fantasy fans.', '2023-07-05'),
(8,8, 8, 'Classic literature.', '2023-08-10'),
(9,9, 9, 'Life-changing book.', '2023-09-15'),
(10,10, 10, 'Not what I expected.', '2023-10-20'),
(11,11, 11, 'Funny and entertaining.', '2023-11-25'),
(12,12, 12, 'Absolutely loved it!', '2023-12-30'),
(13,13, 13, 'Thought-provoking.', '2024-01-05'),
(14,14, 14, 'Couldn''t put it down!', '2024-02-10'),
(15,15, 15, 'Well-written.', '2024-03-15'),
(16,16, 16, 'A classic!', '2024-04-20'),
(17,17, 17, 'Decent read.', '2024-05-25'),
(18,18, 18, 'Engaging story.', '2024-06-30'),
(19,19, 19, 'Beautifully written.', '2024-07-05'),
(20,20, 20, 'Enjoyable read.', '2024-08-10');

-- Chèn dữ liệu vào bảng raing
INSERT INTO rating(user_id, book_id, rating, created_at) VALUES
(1, 1, 4, '2023-01-05'),
(2, 2, 5, '2023-02-10'),
(3, 3, 2, '2023-03-15'),
(4, 4, 4, '2023-04-20'),
(5, 5, 5, '2023-05-25'),
(6, 6, 2, '2023-06-30'),
(7, 7, 4, '2023-07-05'),
(8, 8, 3, '2023-08-10'),
(9, 9, 2, '2023-09-15'),
(10, 10, 1, '2023-10-20'),
(11, 11, 3, '2023-11-25'),
(12, 12, 4, '2023-12-30'),
(13, 13, 5, '2024-01-05'),
(14, 14, 2, '2024-02-10'),
(15, 15, 3, '2024-03-15'),
(16, 16, 2, '2024-04-20'),
(17, 17, 1, '2024-05-25'),
(18, 18, 3, '2024-06-30'),
(19, 19, 3, '2024-07-05'),
(20, 20, 5, '2024-08-10');


-- Chèn dữ liệu vào bảng type
INSERT INTO type (type_id, type_name, description) VALUES
(1, 'Fiction', 'Books that are made up and not true'),
(2, 'Non-Fiction', 'Books that are based on facts and real events'),
(3, 'Fantasy', 'Books that contain elements of magic and mythical creatures'),
(4, 'Science Fiction', 'Books that are based on imaginative scientific concepts'),
(5, 'Mystery', 'Books that involve solving a crime or puzzle');

-- Chèn dữ liệu vào bảng report
INSERT INTO report (user_id, book_id, report_content, created_at) VALUES
(1, 3, 'Inappropriate content.', '2023-01-05'),
(2, 6, 'Spam.', '2023-02-10'),
(3, 9, 'Not as described.', '2023-03-15'),
(4, 12, 'Plagiarism.', '2023-04-20'),
(5, 15, 'Hate speech.', '2023-05-25'),
(6, 18, 'Copyright infringement.', '2023-06-30'),
(7, 1, 'Violence.', '2023-07-05'),
(8, 4, 'Sexual content.', '2023-08-10'),
(9, 7, 'Misinformation.', '2023-09-15'),
(10, 10, 'Scam.', '2023-10-20'),
(11, 13, 'Fraud.', '2023-11-25'),
(12, 16, 'Bullying.', '2023-12-30'),
(13, 19, 'Privacy violation.', '2024-01-05'),
(14, 2, 'Offensive language.', '2024-02-10'),
(15, 5, 'Duplicated content.', '2024-03-15'),
(16, 8, 'Identity theft.', '2024-04-20'),
(17, 11, 'Impersonation.', '2024-05-25'),
(18, 14, 'Slander.', '2024-06-30'),
(19, 17, 'Defamation.', '2024-07-05'),
(20, 20, 'Harassment.', '2024-08-10');

-- Chèn dữ liệu vào bảng author
INSERT INTO author (author_id, author_name, DOB, bio) VALUES
(1, 'F. Scott Fitzgerald', '1896-09-24', 'Francis Scott Key Fitzgerald was an American fiction writer, whose works helped to illustrate the flamboyance and excess of the Jazz Age.'),
(2, 'Harper Lee', '1926-04-28', 'Harper Lee was an American novelist widely known for To Kill a Mockingbird, published in 1960.'),
(3, 'George Orwell', '1903-06-25', 'Eric Arthur Blair, known by his pen name George Orwell, was an English novelist, essayist, journalist, and critic.'),
(4, 'Jane Austen', '1775-12-16', 'Jane Austen was an English novelist known primarily for her six major novels, which interpret, critique and comment upon the British landed gentry at the end of the 18th century.'),
(5, 'J.D. Salinger', '1919-01-01', 'Jerome David Salinger was an American writer best known for his novel The Catcher in the Rye.'),
(6, 'George Orwell', '1903-06-25', 'Eric Arthur Blair, known by his pen name George Orwell, was an English novelist, essayist, journalist, and critic.'),
(7, 'J.R.R. Tolkien', '1892-01-03', 'John Ronald Reuel Tolkien was an English writer, poet, philologist, and academic, best known as the author of the classic high fantasy works The Hobbit, The Lord of the Rings, and The Silmarillion.'),
(8, 'William Golding', '1911-09-19', 'Sir William Gerald Golding was a British novelist, playwright, and poet. He was awarded the Nobel Prize in Literature for his outstanding contribution to literature in 1983.'),
(9, 'Aldous Huxley', '1894-07-26', 'Aldous Leonard Huxley was an English writer and philosopher. He authored nearly fifty books—both novels and non-fiction works—as well as wide-ranging essays, narratives, and poems.'),
(10, 'C.S. Lewis', '1898-11-29', 'Clive Staples Lewis was a British writer and lay theologian. He held academic positions in English literature at both Oxford University and Cambridge University.'),
(11, 'Douglas Adams', '1952-03-11', 'Douglas Noel Adams was an English author, screenwriter, essayist, humorist, satirist, and dramatist.'),
(12, 'Mary Shelley', '1797-08-30', 'Mary Wollstonecraft Shelley was an English novelist who wrote the Gothic novel Frankenstein; or, The Modern Prometheus.'),
(13, 'Herman Melville', '1819-08-01', 'Herman Melville was an American novelist, short story writer, and poet of the American Renaissance period.'),
(14, 'Oscar Wilde', '1854-10-16', 'Oscar Fingal O''Flahertie Wills Wilde was an Irish poet and playwright. After writing in different forms throughout the 1880s, he became one of London''s most popular playwrights in the early 1890s.'),
(15, 'Homer', '1800-1-1', 'Homer is the presumed author of the Iliad and the Odyssey, two epic poems that are the central works of ancient Greek literature.'),
(16, 'Lewis Carroll', '1832-01-27', 'Charles Lutwidge Dodgson, better known by his pen name Lewis Carroll, was an English writer of children''s fiction, notably Alice''s Adventures in Wonderland and its sequel Through the Looking-Glass.'),
(17, 'Mark Twain', '1835-11-30', 'Samuel Langhorne Clemens, known by his pen name Mark Twain, was an American writer, humorist, entrepreneur, publisher, and lecturer.'),
(18, 'Bram Stoker', '1847-11-08', 'Abraham "Bram" Stoker was an Irish author, best known today for his 1897 Gothic horror novel Dracula.'),
(19, 'Homer', '1800-1-1', 'Homer is the presumed author of the Iliad and the Odyssey, two epic poems that are the central works of ancient Greek literature.'),
(20, 'Alexandre Dumas', '1802-07-24', 'Alexandre Dumas, also known as Alexandre Dumas père, was a French writer. His works have been translated into many languages, and he is one of the most widely read French authors.');

-- Chèn dữ liệu vào bảng bookmark
INSERT INTO bookmark (user_id, book_id) VALUES
(1, 5),
(2, 6),
(3, 7),
(4, 8),
(5, 9),
(6, 10),
(7, 11),
(8, 12),
(9, 13),
(10, 14),
(11, 15),
(12, 16),
(13, 17),
(14, 18),
(15, 19),
(16, 20),
(17, 1),
(18, 2),
(19, 3),
(20, 4);


-- Chèn dữ liệu vào bảng author_book

INSERT INTO author_book (book_id, author_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10),
(11, 11),
(12, 12),
(13, 13),
(14, 14),
(15, 15),
(16, 16),
(17, 17),
(18, 18),
(19, 19),
(20, 20);


-- Chèn dữ liệu vào bảng role_user
INSERT INTO role_user (role_id, user_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(3, 4),
(3, 5),
(3, 6),
(3, 7),
(3, 8),
(3, 9),
(3, 10),
(3, 11),
(3, 12),
(3, 13),
(3, 14),
(3, 15),
(3, 16),
(3, 17),
(3, 18),
(3, 19),
(3, 20);




-- Chèn dữ liệu vào bảng transaction_type
INSERT INTO transaction_type (type_id, type_name) VALUES
(1, 'Add Points'),
(2, 'Deduct Points');


-- Chèn dữ liệu vào bảng book_type
INSERT INTO book_type (book_id, type_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
(6, 1),
(7, 1),
(8, 1),
(9, 1),
(10, 1),
(11, 1),
(12, 1),
(13, 1),
(14, 1),
(15, 1),
(16, 1),
(17, 1),
(18, 1),
(19, 1),
(20, 1);


-- Chèn dữ liệu vào bảng point_transaction
INSERT INTO point_transaction (id_transaction, user_id, transaction_date, points_added, transaction_type) VALUES
(1, 1, '2023-01-01', 100, 1),
(2, 2, '2023-02-01', 50, 1),
(3, 3, '2023-03-01', 150, 1),
(4, 4, '2023-04-01', 75, 1),
(5, 5, '2023-05-01', 125, 1),
(6, 6, '2023-06-01', 200, 1),
(7, 7, '2023-07-01', 100, 1),
(8, 8, '2023-08-01', 50, 1),
(9, 9, '2023-09-01', 250, 1),
(10, 10, '2023-10-01', 25, 1),
(11, 11, '2023-11-01', 100, 1),
(12, 12, '2023-12-01', 175, 1),
(13, 13, '2024-01-01', 300, 1),
(14, 14, '2024-02-01', 100, 1),
(15, 15, '2024-03-01', 150, 1),
(16, 16, '2024-04-01', 400, 1),
(17, 17, '2024-05-01', 100, 1),
(18, 18, '2024-06-01', 50, 1),
(19, 19, '2024-07-01', 200, 1),
(20, 20, '2024-08-01', 250, 1);

-- Chèn dữ liệu vào bảng rental_receipt
INSERT INTO rental_receipt (user_id, book_id, rental_date, point_price) VALUES
(1, 3, '2023-01-01', 10),
(2, 6, '2023-02-01', 8),
(3, 9, '2023-03-01', 12),
(4, 12, '2023-04-01', 9),
(5, 15, '2023-05-01', 10),
(6, 18, '2023-06-01', 8),
(7, 1, '2023-07-01', 15),
(8, 4, '2023-08-01', 10),
(9, 7, '2023-09-01', 14),
(10, 10, '2023-10-01', 12),
(11, 13, '2023-11-01', 14),
(12, 16, '2023-12-01', 9),
(13, 19, '2024-01-01', 15),
(14, 2, '2024-02-01', 12),
(15, 5, '2024-03-01', 10),
(16, 8, '2024-04-01', 14),
(17, 11, '2024-05-01', 11),
(18, 14, '2024-06-01', 10),
(19, 17, '2024-07-01', 13),
(20, 20, '2024-08-01', 12);