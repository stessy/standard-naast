package standardNaast.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.Person;
import standardNaast.backend.dto.MemberCreateUpdateDto;
import standardNaast.backend.dto.MemberDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.MemberMapper;
import standardNaast.backend.repository.PersonRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final PersonRepository personRepository;
    private final MemberMapper memberMapper;

    @Override
    public Page<MemberDto> getMembers(final String search, final Pageable pageable) {
        log.debug("Fetching members with search='{}' and pageable={}", search, pageable);
        if (search == null || search.trim().isEmpty()) {
            return this.personRepository.findAll(pageable).map(this.memberMapper::toDto);
        }
        return this.personRepository.searchMembers(search.trim(), pageable).map(this.memberMapper::toDto);
    }

    @Override
    public MemberDto getMemberById(final Long id) {
        log.debug("Fetching member with id={}", id);
        return this.personRepository.findById(id)
                .map(this.memberMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Membre non trouvé avec l'id : " + id));
    }

    @Override
    public Optional<MemberDto> getMemberByMemberNumber(final Long memberNumber) {
        log.debug("Fetching member with memberNumber={}", memberNumber);
        return this.personRepository.findByMemberNumber(memberNumber)
                .map(this.memberMapper::toDto);
    }

    @Override
    @Transactional
    public MemberDto createMember(final MemberCreateUpdateDto dto) {
        log.info("Creating new member with name='{}' and firstname='{}'", dto.name(), dto.firstname());
        final Person person = this.memberMapper.toEntity(dto);
        if (person.getMemberNumber() == null || person.getMemberNumber() == 0) {
            person.setMemberNumber(this.getNextMemberNumber());
        }
        final Person savedPerson = this.personRepository.save(person);
        return this.memberMapper.toDto(savedPerson);
    }

    @Override
    @Transactional
    public MemberDto updateMember(final Long id, final MemberCreateUpdateDto dto) {
        log.info("Updating member with id={}", id);
        final Person person = this.personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membre non trouvé avec l'id : " + id));

        this.memberMapper.updateEntityFromDto(dto, person);
        final Person updatedPerson = this.personRepository.save(person);
        return this.memberMapper.toDto(updatedPerson);
    }

    @Override
    @Transactional
    public void deleteMember(final Long id) {
        log.info("Deleting member with id={}", id);
        if (!this.personRepository.existsById(id)) {
            throw new ResourceNotFoundException("Membre non trouvé avec l'id : " + id);
        }
        this.personRepository.deleteById(id);
    }

    @Override
    public Long getNextMemberNumber() {
        final Long max = this.personRepository.findMaxMemberNumber().orElse(0L);
        return max + 1;
    }
}
