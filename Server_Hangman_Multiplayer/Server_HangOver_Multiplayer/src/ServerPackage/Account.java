package ServerPackage;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

/**
 * KTH - ID2212, Final Project
 * Account.java
 * Purpose: Database entity.
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 05/01/2015
 **/

@NamedQueries({
        @NamedQuery(
                name = "deleteAccountWithName",
                query = "DELETE FROM Account acct WHERE acct.accname LIKE :ownerName"
        ),
        @NamedQuery(
                name = "findAccountWithName",
                query = "SELECT acct FROM Account acct WHERE acct.accname LIKE :ownerName",
                lockMode = LockModeType.OPTIMISTIC
        )
})

@Entity
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "accname", nullable = false)
    private String accname;
    
    @Column(name = "score", nullable = false)
    private Integer score;
    
    @Column(name = "totgames", nullable = false)
    private Integer totgames;
    
    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;

    public Account() {
    }
    
    
    Account(String accname) {
        this.accname = accname;
        this.score = 0;
        this.totgames = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getTotgames() {
        return totgames;
    }
    
    public void incScore(){
        this.score = this.score + 1;
    }
    
    public void decScore(){
        this.score = this.score - 1;
    }
    
    public void incGames() {
        this.totgames = this.totgames + 1;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ServerPackage.Account[ id=" + id + " ]";
    }
    
}
