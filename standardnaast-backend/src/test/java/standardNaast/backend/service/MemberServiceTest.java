package standardNaast.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.domain.Person;
import standardNaast.backend.dto.MemberCreateUpdateDto;
import standardNaast.backend.dto.MemberDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.MemberMapper;
import standardNaast.backend.repository.PersonRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Spy
    private MemberMapper memberMapper = Mappers.getMapper(MemberMapper.class);

    @InjectMocks
    private MemberServiceImpl memberService;

    private Person samplePerson;
    private MemberCreateUpdateDto sampleDto;

    @BeforeEach
    void setUp() {
        this.samplePerson = Person.builder()
                .id(1L)
                .name("Dupont")
                .firstname("Jean")
                .email("jean.dupont@test.com")
                .memberNumber(42L)
                .city("Naast")
                .postalCode("7191")
                .birthdate(LocalDate.of(1990, 5, 15))
                .build();

        this.sampleDto = new MemberCreateUpdateDto(
                "Dupont",
                "Jean",
                "Rue de Naast, 1",
                "7191",
                "Naast",
                LocalDate.of(1990, 5, 15),
                "jean.dupont@test.com",
                "0470123456",
                null,
                null,
                null,
                42L,
                false,
                false
        );
    }

    @Test
    void getMembers_withoutSearch_shouldReturnAllPaged() {
        final Pageable pageable = PageRequest.of(0, 10);
        when(this.personRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(this.samplePerson)));

        final Page<MemberDto> result = this.memberService.getMembers(null, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Dupont");
        verify(this.personRepository).findAll(pageable);
    }

    @Test
    void getMembers_withSearch_shouldCallSearchMembers() {
        final Pageable pageable = PageRequest.of(0, 10);
        when(this.personRepository.searchMembers("Dupont", pageable)).thenReturn(new PageImpl<>(List.of(this.samplePerson)));

        final Page<MemberDto> result = this.memberService.getMembers("Dupont", pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Dupont");
        verify(this.personRepository).searchMembers("Dupont", pageable);
    }

    @Test
    void getMemberById_whenExists_shouldReturnMember() {
        when(this.personRepository.findById(1L)).thenReturn(Optional.of(this.samplePerson));

        final MemberDto result = this.memberService.getMemberById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Dupont");
    }

    @Test
    void getMemberById_whenNotFound_shouldThrowException() {
        when(this.personRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.memberService.getMemberById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void createMember_shouldAssignMemberNumberAndSave() {
        when(this.personRepository.save(any(Person.class))).thenAnswer(invocation -> {
            final Person p = invocation.getArgument(0);
            p.setId(10L);
            return p;
        });

        final MemberDto created = this.memberService.createMember(this.sampleDto);

        assertThat(created).isNotNull();
        assertThat(created.name()).isEqualTo("Dupont");
        verify(this.personRepository).save(any(Person.class));
    }

    @Test
    void deleteMember_whenExists_shouldDelete() {
        when(this.personRepository.existsById(1L)).thenReturn(true);
        doNothing().when(this.personRepository).deleteById(1L);

        this.memberService.deleteMember(1L);

        verify(this.personRepository).deleteById(1L);
    }

    @Test
    void deleteMember_whenNotFound_shouldThrowException() {
        when(this.personRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> this.memberService.deleteMember(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
