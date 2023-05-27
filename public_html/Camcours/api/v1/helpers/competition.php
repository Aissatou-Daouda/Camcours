<?php

function getCompetitions($filter) {
    global $db;
    
    $query = "
        SELECT
            competitionId, competitionName, competitionDate, resultFile, schoolId
        FROM
            Competitions
        INNER JOIN
            Results USING(competitionId)
        WHERE
            $filter
        ORDER BY
            competitionDate DESC
    ";
    
    $results = $db->query($query);
    
    $competitions = array();
    while ($results && ($res = $results->fetch())) {
        $compet = array(
            'id'      => $res['competitionId'] + 0,
            'name'    => $res['competitionName'],
            'date'    => $res['competitionDate'],
            'pdfPath' => $res['resultFile'],
            'school' => array(
                'id' => $res['schoolId'] + 0
            )
        );
        
        array_push($competitions, $compet);
    }
    
    return $competitions;
}

function addCompetition($compet) {
    global $db;
    
    $query = "
        INSERT INTO Competitions(competitionName, competitionDate, schoolId)
        VALUES('$compet->name', '$compet->date', $compet->school)
    ";
    
    if ($db->exec($query) > 0)
        $competId = $db->lastInsertId();
    else
        return 0;
        
    $query = "
        INSERT INTO Results(resultFile, competitionId, agentId)
        VALUES('$compet->pdfPath', $competId, $compet->agent)
    ";
    
    $db->exec($query);
    
    return $competId;
}

function editCompetitions($compet, $filter) {
    global $db;
    
    $competData = array();
    $resData = array();
    $ok = 0;
    
    if (isSet($compet->name))
        array_push($competData, "competitionName = '$compet->name'");
    
    if (isSet($compet->date))
        array_push($competData, "competitionDate = '$compet->date'");
    
    if (isSet($compet->pdfPath))
        array_push($resData, "resultFile = '$compet->pdfPath'");
    
    if (count($competData)) {
        $query = 'UPDATE Competitions SET ' . implode(', ', $competData) . " WHERE $filter";
        $ok += $db->exec($query);
    }
    
    if (count($resData)) {
        $result = $db->query("SELECT resultId FROM Results INNER JOIN Competitions USING(competitionId) WHERE $filter");
        $result = $result->fetch()['resultId'];
        $query = 'UPDATE Results SET ' . implode(', ', $resData) . " WHERE resultId = $result";
        $ok += $db->exec($query);
    }
    
    return $ok;
}

?>
