package standardNaast.backend.domain;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "BLOC")
@Access(AccessType.FIELD)
public class Bloc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "BLOC_ID")
    private Long id;

    @Column(name = "BLOC_VALUE", nullable = false, length = 50)
    private String blocValue;

    @Column(name = "TRIBUNE", length = 100)
    private String tribune;

    @Column(name = "CLUB", nullable = false, length = 200)
    private String club;

    @Column(name = "EQUIPE_ID")
    private Long equipeId;

    public Bloc() {
    }

    public Bloc(Long id, String blocValue, String tribune, String club, Long equipeId) {
        this.id = id;
        this.blocValue = blocValue;
        this.tribune = tribune;
        this.club = club;
        this.equipeId = equipeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBlocValue() {
        return blocValue;
    }

    public void setBlocValue(String blocValue) {
        this.blocValue = blocValue;
    }

    public String getTribune() {
        return tribune;
    }

    public void setTribune(String tribune) {
        this.tribune = tribune;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public Long getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(Long equipeId) {
        this.equipeId = equipeId;
    }
}
