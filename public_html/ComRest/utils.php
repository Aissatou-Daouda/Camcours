<?php

// Processing
function http_run() {
	global $ALLOW_HTTP_GET;
	global $ALLOW_HTTP_POST;
	global $ALLOW_HTTP_PUT;
	global $ALLOW_HTTP_DELETE;
	
    http_headers();

    switch ($_SERVER['REQUEST_METHOD']) {
        case 'GET':
	        if (isSet($ALLOW_HTTP_GET) && $ALLOW_HTTP_GET)
		        http_get();
	        break;

        case 'POST':
	        if (isSet($ALLOW_HTTP_POST) && $ALLOW_HTTP_POST)
	            http_post();
	        break;

        case 'PUT':
	        if (isSet($ALLOW_HTTP_PUT) && $ALLOW_HTTP_PUT)
	            http_put();
	        break;

        case 'DELETE':
	        if (isSet($ALLOW_HTTP_DELETE) && $ALLOW_HTTP_DELETE)
	            http_delete();
	        break;
    }
}

function http_register_run() {
	global $ALLOW_HTTP_GET;
	global $ALLOW_HTTP_POST;
	global $ALLOW_HTTP_PUT;
	global $ALLOW_HTTP_DELETE;
	
    register_shutdown_function('http_headers');

    switch ($_SERVER['REQUEST_METHOD']) {
        case 'GET':
	        if (isSet($ALLOW_HTTP_GET) && $ALLOW_HTTP_GET)
		        register_shutdown_function('http_get');
	        break;

        case 'POST':
	        if (isSet($ALLOW_HTTP_POST) && $ALLOW_HTTP_POST)
		        register_shutdown_function('http_post');
	        break;

        case 'PUT':
	        if (isSet($ALLOW_HTTP_PUT) && $ALLOW_HTTP_PUT)
		        register_shutdown_function('http_put');
	        break;

        case 'DELETE':
	        if (isSet($ALLOW_HTTP_DELETE) && $ALLOW_HTTP_DELETE)
		        register_shutdown_function('http_delete');
	        break;
    }
}

// Utilities
function endpointFile($ext) {
    return urlPath(0, NULL) . '.php';
}

function http_response($data, $code) {
    echo $data;
    http_response_code($code);
}

// Url path
$tmp = str_replace('?' . $_SERVER['QUERY_STRING'], '', $_SERVER['REQUEST_URI']);
$tmp = str_replace($HTTP_ORIGIN . '/', '', $tmp);

$URL_PATHS = explode('/', $tmp);

function urlPath($index, $default)
{
    global $URL_PATHS;
    
    if (count($URL_PATHS) > $index)
        return $URL_PATHS[$index];
    else
        return $default;
}

// Url query
$tmp = explode('&', $_SERVER['QUERY_STRING']);

$URL_QUERIES = array();
foreach ($tmp as $query)
    array_push($URL_QUERIES, explode('=', $query));
    
function urlQuery($name, $default)
{
    global $URL_QUERIES;
        
    foreach ($URL_QUERIES as $q)
        if ($q[0] == $name && count($q) > 1)
            return $q[1];
    
    return $default;
}

// Request body
function requestData() {
    return file_get_contents('php://input');
}

?>
