package co.com.techskill.lab2.library.service.dummy;

import co.com.techskill.lab2.library.domain.dto.BookDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

//For challenges bc there's no database behind
@Service
public class BookService {

    private final List<BookDTO> books = new ArrayList<>();

    public BookService(){
        books.add(new BookDTO("6600ab76-3", "0002005018","Clara Callan", 5, true));
        books.add(new BookDTO("297c17d8-4", "0195153448","Classical Mythology", 3, true));
        books.add(new BookDTO("11b553eb-b", "0399135782","The kitchen God's wife", 8, true));
        books.add(new BookDTO("3c24c2fa-3", "0440234743","The testament", 4, true));
        books.add(new BookDTO("eb25c2d4-7", "0393045218","The mummies of Urumchi", 5, true));
        books.add(new BookDTO("1940136a-2", "0060973129","Decision in Normandy", 3, true));
        books.add(new BookDTO("12a13228-0", "0345402871","Airframe", 1, true));
        books.add(new BookDTO("51ed516f-a", "0375759778","Prague: A Novel", 2, true));
    }

    public Flux<BookDTO> findAll(){
        return Flux.fromIterable(books);
    }

    public Mono<BookDTO> findByBookId(String id){
        return Mono.justOrEmpty(
                books.stream()
                        .filter(bookDTO -> bookDTO.getBookId().equals(id))
                        .findFirst()
        );
    }
}
