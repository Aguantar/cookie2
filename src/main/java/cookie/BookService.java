package cookie;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {
	
	@Autowired
    private BookMapper bookMapper;
	
	public List<Book> getAllBooks() {
        return bookMapper.selectAllBooks();
    }
}