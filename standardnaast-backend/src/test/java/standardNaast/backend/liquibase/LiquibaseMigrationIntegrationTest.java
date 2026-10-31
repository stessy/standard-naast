package standardNaast.backend.liquibase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import standardNaast.backend.domain.*;
import standardNaast.backend.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LiquibaseMigrationIntegrationTest {

    private static final String DATABASE_URL = "jdbc:h2:mem:liquibase_" + UUID.randomUUID()
            + ";DB_CLOSE_DELAY=-1";

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> DATABASE_URL);
    }

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
    private JdbcTemplate jdbcTemplate;

    @Test
    void testAllRepositoriesAfterLiquibaseMigration() {
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM DATABASECHANGELOG WHERE ID = '000-initial-schema' AND EXECTYPE = 'EXECUTED'",
                Long.class)).isEqualTo(1L);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM DATABASECHANGELOG WHERE ID = '099-initial-data'",
                Long.class)).isZero();

        assertThat(personRepository.count()).isZero();
        assertThat(seasonRepository.count()).isZero();
        assertThat(teamRepository.count()).isZero();
        assertThat(matchRepository.count()).isZero();
        assertThat(abonnementRepository.count()).isZero();
        assertThat(accountingRepository.count()).isZero();
        assertThat(benevolatRepository.count()).isZero();
        assertThat(travelPriceRepository.count()).isZero();
        assertThat(cotisationRepository.count()).isZero();
        assertThat(personCotisationRepository.count()).isZero();
        assertThat(personTravelRepository.count()).isZero();
        assertThat(appUserRepository.count()).isZero();
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

        Person saved = personRepository.saveAndFlush(newPerson);
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

        Accounting saved = accountingRepository.saveAndFlush(newEntry);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThanOrEqualTo(3956L);
    }
}
