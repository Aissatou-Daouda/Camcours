<?php

include('helpers/school.php');

$param = urlPath(1, NULL);

$LOGIN_MODE = FALSE;

if ($param == 'login') {
    $object = json_decode(requestData());
    $login = $object->login;
    $pass = $object->password;
    $filter = "agentLogin = '$login' AND agentPassword = '$pass'";
    $LOGIN_MODE = TRUE;
} else if ($param)
    $filter = "agentId = $param";
else
    $filter = "FALSE";

$ALLOW_HTTP_GET = !$LOGIN_MODE;
$ALLOW_HTTP_POST = TRUE;
$ALLOW_HTTP_PUT = !$LOGIN_MODE;
$ALLOW_HTTP_DELETE = !$LOGIN_MODE;
    
    
function http_get() {
    global $filter;
    
    $results = getAgents($filter);
    
    if (count($results) > 0)
        http_response(json_encode($results[0]), 200);
    else
        http_response(json_encode('{"error": 1, "description": "Agent not found"}'), 404);
}

function http_post() {
    global $LOGIN_MODE;
    if ($LOGIN_MODE) {
        http_get();
        return;
    }
    
    $object = json_decode(requestData());
    $code = 201;
    
    if (getAgentCount("agentLogin = '$object->login'") > 0) {
        $object = array(
            'error'       => 2,
            'description' => "Agent $object->login already exists"
        );
        
        $code = 409;
    } else {
        $object->id = addAgent($object);
        
        if ($object->id <= 0) {
            $object = array('error' => 3, 'description' => '');
            $code = 500;
        } else
            $object->school = getSchools("schoolId = $object->school")[0];
    }
    
    http_response(json_encode($object), $code);
}

function http_put() {
    global $filter;
    
    $object = json_decode(requestData());
    $code = 200;
    
    if (getAgentCount($filter) != 1) {
        $object = array(
            'error'       => 4,
            'description' => "Agent $object->login doesn't exists"
        );
        $code = 404;
    } else {
        editAgents($object, $filter);
        $object = getAgents($filter)[0];
    }
    
    http_response(json_encode($object), $code);
}

?>
