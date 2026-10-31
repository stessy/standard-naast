package standardNaast.backend.desktop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import standardNaast.backend.domain.Accounting;
import standardNaast.backend.domain.Person;
import standardNaast.backend.domain.Abonnement;
import standardNaast.backend.domain.AbonnementPrices;
import standardNaast.backend.domain.Benevolat;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.Match;
import standardNaast.backend.domain.PersonCotisation;
import standardNaast.backend.domain.PersonTravel;
import standardNaast.backend.domain.Place;
import standardNaast.backend.domain.Season;
import standardNaast.backend.domain.SeasonTeam;
import standardNaast.backend.domain.Team;
import standardNaast.backend.domain.TravelPrice;
import standardNaast.backend.security.InitialAdminSetup;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("desktop")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SqliteDesktopIntegrationTest {
    @TempDir
    static Path directory;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("app.desktop.data-dir", directory::toString);
    }

    @Autowired
    EntityManagerFactory factory;
    @Autowired
    DataSource dataSource;

    @Test
    void persistsIdentifiersDatesBooleansAndMoneyAcrossTransactions() {
        List<Long> ids = new ArrayList<>();
        EntityManager entityManager = factory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            for (int index = 0; index < 55; index++) {
                Person person = Person.builder().name("Membre " + index)
                        .birthdate(LocalDate.of(1980, 2, 29)).student(false).redCard(true).build();
                entityManager.persist(person);
                ids.add(person.getId());
            }
            Accounting accounting = Accounting.builder().amount(new BigDecimal("123456.78"))
                    .date(LocalDate.of(2026, 10, 25)).description("Test SQLite").build();
            entityManager.persist(accounting);
            entityManager.getTransaction().commit();
            entityManager.clear();
            assertThat(ids).doesNotHaveDuplicates().doesNotContainNull();
            Person loaded = entityManager.find(Person.class, ids.getFirst());
            assertThat(loaded.getBirthdate()).isEqualTo(LocalDate.of(1980, 2, 29));
            assertThat(loaded.getStudent()).isFalse();
            assertThat(loaded.getRedCard()).isTrue();
            Accounting loadedAccounting = entityManager.find(Accounting.class, accounting.getId());
            assertThat(loadedAccounting.getAmount()).isEqualByComparingTo("123456.78");
            assertThat(loadedAccounting.getDate()).isEqualTo(LocalDate.of(2026, 10, 25));
            entityManager.getTransaction().begin();
            Person next = Person.builder().name("Apres commit").build();
            entityManager.persist(next);
            entityManager.getTransaction().commit();
            assertThat(ids).doesNotContain(next.getId());
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }

    @Test
    void enablesForeignKeysAndCreatesOnlyTheFirstAdministrator() throws Exception {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            try (var rows = statement.executeQuery("PRAGMA foreign_keys")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getInt(1)).isEqualTo(1);
            }
            assertThatThrownBy(() -> statement.executeUpdate(
                    "INSERT INTO APP_USER_ROLES(USER_ID, ROLE) VALUES (-1, 'ROLE_ADMIN')"))
                    .isInstanceOf(java.sql.SQLException.class);
            statement.executeUpdate("""
                    INSERT INTO APP_USERS (USERNAME, PASSWORD, ENABLED, CREATED_AT, UPDATED_AT)
                    VALUES ('membre-import', 'hash-existant', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                    """);
            long importedUserId;
            try (var rows = statement.executeQuery("SELECT USER_ID FROM APP_USERS WHERE USERNAME='membre-import'")) {
                assertThat(rows.next()).isTrue();
                importedUserId = rows.getLong(1);
            }
            try (var role = connection.prepareStatement(
                    "INSERT INTO APP_USER_ROLES(USER_ID, ROLE) VALUES (?, 'ROLE_MEMBER')")) {
                role.setLong(1, importedUserId);
                role.executeUpdate();
            }
            InitialAdminSetup.createAdmin(connection, "admin-test", "password-test-123");
            assertThat(InitialAdminSetup.hasUsers(connection)).isTrue();
            assertThat(InitialAdminSetup.hasAdmin(connection)).isTrue();
            assertThatThrownBy(() -> InitialAdminSetup.createAdmin(connection, "autre-admin", "password-test-456"))
                    .isInstanceOf(IllegalStateException.class);
            try (var rows = statement.executeQuery("SELECT COUNT(*) FROM APP_USER_ROLES WHERE ROLE='ROLE_ADMIN'")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getInt(1)).isEqualTo(1);
            }
        }

    }

    @Test
    void persistsTheCompleteRelationshipGraphWithSqliteIdentityGeneration() {
        EntityManager entityManager = factory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            Season season = Season.builder().id("2026-2027")
                    .dateStart(LocalDate.of(2026, 7, 1)).dateEnd(LocalDate.of(2027, 6, 30)).build();
            entityManager.persist(season);
            Person person = Person.builder().name("Relations SQLite").build();
            entityManager.persist(person);
            Team team = Team.builder().name("Adversaire").build();
            entityManager.persist(team);
            SeasonTeam seasonTeam = SeasonTeam.builder().season(season).opponent(team).build();
            entityManager.persist(seasonTeam);
            LocalDateTime matchDate = LocalDateTime.of(2026, 10, 25, 18, 30, 15);
            Match match = Match.builder().season(season).opponent(team).seasonTeam(seasonTeam)
                    .place(Place.HOME).competitionType(CompetitionType.CHAMPIONSHIP).dateMatch(matchDate).build();
            entityManager.persist(match);
            AbonnementPrices price = new AbonnementPrices();
            price.setSeason(season);
            price.setPrice(150L);
            price.setBloc("A1");
            price.setTypeCompetition(CompetitionType.CHAMPIONSHIP);
            entityManager.persist(price);
            TravelPrice travelPrice = TravelPrice.builder().season(season).montant(new BigDecimal("12.50"))
                    .place(Place.AWAY).membre(true).build();
            entityManager.persist(travelPrice);
            Benevolat benevolat = Benevolat.builder().person(person).amount(new BigDecimal("10.50"))
                    .dateBenevolat(LocalDate.of(2026, 10, 25)).build();
            entityManager.persist(benevolat);
            PersonCotisation cotisation = PersonCotisation.builder().person(person).season(season)
                    .datePaiement(LocalDate.of(2026, 10, 25)).carteMembreEnvoyee(true).build();
            entityManager.persist(cotisation);
            PersonTravel travel = PersonTravel.builder().person(person).match(match).carTravelAmount(15).build();
            entityManager.persist(travel);
            Abonnement abonnement = new Abonnement();
            abonnement.setSeason(season);
            abonnement.setPersonne(person);
            abonnement.setAbonnementPrice(price);
            abonnement.setPaye(true);
            entityManager.persist(abonnement);
            entityManager.getTransaction().commit();
            entityManager.clear();
            assertThat(List.of(team.getId(), seasonTeam.getId(), match.getId(), price.getId(),
                    travelPrice.getId(), benevolat.getId(), cotisation.getId(), travel.getId(), abonnement.getId()))
                    .allMatch(id -> id > 0);
            assertThat(entityManager.find(Match.class, match.getId()).getDateMatch()).isEqualTo(matchDate);
            assertThat(entityManager.find(Abonnement.class, abonnement.getId()).getAbonnementPrice().getPrice())
                    .isEqualTo(150L);
            assertThat(entityManager.find(PersonCotisation.class, cotisation.getId()).isCarteMembreEnvoyee()).isTrue();
            assertThat(entityManager.find(TravelPrice.class, travelPrice.getId()).getMontant())
                    .isEqualByComparingTo("12.50");
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
    }
}
