package cookie;

import lombok.*;
import java.math.BigDecimal;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
public class Book {
    private Long bookId;
    private String title;
    private String author;
    private String description;
    private Integer price;
    private Integer stock;
    private String coverImage;
}
