package cookie;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper {
    List<Book> selectAllBooks();
}