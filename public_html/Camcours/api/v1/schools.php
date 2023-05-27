<?php

function http_get() {
    $query = urlQuery('query', NULL);
    if ($query)
        $filter = "schoolName LIKE '$query%'";
    else
        $filter = 'TRUE';

    $object = array(
        'results' => getSchools($filter)
    );
    
    http_response(json_encode($object), 200);
}

?>
