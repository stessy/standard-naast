package standardNaast.dao;

import com.standardnaast.persistence.EntityManagerFactoryHelper;
import standardNaast.entities.Season;
import standardNaast.entities.SeasonTeam;
import standardNaast.entities.Team;
import standardNaast.types.CompetitionType;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class TeamDAOImpl implements TeamDAO {

    @Override
    public List<Team> getAllTeams() {
        final CriteriaQuery<Team> queryAll = this.getEntityManager()
                .getCriteriaBuilder().createQuery(Team.class);
        queryAll.from(Team.class);
        return this.getEntityManager().createQuery(queryAll).getResultList();
    }

    @Override
    public List<SeasonTeam> getTeamsPerSeason(final Season season) {
        EntityManager entityManager = this.getEntityManager();
        final TypedQuery<SeasonTeam> query = entityManager
                .createNamedQuery("teamPerSeason", SeasonTeam.class);
        query.setParameter("season", season);
        final List<SeasonTeam> resultList = query.getResultList();
        entityManager.close();
        return resultList;
    }

    public List<Team> getTeamsPerSeasonAndCompetitionType(final Season season,
                                                          final CompetitionType competitionType) {
        final TypedQuery<Team> query = this.getEntityManager()
                .createNamedQuery("teamPerSeason", Team.class);
        query.setParameter("season", season);
        return query.getResultList();
    }

    @Override
    public Team getTeam(final Long id) {
        return this.getEntityManager().find(Team.class, id);
    }

    @Override
    public Team addTeam(final Team team) {
        EntityManager entityManager = this.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(team);
        entityManager.getTransaction().commit();
        return team;
    }

    @Override
    public Team updateTeam(final Team team) {
        EntityManager entityManager = this.getEntityManager();
        entityManager.getTransaction().begin();
        final Team mergedTeam = entityManager.merge(team);
        entityManager.getTransaction().commit();
        return mergedTeam;
    }

    @Override
    public void addTeamToSeason(final Team team, final Season season) {
        final SeasonTeam seasonTeam = new SeasonTeam();
        seasonTeam.setOpponent(team);
        seasonTeam.setSeason(season);
        EntityManager entityManager = this.getEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(seasonTeam);
        entityManager.getTransaction().commit();
    }

    public EntityManager getEntityManager() {
        return EntityManagerFactoryHelper
                .getFactory().createEntityManager();
    }

}
