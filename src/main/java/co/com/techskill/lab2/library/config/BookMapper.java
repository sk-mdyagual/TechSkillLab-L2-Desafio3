package co.com.techskill.lab2.library.config;

import co.com.techskill.lab2.library.domain.dto.BookDTO;
import co.com.techskill.lab2.library.domain.entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDTO toEntity(BookDTO bookDTO);

    BookDTO toDTO(Book book);
}
