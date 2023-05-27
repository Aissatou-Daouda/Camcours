<?php

$HTTP_ORIGIN = '/Camcours/api/v1';
$ALLOW_HTTP_GET = TRUE;
include('../../../ComRest/bootstrap.php');

$key = urlQuery('api_key', '');

function http_headers() {
    header('Content-Type: application/json');
}

$file = urlPath(0, NULL);
$loadFile = file_exists($file);

if ($key != 'f9575f33-f3f0-11ed-bba4-028037ec0200') {
    $ALLOW_HTTP_GET = FALSE;
    http_response('{"error": "Invalid API Key"}', 400);
} else if (file_exists($file . '.php')) {
    // Database
    include('../../database.php');
    
    // Helper
    if (file_exists('helpers/' . $file . '.php'))
        include('helpers/' . $file . '.php');
    else if (file_exists('helpers/' . substr($file, 0, -1) . '.php'))
        include('helpers/' . substr($file, 0, -1) . '.php');
    
    // Endpoint
    include($file . '.php');
    
} else {
    $ALLOW_HTTP_GET = FALSE;
    http_response('{"error": "Not found !"}', 404);
    $loadFile = FALSE;
}

// Processing
http_run();

?>
