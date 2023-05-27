<?php

include('utils.php');

// Check
if (!isSet($HTTP_ORIGIN)) {
    echo 'HTTP_ORIGIN not set !';
    exit(1);
}

// Processing
if (isSet($HTTP_AUTORUN) && $HTTP_AUTORUN)
    http_register_run();

?>
