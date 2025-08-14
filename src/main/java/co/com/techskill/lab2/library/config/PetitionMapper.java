package co.com.techskill.lab2.library.config;

import co.com.techskill.lab2.library.domain.dto.PetitionDTO;
import co.com.techskill.lab2.library.domain.entity.Petition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PetitionMapper {

    Petition toEntity(PetitionDTO petitionDTO);

    PetitionDTO toDTO(Petition petition);
}
