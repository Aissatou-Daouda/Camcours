<?php

header('Content-Type: application/json');

$file = '/upload/pdf/doc-' .  uniqid() . '.pdf';
$data = file_get_contents('php://input');

if (FALSE) {
    $object = array(
        'error' => ''
    );
    http_response_code(401);
} else if (file_put_contents(getcwd() . $file, $data)) {
    $object = array(
        'pdfPath' => $file
    );
    http_response_code(201);
} else {
    $object = array(
        'error' => ''
    );
    http_response_code(500);
}

echo json_encode($object);

?>
