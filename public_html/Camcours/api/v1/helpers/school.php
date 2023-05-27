<?php

function getSchoolCount($filter) {
    global $db;
    
    $query = "
        SELECT
            COUNT(schoolId)
        FROM
            Schools
        WHERE
            $filter
    ";
    
    $result = $db->query($query);
    return ($result ? $result->fetch()[0] : 0) + 0;
}

function getSchools($filter) {
    global $db;
    
    $query = "
        SELECT
            schoolId, schoolName
        FROM
            Schools
        WHERE
            $filter
    ";
    
    $results = $db->query($query);
    $schools = array();
    
    while ($results && ($res = $results->fetch())) {
        $school = array(
            'id'   => $res['schoolId'] + 0,
            'name' => $res['schoolName']
        );
        
        array_push($schools, $school);
    }
    
    return $schools;
}

function addSchool($school) {
    global $db;
    
    $query = "
        INSERT INTO Schools(schoolName)
        VALUES('$school->name')
    ";
    
    if ($db->exec($query) > 0)
        return $db->lastInsertId() + 0;
    else
        return 0;
}

?>
