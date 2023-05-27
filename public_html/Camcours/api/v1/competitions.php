<?php

function http_get() {
    $schoolId = urlQuery('school', 0);
    $agentId = urlQuery('agent', 0);
    
    $filters = array();
    if ($schoolId > 0)
        array_push($filters, "schoolId = $schoolId");
    if ($agentId > 0)
        array_push($filters, "agentId = $agentId");
        
    if (count($filters) < 1)
        array_push($filters, 'TRUE');

    $object = array(
        'results' => getCompetitions(implode(' AND ', $filters))
    );

    http_response(json_encode($object), 200);
}

?>
