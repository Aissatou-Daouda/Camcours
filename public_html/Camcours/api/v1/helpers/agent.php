<?php

function getAgents($filter) {
    global $db;

    $query = "
        SELECT
            agentId, agentName, agentLogin, agentPassword,
            schoolId, schoolName
        FROM
            Agents
        LEFT JOIN
            Schools USING(schoolId)
        WHERE
            $filter
    ";
    
    $results = $db->query($query);
    $agents = array();
    
    while ($results && ($res = $results->fetch())) {
        $a = array(
            'id'     => $res['agentId'] + 0,
            'name'   => $res['agentName'],
            'login'  => $res['agentLogin'],
            'password' => $res['agentPassword'],
            'school' => array(
                'id' => $res['schoolId'],
                'name' => $res['schoolName']
            )
        );
        
        array_push($agents, $a);
    }
    
    return $agents;
}

function getAgentCount($filter) {
    global $db;

    $query = "
        SELECT
            COUNT(agentId)
        FROM
            Agents
        WHERE
            $filter
    ";
    
    $result = $db->query($query);
    return ($result ? $result->fetch()[0] : 0) + 0;
}

function addAgent($agent) {
    global $db;

    $query = "
        INSERT INTO Agents(agentName, agentLogin, agentPassword, schoolId)
        VALUES('$agent->name', '$agent->login', '$agent->password', '$agent->school')
    ";
    
    $db->query($query);
    return $db->lastInsertId() + 0;
}

function editAgents($agent, $filter) {
    global $db;
    
    $data = array();
    
    if (isSet($agent->name)) 
        array_push($data, "agentName = '$agent->name'");
    
    if (isSet($agent->password)) 
        array_push($data, "agentPassword = '$agent->password'");

    $query = "UPDATE Agents SET " . implode(', ', $data) . " WHERE $filter";
    return $db->exec($query);
}

?>
