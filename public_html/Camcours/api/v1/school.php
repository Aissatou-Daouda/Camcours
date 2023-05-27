<?php

$ALLOW_HTTP_GET = TRUE;
$ALLOW_HTTP_POST = TRUE;
$ALLOW_HTTP_PUT = TRUE;
$ALLOW_HTTP_DELETE = TRUE;

$schoolId = urlPath(1, 0);
$filter = "schoolId = $schoolId";

function http_get() {
    global $filter;
    
    $results = getSchools($filter);
    
    if (count($results) > 0) {
        $object = $results[0];
        $code = 200;
    } else {
        $object = array('error' => 1);
        $code = 404;
    }
    
    http_response(json_encode($object), $code);
}

function http_post() {
    $school = json_decode(requestData());
    
    if (getSchoolCount("schoolName = '$school->name'") > 0)
        http_response('{"error": 2}', 409);
    else if (($school->id = addSchool($school)) > 0)
        http_response(json_encode($school), 201);
    else
        http_response('{"error": 3}', 500);
}

?>
