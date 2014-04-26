package com.seanshubin.schulze.persistence.datomic_functions;

import datomic.Database;
import datomic.function.Function;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static datomic.Peer.q;
import static datomic.Peer.tempid;
import static datomic.Util.list;
import static datomic.Util.map;

public class DatomicFunctions {
    public Long findElectionByName(Database db, String name) {
        String query =
                "[:find ?election :in $ ?electionName :where " +
                        "[?election :election/name ?electionName]]";
        Collection rows = q(query, db, name);
        if (rows.size() == 0) {
            return null;
        } else {
            List row = (List) rows.iterator().next();
            Long electionRef = (Long) row.get(0);
            return electionRef;
        }
    }

    public List createElection(Object dbObject, Object nameObject) {
        Database db = (Database) dbObject;
        String name = (String) nameObject;
        Function findElectionByNameFunction = (Function) db.entity("findElectionByName").get("db/fn");
        Long electionRef = (Long) findElectionByNameFunction.invoke(db, name);
        if (electionRef == null) {
            return list(map(
                    ":db/id", tempid(":db.part/user"),
                    ":election/name", name));
        } else {
            return list();
        }
    }

    public List deleteElection(Object dbObject, Object nameObject) {
        Database db = (Database) dbObject;
        Function findElectionByNameFunction = (Function) db.entity("findElectionByName").get("db/fn");
        Long electionRef = (Long) findElectionByNameFunction.invoke(db, nameObject);
        if (electionRef == null) {
            return list();
        } else {
            Function findCandidatesByElectionFunction = (Function) db.entity("findElectionByName").get("db/fn");
            Function findRankingsByElectionFunction = (Function) db.entity("findElectionByName").get("db/fn");
            List toDelete = list();
            toDelete.add(electionRef);
            toDelete.addAll((Collection) findCandidatesByElectionFunction.invoke(db, electionRef));
            toDelete.addAll((Collection) findRankingsByElectionFunction.invoke(db, electionRef));
            List retractions = list();
            for (Object o : toDelete) {
                retractions.add(list(":db.fn/retractEntity", o));
            }
            return retractions;
        }
    }

    public List createVoter(Object nameObject) {
        String name = (String) nameObject;
        return list();
    }

    public List deleteVoter(Object nameObject) {
        String name = (String) nameObject;
        return list();
    }

    public List createCandidate(Object electionNameObject, Object candidateNameObject) {
        String electionName = (String) electionNameObject;
        String candidateName = (String) candidateNameObject;
        return list();
    }

    public List updateCandidate(Object electionNameObject, Object candidateNameObject, Object maybeDescriptionObject) {
        String electionName = (String) electionNameObject;
        String candidateName = (String) candidateNameObject;
        String maybeDescription = (String) maybeDescriptionObject;
        return list();
    }

    public List deleteCandidate(Object electionNameObject, Object candidateNameObject) {
        String electionName = (String) electionNameObject;
        String candidateName = (String) candidateNameObject;
        return list();
    }

    public List updateVote(Object electionNameObject, Object voterNameObject, Object rankingsObject) {
        String electionName = (String) electionNameObject;
        String voterName = (String) voterNameObject;
        Map<String, Long> rankings = (Map<String, Long>) rankingsObject;
        return list();
    }

    public List resetVote(Object electionNameObject, Object voterNameObject) {
        String electionName = (String) electionNameObject;
        String voterName = (String) voterNameObject;
        return list();
    }
}
