package co.com.techskill.lab2.library.actor;

import co.com.techskill.lab2.library.domain.entity.Book;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private final List<Book> books = new ArrayList<>();

    public BookService(){
        books.add(new Book("6600ab76-3", "0002005018","Clara Callan", 5, true));
        books.add(new Book("297c17d8-4", "0195153448","Classical Mythology", 3, true));
        books.add(new Book("11b553eb-b", "0399135782","The kitchen God's wife", 8, true));
        books.add(new Book("3c24c2fa-3", "0440234743","The testament", 4, true));
        books.add(new Book("eb25c2d4-7", "0393045218","The mummies of Urumchi", 5, true));
        books.add(new Book("1940136a-2", "0060973129","Decision in Normandy", 3, true));
        books.add(new Book("12a13228-0", "0345402871","Airframe", 1, true));
        books.add(new Book("51ed516f-a", "0375759778","Prague: A Novel", 2, true));
    }

    public Flux<Book> dummyFindAll(){
        return Flux.fromIterable(books);
    }

    public Mono<Book> dummyFindById(String id){
        return Mono.justOrEmpty(
                books.stream()
                        .filter(book -> book.getBookId().equals(id))
                        .findFirst()
        );
    }
}
