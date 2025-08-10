package cookie;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BookMapper {
    List<Book> selectAllBooks();
    Book findBookForCheckout(@Param("bookId") Long bookId);
    int decrementStock(@Param("bookId") Long bookId, @Param("qty") int qty);
}
