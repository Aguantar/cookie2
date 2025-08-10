CREATE TABLE `books` (
  `book_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `author` varchar(50) NOT NULL,
  `description` text,
  `price` decimal(10,2) NOT NULL,
  `stock` int unsigned NOT NULL DEFAULT '0',
  `cover_image` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`book_id`),
  CONSTRAINT `chk_books_price` CHECK ((`price` >= 0)),
  CONSTRAINT `chk_books_stock` CHECK ((`stock` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
