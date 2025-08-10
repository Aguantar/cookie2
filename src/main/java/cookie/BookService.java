package cookie;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;

    public List<Book> getAllBooks() {
        return bookMapper.selectAllBooks();
    }
}
