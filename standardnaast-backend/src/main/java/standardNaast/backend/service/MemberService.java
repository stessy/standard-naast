package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.dto.MemberCreateUpdateDto;
import standardNaast.backend.dto.MemberDto;

import java.util.Optional;

public interface MemberService {

    Page<MemberDto> getMembers(String search, Pageable pageable);

    MemberDto getMemberById(Long id);

    Optional<MemberDto> getMemberByMemberNumber(Long memberNumber);

    MemberDto createMember(MemberCreateUpdateDto dto);

    MemberDto updateMember(Long id, MemberCreateUpdateDto dto);

    void deleteMember(Long id);

    Long getNextMemberNumber();
}
