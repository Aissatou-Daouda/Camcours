<?php

// HTTP Methods
$ALLOW_HTTP_GET = TRUE;
$ALLOW_HTTP_POST = TRUE;
$ALLOW_HTTP_PUT = TRUE;
$ALLOW_HTTP_DELETE = TRUE;

$competId = urlPath(1, 0);
$filter = "competitionId = $competId";

function http_get() {
    global $filter;
    
    $results = getCompetitions($filter);
    
    if (count($results) > 0)
        http_response(json_encode($results[0]), 200);
    else
        http_response('{}', 404);
}

function http_post() {
    global $filter;
    
    $compet = json_decode(requestData());
    
    if (($compet->id = addCompetition($compet)) > 0)
        http_response(json_encode($compet), 201);
    else
        http_response('{}', 500);
}

function http_put() {
    global $filter;
    
    $compet = json_decode(requestData());
    
    if (editCompetitions($compet, $filter) > 0)
        http_get();
    else
        http_response('{}', 500);
}

?>
