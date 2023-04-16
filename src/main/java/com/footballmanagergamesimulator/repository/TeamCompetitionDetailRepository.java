package com.footballmanagergamesimulator.repository;

import com.footballmanagergamesimulator.model.TeamCompetitionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamCompetitionDetailRepository extends JpaRepository<TeamCompetitionDetail, Long> {

  TeamCompetitionDetail findByTeamIdAndCompetitionId(long teamId, long competitionId);
}
