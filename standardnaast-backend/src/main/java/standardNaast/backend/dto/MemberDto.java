package standardNaast.backend.dto;

import java.time.LocalDate;

public record MemberDto(
        Long id,
        String name,
        String firstname,
        String address,
        String postalCode,
        String city,
        LocalDate birthdate,
        String email,
        String mobilePhone,
        String phone,
        LocalDate passportValidity,
        String identityCardNumber,
        Long memberNumber,
        Boolean student,
        Boolean redCard
) {}
