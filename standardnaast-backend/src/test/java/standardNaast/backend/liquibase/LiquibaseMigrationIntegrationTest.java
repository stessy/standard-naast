package standardNaast.backend.liquibase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import standardNaast.backend.domain.*;
import standardNaast.backend.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LiquibaseMigrationIntegrationTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private AbonnementRepository abonnementRepository;

    @Autowired
    private AccountingRepository accountingRepository;

    @Autowired
    private BenevolatRepository benevolatRepository;

    @Autowired
    private CotisationRepository cotisationRepository;

    @Autowired
    private PersonCotisationRepository personCotisationRepository;

    @Autowired
    private PersonTravelRepository personTravelRepository;

    @Autowired
    private TravelPriceRepository travelPriceRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private org.springframework.context.ApplicationContext applicationContext;

    @Test
    void testAllRepositoriesAfterLiquibaseMigration() {
        System.out.println("Liquibase beans: " + java.util.Arrays.toString(applicationContext.getBeanNamesForType(Object.class)));
        // Verify tables are created and populated by Liquibase
        long personCount = personRepository.count();
        assertThat(personCount).isGreaterThan(1000);

        long seasonCount = seasonRepository.count();
        assertThat(seasonCount).isGreaterThan(10);

        long teamCount = teamRepository.count();
        assertThat(teamCount).isGreaterThan(10);

        long matchCount = matchRepository.count();
        assertThat(matchCount).isGreaterThan(100);

        long abonnementCount = abonnementRepository.count();
        assertThat(abonnementCount).isGreaterThan(1000);

        long accountingCount = accountingRepository.count();
        assertThat(accountingCount).isGreaterThan(1000);

        long travelPriceCount = travelPriceRepository.count();
        assertThat(travelPriceCount).isGreaterThan(50);

        long cotisationCount = cotisationRepository.count();
        assertThat(cotisationCount).isGreaterThan(5);

        long personCotisationCount = personCotisationRepository.count();
        assertThat(personCotisationCount).isGreaterThan(1000);

        long personTravelCount = personTravelRepository.count();
        assertThat(personTravelCount).isGreaterThan(5000);
    }

    @Test
    void testInsertPersonSequenceWorks() {
        Person newPerson = Person.builder()
                .name("Dupont")
                .firstname("Jean")
                .email("jean.dupont@example.com")
                .memberNumber(99999L)
                .redCard(false)
                .student(false)
                .build();

        Person saved = personRepository.save(newPerson);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(10000000L);
    }

    @Test
    void testInsertAccountingSequenceWorks() {
        Accounting newEntry = Accounting.builder()
                .date(LocalDate.now())
                .description("Test Accounting Entry")
                .type(AccountingType.ENTRY)
                .amount(new BigDecimal("150.00"))
                .build();

        Accounting saved = accountingRepository.save(newEntry);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThanOrEqualTo(3956L);
    }
}
